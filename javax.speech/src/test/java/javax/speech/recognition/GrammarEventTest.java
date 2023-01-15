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

import javax.speech.mock.recognition.MockGrammar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test case for {@link javax.speech.recognition.GrammarEvent}.
 * 
 * @author Dirk Schnelle
 */
public class GrammarEventTest {
    /** The grammar. */
    private Grammar grammar;

    @BeforeEach
    protected void setUp() throws Exception {
        grammar = new MockGrammar();
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarEvent#getGrammarException()}.
     */
    @Test
    void testGetGrammarException() {
        final GrammarEvent event = new GrammarEvent(grammar, 43);
        assertNull(event.getGrammarException());

        final GrammarException grammarException = new GrammarException();
        final GrammarEvent event2 = new GrammarEvent(grammar,
                GrammarEvent.GRAMMAR_CHANGES_REJECTED, true, true,
                grammarException);
        assertEquals(grammarException, event2.getGrammarException());

        final GrammarEvent event3 = new GrammarEvent(grammar,
                GrammarEvent.GRAMMAR_CHANGES_COMMITTED, true, true,
                null);
        assertNull(event3.getGrammarException());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarEvent#isDefinitionChanged()}.
     */
    @Test
    void testIsDefinitionChanged() {
        final GrammarEvent event = new GrammarEvent(grammar, 43);
        assertFalse(event.isDefinitionChanged());

        final GrammarException grammarException = new GrammarException();
        final GrammarEvent event2 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_CHANGES_REJECTED, true, true,
                grammarException);
        assertTrue(event2.isDefinitionChanged());

        final GrammarEvent event3 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_CHANGES_COMMITTED, true, false,
                null);
        assertFalse(event3.isDefinitionChanged());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarEvent#isActivableChanged()}.
     */
    @Test
    void testIsActivableChanged() {
        final GrammarEvent event = new GrammarEvent(grammar, 43);
        assertFalse(event.isActivableChanged());

        final GrammarException grammarException = new GrammarException();
        final GrammarEvent event2 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_CHANGES_REJECTED, true, true,
                grammarException);
        assertTrue(event2.isActivableChanged());

        final GrammarEvent event3 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_ACTIVATED, false, true,
                null);
        assertFalse(event3.isActivableChanged());
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#paramString()}.
     */
    @Test
    void testParamString() {
        final GrammarEvent event = new GrammarEvent(grammar, 43);
        final String str = event.paramString();
        assertTrue(str.contains("43"), "id not found in toString");

        final GrammarException grammarException = new GrammarException();
        final GrammarEvent event2 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_CHANGES_REJECTED, true, true,
                grammarException);
        final String str2 = event2.paramString();
        assertTrue(str2.contains("GRAMMAR_CHANGES_REJECTED"), "id not found in toString");
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#toString()}.
     */
    @Test
    void testToString() {
        final GrammarEvent event = new GrammarEvent(grammar, 43);
        final String str = event.toString();
        assertTrue(str.contains("43"), "id not found in toString");

        String paramString = event.paramString();
        assertTrue(str.length() > paramString.length(), "toString not longer than paramString");

        final GrammarException grammarException = new GrammarException();
        final GrammarEvent event2 = new GrammarEvent(grammar, 
                GrammarEvent.GRAMMAR_CHANGES_REJECTED, true, true,
                grammarException);
        final String str2 = event2.toString();
        assertTrue(str2.contains("GRAMMAR_CHANGES_REJECTED"), "id not found in toString");

        String paramString2 = event.paramString();
        assertTrue(str2.length() > paramString2.length(), "toString not longer than paramString");
    }
}
