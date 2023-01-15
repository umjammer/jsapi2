package org.jvoicexml.jsapi2.sapi.synthesis;

import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.synthesis.SpeakableEvent;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.sapi.SapiEngineListFactory;

/**
 * Test cases for {@link SapiSynthesizer}.
 * <p>
 * Run this unit test with the VM argument:
 * <code>-Djava.library.path=cpp/Jsapi2SapiBridge/Debug</code>.
 * </p>
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 *
 *!!!!!!!!!!!!!!!!!!!!!!!Beware!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * each Test allocate the Synthesizer so beware that
 * a test is run completely before the next Test starts.
 * 
 * Disregard this will cause native code to crash
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
@EnabledOnOs(OS.WINDOWS)
public final class TestSynthesizer {
    /** The test object. */
    private Synthesizer synthesizer;

    /**
     * Prepare the test environment for all tests.
     * @throws Exception
     *         prepare failed
     */
    @BeforeAll
    public static void init() throws Exception {
        EngineManager.registerEngineListFactory(
                SapiEngineListFactory.class.getCanonicalName());
    }

    /**
     * Set up the test .
     * @throws Exception
     *         set up failed
     */
    @BeforeEach
    public void setUp() throws Exception {
        synthesizer =  (Synthesizer) EngineManager
            .createEngine(SynthesizerMode.DEFAULT);
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        synthesizer.resume();
    }

    /**
     * Tear down the test .
     * @throws Exception
     *         tear down failed
     */
    @AfterEach
    public void tearDown() throws Exception {
       if (synthesizer != null) {
           synthesizer.deallocate();
           synthesizer.waitEngineState(Engine.DEALLOCATED);
       }
    }

    /**
     * Test case for {@link SapiSynthesizer#handleSpeak(int, String)}.
     * @throws Exception
     *         test failed
     */
    @Test
    void testSpeak() throws Exception {
        synthesizer.speak("this is a test output", null);
        synthesizer.speak("this is another test output", null);
        System.out.println("this is a test output");
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
//        synthesizer.speakMarkup(
//                "This sounds normal <pitch middle = '+10'/> but the pitch drops half way through", null);
//        System.out.println("This sounds normal <pitch middle = '+10'/> but the pitch drops half way through");
//        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    /**
     * Test case for {@link SapiSynthesizer#handlePause()}.
     * Test case for {@link SapiSynthesizer#handleResume()}.
     * @throws Exception
     *         test failed
     */
    @Test
    void testPause() throws Exception {
        synthesizer.speak("this is a test output with a pause and resume test", null);     
        System.out.println("this is a test output with a pause and resume test");
        Thread.sleep(1800);
        synthesizer.pause();
        System.out.println("Pause");
        synthesizer.waitEngineState(Synthesizer.PAUSED);
        Thread.sleep(800);
        synthesizer.resume();
        System.out.println("Resume");
        synthesizer.waitEngineState(Synthesizer.RESUMED);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    /**
     * Test case for {@link SapiSynthesizer#handleCancel()}.
     * @throws Exception
     *         test failed
     */
    @Test
    void testCancel() throws Exception {
        final Object lock = new Object();
        final SpeakableListener listener = new SpeakableListener() {
            @Override
            public void speakableUpdate(final SpeakableEvent e) {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        };
        synthesizer.speak("this is a test output for the cancel test", listener);
        System.out.println("this is a test output for the cancel test");
        synchronized (lock) {
            lock.wait();
        }
        Thread.sleep(800);
        System.out.println("Cancel");
        synthesizer.cancel();
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    /**
     * Test case for {@link SapiSynthesizer#handleSpeak(int, javax.speech.synthesis.Speakable)}.
     * @throws Exception
     *         test failed
     */
    @Test
    void testSpeakSsml() throws Exception {
        synthesizer.speakMarkup(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><speak version=\"1.0\" xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schematicLocation=\"http://www.w3.org/2001/10/synthesis http://www.w3.org/TR/speech-synthesis/synthesis.xsd\">This is a test</speak>", null);
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><speak version=\"1.0\" xml:lang=\"en-US\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schematicLocation=\"http://www.w3.org/2001/10/synthesis http://www.w3.org/TR/speech-synthesis/synthesis.xsd\">This is a test</speak>");
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }
}
