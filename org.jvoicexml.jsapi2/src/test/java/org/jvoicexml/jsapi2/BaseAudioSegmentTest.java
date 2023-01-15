/**
 *
 */

package org.jvoicexml.jsapi2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import javax.speech.AudioSegment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test cases for {@link BaseAudioSegment}.
 *
 * @author Dirk Schnelle-Walka
 */
class BaseAudioSegmentTest {

    /**
     * Test method for {@link org.jvoicexml.jsapi2.BaseAudioSegment#openInputStream()}.
     *
     * @throws Exception test failed.
     */
    @Test
    void testOpenInputStream() throws Exception {
        String test = "test";
        ByteArrayInputStream input = new ByteArrayInputStream(test.getBytes());
        AudioSegment segment1 = new BaseAudioSegment("http://localhost", "test", input);
        assertEquals(input, segment1.openInputStream());

        File file = new File("pom.xml");
        AudioSegment segment2 = new BaseAudioSegment(file.toURI().toString(), "test");
        InputStream input2 = segment2.openInputStream();
        assertNotNull(input2);
    }
}
