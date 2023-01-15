/**
 *
 */

package org.jvoicexml.jsapi2.protocols.capture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test cases for {@link CaptureURLConnection}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class CaptureURLConnectionTest {

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
        URL url = new URL("capture://audio?rate=16000&bits=16&channels=2&endian=big&encoding=pcm&signed=true");
        CaptureURLConnection connection = new CaptureURLConnection(url);
        InputStream input = connection.getInputStream();
        byte[] buffer = new byte[1024];
        assertEquals(buffer.length, input.read(buffer));
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.protocols.playback.PlaybackURLConnection#getOutputStream()}.
     */
    @Test
    void testGetOutputStream() {
        assertThrows(IOException.class, () -> {
            URL url = new URL("playback://audio?rate=8000&channels=1&encoding=pcm");
            CaptureURLConnection connection = new CaptureURLConnection(url);
            connection.connect();
            OutputStream output = connection.getOutputStream();
        });
    }
}
