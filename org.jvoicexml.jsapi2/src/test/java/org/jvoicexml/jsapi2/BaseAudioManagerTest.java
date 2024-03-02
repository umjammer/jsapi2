/**
 *
 */

package org.jvoicexml.jsapi2;


import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.MockAudioManager;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test cases for {@link BaseAudioManager}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class BaseAudioManagerTest {

    /**
     * Test method for {@link org.jvoicexml.jsapi2.BaseAudioManager#setMediaLocator(java.lang.String)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testSetMediaLocator() throws Exception {
        BaseAudioManager manager = new MockAudioManager();
        final String locator = "file://test.wav";
        manager.setMediaLocator(locator);
        assertEquals(locator, manager.getMediaLocator());
    }
}
