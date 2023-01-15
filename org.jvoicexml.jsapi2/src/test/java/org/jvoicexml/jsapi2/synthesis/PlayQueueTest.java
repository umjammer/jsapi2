/**
 *
 */

package org.jvoicexml.jsapi2.synthesis;

import javax.speech.AudioSegment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    public void setUp() throws Exception {
        final BaseSynthesizer synthesizer = new MockSynthesizer();
        final QueueManager manager = new QueueManager(synthesizer);
        queue = manager.getPlayQueue();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#getNextQueueItem()}.
     */
    @Test
    void testGetNextQueueItem() {
        final AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        final QueueItem item1 = new QueueItem(1, segment1, null);
        item1.setSynthesized(true);
        queue.addQueueItem(item1);
        final AudioSegment segment2 = new AudioSegment("http://foreignhost", "test2");
        final QueueItem item2 = new QueueItem(2, segment2, null);
        item2.setSynthesized(true);
        queue.addQueueItem(item2);
        assertEquals(item1, queue.getNextQueueItem());
        queue.cancelItemAtTopOfQueue();
        assertEquals(item2, queue.getNextQueueItem());
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.PlayQueue#getNextQueueItem()}.
     */
    @Test
    void testGetNextQueueItemNotSynthesized() {
        final AudioSegment segment1 = new AudioSegment("http://localhost", "test");
        final QueueItem item1 = new QueueItem(1, segment1, null);
        queue.addQueueItem(item1);
        final Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                item1.setSynthesized(true);
                queue.itemChanged(item1);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        });
        thread.start();
        final QueueItem fetchedItem = queue.getNextQueueItem();
        assertTrue(fetchedItem.isSynthesized());
        assertEquals(item1, fetchedItem);
    }
}
