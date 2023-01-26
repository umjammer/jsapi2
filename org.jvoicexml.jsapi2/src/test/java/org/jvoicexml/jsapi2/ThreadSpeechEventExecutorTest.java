/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test cases for {@link ThreadSpeechEventExecutor}.
 *
 * @author Dirk Schnelle-Walka
 */
class ThreadSpeechEventExecutorTest {

    /** The test object. */
    private ThreadSpeechEventExecutor executor;

    /**
     * Setup the test environment.
     *
     * @throws java.lang.Exception setup failed
     */
    @BeforeEach
    public void setUp() throws Exception {
        executor = new ThreadSpeechEventExecutor();
    }

    /**
     * Cleanup of the test environment.
     *
     * @throws java.lang.Exception cleanup failed
     */
    public void tearDown() throws Exception {
        executor.terminate();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.ThreadSpeechEventExecutor#execute(java.lang.Runnable)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testExecute() throws Exception {
        List<Integer> list = new java.util.ArrayList<>();
        Runnable runnable1 = () -> list.add(1);
        Runnable runnable2 = () -> {
            list.add(2);
            synchronized (list) {
                list.notifyAll();
            }
        };
        executor.execute(runnable1);
        executor.execute(runnable2);
        synchronized (list) {
            list.wait();
        }
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }
}
