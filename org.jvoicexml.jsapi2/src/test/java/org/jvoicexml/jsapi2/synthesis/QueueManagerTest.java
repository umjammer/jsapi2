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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.speech.AudioSegment;
import javax.speech.synthesis.SpeakableEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.jvoicexml.jsapi2.mock.synthesis.MockSpeakableListener;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link QueueManager}.
 * <p>
 * Every wait in here is bounded: a broken hand-off between the queue threads
 * must show up as a failed test with a stack trace, never as a hanging build.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
@Timeout(value = 20, unit = TimeUnit.SECONDS)
public final class QueueManagerTest {

    /** Upper bound for a single wait, in seconds. */
    private static final long WAIT = 10;

    /** Synthesizer. */
    private MockSynthesizer synthesizer;

    /**
     * Set up the test environment.
     */
    @BeforeEach
    public void setUp() {
        synthesizer = new MockSynthesizer(true);
    }

    /**
     * Stops the queue threads so that they can not interfere with the next test.
     */
    @AfterEach
    public void tearDown() {
        synthesizer.shutdown();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.QueueManager#appendItem(javax.speech.synthesis.Speakable, javax.speech.synthesis.SpeakableListener)}.
     *
     * @throws Exception test failed.
     */
    @Test
    void testAppendItemSpeakableSpeakableListener() throws Exception {
        QueueManager manager = synthesizer.getQueueManager();
        AudioSegment segment = new AudioSegment("http://nowhere", "test");
        MockSpeakableListener listener = new MockSpeakableListener();
        CountDownLatch release = new CountDownLatch(1);
        // hack, stopping at 1st synthesis
        synthesizer.setSpeakHandler(id -> {
Debug.println("pretend item taking long time...");
            try { release.await(); } catch (InterruptedException ignore) {}
Debug.println("item done");
        });
        manager.appendItem(segment, listener);
        QueueItem item;
        while ((item = manager.getQueueItem()) == null) Thread.yield();
        assertNotNull(item);
        assertEquals(segment.getMarkupText(), item.getAudioSegment().getMarkupText());
        assertEquals(listener, item.getListener());
        release.countDown();
        assertTrue(listener.waitForSize(2, WAIT, TimeUnit.SECONDS), "timed out waiting for 2 speakable events");
        SpeakableEvent started = listener.getEvent(0);
        assertEquals(SpeakableEvent.SPEAKABLE_STARTED, started.getId());
        assertEquals(segment.getMarkupText(), started.getSource());
        SpeakableEvent ended = listener.getEvent(1);
        assertEquals(SpeakableEvent.SPEAKABLE_FAILED, ended.getId());
        assertEquals(segment.getMarkupText(), ended.getSource());
    }
}
