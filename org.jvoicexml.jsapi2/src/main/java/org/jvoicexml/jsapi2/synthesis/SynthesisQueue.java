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

import java.util.List;
import javax.speech.AudioSegment;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerEvent;


/**
 * Synthesis thread. Queues all speakable and calls the synthesizer to
 * synthesize them without actually playing back the audio.
 *
 * @author Dirk Schnelle-Walka
 */
final class SynthesisQueue implements Runnable {

    /** Reference to the queue manager. */
    private final QueueManager queueManager;

    /** Reference to the play queue. */
    private final PlayQueue playQueue;

    /** Queued speakables. */
    private final List<QueueItem> queue;

    /** Id of the last queued item. */
    private int queueId;

    /**
     * Constructs a new object.
     *
     * @param manager reference to the queue manager
     * @param pqueue  reference to the play queue
     */
    public SynthesisQueue(QueueManager manager, PlayQueue pqueue) {
        queueManager = manager;
        playQueue = pqueue;
        queue = new java.util.ArrayList<>();
        queueId = 0;
    }

    /**
     * Terminates the synthesis queue and clears all pending speak requests.
     */
    public void terminate() {
        synchronized (queue) {
            queue.clear();
            queueManager.done();
            queue.notifyAll();
        }
    }

    /**
     * Add a speakable item to be spoken to the output queue. Fires the
     * appropriate queue events.
     *
     * @param speakable the speakable item to add
     * @param listener  a listener to notify about events of this item
     * @param text      the text to be spoken, maybe <code>null</code> if the
     *                  speakable contains markup text
     * @return queue id.
     */
    public int appendItem(Speakable speakable, SpeakableListener listener, String text) {
        boolean topOfQueueChanged;
        int addedId;
        synchronized (queue) {
            addedId = ++queueId;
            QueueItem item;
            if (text == null) {
                item = new QueueItem(addedId, speakable, listener);
            } else {
                item = new QueueItem(addedId, speakable, listener, text);
            }
            topOfQueueChanged = append(item);
        }
        adaptSynthesizerState(topOfQueueChanged);
        return addedId;
    }

    /**
     * Add an audio segment to be spoken to the output queue.
     * Fires the appropriate queue events.
     *
     * @param audioSegment the audio segment to add
     * @param listener     listeners of this audio segment
     * @return queue id.
     */
    public int appendItem(AudioSegment audioSegment, SpeakableListener listener) {
        boolean topOfQueueChanged;
        synchronized (queue) {
            ++queueId;
            QueueItem item = new QueueItem(queueId, audioSegment, listener);
            topOfQueueChanged = append(item);
        }
        adaptSynthesizerState(topOfQueueChanged);
        return queueId;
    }

    /**
     * Appends the given queue item to the end of the list.
     *
     * @param item the item to append
     * @return <code>true</code> if the appended item is the only one
     * in the queue
     */
    private boolean append(QueueItem item) {
        boolean topOfQueueChanged = isQueueEmpty();
        queue.add(item);
        queue.notifyAll();
        return topOfQueueChanged;
    }

    /**
     * Adapts the synthesizer state after a queue item has been added.
     *
     * @param topOfQueueChanged <code>true</code> if the top of the
     *                          queue changed after the item has been appended
     */
    private void adaptSynthesizerState(boolean topOfQueueChanged) {
        long[] states;
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        if (topOfQueueChanged) {
            states = synthesizer.setEngineState(Synthesizer.QUEUE_EMPTY, Synthesizer.QUEUE_NOT_EMPTY);
        } else {
            states = synthesizer.setEngineState(Synthesizer.QUEUE_NOT_EMPTY, Synthesizer.QUEUE_NOT_EMPTY);
        }
        synthesizer.postSynthesizerEvent(states[0], states[1],
                SynthesizerEvent.QUEUE_UPDATED, topOfQueueChanged);
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
     * Cancels the first item in the queue.
     *
     * @return <code>true</code> if an item was removed from the queue
     */
    boolean cancelFirstItem() {
        synchronized (queue) {
            if (queue.size() == 0) {
                return false;
            }
            // Get the data of the first item for the notification
            QueueItem item = queue.get(0);
            cancelItem(item);
            return true;
        }
    }

    /**
     * Cancels the item with the given id.
     *
     * @param id the id of the item to cancel
     * @return <code>true</code> if the item was cancelled
     */
    boolean cancelItem(int id) {
        // search item in queue
        synchronized (queue) {
            QueueItem item = getQueueItem(id);
            if (item == null) {
                return false;
            }
            cancelItem(item);
            return true;
        }
    }

    /**
     * Removes the given item from the queue and sends the corresponding event.
     *
     * @param item the item to remove.
     */
    private void cancelItem(QueueItem item) {
        int id = item.getId();
        Object source = item.getSource();
        SpeakableListener listener = item.getListener();
        SpeakableEvent event = new SpeakableEvent(source, SpeakableEvent.SPEAKABLE_CANCELLED, id);
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        synthesizer.postSpeakableEvent(event, listener);
        queue.remove(item);
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
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Returns, but does not remove, the first item on the queue. Waits
     * until a queue item has been added if the queue is empty.
     *
     * @return the first queue item
     */
    QueueItem getNextQueueItem() {
        synchronized (queue) {
//Debug.println("queue.size(): " + queue.size() + ", queueManager.isDone(): " + queueManager.isDone());
            while (queue.size() == 0 && !queueManager.isDone()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    return null;
                }
            }

            if (queueManager.isDone()) {
                return null;
            }
            return queue.get(0);
        }
    }

    /**
     * Removes the given item, posting the appropriate events.
     * The item may have already been removed (due to a cancel).
     *
     * @param item the item to remove
     */
    void removeQueueItem(QueueItem item) {
        synchronized (queue) {
            boolean found = queue.remove(item);
            if (found) {
                queueManager.queueDrained();
            }
        }
    }

    /**
     * Gets the next item from the queue and outputs it.
     */
    public void run() {
        long lastFocusEvent = Synthesizer.DEFOCUSED;

        while (!queueManager.isDone()) {
            QueueItem item = getNextQueueItem();
            if (item != null) {
                BaseSynthesizer synthesizer = queueManager.getSynthesizer();
                if (lastFocusEvent == Synthesizer.DEFOCUSED) {
                    long[] states = synthesizer.setEngineState(Synthesizer.DEFOCUSED, Synthesizer.FOCUSED);
                    synthesizer.postSynthesizerEvent(states[0], states[1], SynthesizerEvent.ENGINE_FOCUSED, true);
                    lastFocusEvent = Synthesizer.FOCUSED;
                }

                // transfer item from the queue to the play queue
                removeQueueItem(item);
                playQueue.addQueueItem(item);
                // Synthesize it
                try {
                    synthesize(item);
                } catch (SpeakableException e) {
                    e.printStackTrace();
                    int id = item.getId();
                    Speakable speakable = item.getSpeakable();
                    String textInfo = speakable.getMarkupText();
                    SpeakableEvent event = new SpeakableEvent(this,
                            SpeakableEvent.SPEAKABLE_FAILED, id, textInfo,
                            SpeakableEvent.SPEAKABLE_FAILURE_UNRECOVERABLE, e);
                    synthesizer.postSpeakableEvent(event, null);
                }
                // Notify the observers that something changed
                playQueue.itemChanged(item);
            }
        }
    }

    /**
     * Synthesizes the given queue item.
     *
     * @param item the queue item to synthesize
     * @throws SpeakableException error processing the item
     */
    private void synthesize(QueueItem item) throws SpeakableException {
        Object itemSource = item.getSource();
        int id = item.getId();
        AudioSegment segment;
        // TODO this won't work for queued audio segments
        BaseSynthesizer synthesizer = queueManager.getSynthesizer();
        if (itemSource instanceof String) {
            String text = (String) itemSource;
            segment = synthesizer.handleSpeak(id, text);
        } else if (itemSource instanceof Speakable) {
            Speakable speakable = (Speakable) itemSource;
            segment = synthesizer.handleSpeak(id, speakable);
        } else {
            throw new RuntimeException("WTF! It could only be text or speakable but was "
                            + (itemSource == null ? "null" : item.getClass().getName()));
        }

        item.setAudioSegment(segment);
        item.setSynthesized(true);
    }
}
