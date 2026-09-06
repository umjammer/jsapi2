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

package org.jvoicexml.jsapi2.mock.synthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;

import vavi.util.Debug;


/**
 * A {@link SpeakableListener} that records the received events and lets a
 * test wait until a given number of events has arrived.
 *
 * @author Dirk Schnelle-Walka
 */
public class MockSpeakableListener implements SpeakableListener {

    /** Received speakable events, guarded by {@link #lock}. */
    private final List<SpeakableEvent> events;

    /** Synchronization lock. */
    private final Object lock;

    /**
     * Constructs a new object.
     */
    public MockSpeakableListener() {
        events = new ArrayList<>();
        lock = new Object();
    }

    /**
     * Waits until at least the given number of events has been received.
     * <p>
     * The check and the wait happen under the same lock, so a notification
     * that arrives between them can not be lost.
     * </p>
     *
     * @param size the number of expected events
     * @throws InterruptedException if waiting was interrupted
     */
    public void waitForSize(int size) throws InterruptedException {
        waitForSize(size, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Waits until at least the given number of events has been received
     * or the timeout elapsed.
     *
     * @param size    the number of expected events
     * @param timeout the maximum time to wait, {@code 0} waits forever
     * @param unit    the unit of {@code timeout}
     * @return {@code true} if the events arrived, {@code false} on timeout
     * @throws InterruptedException if waiting was interrupted
     */
    public boolean waitForSize(int size, long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        synchronized (lock) {
            while (events.size() < size) {
Debug.println("events.size(): " + events.size() + " / " + size);
                if (timeout <= 0) {
                    lock.wait();
                } else {
                    long remaining = deadline - System.nanoTime();
                    if (remaining <= 0) {
                        return false;
                    }
                    TimeUnit.NANOSECONDS.timedWait(lock, remaining);
                }
            }
            return true;
        }
    }

    /**
     * Returns the {@link SpeakableEvent} at the given position.
     *
     * @param pos position of the event to return
     * @return the event at the given position
     */
    public SpeakableEvent getEvent(int pos) {
        synchronized (lock) {
            return events.get(pos);
        }
    }

    /**
     * Returns the number of received events.
     *
     * @return number of received events
     */
    public int size() {
        synchronized (lock) {
            return events.size();
        }
    }

    @Override
    public void speakableUpdate(SpeakableEvent e) {
Debug.println("event added: " + e);
        synchronized (lock) {
            events.add(e);
            lock.notifyAll();
        }
    }
}
