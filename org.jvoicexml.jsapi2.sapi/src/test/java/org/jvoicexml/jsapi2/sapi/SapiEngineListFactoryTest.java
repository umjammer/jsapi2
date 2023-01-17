/**
 * 
 */
package org.jvoicexml.jsapi2.sapi;

import java.util.Enumeration;
import javax.speech.EngineList;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link SapiEngineListFactory}.
 * @author Dirk Schnelle-Walka
 *
 */
@EnabledOnOs(OS.WINDOWS)
public final class SapiEngineListFactoryTest {

    /**
     * Test method for {@link org.jvoicexml.jsapi2.sapi.SapiEngineListFactory#createEngineList(javax.speech.EngineMode)}.
     */
    @Test
    void testCreateEngineList() {
        SapiEngineListFactory factory = new SapiEngineListFactory();
        EngineList list =
            factory.createEngineList(SynthesizerMode.DEFAULT);
        Enumeration<?> e = list.elements();
        assertTrue(e.hasMoreElements());
        SynthesizerMode mode = (SynthesizerMode) e.nextElement();
        Voice[] voices = mode.getVoices();
        assertTrue(voices.length > 0);
        Voice voice = voices[0];
        assertEquals("Microsoft Anna", voice.getName());
    }
}
