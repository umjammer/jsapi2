/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: $
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

package javax.speech.recognition;

import javax.speech.mock.recognition.MockResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test case for {@link javax.speech.recognition.ResultEvent}.
 * 
 * @author Dirk Schnelle
 */
public class ResultEventTest {

    /** The result. */
    private Result result;

    @BeforeEach
    protected void setUp() throws Exception {
        result = new MockResult();
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.ResultEvent#isFinalizedChanged()}.
     */
    @Test
    void testIsFinalizedChanged() {
        final ResultEvent event = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED);
        assertFalse(event.isFinalizedChanged());

        final ResultEvent event2 = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED, false, true);
        assertFalse(event2.isFinalizedChanged());

        final ResultEvent event3 = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED, true, true);
        assertFalse(event3.isFinalizedChanged());

        final ResultEvent event4 = new ResultEvent(result,
                ResultEvent.RESULT_CREATED, true, true);
        assertTrue(event4.isFinalizedChanged());

        final ResultEvent event5 = new ResultEvent(result,
                ResultEvent.RESULT_CREATED, false, true);
        assertFalse(event5.isFinalizedChanged());

        final ResultEvent event6 = new ResultEvent(result,
                ResultEvent.RESULT_UPDATED, true, true);
        assertTrue(event6.isFinalizedChanged());

        final ResultEvent event7 = new ResultEvent(result,
                ResultEvent.RESULT_UPDATED, false, true);
        assertFalse(event7.isFinalizedChanged());

        final ResultEvent event8 = new ResultEvent(result,
                ResultEvent.RESULT_ACCEPTED, true, true);
        assertTrue(event8.isFinalizedChanged());

        final ResultEvent event9 = new ResultEvent(result,
                ResultEvent.RESULT_ACCEPTED, false, true);
        assertFalse(event9.isFinalizedChanged());

        final ResultEvent event10 = new ResultEvent(result,
                ResultEvent.RESULT_REJECTED, true, true);
        assertTrue(event10.isFinalizedChanged());

        final ResultEvent event11 = new ResultEvent(result,
                ResultEvent.RESULT_REJECTED, false, true);
        assertFalse(event11.isFinalizedChanged());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.ResultEvent#isUnfinalizedChanged()}.
     */
    @Test
    void testIsUnfinalizedChanged() {
        final ResultEvent event = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED);
        assertFalse(event.isUnfinalizedChanged());

        final ResultEvent event2 = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED, false, true);
        assertFalse(event2.isUnfinalizedChanged());

        final ResultEvent event3 = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED, true, true);
        assertFalse(event3.isUnfinalizedChanged());

        final ResultEvent event4 = new ResultEvent(result,
                ResultEvent.RESULT_CREATED, true, true);
        assertTrue(event4.isUnfinalizedChanged());

        final ResultEvent event5 = new ResultEvent(result,
                ResultEvent.RESULT_CREATED, false, true);
        assertFalse(event5.isUnfinalizedChanged());

        final ResultEvent event6 = new ResultEvent(result,
                ResultEvent.RESULT_UPDATED, true, true);
        assertTrue(event6.isUnfinalizedChanged());

        final ResultEvent event7 = new ResultEvent(result,
                ResultEvent.RESULT_UPDATED, false, true);
        assertFalse(event7.isUnfinalizedChanged());

        final ResultEvent event8 = new ResultEvent(result,
                ResultEvent.RESULT_ACCEPTED, true, true);
        assertTrue(event8.isUnfinalizedChanged());

        final ResultEvent event9 = new ResultEvent(result,
                ResultEvent.RESULT_ACCEPTED, false, true);
        assertFalse(event9.isUnfinalizedChanged());

        final ResultEvent event10 = new ResultEvent(result,
                ResultEvent.RESULT_REJECTED, true, true);
        assertTrue(event10.isUnfinalizedChanged());

        final ResultEvent event11 = new ResultEvent(result,
                ResultEvent.RESULT_REJECTED, false, true);
        assertFalse(event11.isUnfinalizedChanged());
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#paramString()}.
     */
    @Test
    void testParamString() {
        final ResultEvent event = new ResultEvent(result, 43);
        final String str = event.paramString();
        assertTrue(str.contains("43"), "id not found in toString");

        final ResultEvent event2 = new ResultEvent(result, 44, true, true);
        final String str2 = event2.paramString();
        assertTrue(str2.contains("44"), "id not found in toString");
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#toString()}.
     */
    @Test
    void testToString() {
        final ResultEvent event = new ResultEvent(result, 43);
        final String str = event.toString();
        assertTrue(str.contains("43"), "id not found in toString");

        String paramString = event.paramString();
        assertTrue(str.length() > paramString.length(),
                "toString not longer than paramString");

        final ResultEvent event2 = new ResultEvent(result, 44, true, true);
        final String str2 = event2.toString();
        assertTrue(str2.contains("44"), "id not found in toString");

        String paramString2 = event.paramString();
        assertTrue(str2.length() > paramString2.length(),
                "toString not longer than paramString");
    }
}
