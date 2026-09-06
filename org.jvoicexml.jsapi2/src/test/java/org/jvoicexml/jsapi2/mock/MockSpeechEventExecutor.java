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

package org.jvoicexml.jsapi2.mock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.speech.SpeechEventExecutor;

import vavi.util.Debug;


/**
 * A {@link SpeechEventExecutor} for tests.
 * <p>
 * Events are delivered asynchronously but strictly in the order they were
 * posted, on a single daemon thread. A listener that throws does not stop
 * the delivery of later events. This mirrors the ordering guarantee of the
 * real {@link org.jvoicexml.jsapi2.ThreadSpeechEventExecutor} and keeps
 * tests deterministic; a thread per event would deliver events in random
 * order under load.
 * </p>
 * <p>
 * Deliberately not a {@code TerminatableSpeechEventExecutor}: the engine
 * terminates those on deallocation, and state transition tests re-allocate
 * the same engine afterwards. Tests that want the thread gone call
 * {@link #shutdown()} in their tear down.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public class MockSpeechEventExecutor implements SpeechEventExecutor {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "Mock Speech Event " + COUNTER.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void execute(Runnable command) throws IllegalStateException, NullPointerException {
        if (command == null) {
            throw new NullPointerException("command must not be null!");
        }
if (Debug.isLoggable(Level.FINEST)) {
 new Exception("***DUMMY***").printStackTrace(System.err);
}
        executor.execute(() -> {
            try {
                command.run();
            } catch (Throwable t) {
Debug.println(Level.WARNING, "listener threw: " + t);
                t.printStackTrace(System.err);
            }
        });
    }

    /** Stops the delivery thread. */
    public void shutdown() {
        executor.shutdownNow();
    }
}
