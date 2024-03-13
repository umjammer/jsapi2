/**
 *
 */

package org.jvoicexml.jsapi2.protocols;

import java.net.URL;
import javax.sound.sampled.AudioFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test cases for {@link JavaSoundParser}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class JavaSoundParserTest {

    /**
     * Test method for {@link org.jvoicexml.jsapi2.protocols.JavaSoundParser#parse(java.net.URL)}.
     *
     * @throws Exception test failed.
     */
    @Test
    void testParse() throws Exception {
        URL url = new URL("playback://audio?rate=8000&channels=2&encoding=pcm");
        AudioFormat format = JavaSoundParser.parse(url);
        assertEquals(8000.0, format.getSampleRate());
        assertEquals(2, format.getChannels());
        assertEquals(AudioFormat.Encoding.PCM_SIGNED, format.getEncoding());
    }
}
