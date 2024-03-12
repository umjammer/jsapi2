/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package test;

import java.util.logging.Level;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;

import vavi.util.Debug;


/**
 * TestEngineListFactory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-11 nsano initial version <br>
 */
public class TestEngineListFactory implements EngineListFactory {

    @Override
    public EngineList createEngineList(EngineMode require) {
Debug.println(Level.FINEST, "require: " + require);
        if (require == null || require instanceof TestSynthesizerMode || require == SynthesizerMode.DEFAULT)
            return new EngineList(new EngineMode[] { new TestSynthesizerMode() });
        else
            return null;
    }
}
