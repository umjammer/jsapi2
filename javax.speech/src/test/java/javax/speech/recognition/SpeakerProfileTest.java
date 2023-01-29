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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test case for {@link javax.speech.recognition.SpeakerProfile}.
 *
 * @author Dirk Schnelle-Walka
 */
public class SpeakerProfileTest {

    /**
     * Test method for
     * {@link javax.speech.recognition.SpeakerProfile#hashCode()}.
     */
    @Test
    void testHashCode() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        SpeakerProfile profile2 = SpeakerProfile.DEFAULT;

        assertEquals(profile1.hashCode(), profile2.hashCode());
    }

    /**
     * Test method for {@link javax.speech.recognition.SpeakerProfile#getName()}.
     */
    @Test
    void testGetName() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        assertNull(profile1.getName());

        final String name2 = "name2";
        final String variant2 = "variant2";
        SpeakerProfile profile2 = new SpeakerProfile(name2, variant2);
        assertEquals(name2, profile2.getName());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.SpeakerProfile#getVariant()}.
     */
    @Test
    void testGetVariant() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        assertNull(profile1.getVariant());

        final String name2 = "name2";
        final String variant2 = "variant2";
        SpeakerProfile profile2 = new SpeakerProfile(name2, variant2);
        assertEquals(variant2, profile2.getVariant());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.SpeakerProfile#equals(Object)}.
     */
    @Test
    void testEqualsObject() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        assertNotEquals("test", profile1);
        assertNotEquals(profile1, null);

        SpeakerProfile profile2 = SpeakerProfile.DEFAULT;
        assertEquals(profile1, profile2);

        final String name3 = "name3";
        final String variant3 = "variant3";
        SpeakerProfile profile3 = new SpeakerProfile(name3, variant3);
        assertNotEquals(profile1, profile3);
        assertEquals(profile3, profile3);

        final String name4 = "name3";
        final String variant4 = "variant3";
        SpeakerProfile profile4 = new SpeakerProfile(name4, variant4);
        assertEquals(profile3, profile4);
        assertEquals(profile4, profile3);

        final String name5 = "name5";
        final String variant5 = "variant5";
        SpeakerProfile profile5 = new SpeakerProfile(name5, variant5);
        assertNotEquals(profile3, profile5);
        assertNotEquals(profile5, profile3);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.SpeakerProfile#toString()}.
     */
    @Test
    void testToString() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        String str1 = profile1.toString();
        assertNotNull(str1);

        final String name2 = "name2";
        final String variant2 = "variant2";
        SpeakerProfile profile2 = new SpeakerProfile(name2, variant2);
        String str2 = profile2.toString();
        assertNotNull(str2);
        assertTrue(str2.indexOf(name2) > 0);
        assertTrue(str2.indexOf(variant2) > 0);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.SpeakerProfile#match(SpeakerProfile)}.
     */
    @Test
    void testMatch() {
        SpeakerProfile profile1 = SpeakerProfile.DEFAULT;
        assertTrue(profile1.match(null));

        SpeakerProfile profile2 = SpeakerProfile.DEFAULT;
        assertTrue(profile1.match(profile2));

        final String name3 = "name3";
        final String variant3 = "variant3";
        SpeakerProfile profile3 = new SpeakerProfile(name3, variant3);
        assertFalse(profile1.match(profile3));
        assertTrue(profile3.match(profile3));
        assertTrue(profile3.match(profile1));

        final String name4 = "name3";
        final String variant4 = "variant3";
        SpeakerProfile profile4 = new SpeakerProfile(name4, variant4);
        assertTrue(profile3.match(profile4));
        assertTrue(profile4.match(profile3));

        final String name5 = "name5";
        final String variant5 = "variant5";
        SpeakerProfile profile5 = new SpeakerProfile(name5, variant5);
        assertFalse(profile3.match(profile5));
        assertFalse(profile5.match(profile3));
    }
}
