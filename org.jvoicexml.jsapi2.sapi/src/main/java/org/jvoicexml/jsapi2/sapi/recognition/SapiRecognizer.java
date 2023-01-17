/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2010-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.jsapi2.sapi.recognition;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.EngineEvent;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.jvoicexml.jsapi2.BaseAudioManager;
import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.recognition.BaseRecognizer;
import org.jvoicexml.jsapi2.recognition.GrammarDefinition;

/**
 * A SAPI recognizer.
 * 
 * @author Dirk Schnelle-Walka
 * @author Markus Baumgart
 * 
 */
public final class SapiRecognizer extends BaseRecognizer {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(SapiRecognizer.class
            .getName());

    /** SAPI recognizer Handle. **/
    private long recognizerHandle;

    /** Asynchronous recognition. */
    private SapiRecognitionThread recognitionThread;

    /** Listener for AudioEvents (e.g. <code>AudioEvent.AUDIO_CHANGED</code>) */
    private SapiRecognizerAudioEventListener listener;

    /**
     * Constructs a new object.
     * 
     * @param mode
     *            the recognizer mode.
     */
    public SapiRecognizer(SapiRecognizerMode mode) {
        super(mode);
    }

    @Override
    public Collection<Grammar> getBuiltInGrammars() {
        return sapiGetBuildInGrammars(recognizerHandle);
    }

    private native Collection<Grammar> sapiGetBuildInGrammars(long handle);

    @Override
    public void handleAllocate() throws EngineStateException, EngineException,
            AudioException, SecurityException {
        // allocate the CPP-Recognizer
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Allocationg SAPI-recognizer...");
        }
        recognizerHandle = sapiAllocate();
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("... allocated SAPI-recognizer");
        }
    }

    /**
     * Sets the input stream for the recognizer to the default input stream
     * from the {@link javax.speech.AudioManager}.
     * 
     * @return {@code true} if the stream has been set
     */
    protected boolean setRecognizerInputStream() {
        // Get the source audioStream
        BaseAudioManager audioManager = (BaseAudioManager) getAudioManager();
        // audioManager.audioStart();
        InputStream in = audioManager.getInputStream();
        return setRecognizerInputStream(in);
    }

    /**
     * Sets the input stream for the recognizer.
     * @param in the input stream
     * @return {@code true} if the stream has been set
     */
    private boolean setRecognizerInputStream(InputStream in) {
        /* problem: TypeMismatch JSAPI2-AudioFormat <-> JAVAX-AudioFormat */
        // AudioFormat audioFormat = audioManager.getAudioFormat();
        // => alternatively: hardcoded streamInfo
        final float sampleRate = 16000;
        final int bitsPerSample = 16;
        final int channels = 1;
        final boolean endian = false;
        final boolean signed = true;

        return sapiSetRecognizerInputStream(recognizerHandle, in, sampleRate,
                bitsPerSample, channels, endian, signed,
                AudioFormat.Encoding.PCM_SIGNED.toString().toLowerCase());
    }

    private native long sapiAllocate();

    private native boolean sapiSetRecognizerInputStream(long handle,
                                                        InputStream in, float sampleRate, int bitsPerSec,
                                                        int channels, boolean endian, boolean signed,
                                                        String encoding);

    @Override
    public void handleDeallocate() {
        sapiDeallocate(recognizerHandle);
    }

    private native void sapiDeallocate(long handle);

    @Override
    protected void handlePause() {
        sapiPause(recognizerHandle);
        if (recognitionThread != null) {
            recognitionThread.stopRecognition();
            recognitionThread = null;
        }
    }

    private native void sapiPause(long handle);

    @Override
    protected void handlePause(int flags) {
        sapiPause(recognizerHandle, flags);
        if (recognitionThread != null) {
            recognitionThread.stopRecognition();
            recognitionThread = null;
        }
    }

    private native void sapiPause(long handle, int flags);

    /**
     * Start recognition.
     * 
     * @param handle
     *            the recognizer handle
     * @return recognition result
     * @exception EngineException
     *                if there were errors during recognition
     */
    native int sapiRecognize(long handle, String[] result)
            throws EngineException;

    public long getRecognizerHandle() {
        return recognizerHandle;
    }

    @Override
    protected boolean handleResume(InputStream in)
            throws EngineStateException {
        setRecognizerInputStream(in);

        GrammarManager manager = getGrammarManager();
        Grammar[] grammars = manager.listGrammars();
        String[] grammarSources = new String[grammars.length];
        String[] grammarReferences = new String[grammars.length];
        int i = 0;

        if (LOGGER.isLoggable(Level.FINE)) {
            for (Grammar grammar : grammars) {
                LOGGER.log(Level.FINE, "Activate Grammar: {0}",
                        grammar.getReference());
            }
        }

        for (Grammar grammar : grammars) {
            grammarSources[i] = grammar.toString();
            grammarReferences[i] = grammar.getReference();
            i++;
        }

        sapiResume(recognizerHandle, grammarSources, grammarReferences);
        recognitionThread = new SapiRecognitionThread(this);
        recognitionThread.start();

        return true;
    }

    private native boolean sapiResume(long handle, String[] grammars,
            String[] references) throws EngineStateException;

    @Override
    protected boolean setGrammars(
            Collection<GrammarDefinition> grammarDefinition) {
        return false;
    }

    public boolean setGrammar(String grammarPath, String reference) {
        return sapiSetGrammar(recognizerHandle, grammarPath, reference);
    }

    private native boolean sapiSetGrammar(long handle, String grammarPath,
            String reference);

    public boolean setGrammarContent(String grammarContent,
                                     String reference) {
        return sapiSetGrammarContent(recognizerHandle, grammarContent,
                reference);
    }

    private native boolean sapiSetGrammarContent(long handle,
            String grammarPath, String reference);

    private native void start(long handle);

    /**
     * Callback of the SAPI recognizer if the recognition failed.
     */
    public void reportResultRejected() {
        postResultCreated();

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("No Match Recognized => False Recognition ...");
        }
        postResultRejected();

    }

    /**
     * Callback of the SAPI recognizer if the recognition succeeded.
     * 
     * @param ruleName
     *            name of the rule matching the utterance
     * @param utterance
     *            the utterance
     */
    public void reportResult(String ruleName, String utterance) {
        postResultCreated();
        Grammar grammar = getGrammar(ruleName);
        if (grammar == null) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Could not find the RuleGrammar");
            }
            SapiResult result = new SapiResult();
            postResultRejected(result);

            return;
        }
        SapiResult result = new SapiResult(grammar);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "reporting SML Result String : {0}",
                    utterance);
        }

        try {
            result.setSml(utterance);
        } catch (TransformerException ex) {
            LOGGER.log(Level.WARNING, "error parsing SML: {0}", ex.getMessage());
            EngineException ee = new EngineException(ex.getMessage());
            postEngineException(ee);
            return;
        }

        // Check if the utterance was only noise
        if (utterance.equals("...")) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Single Garbage Recognized ...");
            }
            postResultRejected(result);

            return;
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE,
                    "Recognized utterance : ''{0}'' Confidence : ''{1}''",
                    new Object[] { utterance, result.getConfidence() });
        }

        ResultEvent tokensUpdated = new ResultEvent(result,
                ResultEvent.RESULT_UPDATED, true, false);
        postResultEvent(tokensUpdated);

        ResultEvent grammarFinalized = new ResultEvent(result,
                ResultEvent.GRAMMAR_FINALIZED);
        postResultEvent(grammarFinalized);

        // set the confidenceLevel
        // map the actual confidence ([0; 1] (float)) to a new Integer-Value in
        // javax.speech's range [MIN_CONFIDENCE; MAX_CONFIDENCE]
        // e.g. be MAX_CONFIDENCE = 20; MIN_CONFIDENCE = -10;
        // then, a value of 0.4f from the Recognizer (working in [0; 1]) should
        // be mapped to +2 (in [-10; 20])
        // [because +2 is 2/5th of the complete RecognizerProperties' range]

        // get the whole range (in the example above => 20 - -10 = 30;
        int range = RecognizerProperties.MAX_CONFIDENCE
                - RecognizerProperties.MIN_CONFIDENCE;

        // set the value and shift it (again, with the sample above: set the
        // value to +12 from [0; 30] and shift it to +2 [-10; 20]
        float confTmp = (result.getConfidence() * range)
                + RecognizerProperties.MIN_CONFIDENCE;
        int resultconfidenceLevel = Math.round(confTmp);
        result.setConfidenceLevel(resultconfidenceLevel);

        // if the actual confidenceLevel is below the required one, reject the
        // result
        int minConfidenceLevel = recognizerProperties.getConfidenceThreshold();
        if (resultconfidenceLevel < minConfidenceLevel) {
            result.setResultState(Result.REJECTED);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                        "Result confidence too low, new ResultState: ''{0}''",
                        result.getResultState());
            }
        }

        /* post the result */
        if (result.getResultState() == Result.REJECTED) {
            postResultRejected(result);
        } else {
            result.setResultState(Result.ACCEPTED);
            ResultEvent accepted = new ResultEvent(result,
                    ResultEvent.RESULT_ACCEPTED, false, false);
            postResultEvent(accepted);
        }
    }

    /**
     * Obtain the grammar from the grammar manager.
     * @param ruleName name of the rule
     * @return determined grammar, maybe {@code null}
     */
    private Grammar getGrammar(String ruleName) {
        GrammarManager manager = getGrammarManager();
        Grammar grammar = manager.getGrammar("grammar:" + ruleName);
        if (grammar != null) {
            return grammar;
        }
        return manager.getGrammar(ruleName);
    }

    /**
     * Parses the given SML string.
     * 
     * @param sml
     *            the SML to parse
     * @return the parsed information
     * @throws TransformerException
     *             error parsing
     */
    private SmlInterpretationExtractor parseSml(String sml)
            throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        Reader reader = new StringReader(sml);
        Source source = new StreamSource(reader);
        SmlInterpretationExtractor extractor = new SmlInterpretationExtractor();
        javax.xml.transform.Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        return extractor;
    }

    /**
     * Notifies all listeners that a recognition result has been created.
     */
    private void postResultCreated() {
        SapiResult result = new SapiResult();
        result.setResultState(Result.UNFINALIZED);
        result.setConfidenceLevel(0);
        ResultEvent created = new ResultEvent(result,
                ResultEvent.RESULT_CREATED, false, false);
        postResultEvent(created);
    }

    /**
     * Notifies all listeners that a recognition result has been created.
     */
    private void postResultRejected() {
        SapiResult result = new SapiResult();
        postResultRejected(result);
    }

    /**
     * Notifies all listeners that a recognition result has been created.
     * 
     * @param result
     *            the current result.
     */
    private void postResultRejected(SapiResult result) {
        result.setResultState(Result.REJECTED);
        ResultEvent rejected = new ResultEvent(result,
                ResultEvent.RESULT_REJECTED, false, false);
        postResultEvent(rejected);
    }

    protected void postEngineException(EngineException exc) {
        long oldEngineState = getEngineState();
        setEngineState(~CLEAR_ALL_STATE, ERROR_OCCURRED);
        long newEngineState = getEngineState();

        RecognizerEvent event = new RecognizerEvent(this,
                EngineEvent.ENGINE_ERROR, oldEngineState, newEngineState, exc,
                null, RecognizerEvent.UNKNOWN_AUDIO_POSITION);
        postEngineEvent(event);
    }

    @Override
    protected void handleRequestFocus() {
    }

    @Override
    protected void handleReleaseFocus() {
    }

    native void sapiAbortRecognition(long handle);

    @Override
    protected AudioFormat getAudioFormat() {
        return sapiGetAudioFormat(recognizerHandle);
    }

    /**
     * Retrieves the default audio format.
     * 
     * @param handle
     *            recognizer handle.
     * @return native audio format
     */
    private native AudioFormat sapiGetAudioFormat(long handle);

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties, String propName,
            Object oldValue, Object newValue) {
        LOGGER.warning("changing property '" + propName + "' to '" + newValue
                + "' ignored");
    }
}
