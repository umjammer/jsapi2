/**
 * 
 */
package org.jvoicexml.jsapi2.mac.synthesis;

import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.SpeechLocale;
import javax.speech.spi.EngineFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

/**
 * Synthesizer mode for SAPI.
 * @author Dirk Schnelle-Walka
 *
 */
public final class MacSynthesizerMode extends SynthesizerMode
    implements EngineFactory {
    /**
     * Constructs a new object.
     */
    public MacSynthesizerMode() {
        super();
    }

    /**
     * Constructs a new object.
     * @param locale  the locale associated with this mode
     */
    public MacSynthesizerMode(SpeechLocale locale) {
        super(locale);
    }

    /**
     * Constructs a new object.
     * 
     * Mac synthesizer does not support ssml
     * 
     * @param engineName the name of the engine
     * @param modeName the name of the mode
     */
    public MacSynthesizerMode(String engineName,
                              String modeName,
                              Boolean running, Boolean supportsLetterToSound,
                              Boolean supportsMarkup, Voice[] voices) {
    	super(engineName, modeName, running, supportsLetterToSound,
                false, voices);
    }

    @Override
    public Engine createEngine() throws IllegalArgumentException,
            EngineException {
        return new MacSynthesizer(this);
    }
}
