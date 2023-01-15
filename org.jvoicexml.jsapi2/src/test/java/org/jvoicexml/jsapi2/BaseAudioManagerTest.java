/**
 *
 */

package org.jvoicexml.jsapi2;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.MockAudioManager;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test cases for {@link BaseAudioManager}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class BaseAudioManagerTest {

    @AfterEach
    public void tearDown() {
        System.setSecurityManager(null);
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.BaseAudioManager#setMediaLocator(java.lang.String)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testSetMediaLocator() throws Exception {
        final BaseAudioManager manager = new MockAudioManager();
        final String locator = "file://test.wav";
        manager.setMediaLocator(locator);
        assertEquals(locator, manager.getMediaLocator());
    }

//    /**
//     * Test method for {@link org.jvoicexml.jsapi2.BaseAudioManager#setMediaLocator(java.lang.String)}.
//     * @throws Exception
//     *         test failed
//     */
//    @Test
//    void testSetMediaLocatorSecurityException() throws Exception {
//        assertThrows(SecurityException.class, () -> {
//            System.setSecurityManager(new SecurityManager());
//            BaseAudioManager manager = new MockAudioManager();
//            final String locator = "file://test.wav";
//            manager.setMediaLocator(locator);
//        });
//    }
}
