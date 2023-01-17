/**
 *
 */

package org.jvoicexml.jsapi2.protocols.playback;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownServiceException;
import javax.sound.sampled.AudioSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test cases for {@link PlaybackURLConnection}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class PlaybackURLConnectionTest {

    /**
     * Set up the test environment.
     */
    @BeforeEach
    public void setUp() {
        System.setProperty("java.protocol.handler.pkgs", "org.jvoicexml.jsapi2.protocols");
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.protocols.playback.PlaybackURLConnection#getInputStream()}.
     *
     * @throws Exception test failed.
     */
    @Test
    void testGetInputStream() throws Exception {
        assertThrows(UnknownServiceException.class, () -> {
            URL url = new URL("capture://audio?rate=8000&channels=1&encoding=pcm");
            PlaybackURLConnection connection = new PlaybackURLConnection(url);
            connection.connect();
            InputStream input = connection.getInputStream();
        });
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.protocols.playback.PlaybackURLConnection#getOutputStream()}.
     *
     * @throws Exception test failed.
     */
    @Test
    void testGetOutputStream() throws Exception {
        System.out.println(AudioSystem.getClip().getFormat());
        URL url = new URL("playback://audio?rate=44100&channels=2&encoding=pcm&bits=16");
        PlaybackURLConnection connection = new PlaybackURLConnection(url);
        connection.connect();
        OutputStream output = connection.getOutputStream();
    }
}
