package org.jvoicexml.jsapi2.mac.synthesis;

import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.Voice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.mac.MacEngineListFactory;


/**
 * Test cases for {@link Synthesizer}.
 * <p>
 * Run this unit test with the VM argument:
 * <code>-Djava.library.path=cpp/build</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 * @author Stefan Radomski
 */
@EnabledOnOs(OS.MAC)
public final class TestSynthesizer {

    /** The test object. */
    private Synthesizer synthesizer;

    /**
     * Prepare the test environment for all tests.
     *
     * @throws Exception prepare failed
     */
    @BeforeAll
    public static void init() throws Exception {
        //
        // You have to set one of these properties to true on Mac OSX 10.6,
        // otherwise JDK13Services.getProviders() called by the AudioManager
        // will never return.
        //
//		System.setProperty("com.apple.javaws.usingSWT", Boolean.TRUE.toString());
        System.setProperty("java.awt.headless", Boolean.TRUE.toString());
        EngineManager.registerEngineListFactory(MacEngineListFactory.class.getCanonicalName());
    }

    /**
     * Set up the test.
     *
     * @throws Exception set up failed
     */
    @BeforeEach
    public void setUp() throws Exception {
        Voice alex = new Voice(null, "Alex Compact", Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
        MacSynthesizerMode msm = new MacSynthesizerMode(null, null, null, null, false, new Voice[] {alex});
        synthesizer = (Synthesizer) EngineManager.createEngine(msm);
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        synthesizer.getSynthesizerProperties().setVolume(10);
    }

    /**
     * Tear down the test.
     *
     * @throws Exception tear down failed
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (synthesizer != null) {
            synthesizer.deallocate();
            synthesizer.waitEngineState(Engine.DEALLOCATED);
        }
    }

    /**
     * Test case for {@link Synthesizer#speak(String, SpeakableListener)}.
     */
    @Test
    void testSpeak() throws Exception {
        synthesizer.resume();
        synthesizer.speak("I'll be artificial intelligence complete!", null);
//        synthesizer.speak("Half past 8", null);
//        synthesizer.speak("Ups!", null);
        System.out.println("this is a test output");
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    /**
     * Test case for {@link Synthesizer#pause()}.
     * Test case for {@link Synthesizer#resume()}.
     */
    @Test
    void testPause() throws Exception {
//        synthesizer.speak("this is a test output with a pause and resume test", null);
        System.out.println("this is a test output with a pause and resume test");
        Thread.sleep(1800);
        synthesizer.pause();
        System.out.println("Pause");
        Thread.sleep(3000);
        synthesizer.resume();
        System.out.println("Resume");
        Thread.sleep(1500);
    }

    /**
     * Test case for {@link Synthesizer#speakMarkup(String, javax.speech.synthesis.SpeakableListener)}.
     */
    @Test
    void testSpeakSsml() throws Exception {
        synthesizer.speakMarkup("This sounds normal <pitch middle = '+10'/> but the pitch drops half way through", null);
        System.out.println("This sounds normal <pitch middle = '+10'/> but the pitch drops half way through");

        Thread.sleep(4000);
    }
}