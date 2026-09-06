/**
 *
 */

package org.jvoicexml.jsapi2.synthesis;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.speech.AudioSegment;
import javax.speech.synthesis.SpeakableEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link PlayQueue}.
 * <p>
 * Every wait is bounded so that a regression fails instead of hanging the build.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
@Timeout(value = 20, unit = TimeUnit.SECONDS)
public class PlayQueueTest {

    /** Upper bound for a single wait, in seconds. */
    private static final long WAIT = 10;

    /** The test object. */
    private PlayQueue queue;

    /** The queue manager owning {@link #queue}. */
    private QueueManager manager;

    private MockSynthesizer synthesizer;

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    public void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
        manager = new QueueManager(synthesizer);
        queue = manager.getPlayQueue();
    }

    /**
     * Stops the queue threads so that they can not interfere with the next test.
     */
    @AfterEach
    public void tearDown() {
        manager.terminate();
        synthesizer.shutdown();
    }

    /** Waits for the latch, failing instead of hanging. */
    private static void await(CountDownLatch latch, String what) throws InterruptedException {
        assertTrue(latch.await(WAIT, TimeUnit.SECONDS), "timed out waiting for " + what);
    }

    /**
     * Creates an audio segment whose stream opening blocks until {@code release}
     * is counted down, reporting via {@code opening} that it is being opened.
     */
    private static AudioSegment slowSegment(CountDownLatch opening, CountDownLatch release) {
        return new AudioSegment("http://localhost", "test") {
            @Override
            public InputStream openInputStream() throws IOException, SecurityException {
Debug.println("pretend taking long time when open id");
                opening.countDown();
                try {
                    release.await(WAIT, TimeUnit.SECONDS);
                } catch (InterruptedException ignore) {
                }
Debug.println("pretend done");
                return super.openInputStream();
            }
        };
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#cancelItemAtTopOfQueue()}.
     */
    @Test
    void testCancelItemAtTopOfQueue() throws Exception {
        CountDownLatch opening = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AudioSegment segment1 = slowSegment(opening, release);
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        QueueItem item1 = new QueueItem(1, segment1, null);
        QueueItem item2 = new QueueItem(2, segment2, null);
        CountDownLatch cancelled1 = new CountDownLatch(1);
        CountDownLatch started2 = new CountDownLatch(1);
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == item1.getId() && e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                cancelled1.countDown();
Debug.println("cancelled1: " + cancelled1.getCount());
            } else if (e.getRequestId() == item2.getId() && e.getId() == SpeakableEvent.SPEAKABLE_STARTED) {
                started2.countDown();
Debug.println("started2: " + started2.getCount());
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        item1.setSynthesized(true);
        queue.addQueueItem(item1);
        await(opening, "1st item to be played");
        item2.setSynthesized(true);
        queue.addQueueItem(item2);
        assertNull(queue.getQueueItem(item1.getId()), "already out of queue, now playing");
        assertEquals(item2, queue.getQueueItem(item2.getId()), "1st is playing, so in queue");
        assertTrue(queue.cancelItemAtTopOfQueue(), "1st is playing, so cancelable");
        release.countDown();
        await(cancelled1, "cancel event of the 1st item");
        await(started2, "start event of the 2nd item");
        assertNull(queue.getQueueItem(item2.getId()), "1st is canceled, so not in queue, it's consumed");
Debug.println("testCancelItemAtTopOfQueue::done");
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#cancelItem(int)}.
     */
    @Test
    void testCancelItem() throws Exception {
        CountDownLatch opening = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AudioSegment segment1 = slowSegment(opening, release);
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        QueueItem item1 = new QueueItem(1, segment1, null);
        QueueItem item2 = new QueueItem(2, segment2, null);
        CountDownLatch cancelled1 = new CountDownLatch(1);
        CountDownLatch cancelled2 = new CountDownLatch(1);
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == item1.getId() && e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                cancelled1.countDown();
Debug.println("cancelled1: " + cancelled1.getCount());
            } else if (e.getRequestId() == item2.getId() && e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                cancelled2.countDown();
Debug.println("cancelled2: " + cancelled2.getCount());
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        item1.setSynthesized(true);
        queue.addQueueItem(item1);
        await(opening, "1st item to be played");
        item2.setSynthesized(true);
        queue.addQueueItem(item2);
        assertNull(queue.getQueueItem(item1.getId()), "already out of queue, now playing");
        assertEquals(item2, queue.getQueueItem(item2.getId()), "1st is playing, so in queue");
        assertTrue(queue.cancelItem(item2.getId()), "2nd is waiting in queue, so cancelable by id");
        await(cancelled2, "cancel event of the 2nd item");
        // 1st is still blocked in openInputStream(), so it is still the item at the top
        assertTrue(queue.cancelItemAtTopOfQueue(), "1st is playing, so cancelable");
        release.countDown();
        await(cancelled1, "cancel event of the 1st item");
        assertTrue(queue.isQueueEmpty(), "cancelled all");
Debug.println("testCancelItem::done");
    }
}
