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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.speech.AudioSegment;
import javax.speech.synthesis.SpeakableEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link SynthesisQueue}.
 * <p>
 * The synthesis thread starts consuming an item the moment it is appended,
 * possibly before {@code appendItem()} has even returned its id to the test.
 * Therefore nothing in here identifies "the item under test" by an id that is
 * stored after {@code appendItem()}: the speak handler blocks the first
 * synthesis it sees whatever its id, and listeners match events by their
 * source text. Every wait is bounded so that a regression fails instead of
 * hanging the build.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
@Timeout(value = 20, unit = TimeUnit.SECONDS)
class SynthesisQueueTest {

    /** Upper bound for a single wait, in seconds. */
    private static final long WAIT = 10;

    /** The test object. */
    private SynthesisQueue queue;

    /** The queue manager owning {@link #queue}. */
    private QueueManager manager;

    private MockSynthesizer synthesizer;

    /** Failures detected on listener threads, checked at the end of a test. */
    private final List<Throwable> listenerErrors = Collections.synchronizedList(new ArrayList<>());

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
        synthesizer.setEngineMask(0);
        manager = new QueueManager(synthesizer);
        queue = manager.getSynthesisQueue();
    }

    /**
     * Stops the queue threads so that they can not interfere with the next test.
     */
    @AfterEach
    void tearDown() {
        manager.terminate();
        synthesizer.shutdown();
        assertTrue(listenerErrors.isEmpty(), () -> "listener failures: " + listenerErrors);
    }

    /** Waits for the latch, failing instead of hanging. */
    private static void await(CountDownLatch latch, String what) throws InterruptedException {
        assertTrue(latch.await(WAIT, TimeUnit.SECONDS), "timed out waiting for " + what);
    }

    /** Waits for the latch inside a callback where checked exceptions are not possible. */
    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await(WAIT, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Installs a speak handler that blocks the very first synthesis until
     * {@code release} is counted down and reports it via {@code blocked}.
     *
     * @return the id of the blocked item, {@code -1} until it is known
     */
    private AtomicInteger blockFirstSynthesis(CountDownLatch blocked, CountDownLatch release) {
        AtomicInteger blockedId = new AtomicInteger(-1);
        synthesizer.setSpeakHandler(id -> {
            if (blockedId.compareAndSet(-1, id)) {
Debug.println("pretend " + id + " taking long time...");
                blocked.countDown();
                awaitQuietly(release);
Debug.println(id + " done");
            } else {
Debug.println("speak: " + id);
            }
        });
        return blockedId;
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.SynthesisQueue#getNextQueueItem()}.
     */
    @Test
    void testGetNextQueueItem() throws Exception {
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        // one event per item is enough to prove that both reached the play queue
        CountDownLatch cdl = new CountDownLatch(2);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        synthesizer.addSpeakableListener(e -> {
            String source = String.valueOf(e.getSource());
            if (source.equals(segment1.getMarkupText()) || source.equals(segment2.getMarkupText())) {
Debug.println(source + " in playing queue...");
                if (seen.add(source)) {
                    cdl.countDown();
                }
            } else {
                listenerErrors.add(new AssertionError("unexpected event: " + e));
            }
        });
        queue.appendItem(segment1, null);
        queue.appendItem(segment2, null);
        await(cdl, "both items to reach the play queue");
Debug.println("done");
    }

    /**
     * Test method for {@link SynthesisQueue#getQueueItem(int)}.
     */
    @Test
    void testGetQueueItem() throws Exception {
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        CountDownLatch blocked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        // hack, stopping at 1st synthesis
        AtomicInteger blockedId = blockFirstSynthesis(blocked, release);
        int firstId = queue.appendItem(segment1, null);
        int secondId = queue.appendItem(segment2, null);
        await(blocked, "1st item to be taken by the synthesis thread");
        assertEquals(firstId, blockedId.get(), "1st appended item is the one being synthesized");
        QueueItem item1 = queue.getQueueItem(firstId);
        assertNull(item1, "already consumed");
        QueueItem item2 = queue.getQueueItem(secondId);
        assertNotNull(item2, "still in queue");
        assertEquals(segment2.getMarkupText(), item2.getAudioSegment().getMarkupText(), "can retrieve because 1st synthesis is still working");
        QueueItem item3 = queue.getQueueItem(-1);
        assertNull(item3, "no such id");
        release.countDown();
    }

    /**
     * Test method for {@link SynthesisQueue#isQueueEmpty()}.
     */
    @Test
    void testIsQueueEmpty() throws InterruptedException {
        assertTrue(queue.isQueueEmpty());
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        CountDownLatch cdl = new CountDownLatch(1);
        synthesizer.addSpeakableListener(e -> {
            if (segment1.getMarkupText().equals(String.valueOf(e.getSource()))) {
Debug.println("1st in playing queue...");
                cdl.countDown();
            }
        });
        queue.appendItem(segment1, null);
        await(cdl, "1st item to reach the play queue");
        assertTrue(queue.isQueueEmpty(), "1st is in play queue, so empty");
    }

    /**
     * Test method for {@link SynthesisQueue#cancelFirstItem()},
     */
    @Test
    void testCancelFirstItem() throws Exception {
        assertTrue(queue.isQueueEmpty());
        AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        CountDownLatch blocked = new CountDownLatch(1);
        CountDownLatch cancelled = new CountDownLatch(1);
        CountDownLatch secondPlayed = new CountDownLatch(1);
        AtomicInteger firstId = new AtomicInteger();
        // hack, stopping at 1st synthesis until it is cancelled
        AtomicInteger blockedId = blockFirstSynthesis(blocked, cancelled);
        synthesizer.addSpeakableListener(e -> {
            if (e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                if (e.getRequestId() != firstId.get()) {
                    listenerErrors.add(new AssertionError("cancelled wrong item: " + e));
                }
Debug.println("1st canceled");
                cancelled.countDown();
            } else if (segment2.getMarkupText().equals(String.valueOf(e.getSource()))) {
Debug.println("2nd in playing queue, means processing done");
                secondPlayed.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        firstId.set(queue.appendItem(segment1, null)); // takes long time
        queue.appendItem(segment2, null);
        await(blocked, "1st item to be taken by the synthesis thread");
        assertEquals(firstId.get(), blockedId.get(), "1st appended item is the one being synthesized");
        assertTrue(queue.cancelFirstItem(), "1st is processing and not in queue, so 1st is cancelable as 1st");
        await(cancelled, "cancel event of the 1st item");
        await(secondPlayed, "2nd item to reach the play queue");
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
        AtomicInteger firstId = new AtomicInteger();
        AtomicInteger secondId = new AtomicInteger();
        CountDownLatch blocked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch cancelled = new CountDownLatch(2);
        // hack, stopping at 0th synthesis
        AtomicInteger blockedId = blockFirstSynthesis(blocked, release);
        synthesizer.addSpeakableListener(e -> {
            if (e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                if (!List.of(firstId.get(), secondId.get()).contains(e.getRequestId())) {
                    listenerErrors.add(new AssertionError("cancelled wrong item: " + e));
                }
Debug.println("id " + e.getRequestId() + " is canceled");
                cancelled.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        int zerothId = queue.appendItem(segment0, null); // takes long time
        await(blocked, "0th item to be taken by the synthesis thread");
        assertEquals(zerothId, blockedId.get(), "0th appended item is the one being synthesized");
        firstId.set(queue.appendItem(segment1, null));
        secondId.set(queue.appendItem(segment2, null));
        assertFalse(queue.isQueueEmpty(), "because of 0th takes long time");
        assertTrue(queue.cancelItem(firstId.get()), "1st is in queue because 0th takes long time");
        assertTrue(queue.cancelItem(secondId.get()), "2nd is in queue because 0th takes long time");
        await(cancelled, "cancel events of the 1st and 2nd item");
        release.countDown();
        assertTrue(queue.isQueueEmpty(), "queue is empty because all are canceled");
        assertFalse(queue.cancelItem(-1), "no such id");
    }
}
