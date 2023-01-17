/**
 * 
 */
package org.jvoicexml.jsapi2.mock.recognition;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.SpeechLocale;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.GrammarListener;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.RuleGrammar;

/**
 * Dummy implementation of a {@link javax.speech.recognition.GrammarManager}.
 * @author Dirk Schnelle-Walka
 *
 */
public class MockGrammarManager implements GrammarManager {

    @Override
    public void addGrammarListener(GrammarListener listener) {
    }

    @Override
    public void removeGrammarListener(GrammarListener listener) {
    }

    @Override
    public RuleGrammar createRuleGrammar(String grammarReference,
            String rootName) throws IllegalArgumentException,
            EngineStateException, EngineException {
        return null;
    }

    @Override
    public RuleGrammar createRuleGrammar(String grammarReference,
            String rootName, SpeechLocale locale)
            throws IllegalArgumentException, EngineStateException,
            EngineException {
        return null;
    }

    @Override
    public void deleteGrammar(Grammar grammar) throws IllegalArgumentException,
            EngineStateException {
    }

    @Override
    public Grammar getGrammar(String grammarReference)
            throws EngineStateException {
        return null;
    }

    @Override
    public int getGrammarMask() {
        return 0;
    }

    @Override
    public void setGrammarMask(int mask) {
    }

    @Override
    public Grammar[] listGrammars() throws EngineStateException {
        return new Grammar[0];
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType)
            throws GrammarException, IllegalArgumentException, IOException,
            EngineStateException, EngineException {
        return null;
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType,
            boolean loadReferences, boolean reloadReferences,
            @SuppressWarnings("rawtypes") List loadedGrammars) throws GrammarException,
            IllegalArgumentException, IOException, EngineStateException,
            EngineException {
        return null;
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType,
            InputStream byteStream, String encoding) throws GrammarException,
            IllegalArgumentException, IOException, EngineStateException,
            EngineException {
        return null;
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType,
            Reader charStream) throws GrammarException,
            IllegalArgumentException, IOException, EngineStateException,
            EngineException {
        return null;
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType,
            String grammarText) throws GrammarException,
            IllegalArgumentException, IOException, EngineStateException,
            EngineException {
        return null;
    }
}
