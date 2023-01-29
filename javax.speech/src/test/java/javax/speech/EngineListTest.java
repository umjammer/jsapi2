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

package javax.speech;

import java.util.Enumeration;
import javax.speech.mock.MockEngineMode;
import javax.speech.recognition.RecognizerMode;
import javax.speech.synthesis.SynthesizerMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test case for {@link javax.speech.EngineList}.
 *
 * @author Dirk Schnelle
 */
public class EngineListTest {
    /** The engine list to test. */
    private EngineList list;

    @BeforeEach
    protected void setUp() throws Exception {
        EngineMode[] modes = new EngineMode[] {
                new SynthesizerMode(SpeechLocale.US),
                new RecognizerMode(SpeechLocale.GERMAN),
                new MockEngineMode("name1", "mode1",
                        true, true, true)
        };
        list = new EngineList(modes);
    }

    /**
     * Test method for
     * {@link javax.speech.EngineList#anyMatch(javax.speech.EngineMode)}.
     */
    @Test
    void testAnyMatch() {
        EngineMode require1 = new MockEngineMode("name1", "mode1",
                true, true, true);
        assertTrue(list.anyMatch(require1));

        EngineMode require2 = new MockEngineMode("name2", "mode1",
                true, true, true);
        assertFalse(list.anyMatch(require2));
    }

    /**
     * Test method for {@link javax.speech.EngineList#elementAt(int)}.
     */
    @Test
    void testElementAt() {
        EngineMode mode1 = list.elementAt(0);
        assertNotNull(mode1);
        EngineMode mode2 = list.elementAt(1);
        assertNotNull(mode2);
        EngineMode mode3 = list.elementAt(2);
        assertNotNull(mode3);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.elementAt(4));
    }

    /**
     * Test method for {@link javax.speech.EngineList#elements()}.
     */
    @Test
    void testElements() {
        Enumeration<?> enumeration = list.elements();
        assertNotNull(enumeration);
        assertTrue(enumeration.hasMoreElements());
        assertNotNull(enumeration.nextElement());
        assertTrue(enumeration.hasMoreElements());
        assertNotNull(enumeration.nextElement());
        assertTrue(enumeration.hasMoreElements());
        assertNotNull(enumeration.nextElement());
        assertFalse(enumeration.hasMoreElements());
    }

    /**
     * Test method for
     * {@link javax.speech.EngineList#orderByMatch(javax.speech.EngineMode)}.
     */
    @Test
    void testOrderByMatch() {
        EngineMode require = new MockEngineMode("name1", "mode1",
                true, true, true);
        list.orderByMatch(require);
        EngineMode mode = list.elementAt(0);
        assertEquals(3, list.size());
        assertEquals(require, mode);
    }

    /**
     * Test method for
     * {@link javax.speech.EngineList#rejectMatch(javax.speech.EngineMode)}.
     */
    @Test
    void testRejectMatch() {
        assertEquals(3, list.size());
        EngineMode reject = new MockEngineMode("name1", "mode1",
                true, true, true);
        list.rejectMatch(reject);
        assertEquals(2, list.size());
        list.rejectMatch(null);
        assertEquals(0, list.size());
    }

    /**
     * Test method for {@link javax.speech.EngineList#removeElementAt(int)}.
     */
    @Test
    void testRemoveElementAt() {
        assertEquals(3, list.size());
        list.removeElementAt(2);
        assertEquals(2, list.size());

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> list.removeElementAt(2));
    }

    /**
     * Test method for
     * {@link javax.speech.EngineList#requireMatch(javax.speech.EngineMode)}.
     */
    @Test
    void testRequireMatch() {
        assertEquals(3, list.size());
        EngineMode require = new MockEngineMode("name1", "mode1",
                true, true, true);
        list.requireMatch(require);
        assertEquals(1, list.size());
        list.requireMatch(null);
        assertEquals(1, list.size());
    }

    /**
     * Test method for {@link javax.speech.EngineList#size()}.
     */
    @Test
    void testSize() {
        assertEquals(3, list.size());
    }
}
