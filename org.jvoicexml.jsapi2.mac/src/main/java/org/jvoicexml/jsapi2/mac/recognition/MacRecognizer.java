package org.jvoicexml.jsapi2.mac.recognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger.Level;
import java.util.Collection;
import java.lang.System.Logger;
import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.recognition.BaseRecognizer;
import org.jvoicexml.jsapi2.recognition.BaseResult;
import org.jvoicexml.jsapi2.recognition.GrammarDefinition;


/**
 * A SAPI recognizer.
 *
 * @author Dirk Schnelle-Walka
 */
public final class MacRecognizer extends BaseRecognizer {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(MacRecognizer.class.getName());

    /** SAPI recognizer Handle. **/
    private long recognizerHandle;

    /**
     * Constructs a new object.
     *
     * @param mode the recognizer mode.
     */
    public MacRecognizer(MacRecognizerMode mode) {
        super(mode);
    }

    @Override
    public Collection<Grammar> getBuiltInGrammars() {
        return macGetBuildInGrammars(recognizerHandle);
    }

    private Collection<Grammar> macGetBuildInGrammars(long handle){
        logger.log(Level.DEBUG, "macGetBuildInGrammars");
        return null;
    }

    @Override
    public void handleAllocate() throws EngineStateException, EngineException, AudioException, SecurityException {
        recognizerHandle = macAllocate();
    }

    private long macAllocate() {
        logger.log(Level.DEBUG, "macAllocate");
        return 0L;
    }

    @Override
    public void handleDeallocate() {
        macDeallocate(recognizerHandle);
    }

    private void macDeallocate(long handle) {
        logger.log(Level.DEBUG, "macDeallocate");
    }
    @Override
    protected void handlePause() {
        macPause(recognizerHandle);
    }

    private void macPause(long handle) {
        logger.log(Level.DEBUG, "macPause");
    }
    @Override
    protected void handlePause(int flags) {
        macPause(recognizerHandle, flags);
    }

    private void macPause(long handle, int flags) {
        logger.log(Level.DEBUG, "macPause");
    }

    @Override
    protected boolean handleResume(InputStream in) throws EngineStateException {
        GrammarManager manager = getGrammarManager();
        Grammar[] grammars = manager.listGrammars();
        String[] grammarSources = new String[grammars.length];
        int i = 0;
        for (Grammar grammar : grammars) {
            try {
                File file = File.createTempFile("sapi", "xml");
                file.deleteOnExit();
                FileOutputStream out = new FileOutputStream(file);

                StringBuilder xml = new StringBuilder();
                xml.append(grammar.toString());
                int index = xml.indexOf("06/grammar");
                xml.insert(index + 11, " xml:lang=\"de-DE\" ");
                out.write(xml.toString().getBytes());
                out.close();
                grammarSources[i] = file.getCanonicalPath();
//                logger.log(Level.DEBUG, xml);
//                logger.log(Level.DEBUG, grammarSources[i]);

            } catch (IOException e) {
                logger.log(Level.ERROR, e.getMessage(), e);
            }
            ++i;
        }
        return macResume(recognizerHandle, grammarSources);
    }

    private boolean macResume(long handle, String[] grammars) throws EngineStateException {
        logger.log(Level.DEBUG, "macResume");
        return false;
    }

    @Override
    protected boolean setGrammars(Collection<GrammarDefinition> grammarDefinition) {
        return false;
    }

    public boolean setGrammar(String grammarPath) {
        return macSetGrammar(recognizerHandle, grammarPath);
    }

    private boolean macSetGrammar(long handle, String grammarPath) {
        logger.log(Level.DEBUG, "macSetGrammar");
        return false;
    }

    void startRecognition() {
        start(recognizerHandle);
    }

    private void start(long handle) {
        logger.log(Level.DEBUG, "start");
    }

    /**
     * Notification from the SAPI recognizer about a recognition result.
     *
     * @param utterance the detected utterance
     */
    @SuppressWarnings("unused")
    private void reportResult(String utterance) {

        System.out.println("Java Code " + utterance);

        RuleGrammar grammar = currentGrammar; // current grammar is not
        // available
        System.out.println(grammar);

        BaseResult result;
        try {
            result = new BaseResult(grammar, utterance);
        } catch (GrammarException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        ResultEvent created = new ResultEvent(result, ResultEvent.RESULT_CREATED, false, false);
        postResultEvent(created);

        ResultEvent grammarFinalized = new ResultEvent(result, ResultEvent.GRAMMAR_FINALIZED);
        postResultEvent(grammarFinalized);

        if (result.getResultState() == Result.REJECTED) {
            ResultEvent rejected = new ResultEvent(result, ResultEvent.RESULT_REJECTED, false, false);
            postResultEvent(rejected);
        } else {
            ResultEvent accepted = new ResultEvent(result, ResultEvent.RESULT_ACCEPTED, false, false);
            postResultEvent(accepted);
        }
    }

    @Override
    protected void handleReleaseFocus() {
    }

    @Override
    protected void handleRequestFocus() {
    }

    @Override
    protected AudioFormat getAudioFormat() {
        return new AudioFormat(16000, 2, 1, true, false);
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue,
            Object newValue) {
logger.log(Level.WARNING, "changing property '" + propName + "' to '" + newValue + "' ignored");
    }
}
