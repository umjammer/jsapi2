package org.jvoicexml.jsapi2.mock.recognition;

import java.io.InputStream;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.RecognizerMode;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;

import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.mock.MockSpeechEventExecutor;
import org.jvoicexml.jsapi2.recognition.BaseGrammarManager;
import org.jvoicexml.jsapi2.recognition.BaseRecognizer;
import org.jvoicexml.jsapi2.recognition.GrammarDefinition;

/**
 * Dummy implementation of a {@link javax.speech.recognition.Recognizer}
 * for test purposes.
 * @author Dirk Schnelle-Walka
 */
public final class MockRecognizer extends BaseRecognizer {

    /**
     * Constructs a new object.
     */
    public MockRecognizer() {
        super(RecognizerMode.DEFAULT);
    }

    GrammarManager createGrammarManager() {
        return new BaseGrammarManager(this);
    }

    @Override
    protected void postResultEvent(Result result, ResultEvent event, SpeechEventExecutor executor) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void handleAllocate() throws EngineStateException,
            EngineException, AudioException, SecurityException {
    }

    @Override
    protected void handleDeallocate() {
    }

    @Override
    protected void handlePause() {
    }

    @Override
    protected void handlePause(int flags) {
    }

    @Override
    protected boolean handleResume(InputStream in) throws EngineStateException {
        return true;
    }

    @Override
    protected void handleRequestFocus() {
    }

    @Override
    protected void handleReleaseFocus() {
    }

    @Override
    public Collection<Grammar> getBuiltInGrammars() {
        return null;
    }

    @Override
    protected SpeechEventExecutor createSpeechEventExecutor() {
        return new MockSpeechEventExecutor();
    }

    @Override
    protected boolean setGrammars(Collection<GrammarDefinition> grammarDefinition) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue,
            Object newValue) {
        properties.commitPropertyChange(propName, oldValue, newValue);
    }

    @Override
    protected AudioFormat getAudioFormat() {
        return new AudioFormat(16000f, 16, 1, true, false);
    }
}
