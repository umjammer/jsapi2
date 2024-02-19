/**
 *
 */

package org.jvoicexml.jsapi2.mac;

import java.util.Enumeration;
import javax.speech.EngineList;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link EngineListFactory}.
 *
 * @author Stefan Radomski
 */
@EnabledOnOs(OS.MAC)
public final class MacEngineListFactoryTest {

    /**
     * Test method for {@link javax.speech.spi.EngineListFactory#createEngineList(javax.speech.EngineMode)}.
     */
    @Test
    void testCreateEngineList() {
        MacEngineListFactory factory = new MacEngineListFactory();
        EngineList list = factory.createEngineList(SynthesizerMode.DEFAULT);
        Enumeration<?> e = list.elements();
        assertTrue(e.hasMoreElements());
        SynthesizerMode mode = (SynthesizerMode) e.nextElement();
        Voice[] voices = mode.getVoices();
        assertTrue(voices.length > 0);
        Voice voice = voices[0];
        assertEquals("Albert", voice.getName()); // mhh... os versin dependent
    }
}
