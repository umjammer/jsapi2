/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 296 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Vector;

import javax.speech.AudioException;
import javax.speech.AudioSegment;
import javax.speech.Engine;
import javax.speech.EngineStateException;
import javax.speech.synthesis.PhoneInfo;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;

import org.jvoicexml.jsapi2.AudioFormat;
import org.jvoicexml.jsapi2.BaseAudioManager;

/**
 * Play back the audio coming from the synthesizer.
 * @author Dirk Schnelle-Walka
 */
class PlayQueue implements Runnable {
    /** Reference to the queue manager. */
    private final QueueManager queueManager;

    /** Buffer size when reading from the audio segment input stream. */
    private static final int BUFFER_LENGTH = 1024;

    /** The items to be played back. */
    private Vector playQueue;

    /**
     * Constructs a new object.
     * @param queueManager TODO
     */
    public PlayQueue(QueueManager queueManager) {
        this.queueManager = queueManager;
        playQueue = new Vector();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        int playIndex = 0;
        int wordIndex = 0;
        int wordStart = 0;
        int phonemeIndex = 0;
        double timeNextPhone = 0;
        long nextTimeStamp = 0;

        final byte[] buffer = new byte[BUFFER_LENGTH];

        while (!queueManager.isDone()) {
            final QueueItem item = getNextQueueItem();
            final Object source = item.getSource();
            final int id = item.getId();
            final SpeakableListener listener = item.getListener();
            postTopOfQueue(item);
            try {
                delayUntilResumed(item);
            } catch (InterruptedException e1) {
                return;
            }

            long totalBytesRead = 0;
            final SpeakableEvent startedEvent = new SpeakableEvent(source,
                    SpeakableEvent.SPEAKABLE_STARTED, id);
            queueManager.synthesizer.postSpeakableEvent(startedEvent, listener);

            playIndex = 0;
            wordIndex = 0;
            wordStart = 0;
            phonemeIndex = 0;
            timeNextPhone = 0;
            int bytesRead = 0;
            final BaseAudioManager manager =
                (BaseAudioManager) queueManager.synthesizer.getAudioManager();
            final AudioFormat format;
            try {
                format = manager.getAudioFormat();
            } catch (AudioException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                break;
            }
            final float sampleRate = format.getSampleRate();
            long bps = format.getChannels();
            bps *= sampleRate;
            bps *= (format.getSampleSizeInBits() / 8);
            try {
                while (true) {
                    final AudioSegment segment = item.getAudioSegment();
                    if ((segment == null) || !segment.isGettable()) {
                        throw new SecurityException(
                            "The platform does not allow to access the input "
                                + "stream!");
                    }
                    final InputStream inputStream =
                            segment.openInputStream();
                    if (inputStream == null) {
                        break;
                    }
                    bytesRead = inputStream.read(buffer);
                    if (bytesRead < 0)  {
                        break;
                    }

                    totalBytesRead += bytesRead;
                    synchronized (queueManager.cancelLock) {
                        if (queueManager.cancelFirstItem) {
                            final SpeakableEvent cancelledEvent =
                                new SpeakableEvent(source,
                                    SpeakableEvent.SPEAKABLE_CANCELLED, id);
                            queueManager.synthesizer.postSpeakableEvent(cancelledEvent,
                                    listener);
                            break;
                        }
                    }
                    try {
                        delayUntilResumed(item);
                    } catch (InterruptedException e) {
                        return;
                    }

                    synchronized (queueManager.cancelLock) {
                        if (queueManager.cancelFirstItem) {
                            queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(
                                    source,
                                    SpeakableEvent.SPEAKABLE_CANCELLED, id),
                                    listener);
                            break;
                        }
                    }

                    while (wordIndex < item.getWords().length
                            && (item.getWordsStartTime()[wordIndex] * sampleRate
                                <= playIndex * bytesRead)) {
                        queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(
                                source, SpeakableEvent.WORD_STARTED, id,
                                item.getWords()[wordIndex],
                                    wordStart, wordStart
                                    + item.getWords()[wordIndex].length()),
                                listener);
                        wordStart += item.getWords()[wordIndex].length() + 1;
                        wordIndex++;
                    }

                    while (phonemeIndex < item.getPhonesInfo().length
                            && timeNextPhone * sampleRate < playIndex
                                    * bytesRead) {
                        queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(
                                source, SpeakableEvent.PHONEME_STARTED,
                                id, item.getWords()[wordIndex - 1],
                                item.getPhonesInfo(), phonemeIndex),
                                listener);
                        timeNextPhone +=
                            (double) item.getPhonesInfo()[phonemeIndex]
                                .getDuration() / (double) 1000;
                        phonemeIndex++;
                    }

                    playIndex++;

                    final OutputStream out = manager.getOutputStream();
                    out.write(buffer, 0, bytesRead);
                    // update next timestamp
                    long dataTime = (long) (1000 * bytesRead / bps);
                    final long system = System.currentTimeMillis();
                    if (nextTimeStamp - system < -dataTime) {
                        nextTimeStamp = system + dataTime;
                    } else {
                        nextTimeStamp += dataTime;
                    }
                }

                // Flush audio in the stream
                final OutputStream out = manager.getOutputStream();
                if (out != null) {
                    out.flush();
                }
            } catch (IOException ex) {
                return;
            }

            if (!queueManager.cancelFirstItem) {
                queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(
                        source, SpeakableEvent.SPEAKABLE_ENDED, id),
                        listener);

                synchronized (playQueue) {
                    playQueue.removeElement(item);
                }
            }

            synchronized(this.queueManager.cancelLock) {
                queueManager.cancelFirstItem = false;
            }
            postEventsAfterPlay();
        }
    }

    /**
     * Posts an event that processing of the queue has started
     * @param item the top most queue item
     */
    private void postTopOfQueue(final QueueItem item) {
        final SpeakableListener listener = item.getListener();
        if (listener == null) {
            return;
        }
        final Object source = item.getSource();
        final int id = item.getId();
        queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, id), listener);
    }

    /**
     * Delays until the synthesizer moved into the
     * {@link Synthesizer.RESUMED} state.
     * <p>
     * If the {@link Synthesizer} is in the {@link Synthesizer.PAUSED}
     * state, first a {@link SpeakableEvent.SPEAKABLE_PAUSED} is issued.
     * Once the {@link Synthesizer} is resumed, a
     * {@link SpeakableEvent.SPEAKABLE_RESUMED} is issued.
     * </p>
     * @param item the next queue item to play
     * @throws InterruptedException
     *         if the waiting was interrupted
     */
    private void delayUntilResumed(final QueueItem item)
            throws InterruptedException {
        while (this.queueManager.synthesizer.testEngineState(Synthesizer.PAUSED)) {
            final SpeakableListener listener = item.getListener();
            final Object source = item.getSource();
            final int id = item.getId();
            final SpeakableEvent pausedEvent = new SpeakableEvent(
                            source, SpeakableEvent.SPEAKABLE_PAUSED,
                            id);
            queueManager.synthesizer.postSpeakableEvent(pausedEvent, listener);

            queueManager.synthesizer.waitEngineState(Engine.RESUMED);
            final SpeakableEvent resumedEvent =
                new SpeakableEvent(source,
                        SpeakableEvent.SPEAKABLE_RESUMED, id);
            queueManager.synthesizer.postSpeakableEvent(resumedEvent, listener);
        }
    }

    /**
     * Posts an update event after a play has been performed. This is
     * either a {@link Synthesizer.QUEUE_EMPTIED} event if the
     * queue is empty or {@link Synthesizer.QUEUE_UPDATED} if there
     * are more speakables to process,
     */
    private void postEventsAfterPlay() {
        if (isQueueEmpty()) {
            long[] states = this.queueManager.synthesizer.setEngineState(
                  Synthesizer.QUEUE_NOT_EMPTY, Synthesizer.QUEUE_EMPTY);
            queueManager.synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_EMPTIED, true);
        } else {
            long[] states = this.queueManager.synthesizer.setEngineState(
                    Synthesizer.QUEUE_NOT_EMPTY,
                    Synthesizer.QUEUE_NOT_EMPTY);
            queueManager.synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_UPDATED, true);
        }
    }

    /**
     * Notifies the play queue that the given item has changed.
     * @param item the item that has changed
     */
    public void itemChanged(final QueueItem item) {
        synchronized (playQueue) {
            playQueue.notifyAll();
        }
    }

    /**
     * Adds the given item to the play queue.
     * @param item the item to add
     */
    public void addQueueItem(final QueueItem item) {
        synchronized (playQueue) {
            playQueue.addElement(item);
            playQueue.notifyAll();
        }
    }

    /**
     * Retrieves the queue item with the given id.
     * @param id the id of the queue item to look for
     * @return found queue item, or <code>null</code> if there is no
     *      queue item with the given id
     */
    public QueueItem getQueueItem(final int id) {
        synchronized (playQueue) {
            for (int i=0; i< playQueue.size(); i++) {
                final QueueItem item = (QueueItem) playQueue.elementAt(i);
                if (item.getId() == id) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Return, but do not remove, the first item on the play queue.
     *
     * @return a queue item to play
     */
    protected QueueItem getNextQueueItem() {
        synchronized (playQueue) {
            while (playQueue.isEmpty()
                    || (((QueueItem) playQueue.elementAt(0)).getAudioSegment())
                        == null && !queueManager.isDone()) {
                try {
                    playQueue.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }
            if (queueManager.isDone()) {
                return null;
            }
            return (QueueItem) playQueue.elementAt(0);
        }
    }


    /**
     * Determines if the input queue is empty.
     *
     * @return <code>true</code> if the queue is empty; otherwise
     * <code>false</code>
     */
    public boolean isQueueEmpty() {
        synchronized (playQueue) {
            return playQueue.size() == 0;
        }
    }

    /**
     * Cancel the item at the top of the queue
     * @return <code>true</code> if an item was canceled
     * @exception EngineStateException
     *            if the engine is in an invalid state
     */
    protected boolean cancelItemAtTopOfQueue() throws EngineStateException {
        synchronized (playQueue) {
            if (playQueue.isEmpty()) {
                return false;
            }
            final QueueItem item = (QueueItem) playQueue.elementAt(0);
            final AudioSegment segment = item.getAudioSegment();
            // Having an audio segment means that the handleSpeak
            // method is already being called
            if (segment == null) {
                queueManager.synthesizer.handleCancel();
                final Object source = item.getSource();
                final int id = item.getId();
                final SpeakableListener listener = item.getListener();
                this.queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(
                        source, SpeakableEvent.SPEAKABLE_CANCELLED, id),
                        listener);
                playQueue.removeElementAt(0);
            } else {
                // The SpeakableEvent.SPEAKABLE_CANCELLED is posted in the
                // play method
                playQueue.removeElementAt(0);
            }
            synchronized (this.queueManager.cancelLock) {
                queueManager.cancelFirstItem = true;
            }
            return true;
        }
    }

    /**
     * Cancels the playback of the speakable with the given id
     * @param id the speakable to cancel
     * @return <code>true</code> if the speakable was canceled
     */
    protected boolean cancelItem(final int id) {
        boolean found = false;

        // search item in playqueue
        synchronized (playQueue) {
            for (int i = 0; i < playQueue.size(); ++i) {
                final QueueItem item = (QueueItem) playQueue.elementAt(i);
                final int currentId = item.getId();
                if (currentId == id) {
                    if (i == 0) {
                        found = cancelItemAtTopOfQueue();
                    } else {
                        if (item.getAudioSegment() == null) {
                            this.queueManager.synthesizer.handleCancel(i);
                        }
                        this.queueManager.synthesizer.postSpeakableEvent(new SpeakableEvent(item
                                .getSource(),
                                SpeakableEvent.SPEAKABLE_CANCELLED, currentId),
                                item.getListener());
                        this.queueManager.synthesizer.postSynthesizerEvent(this.queueManager.synthesizer
                                .getEngineState(),
                                this.queueManager.synthesizer.getEngineState(),
                                SynthesizerEvent.QUEUE_UPDATED, false);
                        playQueue.removeElementAt(i);
                        found = true;
                    }
                }
            }
        }
        return found;
    }

    /**
     * Utility method to associate the given audio segment with the
     * queued item with the given id.
     * @param id id of the queue item
     * @param audioSegment the new audio segment
     */
    public void setAudioSegment(final int id,
            final AudioSegment audioSegment) {
        synchronized (playQueue) {
            final QueueItem item = getQueueItem(id);
            if (item != null) {
                item.setAudioSegment(audioSegment);
            }
            playQueue.notifyAll();
        }
    }

    public void setWords(final int id, final String[] words) {
        synchronized (playQueue) {
            final QueueItem item = getQueueItem(id);
            if (item != null) {
                item.setWords(words);
            }
            playQueue.notifyAll();
        }
    }

    public void setWordsStartTimes(final int itemId,
            final float[] starttimes) {
        synchronized (playQueue) {
            final QueueItem item = getQueueItem(itemId);
            if (item != null) {
                item.setWordsStartTimes(starttimes);
            }
            playQueue.notifyAll();
        }
    }

    public void setPhonesInfo(final int itemId,
            final PhoneInfo[] phonesinfo) {
        synchronized (playQueue) {
            final QueueItem item = getQueueItem(itemId);
            if (item != null) {
                item.setPhonesInfo(phonesinfo);
            }
            playQueue.notifyAll();
        }
    }
}