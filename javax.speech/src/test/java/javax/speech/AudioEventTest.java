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

package javax.speech;

import javax.speech.mock.MockEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AudioEventTest {
    /** The test engine. */
    private Engine engine;

    @BeforeEach
    protected void setUp() throws Exception {
        engine = new MockEngine();
    }

    /**
     * Test method for {@link javax.speech.AudioEvent#paramString()}.
     */
    @Test
    void testParamString() {
        AudioEvent event = new AudioEvent(engine, AudioEvent.AUDIO_STARTED);
        String str = event.paramString();
        assertTrue(str.contains("AUDIO_STARTED"),
                "'" + str + "' does not contain AUDIO_STARTED");

        AudioEvent event2 = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_QUIET);
        String str2 = event2.paramString();
        assertTrue(str2.contains("AUDIO_LEVEL"),
                "'" + str2 + "' does not contain AUDIO_LEVEL");
    }

    /**
     * Test method for {@link javax.speech.AudioEvent#toString()}.
     */
    @Test
    void testToString() {
        AudioEvent event = new AudioEvent(engine, AudioEvent.AUDIO_STOPPED);
        String str = event.toString();

        assertTrue(str.contains("AUDIO_STOPPED"));

        AudioEvent event2 = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_LOUD);
        String str2 = event2.paramString();
        assertTrue(str2.contains("AUDIO_LEVEL"));
    }

    @Test
    void testGetAudioLevel() {
        AudioEvent event = new AudioEvent(engine, AudioEvent.AUDIO_STARTED);
        assertEquals(AudioEvent.AUDIO_LEVEL_MIN, event.getAudioLevel());

        AudioEvent eventMin = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_MIN);
        assertEquals(AudioEvent.AUDIO_LEVEL_MIN, eventMin.getAudioLevel());

        AudioEvent eventQuiet = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_QUIET);
        assertEquals(AudioEvent.AUDIO_LEVEL_QUIET, eventQuiet.getAudioLevel());

        AudioEvent eventLoud = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_LOUD);
        assertEquals(AudioEvent.AUDIO_LEVEL_LOUD, eventLoud.getAudioLevel());

        AudioEvent eventMax = new AudioEvent(engine, AudioEvent.AUDIO_LEVEL,
                AudioEvent.AUDIO_LEVEL_MAX);
        assertEquals(AudioEvent.AUDIO_LEVEL_MAX, eventMax.getAudioLevel());
    }
}
