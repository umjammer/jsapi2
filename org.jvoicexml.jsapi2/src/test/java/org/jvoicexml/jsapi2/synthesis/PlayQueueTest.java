/**
 *
 */

package org.jvoicexml.jsapi2.synthesis;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import javax.speech.AudioSegment;
import javax.speech.synthesis.SpeakableEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;
import vavi.util.Debug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Test cases for {@link PlayQueue}.
 *
 * @author Dirk Schnelle-Walka
 */
public class PlayQueueTest {

    /** The test object. */
    private PlayQueue queue;

    private MockSynthesizer synthesizer;

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    public void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
        QueueManager manager = new QueueManager(synthesizer);
        queue = manager.getPlayQueue();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#cancelItemAtTopOfQueue()}.
     */
    @Test
    void testCancelItemAtTopOfQueue() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        AudioSegment segment1 = new AudioSegment("http://localhost", "test") {
            @Override
            public InputStream openInputStream() throws IOException, SecurityException {
Debug.println("pretend taking long time when open id");
                try { cdl.await(); } catch (InterruptedException ignore) {}
Debug.println("pretend done");
                return super.openInputStream();
            }
        };
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        QueueItem item1 = new QueueItem(1, segment1, null);
        QueueItem item2 = new QueueItem(2, segment2, null);
        CountDownLatch cdl1 = new CountDownLatch(1);
        CountDownLatch cdl2 = new CountDownLatch(1);
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == item1.getId() && e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                cdl1.countDown();
            } else if (e.getRequestId() == item2.getId()) {
                cdl2.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        item1.setSynthesized(true);
        queue.addQueueItem(item1);
        item2.setSynthesized(true);
        queue.addQueueItem(item2);
        assertNull(queue.getQueueItem(item1.getId()), "already out of queue, now playing");
        assertEquals(item2, queue.getQueueItem(item2.getId()), "1st is playing, so in queue");
        cdl.countDown();
        queue.cancelItemAtTopOfQueue();
        cdl1.await();
        cdl2.await();
        assertNull(queue.getQueueItem(item2.getId()), "1st is canceled, so not in queue, it's consumed");
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#cancelItem(int)}.
     */
    @Test
    void testCancelItem() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        AudioSegment segment1 = new AudioSegment("http://localhost", "test") {
            @Override
            public InputStream openInputStream() throws IOException, SecurityException {
Debug.println("pretend taking long time when open id");
                try { cdl.await(); } catch (InterruptedException ignore) {}
Debug.println("pretend done");
                return super.openInputStream();
            }
        };
        AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        QueueItem item1 = new QueueItem(1, segment1, null);
        QueueItem item2 = new QueueItem(2, segment2, null);
        CountDownLatch cdl1 = new CountDownLatch(1);
        CountDownLatch cdl2 = new CountDownLatch(1);
        synthesizer.addSpeakableListener(e -> {
            if (e.getRequestId() == item1.getId() && e.getId() == SpeakableEvent.SPEAKABLE_CANCELLED) {
                cdl1.countDown();
            } else if (e.getRequestId() == item2.getId()) {
                cdl2.countDown();
            } else {
Debug.println("eventId: " + Integer.toHexString(e.getId()));
            }
        });
        item1.setSynthesized(true);
        queue.addQueueItem(item1);
        item2.setSynthesized(true);
        queue.addQueueItem(item2);
        assertNull(queue.getQueueItem(item1.getId()), "already out of queue, now playing");
        assertEquals(item2, queue.getQueueItem(item2.getId()), "1st is playing, so in queue");
        cdl.countDown();
        queue.cancelItem(item2.getId());
        cdl2.await();
        queue.cancelItemAtTopOfQueue();
        cdl1.await();
        assertTrue(queue.isQueueEmpty(), "");
    }
}
