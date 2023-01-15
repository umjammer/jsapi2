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

import javax.speech.AudioSegment;

import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    /**
     * Set up the test environment.
     *
     * @throws Exception error setting up the test environment
     */
    @BeforeEach
    void setUp() throws Exception {
        final BaseSynthesizer synthesizer = new MockSynthesizer();
        final QueueManager manager = new QueueManager(synthesizer);
        queue = manager.getSynthesisQueue();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.synthesis.SynthesisQueue#getNextQueueItem()}.
     */
    @Test
    void testGetNextQueueItem() {
        final AudioSegment segment1 =
                new AudioSegment("http://localhost", "test");
        final AudioSegment segment2 =
                new AudioSegment("http://foreignhost", "test2");
        final int firstId = queue.appendItem(segment1, null);
        final QueueItem firstItem = queue.getNextQueueItem();
        assertEquals(firstId, firstItem.getId());
        assertEquals(segment1, firstItem.getAudioSegment());
        queue.removeQueueItem(firstItem);
        final int secondId = queue.appendItem(segment2, null);
        final QueueItem secondItem = queue.getNextQueueItem();
        assertEquals(secondId, secondItem.getId());
        assertEquals(segment2, secondItem.getAudioSegment());
        queue.removeQueueItem(secondItem);
    }

    /**
     * Test method for {@link SynthesisQueue#getQueueItem(int)}.
     */
    @Test
    void testGetQueueItem() {
        final AudioSegment segment1 =
                new AudioSegment("http://localhost", "test");
        final AudioSegment segment2 =
                new AudioSegment("http://foreignhost", "test2");
        final int firstId = queue.appendItem(segment1, null);
        final int secondId = queue.appendItem(segment2, null);
        final QueueItem item1 = queue.getQueueItem(firstId);
        assertEquals(segment1, item1.getAudioSegment());
        final QueueItem item2 = queue.getQueueItem(secondId);
        assertEquals(segment2, item2.getAudioSegment());
        final QueueItem item3 = queue.getQueueItem(-1);
        assertNull(item3);
    }

    /**
     * Test method for {@link SynthesisQueue#isQueueEmpty()}.
     */
    @Test
    void testIsQueueEmpty() {
        assertTrue(queue.isQueueEmpty());
        final AudioSegment segment1 =
                new AudioSegment("http://localhost", "test");
        final int id = queue.appendItem(segment1, null);
        assertFalse(queue.isQueueEmpty());
        final QueueItem item = queue.getQueueItem(id);
        queue.removeQueueItem(item);
        assertTrue(queue.isQueueEmpty());
    }

    /**
     * Test method for {@link SynthesisQueue#cancelFirstItem()},
     */
    @RepeatedIfExceptionsTest(repeats = 10)
    void testCancelFirstItem() {
        assertTrue(queue.isQueueEmpty());
        final AudioSegment segment1 =
                new AudioSegment("http://localhost", "test");
        final AudioSegment segment2 =
                new AudioSegment("http://foreignhost", "test2");
        queue.appendItem(segment1, null);
        final int secondId = queue.appendItem(segment2, null);
        assertFalse(queue.isQueueEmpty());
        assertTrue(queue.cancelFirstItem());
        final QueueItem secondItem = queue.getNextQueueItem();
        assertEquals(secondId, secondItem.getId());
        assertTrue(queue.cancelFirstItem());
        assertTrue(queue.isQueueEmpty());
        assertFalse(queue.cancelFirstItem());
    }

    /**
     * Test method for {@link SynthesisQueue#cancelItem(int)},
     */
    @Test
    void testCancelItem() {
        assertTrue(queue.isQueueEmpty());
        final AudioSegment segment1 =
                new AudioSegment("http://localhost", "test");
        final AudioSegment segment2 =
                new AudioSegment("http://foreignhost", "test2");
        final int firstId = queue.appendItem(segment1, null);
        final int secondId = queue.appendItem(segment2, null);
        assertFalse(queue.isQueueEmpty());
        assertTrue(queue.cancelItem(secondId));
        final QueueItem firstItem = queue.getNextQueueItem();
        assertEquals(firstId, firstItem.getId());
        assertTrue(queue.cancelItem(firstId));
        assertTrue(queue.isQueueEmpty());
        assertFalse(queue.cancelItem(-1));
    }
}
