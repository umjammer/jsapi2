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

import javax.speech.SpeechLocale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for {@link javax.speech.synthesis.SpeakableEvent}.
 * 
 * @author Dirk Schnelle-Walka
 */
public class SpeakableEventTest {
    private Object source;

    @BeforeEach
    protected void setUp() throws Exception {
        source = new Object();
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getAttributes()}.
     */
    @Test
    void testGetAttributes() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(0, event1.getAttributes().length);

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(attrs, event2.getAttributes());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(0, event3.getAttributes().length);

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(0, event4.getAttributes().length);

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(0, event5.getAttributes().length);

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(0, event6.getAttributes().length);

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(0, event7.getAttributes().length);

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(0, event8.getAttributes().length);
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getAudioPosition()}.
     */
    @Test
    void testGetAudioPosition() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event1.getAudioPosition());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event2.getAudioPosition());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(audioPosition, event3.getAudioPosition());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event4.getAudioPosition());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event5.getAudioPosition());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event6.getAudioPosition());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                17);
        assertEquals(17, event7.getAudioPosition());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(SpeakableEvent.UNKNOWN_AUDIO_POSITION,
                event8.getAudioPosition());
    }

    /**
     * Test method for {@link javax.speech.synthesis.SpeakableEvent#getIndex()}.
     */
    @Test
    void testGetIndex() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);

        int index;
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event1.getIndex());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event2.getIndex());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event3.getIndex());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event4.getIndex());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event5.getIndex());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        index = event6.getIndex();
        assertEquals(1, index);

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event7.getIndex());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(SpeakableEvent.UNKNOWN_INDEX, event8.getIndex());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getNewVoice()}.
     */
    @Test
    void testGetNewVoice() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);

        Voice voice;
        assertNull(event1.getNewVoice());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertNull(event2.getNewVoice());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertNull(event3.getNewVoice());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertNull(event4.getNewVoice());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertNull(event5.getNewVoice());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertNull(event6.getNewVoice());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertNull(event7.getNewVoice());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        voice = event8.getNewVoice();
        assertEquals(newVoice, voice);
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getOldVoice()}.
     */
    @Test
    void testGetOldVoice() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertNull(event1.getOldVoice());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertNull(event2.getOldVoice());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertNull(event3.getOldVoice());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertNull(event4.getOldVoice());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertNull(event5.getOldVoice());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertNull(event6.getOldVoice());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertNull(event7.getOldVoice());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(oldVoice, event8.getOldVoice());
    }

    /**
     * Test method for {@link javax.speech.synthesis.SpeakableEvent#getPhones()}.
     */
    @Test
    void testGetPhones() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);

        PhoneInfo[] phoneInfos;
        Exception error = null;
        assertEquals(0, event1.getPhones().length);

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(0, event2.getPhones().length);

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(0, event3.getPhones().length);

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(0, event4.getPhones().length);

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(0, event5.getPhones().length);

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(phones, event6.getPhones());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(0, event7.getPhones().length);

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(0, event8.getPhones().length);
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getRealizedValue()}.
     */
    @Test
    void testGetRealizedValue() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event1.getRealizedValue());


        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event2.getRealizedValue());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event3.getRealizedValue());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(realized, event4.getRealizedValue());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event5.getRealizedValue());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event6.getRealizedValue());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event7.getRealizedValue());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE, event8.getRealizedValue());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getRequestedValue()}.
     */
    @Test
    void testGetRequestedValue() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event1.getRequestedValue());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event2.getRequestedValue());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event3.getRequestedValue());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(requested, event4.getRequestedValue());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event5.getRequestedValue());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event6.getRequestedValue());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event7.getRequestedValue());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(SpeakableEvent.UNKNOWN_VALUE,
                event8.getRequestedValue());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getRequestId()}.
     */
    @Test
    void testGetRequestId() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(42, event1.getRequestId());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(46, event2.getRequestId());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(49, event3.getRequestId());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(51, event4.getRequestId());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(55, event5.getRequestId());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(59, event6.getRequestId());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(60, event7.getRequestId());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 61, "textInfo7", oldVoice,
                newVoice);
        assertEquals(61, event8.getRequestId());
    }

    /**
     * Test method for
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}.
     */
    @Test
    void testGetTextInfo() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);

        String textinfo;

        textinfo = event1.getTextInfo();
        assertNull(textinfo);

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        textinfo = event2.getTextInfo();
        assertEquals("textInfo", textinfo);

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        textinfo = event3.getTextInfo();
        assertEquals("textInfo2", textinfo);

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        textinfo = event4.getTextInfo();
        assertEquals("textInfo3", textinfo);

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        textinfo = event5.getTextInfo();
        assertEquals("textInfo4", textinfo);

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        textinfo = event6.getTextInfo();
        assertEquals("textInfo5", textinfo);

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        textinfo = event7.getTextInfo();
        assertEquals("textInfo6", textinfo);

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        textinfo = event8.getTextInfo();
        assertEquals("textInfo7", textinfo);
    }

    /**
     * Test method for {@link javax.speech.synthesis.SpeakableEvent#getType()}.
     */
    @Test
    void testGetType() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE, event1.getType());

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        assertEquals(SpeakableEvent.ELEMENT_OPEN, event2.getType());

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE, event3.getType());

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        assertEquals(SpeakableEvent.PROSODY_RATE, event4.getType());

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE, event5.getType());

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE, event6.getType());

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE,
                event7.getType());

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        assertEquals(SpeakableEvent.UNKNOWN_TYPE, event8.getType());
    }


    /**
     * Test method for {@link javax.speech.SpeechEvent#paramString()}.
     */
    @Test
    void testParamString() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        String str1 = event1.paramString();
        assertTrue(str1.contains("TOP_OF_QUEUE"),
                "id not found in toString");

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        String str2 = event2.paramString();
        assertTrue(str2.contains("ELEMENT_REACHED"),
                "id not found in toString");

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        String str3 = event3.paramString();
        assertTrue(str3.contains("MARKER_REACHED"),
                "id not found in toString");

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        String str4 = event4.paramString();
        assertTrue(str4.contains("PROSODY_UPDATED"),
                "id not found in toString");

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        String str5 = event5.paramString();
        assertTrue(str5.contains("WORD_STARTED"),
                "id not found in toString");

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        String str6 = event6.paramString();
        assertTrue(str6.contains("PHONEME_STARTED"),
                "id not found in toString");

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        String str7 = event7.paramString();
        assertTrue(str7.contains("MARKER_REACHED"),
                "id not found in toString");

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        String str8 = event8.paramString();
        assertTrue(str8.contains("VOICE_CHANGED"),
                "id not found in toString");
    }

    /**
     * Test method for {@link javax.speech.SpeechEvent#toString()}.
     */
    @Test
    void testToString() {
        SpeakableEvent event1 = new SpeakableEvent(source,
                SpeakableEvent.TOP_OF_QUEUE, 42);
        String str1 = event1.toString();
        assertTrue(str1.contains("TOP_OF_QUEUE"), "id not found in toString");
        String paramString = event1.paramString();
        assertTrue(str1.length() > paramString.length(),
                "toString not longer than paramString");

        String[] attrs = new String[] { "attribute1", "attribute2" };
        SpeakableEvent event2 = new SpeakableEvent(source,
                SpeakableEvent.ELEMENT_REACHED, 46, "textInfo",
                SpeakableEvent.ELEMENT_OPEN, attrs);
        String str2 = event2.toString();
        assertTrue(str2.contains("ELEMENT_REACHED"),
                "id not found in toString");
        String paramString2 = event1.paramString();
        assertTrue(str2.length() > paramString2.length(),
                "toString not longer than paramString");

        final int audioPosition = 46738;
        SpeakableEvent event3 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 49, "textInfo2", audioPosition);
        String str3 = event3.toString();
        assertTrue(str3.contains("MARKER_REACHED"),
                "id not found in toString");
        String paramString3 = event3.paramString();
        assertTrue(str3.length() > paramString3.length(),
                "toString not longer than paramString");

        final int requested = 100;
        final int realized = 99;
        SpeakableEvent event4 = new SpeakableEvent(source,
                SpeakableEvent.PROSODY_UPDATED, 51, "textInfo3",
                SpeakableEvent.PROSODY_RATE, requested, realized);
        String str4 = event4.toString();
        assertTrue(str4.contains("PROSODY_UPDATED"),
                "id not found in toString");
        String paramString4 = event4.paramString();
        assertTrue(str4.length() > paramString4.length(),
                "toString not longer than paramString");

        final int wordStart = 17;
        final int wordEnd = 23;
        SpeakableEvent event5 = new SpeakableEvent(source,
                SpeakableEvent.WORD_STARTED, 55, "textInfo4", wordStart,
                wordEnd);
        String str5 = event5.toString();
        assertTrue(str5.contains("WORD_STARTED"),
                "id not found in toString");
        String paramString5 = event5.paramString();
        assertTrue(str5.length() > paramString5.length(),
                "toString not longer than paramString");

        PhoneInfo[] phones = new PhoneInfo[] { new PhoneInfo("ph1", 1),
                new PhoneInfo("ph2", 2) };
        SpeakableEvent event6 = new SpeakableEvent(source,
                SpeakableEvent.PHONEME_STARTED, 59, "textInfo5", phones, 1);
        String str6 = event6.toString();
        assertTrue(str6.contains("PHONEME_STARTED"),
                "id not found in toString");
        String paramString6 = event6.paramString();
        assertTrue(str6.length() > paramString6.length(),
                "toString not longer than paramString");

        SpeakableEvent event7 = new SpeakableEvent(source,
                SpeakableEvent.MARKER_REACHED, 60, "textInfo6",
                SpeakableEvent.SPEAKABLE_FAILURE_RECOVERABLE);
        String str7 = event7.toString();
        assertTrue(str7.contains("MARKER_REACHED"),
                "id not found in toString");
        String paramString7 = event7.paramString();
        assertTrue(str7.length() > paramString7.length(),
                "toString not longer than paramString");

        Voice oldVoice = new Voice(SpeechLocale.US, "mary",
                Voice.GENDER_FEMALE, 41, Voice.VARIANT_DEFAULT);
        Voice newVoice = new Voice(SpeechLocale.US, "john", Voice.GENDER_MALE,
                Voice.AGE_MIDDLE_ADULT, Voice.VARIANT_DEFAULT);
        SpeakableEvent event8 = new SpeakableEvent(source,
                SpeakableEvent.VOICE_CHANGED, 60, "textInfo7", oldVoice,
                newVoice);
        String str8 = event8.toString();
        assertTrue(str8.contains("VOICE_CHANGED"),
                "id not found in toString");
        String paramString8 = event8.paramString();
        assertTrue(str8.length() > paramString8.length(),
                "toString not longer than paramString");
    }
}
