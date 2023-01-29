/**
 *
 */

package javax.speech;

import javax.speech.mock.MockRecognizerEngineListFactory;
import javax.speech.mock.MockSpeechEventExecutor;
import javax.speech.mock.MockSynthesizerEngineListFactory;
import javax.speech.recognition.RecognizerMode;
import javax.speech.synthesis.SynthesizerMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Test case for {@link javax.speech.recognition.RuleAlternatives}.
 *
 * @author Dirk Schnelle-Walka
 */
public class EngineManagerTest {
    @BeforeEach
    protected void setUp() throws Exception {
        EngineManager.registerEngineListFactory(
                MockRecognizerEngineListFactory.class.getCanonicalName());
        EngineManager.registerEngineListFactory(
                MockSynthesizerEngineListFactory.class.getCanonicalName());
    }

    /**
     * Test method for
     * {@link javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)}.
     */
    @Test
    void testAvailableEngines() {
        EngineMode require1 = null;
        EngineList engines1 = EngineManager.availableEngines(require1);
        assertEquals(2, engines1.size());

        EngineMode require2 = new SynthesizerMode();
        EngineList engines2 = EngineManager.availableEngines(require2);
        assertEquals(1, engines2.size());

        EngineMode require3 = new RecognizerMode();
        EngineList engines3 = EngineManager.availableEngines(require3);
        assertEquals(1, engines3.size());
    }

    /**
     * Test method for
     * {@link javax.speech.EngineManager#createEngine(javax.speech.EngineMode)}.
     */
    @Test
    void testCreateEngine() throws Exception {
        EngineMode require1 = new SynthesizerMode();
        Engine engine1 = EngineManager.createEngine(require1);
        assertNotNull(engine1);

        EngineMode require2 = new RecognizerMode();
        Engine engine2 = EngineManager.createEngine(require2);
        assertNotNull(engine2);

        EngineMode require3 = null;
        Exception failure = null;
        try {
            EngineManager.createEngine(require3);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for
     * {@link javax.speech.EngineManager#getSpeechEventExecutor()}.
     */
    @Test
    void testGetSpeechEventExecutor() {
        SpeechEventExecutor executor =
                EngineManager.getSpeechEventExecutor();
        assertNotNull(executor);
    }

    /**
     * Test method for
     * {@link javax.speech.EngineManager#setSpeechEventExecutor(javax.speech.SpeechEventExecutor)}.
     */
    @Test
    void testSetSpeechEventExecutor() {
        assertNull(EngineManager.getSpeechEventExecutor());
        SpeechEventExecutor executor = new MockSpeechEventExecutor();
        EngineManager.setSpeechEventExecutor(executor);
        assertEquals(executor, EngineManager.getSpeechEventExecutor());
    }

    /**
     * Test method for {@link javax.speech.EngineManager#getVersion()}.
     */
    @Test
    void testGetVersion() {
        assertEquals("2.0.6.0", EngineManager.getVersion());
    }

    /**
     * Test method for
     * {@link javax.speech.EngineManager#registerEngineListFactory(java.lang.String)}.
     */
    @Test
    void testRegisterEngineListFactory() throws Exception {
        EngineList engines1 = EngineManager.availableEngines(null);
        assertEquals(2, engines1.size());

        EngineManager
                .registerEngineListFactory(MockRecognizerEngineListFactory.class
                        .getName());

        EngineList engines2 = EngineManager.availableEngines(null);
        assertEquals(2, engines2.size());

        EngineManager
                .registerEngineListFactory(MockRecognizerEngineListFactory.class
                        .getName());

        EngineList engines3 = EngineManager.availableEngines(null);
        assertEquals(2, engines3.size());
    }
}
