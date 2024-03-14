/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 296 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.jsapi2.synthesis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.speech.AudioSegment;
import javax.speech.Engine;
import javax.speech.EngineStateException;
import javax.speech.synthesis.PhoneInfo;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;

import org.jvoicexml.jsapi2.BaseAudioManager;

import static java.lang.System.getLogger;


/**
 * Play back the audio coming from the synthesizer.
 *
 * @author Dirk Schnelle-Walka
 */
class PlayQueue {

    private static final Logger logger = getLogger(PlayQueue.class.getName());

    /** Reference to the queue manager. */
    private final QueueManager queueManager;

    /** Buffer size when reading from the audio segment input stream. */
    private static final int BUFFER_LENGTH = 256;

    /** The items to be played back. */
    private final BlockingQueue<QueueItem> queue;

    private final AtomicReference<QueueItem> currentItem = new AtomicReference<>();

    /**
     * Constructs a new object.
     *
     * @param manager the queue manager
     */
    public PlayQueue(QueueManager manager) {
        queueManager = manager;
        queue = new java.util.concurrent.LinkedBlockingQueue<>();
    }

    /**
     * Retrieves a stream that matches the target audio format.
     *
     * @param manager the audio manager
     * @param stream  the current stream
     * @return a converting stream
     * @throws IOException error converting the stream
     */
    private static AudioInputStream getConvertedStream(BaseAudioManager manager, InputStream stream)
            throws IOException {
        AudioInputStream in;
        AudioFormat engineFormat = manager.getEngineAudioFormat();
        if (stream instanceof AudioInputStream) {
            in = (AudioInputStream) stream;
        } else {
            in = new AudioInputStream(stream, engineFormat, stream.available());
        }
        AudioFormat targetFormat = manager.getTargetAudioFormat();
        return AudioSystem.getAudioInputStream(targetFormat, in);
    }

    private static class CancelledException extends Exception {}

    private QueueItem getCurrent() throws CancelledException {
        QueueItem item = currentItem.get();
        if (item != null) {
            return item;
        } else {
            throw new CancelledException();
        }
    }

    /** threading task */
    void loop() {
        int playIndex;
        int wordIndex;
        int wordStart;
        int phonemeIndex;
        double timeNextPhone;
        byte[] buffer = new byte[BUFFER_LENGTH];

        while (!queueManager.isDone()) {
            currentItem.set(getNextQueueItem());
            if (currentItem.get() == null) {
logger.log(Level.TRACE, "P:: queue item null???");
                continue;
            }
logger.log(Level.TRACE, "P:: queue item taken: " + currentItem.get());
            try {
                Object source = getCurrent().getSource();
                int id = getCurrent().getId();
                SpeakableListener listener = getCurrent().getListener();
                postTopOfQueue(getCurrent());
                try {
                    delayUntilResumed(getCurrent());
                } catch (InterruptedException e1) {
logger.log(Level.TRACE, "at delayUntilResumed 1: " + e1.getMessage());
                }

                BaseSynthesizer synthesizer = queueManager.getSynthesizer();
                SpeakableEvent startedEvent = new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_STARTED, id);
                synthesizer.postSpeakableEvent(startedEvent, listener);

                playIndex = 0;
                wordIndex = 0;
                wordStart = 0;
                phonemeIndex = 0;
                timeNextPhone = 0;
                int bytesRead;
                BaseAudioManager manager = (BaseAudioManager) synthesizer.getAudioManager();
                AudioFormat format = manager.getEngineAudioFormat();
                float sampleRate = format.getSampleRate();
                try {
                    AudioSegment segment = getCurrent().getAudioSegment();
                    InputStream stream = segment.openInputStream();
                    if (stream == null) {
                        throw new IOException("no audio stream");
                    }
                    InputStream inputStream = getConvertedStream(manager, stream);
                    while ((bytesRead = inputStream.read(buffer)) >= 0) {
                        try {
                            delayUntilResumed(getCurrent());
                        } catch (InterruptedException e) {
logger.log(Level.TRACE, "delayUntilResumed 2: " + e.getMessage());
                            break;
                        }

                        synchronized (queueManager.cancelLock) {
                            if (queueManager.cancelFirstItem) {
                                synthesizer.postSpeakableEvent(new SpeakableEvent(
                                        source, SpeakableEvent.SPEAKABLE_CANCELLED, id), listener);
                                break;
                            }
                        }

                        String[] words = getCurrent().getWords();
                        while (wordIndex < words.length
                                && (getCurrent().getWordsStartTime()[wordIndex] * sampleRate <= playIndex * bytesRead)) {
                            synthesizer.postSpeakableEvent(new SpeakableEvent(
                                            source, SpeakableEvent.WORD_STARTED, id,
                                            words[wordIndex], wordStart,
                                            wordStart + words[wordIndex].length()),
                                    listener);
                            wordStart += words[wordIndex].length() + 1;
                            wordIndex++;
                        }

                        if (words.length > 0 && wordIndex > 0) {
                            PhoneInfo[] phones = getCurrent().getPhonesInfo();
                            while (phonemeIndex < phones.length
                                    && timeNextPhone * sampleRate < playIndex * bytesRead) {
                                synthesizer.postSpeakableEvent(new SpeakableEvent(
                                                source, SpeakableEvent.PHONEME_STARTED,
                                                id, words[wordIndex - 1],
                                                phones, phonemeIndex),
                                        listener);
                                timeNextPhone += (double) phones[phonemeIndex].getDuration() / (double) 1000;
                                phonemeIndex++;
                            }
                        }
                        playIndex++;

                        OutputStream out = manager.getOutputStream();
                        out.write(buffer, 0, bytesRead);
                    }

                    // Flush audio in the stream
                    OutputStream out = manager.getOutputStream();
                    if (out != null) {
                        out.flush();
                    }
                } catch (IOException ex) {
logger.log(Level.TRACE, ex.getMessage(), ex);
                    synthesizer.postSpeakableEvent(
                            new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_FAILED, id), listener);
                    continue;
                }

                if (!queueManager.cancelFirstItem) {
                    synthesizer.postSpeakableEvent(
                            new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_ENDED, id), listener);
                }

                synchronized (queueManager.cancelLock) {
                    queueManager.cancelFirstItem = false;
                }
            } catch (CancelledException e) {
logger.log(Level.TRACE, "cancelled by outer loop: " + e.getStackTrace()[2], e);
                queueManager.cancelFirstItem = false;
                continue;
            }

            currentItem.set(null);
            postEventsAfterPlay();
        }
logger.log(Level.DEBUG, "play queue loop terminated");
    }

    /**
     * Removes the given item from the play queue.
     *
     * @param item the item to remove
     */
    private void removeQueueItem(QueueItem item) {
        synchronized (queue) {
            boolean r = queue.remove(item);
            if (!r) {
logger.log(Level.TRACE, "P:: remove failed: " + item);
            }
        }
    }

    /**
     * Posts an event that processing of the queue has started.
     *
     * @param item the top most queue item
     */
    private void postTopOfQueue(QueueItem item) {
        SpeakableListener listener = item.getListener();
        if (listener == null) {
            return;
        }
        Object source = item.getSource();
        int id = item.getId();
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        synthesizer.postSpeakableEvent(new SpeakableEvent(source, SpeakableEvent.TOP_OF_QUEUE, id), listener);
    }

    /**
     * Delays until the synthesizer moved into the
     * {@link Synthesizer#RESUMED} state.
     * <p>
     * If the {@link Synthesizer} is in the {@link Synthesizer#PAUSED}
     * state, first a {@link SpeakableEvent#SPEAKABLE_PAUSED} is issued.
     * Once the {@link Synthesizer} is resumed, a
     * {@link SpeakableEvent#SPEAKABLE_RESUMED} is issued.
     * </p>
     *
     * @param item the next queue item to play
     * @throws InterruptedException if the waiting was interrupted
     */
    private void delayUntilResumed(QueueItem item) throws InterruptedException {
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        while (synthesizer.testEngineState(Synthesizer.PAUSED)) {
            SpeakableListener listener = item.getListener();
            Object source = item.getSource();
            int id = item.getId();
            SpeakableEvent pausedEvent = new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_PAUSED, id);
            synthesizer.postSpeakableEvent(pausedEvent, listener);

            synthesizer.waitEngineState(Engine.RESUMED);
            SpeakableEvent resumedEvent = new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_RESUMED, id);
            synthesizer.postSpeakableEvent(resumedEvent, listener);
        }
    }

    /**
     * Posts an update event after a play has been performed. This is
     * either a {@link SynthesizerEvent#QUEUE_EMPTIED} event if the
     * queue is empty or {@link SynthesizerEvent#QUEUE_UPDATED} if there
     * are more speakables to process,
     */
    private void postEventsAfterPlay() {
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        if (isQueueEmpty()) {
            long[] states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY, Synthesizer.QUEUE_EMPTY);
            synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_EMPTIED, true);
        } else {
            long[] states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY, Synthesizer.QUEUE_NOT_EMPTY);
            synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_UPDATED, true);
        }
    }

    /**
     * Notifies the play queue that the given item has changed.
     *
     * @param item the item that has changed
     */
    public void itemChanged(QueueItem item) {
logger.log(Level.TRACE, "P:: itemChanged: " + item);
    }

    /**
     * Adds the given item to the play queue.
     *
     * @param item the item to add
     */
    public void addQueueItem(QueueItem item) {
        boolean r = queue.offer(item);
        if (!r) {
logger.log(Level.TRACE, "P:: add failed: " + item);
        }
logger.log(Level.TRACE, "P:: queue is added then size " + queue.size());
    }

    /**
     * Retrieves the queue item with the given id.
     *
     * @param id the id of the queue item to look for
     * @return found queue item, or <code>null</code> if there is no
     * queue item with the given id
     */
    public QueueItem getQueueItem(int id) {
        synchronized (queue) {
            for (QueueItem item : queue) {
                if (item.getId() == id) {
logger.log(Level.TRACE, "P:: item found id: " + id);
                    return item;
                }
            }
        }
logger.log(Level.TRACE, "P:: item not found id: " + id);
        return null;
    }

    /**
     * Return, and remove, the first item on the play queue.
     *
     * @return a queue item to play
     * @since 0.6.7 make item removed from queue
     */
    protected QueueItem getNextQueueItem() {
        if (!queueManager.isDone()) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
logger.log(Level.TRACE, "P:: interrupted");
            } finally {
logger.log(Level.TRACE, "P:: queue is taken then size is " + queue.size());
            }
        }
        return null;
    }

    /**
     * Checks if the queue item at the given index has already been synthesized.
     *
     * @param index the index to look for the queue item
     * @return <code>true</code> if the item at the given index has been
     * synthesized
     */
    private boolean isSynthesized(int index) {
        int i = 0;
        for (QueueItem item : queue) {
            if (i++ == index) {
                return item.isSynthesized();
            }
        }
        return false;
    }

    /**
     * Determines if the input queue is empty.
     *
     * @return <code>true</code> if the queue is empty; otherwise
     * <code>false</code>
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Cancels the item at the top of the queue.
     *
     * @return <code>true</code> if an item was canceled
     * @throws EngineStateException if the engine is in an invalid state
     */
    protected boolean cancelItemAtTopOfQueue() throws EngineStateException {
        QueueItem item = currentItem.get();
        if (item == null) {
logger.log(Level.TRACE, "P:: cancel: queue empty");
            return false;
        }
logger.log(Level.TRACE, "P:: cancel: " + item);

        currentItem.set(null);

        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
//        BaseAudioManager manager = (BaseAudioManager) synthesizer.getAudioManager();
//        OutputStream out = manager.getOutputStream();
        // TODO should cancel the output stream

        synthesizer.handleCancel();
        Object source = item.getSource();
        int id = item.getId();
        SpeakableListener listener = item.getListener();
        synthesizer.postSpeakableEvent(
                new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_CANCELLED, id), listener);

        synchronized (this.queueManager.cancelLock) {
            queueManager.cancelFirstItem = true;
        }
        return true;
    }

    /**
     * Cancels the playback of the speakable with the given id.
     *
     * @param id the speakable to cancel
     * @return <code>true</code> if the speakable was canceled
     */
    protected boolean cancelItem(int id) {
        // search item in playqueue
        synchronized (queue) {
            QueueItem item = getQueueItem(id);
            if (item != null) {
                BaseSynthesizer synthesizer = queueManager.getSynthesizer();
                if (!item.isSynthesized()) {
                    synthesizer.handleCancel(id);
                }
                synthesizer.postSpeakableEvent(new SpeakableEvent(
                                item.getSource(), SpeakableEvent.SPEAKABLE_CANCELLED, id),
                        item.getListener());
                synthesizer.postSynthesizerEvent(
                        synthesizer.getEngineState(),
                        synthesizer.getEngineState(),
                        SynthesizerEvent.QUEUE_UPDATED, false);
                removeQueueItem(item);
                return true;
            } else {
                return false;
            }
        }
    }

    /** */
    public void setWords(int id, String[] words) {
        synchronized (queue) {
            QueueItem item = getQueueItem(id);
            if (item == null) {
                return;
            }
            item.setWords(words);
            itemChanged(item);
        }
    }

    /** */
    public void setWordsStartTimes(int id, float[] starttimes) {
        synchronized (queue) {
            QueueItem item = getQueueItem(id);
            if (item == null) {
                return;
            }
            item.setWordsStartTimes(starttimes);
            itemChanged(item);
        }
    }

    /** */
    public void setPhonesInfo(int itemId, PhoneInfo[] phonesinfo) {
        synchronized (queue) {
            QueueItem item = getQueueItem(itemId);
            if (item == null) {
                return;
            }
            item.setPhonesInfo(phonesinfo);
            itemChanged(item);
        }
    }
}
