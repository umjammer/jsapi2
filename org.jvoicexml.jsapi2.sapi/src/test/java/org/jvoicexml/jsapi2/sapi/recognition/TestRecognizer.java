package org.jvoicexml.jsapi2.sapi.recognition;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.ResultToken;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.sapi.SapiEngineListFactory;

import static java.lang.System.getLogger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for {@link SapiRecognizer}.
 * <p>
 * Run this unit test with the VM option:
 * <code>-Djava.library.path=cpp/Jsapi2SapiBridge/Debug</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 */
@EnabledOnOs(OS.WINDOWS)
public final class TestRecognizer implements ResultListener {

    private static final System.Logger logger = getLogger(TestRecognizer.class.getName());

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
        Locale.setDefault(new Locale("en"));
        // Enable logging at all levels.
        Handler handler = new ConsoleHandler();
        handler.setLevel(java.util.logging.Level.ALL);
        java.util.logging.Logger.getLogger("").addHandler(handler);
        java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.ALL);
    }

    /**
     * Set up the test.
     *
     * @throws Exception set up failed
     */
    @BeforeEach
    public void setUp() throws Exception {
        logger.log(Level.DEBUG, "Allocating ASR Engine");
        recognizer = (Recognizer) EngineManager.createEngine(RecognizerMode.DEFAULT);
        recognizer.allocate();
        recognizer.waitEngineState(Engine.ALLOCATED);
        logger.log(Level.DEBUG, "ASR Engine allocated");
    }

    /**
     * Tear down the test.
     *
     * @throws Exception tear down failed
     */
    @AfterEach
    public void tearDown() throws Exception {
        logger.log(Level.DEBUG, "Deallocating ASR Engine");
        if (recognizer != null) {
            recognizer.pause();
            recognizer.waitEngineState(Engine.PAUSED);
            recognizer.deallocate();
            recognizer.waitEngineState(Engine.DEALLOCATED);
        }
        logger.log(Level.DEBUG, "ASR Engine deallocated");
    }

    /**
     * Simple Test case for the recognizer.
     * Use only one Grammar
     *
     * @throws Exception test failed.
     */
    @Test
    void testRecognize() throws Exception {
        recognizer.addResultListener(this);

        GrammarManager grammarManager = recognizer.getGrammarManager();
        String name = getLocaleGrammarName("hello");
        logger.log(Level.DEBUG, name);
        InputStream in = TestRecognizer.class.getResourceAsStream(name);
        grammarManager.loadGrammar("grammar:greeting", null, in, "UTF-8");
        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        logger.log(Level.DEBUG, "Test1 Please say something...");

        synchronized (lock) {
            lock.wait();
        }

        StringBuilder sb = new StringBuilder("Recognized: ");
        ResultToken[] tokens = result.getBestTokens();

        for (ResultToken token : tokens) {
            sb.append(token.getText()).append(" ");
        }
        logger.log(Level.DEBUG, sb.toString());
    }

    /**
     * Retrieves the local grammar name.
     *
     * @param base base grammar name.
     * @return localized grammar name.
     */
    private String getLocaleGrammarName(String base) {
        Locale locale = Locale.getDefault();
        final String country = "de"; //locale.getLanguage();
        return base + "-" + country + ".xml";
    }

    /**
     * Pause after the recognizer is ready.
     *
     * @throws Exception test failed.
     */
    @Test
    void testRecognizePause() throws Exception {
        /* timeout for enginestate change requests */
        int timeOut = 3000;

        // load testGrammar
        recognizer.addResultListener(this);
        GrammarManager grammarManager = recognizer.getGrammarManager();
        String name = getLocaleGrammarName("hello");
        InputStream in = TestRecognizer.class.getResourceAsStream(name);
        grammarManager.loadGrammar("grammar:greeting", null, in, "UTF-8");

        // test resume
        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED, timeOut);
        assertTrue(recognizer.testEngineState(Engine.RESUMED), "Enginestate should be RESUMED.");

        logger.log(Level.DEBUG, "delaying...");
        Thread.sleep(500);
        logger.log(Level.DEBUG, "...delayed");

        // test pause
        recognizer.pause();
        recognizer.waitEngineState(Engine.PAUSED, timeOut);
        assertTrue(recognizer.testEngineState(Engine.PAUSED), "Enginestate should be PAUSED.");

        // test resume after pause
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED, timeOut);
        assertTrue(recognizer.testEngineState(Engine.RESUMED), "Enginestate should be RESUMED.");
//
//        logger.log(Level.DEBUG, "Say something");
//        synchronized (lock) {
//            lock.wait();
//        }
//        
//        System.out.print("Recognized: ");
//        final ResultToken[] tokens = result.getBestTokens();
//
//        for (int i = 0; i < tokens.length; i++) {
//            System.out.print(tokens[i].getText() + " ");
//        }
//        logger.log(Level.DEBUG, );
    }

    /**
     * Tests pause after recognition and resume with a different grammar.
     *
     * @throws Exception test failed.
     */
    @Test
    void testRecognizeResume() throws Exception {
        recognizer.addResultListener(this);

        GrammarManager grammarManager = recognizer.getGrammarManager();
        String name = getLocaleGrammarName("hello");
        InputStream in = TestRecognizer.class.getResourceAsStream(name);
        logger.log(Level.DEBUG, "Try to load Grammar");
        Grammar hello =
                grammarManager.loadGrammar("grammar:greeting", null, in, "UTF-8");
        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        logger.log(Level.DEBUG, "Please say a greeting...");

        synchronized (lock) {
            lock.wait();
        }

        StringBuilder sb = new StringBuilder("Recognized: ");
        ResultToken[] tokens = result.getBestTokens();

        for (ResultToken token : tokens) {
            sb.append(token.getText()).append(" ");
        }
        logger.log(Level.DEBUG, sb.toString());

//        Object obj = ((SapiResult)result).getSemanticInterpretation();

        logger.log(Level.DEBUG, "Recognizer wait for Enginestate PAUSED");
        recognizer.pause();
        recognizer.waitEngineState(Engine.PAUSED);
        grammarManager.deleteGrammar(hello);

        logger.log(Level.DEBUG, "List grammars after delete... ");

        Grammar[] grams = grammarManager.listGrammars();
        logger.log(Level.DEBUG, "count: " + grams.length);

        for (Grammar gram : grams) {
            logger.log(Level.DEBUG, gram.getReference());
        }
        logger.log(Level.DEBUG, "Load new grammar... ");
        InputStream in2 = TestRecognizer.class.getResourceAsStream("Licht.xml");
        grammarManager.loadGrammar("grammar:LIGHT", null, in2, "UTF-8");
        logger.log(Level.DEBUG, "Resume... ");
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        logger.log(Level.DEBUG, "Please say a command...");

        synchronized (lock) {
            lock.wait();
        }

        StringBuilder sb2 = new StringBuilder("Recognized: ");
        ResultToken[] tokens2 = result.getBestTokens();

        for (ResultToken resultToken : tokens2) {
            sb2.append(resultToken.getText()).append(" ");
        }
        logger.log(Level.DEBUG, sb.toString());
    }

    /**
     * Test case  for the recognizer.
     * <p>
     * Use 2 grammars for the first recognition
     * gretting contains:
     * ( Hallo| Guten Morgen) [ Dirk| David| Renato| Josua]
     * LIGHT contains:
     * (Licht| Lampe) (ein| aus)
     * <p>
     * only use greeting for the second recognition
     *
     * @throws Exception test failed.
     */
    @Test
    void testRecognize2Grammars() throws Exception {
        recognizer.addResultListener(this);

        GrammarManager grammarManager = recognizer.getGrammarManager();
        InputStream in = TestRecognizer.class.getResourceAsStream("Licht.xml");
        grammarManager.loadGrammar("grammar:LIGHT", null, in, "UTF-8");
        String name = getLocaleGrammarName("hello");
        InputStream in2 = TestRecognizer.class.getResourceAsStream(name);
        grammarManager.loadGrammar("grammar:greeting", null, in2, "UTF-8");

        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        logger.log(Level.DEBUG, "Test2.1 Please say something...");

        synchronized (lock) {
            lock.wait();
        }

        StringBuilder sb = new StringBuilder("Recognized: ");
        ResultToken[] tokens = result.getBestTokens();

        for (ResultToken resultToken : tokens) {
            sb.append(resultToken.getText()).append(" ");
        }
        logger.log(Level.DEBUG, sb.toString());

        Grammar gram = grammarManager.getGrammar("grammar:LIGHT");
        grammarManager.deleteGrammar(gram);

        recognizer.pause();
        recognizer.requestFocus();
        recognizer.resume();
        recognizer.waitEngineState(Engine.RESUMED);
        logger.log(Level.DEBUG, "Test2.2 Please say something...");

        Thread.sleep(3000);

        StringBuilder sb2 = new StringBuilder("Recognized: ");
        tokens = result.getBestTokens();

        for (ResultToken token : tokens) {
            sb2.append(token.getText()).append(" ");
        }
        logger.log(Level.DEBUG, sb2.toString());
    }

    /**
     * Simple Test case for setGrammarContent Methode.
     * which uses the Sapi5 Compiler
     * Use only one Grammar
     *
     * @throws Exception test failed.
     */
    @Test
    void testSetGrammarContent() throws Exception {

        recognizer.addResultListener(this);

        byte[] buffer = new byte[(int) new File("./unittests/org/jvoicexml/jsapi2/sapi/recognition/hello.xml").length()];
        BufferedInputStream f = new BufferedInputStream(Files.newInputStream(Paths.get("./unittests/org/jvoicexml/jsapi2/sapi/recognition/hello.xml")));
        f.read(buffer);
        f.close();

        String grammar = new String(buffer);

        //logger.log(Level.DEBUG,  grammar );

        if (((SapiRecognizer) recognizer).setGrammarContent(grammar, "foobar")) {

            SapiRecognitionThread recognitionThread = new SapiRecognitionThread(((SapiRecognizer) recognizer));
            recognitionThread.start();

            logger.log(Level.DEBUG, "Test1 Please say something...");

            synchronized (lock) {
                lock.wait();
            }

            StringBuilder sb = new StringBuilder("Recognized: ");
            ResultToken[] tokens = result.getBestTokens();

            for (ResultToken token : tokens) {
                sb.append(token.getText()).append(" ");
            }
            logger.log(Level.DEBUG, sb.toString());

        } else {
            System.err.println("Activating Grammar failed");
        }

    }


    @Override
    public void resultUpdate(ResultEvent event) {
        logger.log(Level.DEBUG, event);
        if (event.getId() == ResultEvent.RESULT_ACCEPTED) {
            result = (Result) (event.getSource());
            synchronized (lock) {
                lock.notifyAll();
            }
        }
        if (event.getId() == ResultEvent.RESULT_REJECTED) {

            recognizer.pause();
            recognizer.requestFocus();
            recognizer.resume();
            logger.log(Level.DEBUG, "Recognition confidence was too low. Please try again ...");
        }
    }

    /**
     * Test method for {@link SapiRecognizer#reportResult(String, String)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testReportResult() throws Exception {
        recognizer.addResultListener(this);
        GrammarManager manager = recognizer.getGrammarManager();
        final String ruleName = "test";
        RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                ruleName);
        RuleComponent hello = new RuleToken("Hello Dirk");
        Rule rule = new Rule("test", hello);
        grammar.addRule(rule);
        String utterance = load("sml-simple.xml");
        SapiRecognizer sapiRecognizer = (SapiRecognizer) recognizer;
        recognizer.addResultListener(this);
        sapiRecognizer.reportResult(ruleName, utterance);
        synchronized (lock) {
            lock.wait();
        }
        StringBuilder words = new StringBuilder();
        ResultToken[] tokens = result.getBestTokens();
        for (ResultToken token : tokens) {
            words.append(token.getText()).append(" ");
        }
        assertEquals("Hello Dirk ", words.toString());
    }

    /**
     * Test method for {@link SapiRecognizer#reportResult(String, String)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testReportResultMultipleTags() throws Exception {
        recognizer.addResultListener(this);
        GrammarManager manager = recognizer.getGrammarManager();
        final String ruleName = "test";
        RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                ruleName);
        RuleComponent hello = new RuleToken("Hello");
        RuleTag helloTag = new RuleTag("out=\"general\";");
        Rule rule = new Rule("test", hello);
        grammar.addRule(rule);
        String utterance = load("sml-multiple-tags.xml");
        SapiRecognizer sapiRecognizer = (SapiRecognizer) recognizer;
        recognizer.addResultListener(this);
        sapiRecognizer.reportResult(ruleName, utterance);
        synchronized (lock) {
            lock.wait();
        }
        StringBuilder words = new StringBuilder();
        ResultToken[] tokens = result.getBestTokens();
        for (ResultToken token : tokens) {
            words.append(token.getText()).append(" ");
        }
        assertEquals("Hello Dirk ", words.toString());
    }

    /**
     * Loads the given resource as a string.
     *
     * @param resource the name of the resource to load
     * @return contents of the resource
     * @throws IOException error loading the resource
     */
    private String load(String resource) throws IOException {
        InputStream in =
                TestRecognizer.class.getResourceAsStream("sml-simple.xml");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int num;
        byte[] buffer = new byte[1024];
        do {
            num = in.read(buffer);
            if (num > 0) {
                out.write(buffer, 0, num);
            }
        } while (num >= 0);
        return out.toString();
    }
}
