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

package javax.speech.synthesis;

import javax.speech.EngineMode;
import javax.speech.SpeechLocale;
import javax.speech.mock.MockEngineMode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test case for {@link javax.speech.synthesis.SynthesizerMode}.
 *
 * @author Dirk Schnelle
 */
public class SynthesizerModeTest {

    /**
     * Test method for {@link javax.speech.synthesis.SynthesizerMode#hashCode()}.
     */
    @Test
    void testHashCode() {
        SynthesizerMode mode1 = new SynthesizerMode();
        SynthesizerMode mode2 = new SynthesizerMode();
        assertEquals(mode1.hashCode(), mode2.hashCode());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SynthesizerMode#getMarkupSupport()}.
     */
    @Test
    void testGetMarkupSupport() {
        SynthesizerMode mode1 = new SynthesizerMode();
        assertNull(mode1.getMarkupSupport());

        SynthesizerMode mode2 = new SynthesizerMode(SpeechLocale.US);
        assertNull(mode2.getMarkupSupport());

        String engineName1 = "name1";
        String modeName1 = "mode1";
        boolean running1 = true;
        boolean supportsLetterToSound1 = false;
        boolean markupSupport1 = true;
        Voice[] voices1 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode3 = new SynthesizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1, voices1);
        assertTrue(mode3.getMarkupSupport());

        String engineName2 = "name2";
        String modeName2 = "mode2";
        boolean running2 = true;
        boolean supportsLetterToSound2 = false;
        boolean markupSupport2 = false;
        Voice[] voices2 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode4 = new SynthesizerMode(engineName2, modeName2,
                running2, supportsLetterToSound2, markupSupport2, voices2);
        assertFalse(mode4.getMarkupSupport());

        String engineName3 = "name3";
        String modeName3 = "mode3";
        boolean running3 = true;
        boolean supportsLetterToSound3 = false;
        Boolean markupSupport3 = null;
        Voice[] voices3 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode5 = new SynthesizerMode(engineName3, modeName3,
                running3, supportsLetterToSound3, markupSupport3, voices3);
        assertNull(mode5.getMarkupSupport());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SynthesizerMode#match(javax.speech.EngineMode)}.
     */
    @Test
    void testMatch() {
        SynthesizerMode mode1 = new SynthesizerMode();
        assertTrue(mode1.match(null));

        SynthesizerMode mode2 = new SynthesizerMode();
        assertTrue(mode1.match(mode2));

        EngineMode engineMode = new MockEngineMode();
        assertTrue(mode1.match(engineMode));

        String engineName1 = "name1";
        String modeName1 = "mode1";
        boolean running1 = true;
        boolean supportsLetterToSound1 = false;
        boolean markupSupport1 = true;
        Voice[] voices1 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode3 = new SynthesizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1, voices1);
        assertFalse(mode1.match(mode3));
        assertTrue(mode3.match(mode1));

        String engineName2 = "name1";
        String modeName2 = "mode1";
        boolean running2 = true;
        boolean supportsLetterToSound2 = false;
        boolean markupSupport2 = true;
        Voice[] voices2 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode4 = new SynthesizerMode(engineName2, modeName2,
                running2, supportsLetterToSound2, markupSupport2, voices2);
        assertTrue(mode3.match(mode4));
        assertTrue(mode4.match(mode3));
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SynthesizerMode#equals(java.lang.Object)}.
     */
    @Test
    void testEqualsObject() {
        SynthesizerMode mode1 = new SynthesizerMode();
        assertNotEquals("test", mode1);

        SynthesizerMode mode2 = new SynthesizerMode();
        assertEquals(mode1, mode2);

        String engineName1 = "name1";
        String modeName1 = "mode1";
        boolean running1 = true;
        boolean supportsLetterToSound1 = false;
        boolean markupSupport1 = true;
        Voice[] voices1 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode3 = new SynthesizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1, voices1);
        assertNotEquals(mode1, mode3);

        String engineName2 = "name1";
        String modeName2 = "mode1";
        boolean running2 = true;
        boolean supportsLetterToSound2 = false;
        boolean markupSupport2 = true;
        Voice[] voices2 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode4 = new SynthesizerMode(engineName2, modeName2,
                running2, supportsLetterToSound2, markupSupport2, voices2);
        assertEquals(mode3, mode4);
        assertEquals(mode4, mode3);

        String engineName3 = "name1";
        String modeName3 = "mode1";
        boolean running3 = true;
        boolean supportsLetterToSound3 = false;
        boolean markupSupport3 = true;
        Voice[] voices3 = new Voice[] {new Voice()};
        SynthesizerMode mode5 = new SynthesizerMode(engineName3, modeName3,
                running3, supportsLetterToSound3, markupSupport3, voices3);
        assertNotEquals(mode3, mode5);
        assertNotEquals(mode5, mode3);
    }

    /**
     * Test method for {@link javax.speech.synthesis.SynthesizerMode#toString()}.
     */
    @Test
    void testToString() {
        SynthesizerMode mode1 = new SynthesizerMode();
        assertNotNull(mode1.toString());

        SynthesizerMode mode2 = new SynthesizerMode(SpeechLocale.US);
        assertNotNull(mode2.toString());

        String engineName1 = "name1";
        String modeName1 = "mode1";
        boolean running1 = true;
        boolean supportsLetterToSound1 = false;
        boolean markupSupport1 = true;
        Voice[] voices1 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode3 = new SynthesizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1, voices1);
        String str3 = mode3.toString();
        assertTrue(str3.indexOf(engineName1) > 0);
        assertTrue(str3.indexOf(modeName1) > 0);
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SynthesizerMode#getVoices()}.
     */
    @Test
    void testGetVoices() {
        SynthesizerMode mode1 = new SynthesizerMode();
        assertNull(mode1.getVoices());

        SynthesizerMode mode2 = new SynthesizerMode(SpeechLocale.US);
        Voice[] voices = mode2.getVoices();
        assertNotNull(voices);
        assertEquals(1, voices.length);
        assertEquals(SpeechLocale.US, voices[0].getSpeechLocale());

        String engineName1 = "name1";
        String modeName1 = "mode1";
        boolean running1 = true;
        boolean supportsLetterToSound1 = false;
        boolean markupSupport1 = true;
        Voice[] voices1 = new Voice[] {new Voice(), new Voice()};
        SynthesizerMode mode3 = new SynthesizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1, voices1);
        voices = mode3.getVoices();
        assertNotNull(voices);
        assertEquals(2, voices.length);
    }
}
