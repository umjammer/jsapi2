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

import java.util.Locale;
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
 * Test case for {@link javax.speech.recognition.RecognizerMode}.
 * 
 * @author Dirk Schnelle
 */
public class RecognizerModeTest {
    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#getSupportsMarkup()}.
     */
    @Test
    void testGetSupportsMarkup() {
        RecognizerMode mode1 = new RecognizerMode();
        assertNull(mode1.getSupportsMarkup());

        RecognizerMode mode2 = new RecognizerMode(SpeechLocale.US);
        assertNull(mode2.getSupportsMarkup());

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT  };
        RecognizerMode mode3 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        assertEquals(Boolean.TRUE, mode3.getSupportsMarkup());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#match(javax.speech.EngineMode)}.
     */
    @Test
    void testMatch() {
        RecognizerMode mode1 = new RecognizerMode();
        RecognizerMode mode2 = new RecognizerMode();
        assertTrue(mode1.match(mode2));

        RecognizerMode mode3 = new RecognizerMode(SpeechLocale.US);
        assertFalse(mode1.match(mode3));

        RecognizerMode mode4 = new RecognizerMode(SpeechLocale.US);
        assertTrue(mode3.match(mode4));

        RecognizerMode mode5 = new RecognizerMode(SpeechLocale.GERMAN);
        assertFalse(mode3.match(mode5));

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode6 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        assertFalse(mode3.match(mode6));
        assertTrue(mode6.match(mode1));
        assertTrue(mode6.match(mode3));

        final String engineName2 = "name1";
        final String modeName2 = "mode1";
        Boolean running2 = Boolean.TRUE;
        Boolean supportsLetterToSound2 = Boolean.TRUE;
        Boolean markupSupport2 = Boolean.TRUE;
        Integer vocabSupport2 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales2 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles2 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode7 = new RecognizerMode(engineName2, modeName2,
                running2, supportsLetterToSound2, markupSupport2,
                vocabSupport2, locales2, profiles2);
        assertTrue(mode6.match(mode7));
        assertTrue(mode7.match(mode6));

        final String engineName3 = "name1";
        final String modeName3 = "mode1";
        Boolean running3 = Boolean.TRUE;
        Boolean supportsLetterToSound3 = Boolean.TRUE;
        Boolean markupSupport3 = Boolean.TRUE;
        Integer vocabSupport3 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales3 = new SpeechLocale[] { SpeechLocale.US };
        SpeakerProfile[] profiles3 = new SpeakerProfile[] {
                SpeakerProfile.DEFAULT , SpeakerProfile.DEFAULT  };
        RecognizerMode mode8 = new RecognizerMode(engineName3, modeName3,
                running3, supportsLetterToSound3, markupSupport3,
                vocabSupport3, locales3, profiles3);
        assertTrue(mode6.match(mode8));
        assertTrue(mode8.match(mode6));
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#equals(java.lang.Object)}.
     */
    @Test
    void testEqualsObject() {
        RecognizerMode mode1 = new RecognizerMode();
        assertNotEquals("test", mode1);
        assertEquals(mode1, mode1);

        EngineMode engineMode = new MockEngineMode();
        assertTrue(mode1.match(engineMode));

        RecognizerMode mode2 = new RecognizerMode();
        assertEquals(mode1, mode2);

        RecognizerMode mode3 = new RecognizerMode(SpeechLocale.US);
        assertNotEquals(mode1, mode3);

        RecognizerMode mode4 = new RecognizerMode(SpeechLocale.US);
        assertEquals(mode3, mode4);

        RecognizerMode mode5 = new RecognizerMode(SpeechLocale.GERMAN);
        assertNotEquals(mode3, mode5);

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode6 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        assertNotEquals(mode3, mode6);

        final String engineName2 = "name1";
        final String modeName2 = "mode1";
        Boolean running2 = Boolean.TRUE;
        Boolean supportsLetterToSound2 = Boolean.TRUE;
        Boolean markupSupport2 = Boolean.TRUE;
        Integer vocabSupport2 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales2 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles2 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode7 = new RecognizerMode(engineName2, modeName2,
                running2, supportsLetterToSound2, markupSupport2,
                vocabSupport2, locales2, profiles2);
        assertEquals(mode6, mode7);
        assertEquals(mode7, mode6);

        final String engineName3 = "name1";
        final String modeName3 = "mode1";
        Boolean running3 = Boolean.TRUE;
        Boolean supportsLetterToSound3 = Boolean.TRUE;
        Boolean markupSupport3 = Boolean.TRUE;
        Integer vocabSupport3 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales3 = new SpeechLocale[] { SpeechLocale.US };
        SpeakerProfile[] profiles3 = new SpeakerProfile[] {
                SpeakerProfile.DEFAULT, SpeakerProfile.DEFAULT };
        RecognizerMode mode8 = new RecognizerMode(engineName3, modeName3,
                running3, supportsLetterToSound3, markupSupport3,
                vocabSupport3, locales3, profiles3);
        assertNotEquals(mode6, mode8);
        assertNotEquals(mode8, mode6);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#getSpeechLocales()}.
     */
    @Test
    void testGetSpeechLocales() {
        RecognizerMode mode1 = new RecognizerMode();
        assertNull(mode1.getSpeechLocales());

        RecognizerMode mode2 = new RecognizerMode(SpeechLocale.US);
        SpeechLocale[] loc2 = mode2.getSpeechLocales();
        assertEquals(1, loc2.length);
        assertEquals(SpeechLocale.US, loc2[0]);

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode3 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        SpeechLocale[] loc3 = mode3.getSpeechLocales();
        assertEquals(2, loc3.length);
        assertEquals(SpeechLocale.US, loc3[0]);
        assertEquals(SpeechLocale.GERMAN, loc3[1]);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#getSpeakerProfiles()}.
     */
    @Test
    void testGetSpeakerProfiles() {
        RecognizerMode mode1 = new RecognizerMode();
        assertNull(mode1.getSpeakerProfiles());

        RecognizerMode mode2 = new RecognizerMode(SpeechLocale.US);
        assertNull(mode2.getSpeakerProfiles());

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode3 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        SpeakerProfile[] prof3 = mode3.getSpeakerProfiles();
        assertEquals(1, prof3.length);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RecognizerMode#getVocabSupport()}.
     */
    @Test
    void testGetVocabSupport() {
        RecognizerMode mode1 = new RecognizerMode();
        assertNull(mode1.getVocabSupport());

        RecognizerMode mode2 = new RecognizerMode(SpeechLocale.US);
        assertNull(mode2.getVocabSupport());

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT };
        RecognizerMode mode3 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        assertEquals(RecognizerMode.SMALL_SIZE, mode3.getVocabSupport());
    }

    /**
     * Test method for {@link javax.speech.EngineMode#hashCode()}.
     */
    @Test
    void testHashCode() {
        RecognizerMode mode1 = new RecognizerMode();
        RecognizerMode mode2 = new RecognizerMode();
        assertEquals(mode1.hashCode(), mode2.hashCode());
    }

    /**
     * Test method for {@link javax.speech.EngineMode#toString()}.
     */
    @Test
    void testToString() {
        RecognizerMode mode1 = new RecognizerMode();
        String str1 = mode1.toString();
        assertNotNull(str1);

        RecognizerMode mode2 = new RecognizerMode(SpeechLocale.US);
        String str2 = mode2.toString();
        assertNotNull(str2);
        assertTrue(str2.indexOf(Locale.US.toString()) > 0);

        final String engineName1 = "name1";
        final String modeName1 = "mode1";
        Boolean running1 = Boolean.TRUE;
        Boolean supportsLetterToSound1 = Boolean.TRUE;
        Boolean markupSupport1 = Boolean.TRUE;
        Integer vocabSupport1 = RecognizerMode.SMALL_SIZE;
        SpeechLocale[] locales1 = new SpeechLocale[] { SpeechLocale.US,
                SpeechLocale.GERMAN };
        SpeakerProfile[] profiles1 = new SpeakerProfile[] { SpeakerProfile.DEFAULT  };
        RecognizerMode mode3 = new RecognizerMode(engineName1, modeName1,
                running1, supportsLetterToSound1, markupSupport1,
                vocabSupport1, locales1, profiles1);
        String str3 = mode3.toString();
        assertNotNull(str3);
        assertTrue(str3.indexOf(engineName1) > 0);
        assertTrue(str3.indexOf(modeName1) > 0);
        assertTrue(str3.indexOf(Locale.US.toString()) > 0);
        assertTrue(str3.indexOf(Locale.GERMAN.toString()) > 0);
    }
}
