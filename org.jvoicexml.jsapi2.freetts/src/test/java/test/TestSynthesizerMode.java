/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package test;

import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.spi.EngineFactory;
import javax.speech.synthesis.SynthesizerMode;


/**
 * TestSynthesizerMode.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-11 nsano initial version <br>
 */
public class TestSynthesizerMode extends SynthesizerMode implements EngineFactory {

    @Override
    public Engine createEngine() throws IllegalArgumentException, EngineException {
        return new TestSynthesizer();
    }
}
