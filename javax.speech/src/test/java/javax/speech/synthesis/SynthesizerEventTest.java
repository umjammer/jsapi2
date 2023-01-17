/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package javax.speech.synthesis;

import javax.speech.mock.synthesis.MockSynthesizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test case for {@link javax.speech.synthesis.SynthesizerEvent}.
 * 
 * @author Dirk Schnelle
 * 
 */
public class SynthesizerEventTest {

    /** The test synthesizer. */
    private Synthesizer synthesizer;

    @BeforeEach
    protected void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SynthesizerEvent#isTopOfQueueChanged()}.
     */
    @Test
    void testIsTopOfQueueChanged() {
        Throwable problem = new Exception();
        boolean topOfQueueChanged = false;
        SynthesizerEvent event = new SynthesizerEvent(synthesizer,
                SynthesizerEvent.ENGINE_ERROR,
                SynthesizerEvent.QUEUE_EMPTIED,
                SynthesizerEvent.SYNTHESIZER_BUFFER_READY, problem,
                topOfQueueChanged);
        assertFalse(event.isTopOfQueueChanged());

        boolean topOfQueueChanged2 = false;
        SynthesizerEvent event2 = new SynthesizerEvent(synthesizer,
                SynthesizerEvent.QUEUE_UPDATED, SynthesizerEvent.QUEUE_EMPTIED,
                SynthesizerEvent.SYNTHESIZER_BUFFER_READY, null,
                topOfQueueChanged2);
        assertFalse(event2.isTopOfQueueChanged());

        boolean topOfQueueChanged3 = true;
        SynthesizerEvent event3 = new SynthesizerEvent(synthesizer,
                SynthesizerEvent.QUEUE_UPDATED, SynthesizerEvent.QUEUE_EMPTIED,
                SynthesizerEvent.SYNTHESIZER_BUFFER_READY, null,
                topOfQueueChanged3);
        assertTrue(event3.isTopOfQueueChanged());
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#paramString()}.
     */
    @Test
    void testParamString() {
        boolean topOfQueueChanged = false;
        SynthesizerEvent event = new SynthesizerEvent(synthesizer,
                SynthesizerEvent.QUEUE_UPDATED,
                SynthesizerEvent.QUEUE_EMPTIED,
                SynthesizerEvent.SYNTHESIZER_BUFFER_READY, null,
                topOfQueueChanged);
        String str = event.paramString();
        assertTrue(str.contains("QUEUE_UPDATED"));
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#toString()}.
     */
    @Test
    void testToString() {
        boolean topOfQueueChanged = false;
        SynthesizerEvent event = new SynthesizerEvent(synthesizer,
                SynthesizerEvent.QUEUE_UPDATED,
                SynthesizerEvent.QUEUE_EMPTIED,
                SynthesizerEvent.SYNTHESIZER_BUFFER_READY, null,
                topOfQueueChanged);
        String str = event.toString();
        assertTrue(str.contains("QUEUE_UPDATED"));
    }
}
