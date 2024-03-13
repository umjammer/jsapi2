/*
 * File:    $HeadURL: https://jsapi.svn.sourceforge.net/svnroot/jsapi/trunk/org.jvoicexml.jsapi2.jse/demo/InputDemo/src/org/jvoicexml/jsapi2/demo/inputdemo/InputDemo.java $
 * Version: $LastChangedRevision: 329 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - A base implementation for JSR 113.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2.demo.input;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.speech.AudioManager;
import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.SpeechLocale;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.ResultToken;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;

import static java.lang.System.getLogger;


/**
 * @author Dirk Schnelle-Walka
 */
public final class InputDemo implements ResultListener {

    private static final System.Logger logger = getLogger(InputDemo.class.getName());

    /** The synthesizer to use. */
    private Synthesizer synthesizer;

    /** The recognizer to use. */
    private Recognizer recognizer;

    /** Locking mechanism. */
    private final Object lock = new Object();

    /** The recognition result. */
    private Result result;

    /**
     * Working method for the demo.
     *
     * @throws Exception error running the demo
     */
    public void run() throws Exception {
        // Create a synthesizer for the default Locale.
        synthesizer = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
        // Create a recognizer for the default Locale.
        recognizer = (Recognizer) EngineManager.createEngine(new RecognizerMode(SpeechLocale.ENGLISH));

        AudioManager manager = recognizer.getAudioManager();
        manager.setMediaLocator("capture://audio?rate=16000&bits=16&channels=2&endian=big&encoding=pcm&signed=true");
        // Get it ready to speak
        synthesizer.allocate();
        synthesizer.resume();
        synthesizer.waitEngineState(Engine.RESUMED);

        recognizer.allocate();
        recognizer.addResultListener(this);

        GrammarManager grammarManager = recognizer.getGrammarManager();
        InputStream in = InputDemo.class.getResourceAsStream("/yesno.srgs");
        Grammar grammar = grammarManager.loadGrammar("grammar:greeting",
                null, in, StandardCharsets.UTF_8.name());
        grammar.setActivatable(true);
        recognizer.processGrammars();

        recognizer.requestFocus();
        recognizer.resume();
        // Tell the user what to do as soon as the recognizer is ready.
        recognizer.waitEngineState(Engine.RESUMED);
        // Speak the intro string
        synthesizer.speak("Please say yes or no", null);
        System.out.println("Please say yes or no...");
        synchronized (lock) {
            lock.wait();
        }
        System.out.print("Recognized: ");
        ResultToken[] tokens = result.getBestTokens();

        for (ResultToken token : tokens) {
            System.out.print(token.getText() + " ");
        }
        System.out.println();
        recognizer.deallocate();
        synthesizer.deallocate();
        System.exit(0);
    }

    /**
     * Starts this demo.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) throws Exception {
        InputDemo demo = new InputDemo();

        demo.run();
    }

    @Override
    public void resultUpdate(ResultEvent event) {
        System.out.println(event);
        if (event.getId() == ResultEvent.RESULT_ACCEPTED) {
            result = (Result) (event.getSource());
            synchronized (lock) {
                lock.notifyAll();
            }
        } else {
            // synthesizer.speak("I did not understand what you said", null);
        }
    }
}
