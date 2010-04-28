/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 296 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Enumeration;
import java.util.Vector;

import javax.speech.AudioException;
import javax.speech.AudioSegment;
import javax.speech.Engine;
import javax.speech.synthesis.PhoneInfo;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;

import org.jvoicexml.jsapi2.AudioFormat;
import org.jvoicexml.jsapi2.BaseAudioManager;

/**
 * The {@link QueueManager} basically accepts the speech segments to
 * synthesized, appends them to a corresponding queue and hands them to the
 * synthesizer to convert those pieces into audio chunks. These chunks are added
 * to the play queue to be delivered via the configured media locator.
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision: 296 $
 */
public class QueueManager {
    /** Reference to the synthesizer. */
    private BaseSynthesizer synthesizer;
    /** Queued play items. */
    private SynthesisQueue synthThread;
    /** Synthesized play items. */
    private PlayQueue playThread;
    private boolean done;
    private int queueId;
    private boolean cancelFirstItem;
    private final Object cancelLock;

    /**
     * Constructs a new object.
     * @param synth the synthesizer whose queue is managed here.
     */
    public QueueManager(final BaseSynthesizer synth) {
        synthesizer = synth;
        queueId = 0;
        cancelFirstItem = false;
        cancelLock = new Object();

        synthThread = new SynthesisQueue();
        playThread = new PlayQueue();

        synthThread.start();
        playThread.start();
    }

    /**
     * Terminates the queue mananger.
     */
    public void terminate() {
        synthThread.terminate();
    }

    /**
     * Add an item to be spoken to the output queue. Fires the appropriate queue
     * events
     *
     * @param item
     *                the item to add to the queue
     */
    public int appendItem(final Speakable speakable,
            final SpeakableListener listener) {
        return synthThread.appendItem(speakable, listener);
    }

    /**
     * Add an item to be spoken to the output queue. Fires the appropriate queue
     * events
     *
     */
    public int appendItem(Speakable speakable, SpeakableListener listener,
            String text) {
        return synthThread.appendItem(speakable, listener, text);
    }

    /**
     * Add an item to be spoken to the output queue. Fires the appropriate queue
     * events
     *
     * @param item
     *                the item to add to the queue
     */
    public int appendItem(AudioSegment audioSegment, SpeakableListener listener) {
        return synthThread.appendItem(audioSegment, listener);
    }

    /**
     * Utility method to associate the given audio segment with the
     * queued item with the given id.
     * @param id id of the queue item
     * @param audioSegment the new audio segment
     */
    public void setAudioSegment(final int id, final AudioSegment audioSegment) {
        playThread.setAudioSegment(id, audioSegment);
    }

    public void setWords(final int itemId, final String[] words) {
        playThread.setWords(itemId, words);
    }

    public void setWordsStartTimes(final int itemId, final float[] starttimes) {
        playThread.setWordsStartTimes(itemId, starttimes);
    }

    public void setPhonesInfo(final int itemId, final PhoneInfo[] phonesinfo) {
        playThread.setPhonesInfo(itemId, phonesinfo);
    }

    /**
     * Determines if the input queue is empty.
     *
     * @return true if the queue is empty; otherwise false
     */
    public boolean isQueueEmpty() {
        return synthThread.isQueueEmpty() && playThread.isQueueEmpty();
    }

    /**
     * Cancel the current item.
     */
    protected boolean cancelItem() {
        if (playThread.isQueueEmpty()) {
            return synthThread.cancelItem();
        } else {
            return playThread.cancelItem();
        }
    }

    /**
     * Cancel all items in the queue.
     */
    public boolean cancelAllItems() {
        synthesizer.handleCancelAll();
        boolean found = false;

        if (!playThread.isQueueEmpty()) {
            cancelItem(); // cancel and remove first item
            while (!playThread.isQueueEmpty()) {
                playThread.cancelItem();
                found = true;
            }
        }

        while (!synthThread.isQueueEmpty()) {
            synthThread.cancelItem();
            found = true;
        }

        return found;
    }

    /**
     * Cancel the given item.
     *
     * @param source
     *                the item to cancel.
     */
    protected void cancelItem(final Object source) {
        /*
         * Speakable item = null; synchronized (queue) { int index =
         * queue.indexOf(source); if (index == 0) { cancelItem(); } else { item =
         * (Speakable) queue.remove(index); if (item != null) { //
         * item.postSpeakableCancelled(); item.cancelled(); queueDrained(); } } }
         */
    }

    protected boolean cancelItem(final int id) {
        boolean found = false;

        found = playThread.cancelItem(id);
        found = found || synthThread.cancelItem(id);

        return found;
    }

    /**
     * Returns, but does not remove, the first item on the queue.
     *
     * @return the first queue item
     */
    protected QueueItem getQueueItem() {
        return synthThread.getQueueItem();
    }

    /**
     * Removes the given item, posting the appropriate events. The item may have
     * already been removed (due to a cancel).
     *
     * @param item
     *                the item to remove
     */
    protected void removeQueueItem(final QueueItem item) {
        synthThread.removeQueueItem(item);
    }

    /**
     * Should be called if one or more items have been removed from the queue.
     * Generates the appropriate state changes and events.
     */
    private void queueDrained() {
        /*
         * if (queue.size() == 0) { long[] states =
         * setEngineState(synthesizer.QUEUE_NOT_EMPTY, synthesizer.QUEUE_EMPTY);
         * postQueueEmptied(states[0], states[1]); } else { long[] states =
         * setEngineState(synthesizer.QUEUE_NOT_EMPTY,
         * synthesizer.QUEUE_NOT_EMPTY); postQueueUpdated(true, states[0],
         * states[1]); }
         */
    }

    /**
     * Synthesis thread.
     * @author Dirk Schnelle-Walka
     *
     */
    class SynthesisQueue extends Thread {
        /** Queued speakables. */
        private Vector queue;

        /**
         * Constructs a new object.
         */
        public SynthesisQueue() {
            queue = new Vector();
        }

        /**
         * Terminates the queue mananger.
         */
        public void terminate() {
            synchronized (queue) {
                done = true;
                queue.notifyAll();
            }
        }

        /**
         * Add an item to be spoken to the output queue. Fires the appropriate queue
         * events
         *
         * @param item
         *                the item to add to the queue
         */
        public int appendItem(Speakable speakable, SpeakableListener listener) {
            boolean topOfQueueChanged;
            synchronized (queue) {
                queueId += 1;
                QueueItem item = new QueueItem(queueId, speakable, listener);

                topOfQueueChanged = isQueueEmpty();
                queue.addElement(item);
                queue.notifyAll();
            }

            final long[] states;
            if (topOfQueueChanged) {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            } else {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            }
            synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_UPDATED, topOfQueueChanged);

            return queueId;
        }

        /**
         * Add an item to be spoken to the output queue. Fires the appropriate queue
         * events
         *
         */
        public int appendItem(Speakable speakable, SpeakableListener listener,
                String text) {
            boolean topOfQueueChanged;
            synchronized (queue) {
                queueId += 1;
                final QueueItem item = new QueueItem(queueId, speakable,
                        listener, text);

                topOfQueueChanged = isQueueEmpty();
                queue.addElement(item);
                queue.notifyAll();
            }

            final long[] states;
            if (topOfQueueChanged) {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            } else {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            }
            synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_UPDATED, topOfQueueChanged);

            return queueId;
        }

        /**
         * Add an item to be spoken to the output queue. Fires the appropriate
         * queue events.
         *
         * @param item
         *                the item to add to the queue
         */
        public int appendItem(final AudioSegment audioSegment,
                final SpeakableListener listener) {
            final boolean topOfQueueChanged;
            synchronized (queue) {
                ++queueId;
                final QueueItem item =
                    new QueueItem(queueId, audioSegment, listener);

                topOfQueueChanged = isQueueEmpty();
                queue.addElement(item);
                queue.notifyAll();
            }

            final long[] states;
            if (topOfQueueChanged) {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            } else {
                states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY,
                        Synthesizer.QUEUE_NOT_EMPTY);
            }
            synthesizer.postSynthesizerEvent(states[0], states[1],
                    SynthesizerEvent.QUEUE_UPDATED, topOfQueueChanged);

            return queueId;
        }

        /**
         * Determines if the input queue is empty.
         *
         * @return true if the queue is empty; otherwise false
         */
        public boolean isQueueEmpty() {
            synchronized (queue) {
                return queue.size() == 0;
            }

        }

        /**
         * Cancel the current item.
         */
        protected boolean cancelItem() {
            if (queue.size() != 0) {
                QueueItem item = (QueueItem) queue.elementAt(0);
                synthesizer.postSpeakableEvent(new SpeakableEvent(item
                        .getSource(), SpeakableEvent.SPEAKABLE_CANCELLED, item
                        .getId()), item.getListener());
                queue.removeElementAt(0);
                return true;
            }
            return false;
        }

        /**
         * Cancels the itme with the given id.
         * @param id the id of the item to cancel
         * @return <code>true</code> if the item was cancelled
         */
        protected boolean cancelItem(final int id) {
            // search item in queue
            synchronized (queue) {
                for (int i = 0; i < queue.size(); ++i) {
                    final QueueItem item = (QueueItem) queue.elementAt(i);
                    if (item.getId() == id) {
                        final Object source = item.getSource();
                        final SpeakableListener listener = item.getListener();
                        final SpeakableEvent event = new SpeakableEvent(
                                source, SpeakableEvent.SPEAKABLE_CANCELLED,
                                id);
                        synthesizer.postSpeakableEvent(event, listener);
                        queue.removeElementAt(i);
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * Returns, but does not remove, the first item on the queue.
         *
         * @return the first queue item
         */
        protected QueueItem getQueueItem() {
            QueueItem item = null;
            synchronized (queue) {
                while (queue.size() == 0 && !done) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        return null;
                    }
                }

                if (done) {
                    return null;
                }
                item = (QueueItem) queue.elementAt(0);

            }
            return item;
        }

        /**
         * Removes the given item, posting the appropriate events. The item may have
         * already been removed (due to a cancel).
         *
         * @param item
         *                the item to remove
         */
        protected void removeQueueItem(final QueueItem item) {
            synchronized (queue) {
                boolean found = queue.removeElement(item);
                if (found) {
                    queueDrained();
                }
            }
        }

        /**
         * Gets the next item from the queue and outputs it.
         */
        public void run() {
            long lastFocusEvent = Synthesizer.DEFOCUSED;

            while (!done) {
                final QueueItem item = getQueueItem();
                if (item != null) {
                    if (lastFocusEvent == Synthesizer.DEFOCUSED) {
                        long[] states = synthesizer.setEngineState(
                                Synthesizer.DEFOCUSED, Synthesizer.FOCUSED);
                        synthesizer.postSynthesizerEvent(states[0], states[1],
                                SynthesizerEvent.ENGINE_FOCUSED, true);
                        lastFocusEvent = Synthesizer.FOCUSED;
                    }

                    // transfer item from the queue to the play queue
                    removeQueueItem(item);

                    playThread.addQueueItem(item);

                    // Synthesize item
                    final Object itemSource = item.getSource();
                    if (itemSource instanceof String) {
                        synthesizer.handleSpeak(item.getId(),
                                (String) itemSource);
                    } else if (itemSource instanceof Speakable) {
                        synthesizer.handleSpeak(item.getId(),
                                (Speakable) itemSource);
                    } else {
                        throw new RuntimeException(
                                "WTF! It could only be text or speakable....");
                    }
                }
            }
        }
    }

    /**
     * Play back the audio coming from the synthesizer.
     * @author Dirk Schnelle-Walka
     */
    class PlayQueue extends Thread {
        /** Buffer size when reading from the audio segment input stream. */
        final static int BUFFER_LENGTH = 1024;

        private Vector playQueue;

        public PlayQueue() {
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

            while (!done) {
                final QueueItem item = getQueueItemToPlay();

                final Object source = item.getSource();
                final int id = item.getId();
                final SpeakableListener listener = item.getListener();
                synthesizer.postSpeakableEvent(new SpeakableEvent(source,
                        SpeakableEvent.TOP_OF_QUEUE, id), listener);

                while (synthesizer.testEngineState(Synthesizer.PAUSED)) {
                    final SpeakableEvent pausedEvent = new SpeakableEvent(
                                    source, SpeakableEvent.SPEAKABLE_PAUSED,
                                    id);
                    synthesizer.postSpeakableEvent(pausedEvent, listener);

                    try {
                        synthesizer.waitEngineState(Engine.RESUMED);
                        final SpeakableEvent resumedEvent =
                            new SpeakableEvent(source,
                                    SpeakableEvent.SPEAKABLE_RESUMED, id);
                        synthesizer.postSpeakableEvent(resumedEvent, listener);
                    } catch (InterruptedException ex1) {
                        return;
                    }
                }

                long totalBytesRead = 0;
                final SpeakableEvent startedEvent = new SpeakableEvent(source,
                        SpeakableEvent.SPEAKABLE_STARTED, id);
                synthesizer.postSpeakableEvent(startedEvent, listener);

                playIndex = 0;
                wordIndex = 0;
                wordStart = 0;
                phonemeIndex = 0;
                timeNextPhone = 0;
                int bytesRead = 0;
                final BaseAudioManager manager =
                    (BaseAudioManager) synthesizer.getAudioManager();
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
                        bytesRead = inputStream.read(buffer);
                        if (bytesRead < 0)  {
                            break;
                        }

                        totalBytesRead += bytesRead;
                        synchronized (cancelLock) {
                            if (cancelFirstItem) {
                                final SpeakableEvent cancelledEvent =
                                    new SpeakableEvent(source,
                                        SpeakableEvent.SPEAKABLE_CANCELLED, id);
                                synthesizer.postSpeakableEvent(cancelledEvent,
                                        listener);
                                break;
                            }
                        }

                        while (synthesizer.testEngineState(Synthesizer.PAUSED)) {
                            synthesizer.postSpeakableEvent(new SpeakableEvent(
                                    source, SpeakableEvent.SPEAKABLE_PAUSED,
                                    id), listener);
                            try {
                                synthesizer.waitEngineState(Engine.RESUMED);
                                synthesizer.postSpeakableEvent(new SpeakableEvent(
                                        source,
                                        SpeakableEvent.SPEAKABLE_RESUMED, id),
                                        listener);

                            } catch (InterruptedException ex) {
                                return;
                            }
                        }

                        synchronized (cancelLock) {
                            if (cancelFirstItem) {
                                synthesizer.postSpeakableEvent(new SpeakableEvent(
                                        source,
                                        SpeakableEvent.SPEAKABLE_CANCELLED, id),
                                        listener);
                                break;
                            }
                        }

                        while (wordIndex < item.getWords().length
                                && (item.getWordsStartTime()[wordIndex] * sampleRate
                                    <= playIndex * bytesRead)) {
                            synthesizer.postSpeakableEvent(new SpeakableEvent(
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
                            synthesizer.postSpeakableEvent(new SpeakableEvent(
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
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                // Flush audio in the stream
                try {
                    final OutputStream out = manager.getOutputStream();
                    if (out != null) {
                        out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                if (!cancelFirstItem) {

                    // Delay the event sending by the remaining time audio length
                    /*
                     * long audioTime = ((long)((totalBytesRead * 1000) /
                     * (audioFormat.getSampleRate() *
                     * audioFormat.getSampleSizeInBits()/8))); long endStreaming =
                     * System.currentTimeMillis(); long procTime = endStreaming -
                     * startStreaming; long sleepTime = audioTime - procTime; if
                     * (sleepTime > 0) { try {
                     * Thread.currentThread().sleep(sleepTime); } catch
                     * (InterruptedException ex2) { } }
                     */
                    synthesizer.postSpeakableEvent(new SpeakableEvent(
                            source, SpeakableEvent.SPEAKABLE_ENDED, id),
                            listener);

                    synchronized (playQueue) {
                        playQueue.removeElement(item);
                    }
                }

                cancelFirstItem = false;

                if (isQueueEmpty()) {
                    long[] states = synthesizer.setEngineState(
                          Synthesizer.QUEUE_NOT_EMPTY, Synthesizer.QUEUE_EMPTY);
                    synthesizer.postSynthesizerEvent(states[0], states[1],
                            SynthesizerEvent.QUEUE_EMPTIED, true);
                } else {
                    long[] states = synthesizer.setEngineState(
                            Synthesizer.QUEUE_NOT_EMPTY,
                            Synthesizer.QUEUE_NOT_EMPTY);
                    synthesizer.postSynthesizerEvent(states[0], states[1],
                            SynthesizerEvent.QUEUE_UPDATED, true);
                }
            }
        }

        /**
         * Adds the given item to the play queue.
         * @param item the item to add
         */
        public void addQueueItem(QueueItem item) {
            synchronized (playQueue) {
                playQueue.addElement(item);
                playQueue.notifyAll();
            }
        }

        /**
         * Return, but do not remove, the first item on the play queue.
         *
         * @return a queue item to play
         */
        protected QueueItem getQueueItemToPlay() {
            QueueItem item = null;
            synchronized (playQueue) {
                while (playQueue.isEmpty()
                        || (((QueueItem) playQueue.elementAt(0)).getAudioSegment())
                            == null && !done) {
                    try {
                        playQueue.wait();
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                if (done) {
                    return null;
                }
                item = (QueueItem) playQueue.elementAt(0);

            }
            return item;
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
         * Cancel the current item.
         */
        protected boolean cancelItem() {
            synchronized (playQueue) {
                QueueItem item = (QueueItem) playQueue.elementAt(0);
                if (item.getAudioSegment() == null) {
                    synthesizer.handleCancel();
                    final Object source = item.getSource();
                    final int id = item.getId();
                    final SpeakableListener listener = item.getListener();
                    synthesizer.postSpeakableEvent(new SpeakableEvent(
                            source, SpeakableEvent.SPEAKABLE_CANCELLED, id),
                            listener);
                    playQueue.removeElementAt(0);
    
                    return true;
                } else {
                    playQueue.removeElementAt(0);
                    synchronized (cancelLock) {
                        cancelFirstItem = true;
                    }
                    return true;
                }
            }
        }

        protected boolean cancelItem(final int id) {
            boolean found = false;

            // search item in playqueue
            synchronized (playQueue) {
                for (int i = 0; i < playQueue.size(); ++i) {
                    QueueItem item = (QueueItem) playQueue.elementAt(i);
                    if (item.getId() == id) {
                        if (i == 0) {
                            found = cancelItem();
                        } else {
                            if (item.getAudioSegment() == null) {
                                synthesizer.handleCancel(i);
                            }
                            synthesizer.postSpeakableEvent(new SpeakableEvent(item
                                    .getSource(),
                                    SpeakableEvent.SPEAKABLE_CANCELLED, item
                                            .getId()), item.getListener());
                            synthesizer.postSynthesizerEvent(synthesizer
                                    .getEngineState(),
                                    synthesizer.getEngineState(),
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
        public void setAudioSegment(final int id, final AudioSegment audioSegment) {
            synchronized (playQueue) {
                final Enumeration e = playQueue.elements();
                while (e.hasMoreElements()) {
                    final QueueItem item = (QueueItem) e.nextElement();
                    if (item.getId() == id) {
                        item.setAudioSegment(audioSegment);
                        break;
                    }
                }

                playQueue.notifyAll();
            }
        }

        public void setWords(final int itemId, final String[] words) {
            synchronized (playQueue) {
                final Enumeration e = playQueue.elements();
                while (e.hasMoreElements()) {
                    final QueueItem item = (QueueItem) e.nextElement();
                    if (item.getId() == itemId) {
                        item.setWords(words);
                        break;
                    }
                }

                playQueue.notifyAll();
            }
        }

        public void setWordsStartTimes(final int itemId,
                final float[] starttimes) {
            synchronized (playQueue) {
                final Enumeration e = playQueue.elements();
                while (e.hasMoreElements()) {
                    final QueueItem item = (QueueItem) e.nextElement();
                    if (item.getId() == itemId) {
                        item.setWordsStartTimes(starttimes);
                        break;
                    }
                }

                playQueue.notifyAll();
            }
        }

        public void setPhonesInfo(final int itemId,
                final PhoneInfo[] phonesinfo) {
            synchronized (playQueue) {
                final Enumeration e = playQueue.elements();
                while (e.hasMoreElements()) {
                    final QueueItem item = (QueueItem) e.nextElement();
                    if (item.getId() == itemId) {
                        item.setPhonesInfo(phonesinfo);
                        break;
                    }
                }

                playQueue.notifyAll();
            }
        }
    }
}
