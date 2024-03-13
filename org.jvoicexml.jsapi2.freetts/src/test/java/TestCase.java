/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import javax.speech.Engine;
import javax.speech.EngineManager;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.synthesis.freetts.FreeTTSSynthesizer;
import org.jvoicexml.jsapi2.synthesis.freetts.FreeTTSSynthesizerMode;
import test.TestSynthesizer;
import test.TestSynthesizerMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-11 nsano initial version <br>
 */
public class TestCase {

    @BeforeEach
    void setup() throws Exception {
        assertEquals(2, EngineManager.availableEngines(null).size());
    }

    @Test
    void test1() throws Exception {
        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(new FreeTTSSynthesizerMode());
        assertInstanceOf(FreeTTSSynthesizer.class, synthesizer);
    }

    @Test
    void test2() throws Exception {
        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(new TestSynthesizerMode());
        assertInstanceOf(TestSynthesizer.class, synthesizer);
    }

    @Test
    void test3() throws Exception {
        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(SynthesizerMode.DEFAULT);
        assertInstanceOf(TestSynthesizer.class, synthesizer); // because of TestSynthesizer is prior than FreeTTSSynthesizer
    }

    @Test
    void test4() throws Exception {
        Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(new FreeTTSSynthesizerMode());
        assertNotNull(synthesizer);
        synthesizer.addSynthesizerListener(System.err::println);
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        synthesizer.resume();
        synthesizer.waitEngineState(Synthesizer.RESUMED);

        synthesizer.speak("Peter Piper picked a peck of pickled peppers.", System.err::println);

        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
        synthesizer.deallocate();
    }
}
