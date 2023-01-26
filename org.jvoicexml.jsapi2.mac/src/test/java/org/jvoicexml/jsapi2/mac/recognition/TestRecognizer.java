package org.jvoicexml.jsapi2.mac.recognition;

import java.io.InputStream;
import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.ResultToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.mac.MacEngineListFactory;


/**
 * Test cases for {@link Recognizer}.
 * <p>
 * Run this unit test with the VM option:
 * <code>-Djava.library.path=cpp/build</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 */
@Disabled("TODO")
@EnabledOnOs(OS.MAC)
public final class TestRecognizer implements ResultListener {

    /** The test object. */
    private Recognizer recognizer;

    /** Locking mechanism. */
    private final Object lock = new Object();

    /** The recognition result. */
    private Result result;

    /**
     * Prepare the test environment for all tests.
     *
     * @throws Exception prepare failed
     */
    @BeforeAll
    public static void init() throws Exception {
        EngineManager.registerEngineListFactory(
                MacEngineListFactory.class.getCanonicalName());
System.err.println(EngineManager.availableEngines(RecognizerMode.DEFAULT));
    }

    /**
     * Set up the test .
     *
     * @throws Exception set up failed
     */
    @BeforeEach
    public void setUp() throws Exception {
        recognizer =
                (Recognizer) EngineManager.createEngine(RecognizerMode.DEFAULT);
        recognizer.allocate();
        recognizer.waitEngineState(Engine.ALLOCATED);
    }

    /**
     * Tear down the test .
     *
     * @throws Exception tear down failed
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (recognizer != null) {
            recognizer.deallocate();
            recognizer.waitEngineState(Engine.DEALLOCATED);
        }
    }

    /**
     * Test case for the recognizer.
     *
     * @throws Exception test failed.
     */
    @Test
    void testRecognize() throws Exception {
        recognizer.addResultListener(this);

        GrammarManager grammarManager = recognizer.getGrammarManager();
        InputStream in = TestRecognizer.class.getResourceAsStream("Licht.xml");
        grammarManager.loadGrammar("grammar:LIGHT", null, in, "iso-8859-1");

        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        System.out.println("Please say something...");

        synchronized (lock) {
            lock.wait();
        }

        System.out.print("Recognized: ");
        ResultToken[] tokens = result.getBestTokens();

        for (ResultToken token : tokens) {
            System.out.print(token.getText() + " ");
        }
        System.out.println();

    }

//    /**
//     * Test case for {@link SapiRecognizer#handlePause()}.
//     * Test case for {@link SapiRecognizer#handleResume()}.
//     * @throws Exception
//     *         test failed
//     */  
//    @Test
//    void testPause() throws Exception {
//            recognizer.pause();
//            System.out.println("\tPause Recognizer \n");
//            Thread.sleep(5000);
//            recognizer.resume();
//            System.out.println("\tResume Recognizer \n");
//            Thread.sleep(5000);
//    }

    @Override
    public void resultUpdate(ResultEvent event) {
        System.out.println(event);
        if (event.getId() == ResultEvent.RESULT_ACCEPTED) {
            result = (Result) (event.getSource());
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

//    @Test
//    void testRecognition() throws Exception {
//
//        SapiRecognizer recognizer = new SapiRecognizer();
//        recognizer.allocate();
//        recognizer.waitEngineState(Engine.ALLOCATED);
//        
//        recognizer.setGrammar("Licht.xml");       
//        Thread.sleep(6000);
//        recognizer.deallocate();
//    }
}
