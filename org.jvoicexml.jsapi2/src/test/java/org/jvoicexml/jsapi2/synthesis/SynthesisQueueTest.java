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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javax.speech.AudioSegment;
import javax.speech.synthesis.SpeakableEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link SynthesisQueue}.
 *
 * @author Dirk Schnelle-Walka
 */
class SynthesisQueueTest {

    /** The test object. */
    private SynthesisQueue queue;

    private MockSynthesizer synthesizer;

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
        synthesizer.setEngineMask(0);
        QueueManager manager = new QueueManager(synthesizer);
        queue = manager.getSynthesisQueue();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.SynthesisQueue#getNextQueueItem()}.
     */
    @Test
    void testGetNextQueueItem() throws Exception {
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        CountDownLatch cdl = new CountDownLatch(2);
        AtomicInteger firstId = new AtomicInteger();
        AtomicInteger secondId = new AtomicInteger();
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == firstId.get()) {
                assertEquals(segment1.getMarkupText(), e.getSource().toString());
Debug.println("1st in playing queue...");
                cdl.countDown();
            } else if (e.getRequestId() == secondId.get()) {
                assertEquals(segment2.getMarkupText(), e.getSource().toString());
Debug.println("2nd in playing queue...");
                cdl.countDown();
            } else {
                assert false;
            }
        });
        firstId.set(queue.appendItem(segment1, null));
        secondId.set(queue.appendItem(segment2, null));
        cdl.await();
Debug.println("done");
    }

    /**
     * Test method for {@link SynthesisQueue#getQueueItem(int)}.
     */
    @Test
    void testGetQueueItem() {
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        AtomicInteger firstId = new AtomicInteger();
        CountDownLatch cdl = new CountDownLatch(1);
        // hack, stopping at 1st synthesis
        synthesizer.setSpeakHandler(id -> {
            if (id == firstId.get()) {
Debug.println("pretend 1st taking long time...");
                try { cdl.await(); } catch (InterruptedException ignore) {}
Debug.println("1st done");
            }
        });
        firstId.set(queue.appendItem(segment1, null));
        int secondId = queue.appendItem(segment2, null);
        QueueItem item1 = queue.getQueueItem(firstId.get());
        assertNull(item1, "already consumed");
        QueueItem item2 = queue.getQueueItem(secondId);
        assertNotNull(item2, "still in queue");
        assertEquals(segment2.getMarkupText(), item2.getAudioSegment().getMarkupText(), "can retrieve because 1st synthesis is still working");
        QueueItem item3 = queue.getQueueItem(-1);
        assertNull(item3, "no such id");
        cdl.countDown();
    }

    /**
     * Test method for {@link SynthesisQueue#isQueueEmpty()}.
     */
    @Test
    void testIsQueueEmpty() throws InterruptedException {
        assertTrue(queue.isQueueEmpty());
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        CountDownLatch cdl = new CountDownLatch(1);
        AtomicInteger id = new AtomicInteger();
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == id.get()) {
Debug.println("1st in playing queue...");
                cdl.countDown();
            }
        });
        id.set(queue.appendItem(segment1, null));
        cdl.await();
        assertTrue(queue.isQueueEmpty(), "1st is in queue, so empty");
    }

    /**
     * Test method for {@link SynthesisQueue#cancelFirstItem()},
     */
    @Test
    void testCancelFirstItem() throws Exception {
        assertTrue(queue.isQueueEmpty());
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        CountDownLatch cdl1 = new CountDownLatch(1);
        CountDownLatch cdl2 = new CountDownLatch(1);
        CountDownLatch cdl3 = new CountDownLatch(1);
        AtomicInteger firstId = new AtomicInteger();
        AtomicInteger secondId = new AtomicInteger();
        // hack, stopping at 1st synthesis
        synthesizer.setSpeakHandler(id -> {
            if (id == firstId.get()) {
Debug.println("pretend 1st taking long time...");
                cdl1.countDown();
                try { cdl2.await(); } catch (InterruptedException ignore) {}
Debug.println("1st done");
            } else {
Debug.println("speak: " + id);
            }
        });
        synthesizer.addSpeakableListener(e -> {
            if (e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                assertEquals(firstId.get(), e.getRequestId());
Debug.println("1st canceled");
                cdl2.countDown();
            } else if (e.getRequestId() == secondId.get()) {
Debug.println("2nd in playing queue, means processing done");
                cdl3.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        firstId.set(queue.appendItem(segment1, null)); // takes long time
        secondId.set(queue.appendItem(segment2, null));
        cdl1.await();
        assertTrue(queue.cancelFirstItem(), "1st is processing and not in queue, so 1st is cancelable as 1st");
        cdl3.await();
        assertFalse(queue.cancelFirstItem(), "no processing item");
        assertTrue(queue.isQueueEmpty(), "queue is empty");
Debug.println("done");
    }

    /**
     * Test method for {@link SynthesisQueue#cancelItem(int)},
     */
    @Test
    void testCancelItem() throws Exception {
        assertTrue(queue.isQueueEmpty());
        AudioSegment segment0 = new AudioSegment("http://localhost", "test0");
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        AtomicInteger zerothId = new AtomicInteger();
        AtomicInteger firstId = new AtomicInteger();
        AtomicInteger secondId = new AtomicInteger();
        CountDownLatch cdl0 = new CountDownLatch(1);
        CountDownLatch cdl1 = new CountDownLatch(1);
        CountDownLatch cdl2 = new CountDownLatch(2);
        // hack, stopping at 1st synthesis
        synthesizer.setSpeakHandler(id -> {
            if (id == zerothId.get()) {
Debug.println("pretend 0th taking long time...");
                cdl1.countDown();
                try { cdl0.await(); } catch (InterruptedException ignore) {}
Debug.println("0th done");
            }
        });
        synthesizer.addSpeakableListener(e -> {
            if (e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                assertTrue(List.of(firstId.get(), secondId.get()).contains(e.getRequestId()));
Debug.println("id " + e.getRequestId() + " is canceled");
                cdl2.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        zerothId.set(queue.appendItem(segment0, null)); // takes long time
        cdl1.await();
        firstId.set(queue.appendItem(segment1, null));
        secondId.set(queue.appendItem(segment2, null));
        assertFalse(queue.isQueueEmpty(), "because of 0th takes long time"); // TODO ci error
        assertTrue(queue.cancelItem(firstId.get()), "1st is in queue because 0th takes long time");
        assertTrue(queue.cancelItem(secondId.get()), "2nd is in queue because 0th takes long time");
        cdl2.await();
        cdl0.countDown();
        assertTrue(queue.isQueueEmpty(), "queue is empty because all are canceled");
        assertFalse(queue.cancelItem(-1), "no such id");
    }
}
