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

import java.util.Locale;

import javax.speech.SpeechLocale;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for {@link javax.speech.synthesis.Voice}.
 * 
 * @author Dirk Schnelle-Walka
 * 
 */
public class VoiceTest {

    /**
     * Test method for {@link javax.speech.synthesis.Voice#hashCode()}.
     */
    @Test
    void testHashCode() {
        Voice voice1 = new Voice();
        Voice voice2 = new Voice();
        assertEquals(voice1.hashCode(), voice2.hashCode());
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#getAge()}.
     */
    @Test
    void testGetAge() {
        Voice voice1 = new Voice();
        assertEquals(Voice.AGE_DONT_CARE, voice1.getAge());

        Voice voice2 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals(Voice.AGE_DONT_CARE, voice2.getAge());

        Voice voice3 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                42, Voice.VARIANT_DEFAULT);
        assertEquals(42, voice3.getAge());
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#getGender()}.
     */
    @Test
    void testGetGender() {
        Voice voice1 = new Voice();
        assertEquals(Voice.GENDER_DONT_CARE, voice1.getGender());

        Voice voice2 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals(Voice.GENDER_MALE, voice2.getGender());

        Voice voice3 = new Voice(SpeechLocale.US, "mary", Voice.GENDER_FEMALE,
                41, Voice.VARIANT_DEFAULT);
        assertEquals(Voice.GENDER_FEMALE, voice3.getGender());
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#getSpeechLocale()}.
     */
    @Test
    void testGetSpeechLocale() {
        Voice voice1 = new Voice();
        assertNull(voice1.getSpeechLocale());

        Voice voice2 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals(SpeechLocale.US, voice2.getSpeechLocale());

        Voice voice3 = new Voice(null, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertNull(voice3.getSpeechLocale());
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#getName()}.
     */
    @Test
    void testGetName() {
        Voice voice1 = new Voice();
        assertNull(voice1.getName());

        Voice voice2 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals("john", voice2.getName());

        Voice voice3 = new Voice(SpeechLocale.US, "", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals("", voice3.getName());

        Voice voice4 = new Voice(SpeechLocale.US, null, Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertNull(voice4.getName());
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#getVariant()}.
     */
    @Test
    void testGetVariant() {
        Voice voice1 = new Voice();
        assertEquals(Voice.VARIANT_DONT_CARE, voice1.getVariant());

        Voice voice2 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals(Voice.VARIANT_DEFAULT, voice2.getVariant());

        Voice voice3 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
        assertEquals(Voice.VARIANT_DONT_CARE, voice3.getVariant());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.Voice#equals(java.lang.Object)}.
     */
    @Test
    void testEqualsObject() {
        Voice voice1 = new Voice();
        assertNotEquals("test", voice1);

        Voice voice2 = new Voice();
        assertEquals(voice1, voice2);

        Voice voice3 = new Voice(null, null, Voice.GENDER_DONT_CARE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
        assertEquals(voice1, voice3);

        Voice voice4 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertNotEquals(voice1, voice4);

        Voice voice5 = new Voice(SpeechLocale.US, "mary", Voice.GENDER_FEMALE,
                41, Voice.VARIANT_DEFAULT);
        assertNotEquals(voice1, voice5);
        assertNotEquals(voice4, voice5);

        Voice voice6 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertEquals(voice4, voice6);
    }

    /**
     * Test method for {@link javax.speech.synthesis.Voice#toString()}.
     */
    @Test
    void testToString() {
        Voice voice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        String str = voice.toString();
        assertTrue(str.indexOf("john") > 0);
        assertTrue(str.indexOf(Locale.US.toString()) > 0);
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.Voice#match(javax.speech.synthesis.Voice)}.
     */
    @Test
    void testMatch() {
        Voice voice1 = new Voice();
        assertTrue(voice1.match(null));

        Voice voice2 = new Voice();
        assertTrue(voice1.match(voice2));

        Voice voice3 = new Voice(null, null, Voice.GENDER_DONT_CARE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
        assertTrue(voice1.match(voice3));

        Voice voice4 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertTrue(voice4.match(voice1));
        assertFalse(voice1.match(voice4));

        Voice voice5 = new Voice(SpeechLocale.US, "mary", Voice.GENDER_FEMALE,
                41, Voice.VARIANT_DEFAULT);
        assertTrue(voice5.match(voice1));
        assertFalse(voice4.match(voice5));

        Voice voice6 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DEFAULT);
        assertTrue(voice4.match(voice6));

        Voice voice7 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        Voice voice8 = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        assertTrue(voice7.match(voice8));
    }
}
