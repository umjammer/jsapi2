/**
 * JSAPI - An independent reference implementation of JSR 113.
 * <p>
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jvoicexml.jsapi2.recognition;

import java.io.Serial;
import java.io.Serializable;
import java.lang.System.Logger.Level;
import java.util.Collection;
import java.util.StringTokenizer;
import javax.speech.AudioSegment;
import javax.speech.SpeechEventExecutor;
import javax.speech.recognition.FinalResult;
import javax.speech.recognition.FinalRuleResult;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RecognizerProperties;
import javax.speech.recognition.Result;
import javax.speech.recognition.ResultEvent;
import javax.speech.recognition.ResultListener;
import javax.speech.recognition.ResultStateException;
import javax.speech.recognition.ResultToken;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleCount;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.RuleReference;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;

import static java.lang.System.getLogger;


/**
 * Very simple implementation of JSAPI Result, FinalResult, FinalRuleResult, and
 * FinalDictationResult.
 * <p>
 * Ignores many things like N-Best, partial-results, etc.
 */
public class BaseResult implements Result, FinalResult, FinalRuleResult, Serializable, Cloneable {

    private static final System.Logger logger = getLogger(BaseResult.class.getName());
    
    /** The serial version UID. */
    @Serial
    private static final long serialVersionUID = -7742652622067884474L;

    /** Registered result listeners. */
    private Collection<ResultListener> resultListeners;
    private ResultToken[] tokens;
    private int nTokens;
    /** The rule grammar that matches this result. */
    private transient Grammar grammar;
    /** Name of the rule within the grammar that matches this result. */
    private String ruleName;
    /** The result state. */
    private int state;
    /** The confidence level. */
    private int confidenceLevel;

    protected Object[] tags;

    /**
     * Create an empty result.
     */
    public BaseResult() {
        this(null);
    }

    /**
     * Create an empty result.
     *
     * @param gram the grammar
     */
    public BaseResult(Grammar gram) {
        resultListeners = new java.util.ArrayList<>();
        grammar = gram;
        confidenceLevel = RecognizerProperties.UNKNOWN_CONFIDENCE;
        state = Result.UNFINALIZED;
    }

    /**
     * Create a result with a result string.
     *
     * @param gram   the grammar
     * @param result the result string
     * @throws GrammarException error evaluating the grammar
     */
    public BaseResult(Grammar gram, String result) throws GrammarException {
        resultListeners = new java.util.ArrayList<>();
        grammar = gram;
        confidenceLevel = RecognizerProperties.UNKNOWN_CONFIDENCE;
        setResult(result);
    }

    /**
     * Simple method to set the result as a string.
     *
     * @param result the result
     * @throws GrammarException error parsing the result against the grammar
     */
    public void setResult(String result) throws GrammarException {
        if (result == null) {
            state = Result.UNFINALIZED;
        } else {
            state = Result.REJECTED;
            boolean success = tryTokens(grammar, result);
            if (success) {
                state = Result.ACCEPTED;
            }
        }
    }

    /**
     * Copy a result. If the result to be copied is a BaseResult then clone it
     * otherwise create a BaseResult and copy the tokens onto it.
     *
     * @param result the result to copy to
     * @return copied result
     * @throws GrammarException if the related grammar could not be evaluated
     */
    static BaseResult copyResult(Result result) throws GrammarException {
        BaseResult copy = null;
        if (result instanceof BaseResult) {
            try {
                copy = (BaseResult) ((BaseResult) result).clone();
            } catch (CloneNotSupportedException e) {
                logger.log(Level.WARNING, "ERROR: " + e);
            }
            return copy;
        } else {
            copy = new BaseResult(result.getGrammar());
            copy.nTokens = result.getNumTokens();
            copy.tokens = new ResultToken[copy.nTokens];
            for (int i = 0; i < result.getNumTokens(); i++) {
                ResultToken sourceToken = result.getBestToken(i);
                BaseResultToken destinationToken = new BaseResultToken(copy, sourceToken.getText());
                destinationToken.setConfidenceLevel(sourceToken.getConfidenceLevel());
                destinationToken.setStartTime(sourceToken.getStartTime());
                destinationToken.setEndTime(sourceToken.getEndTime());
                copy.tokens[i] = destinationToken;
            }
            return copy;
        }
    }

    /**
     * Return the current state of the Result object.
     */
    @Override
    public int getResultState() {
        return state;
    }

    /**
     * Retrieves a human readable representation of the state.
     *
     * @return state as a string
     */
    private String getResultStateAsString() {
        if (state == ACCEPTED) {
            return "ACCEPTED";
        } else if (state == REJECTED) {
            return "REJECTED";
        } else if (state == UNFINALIZED) {
            return "UNFINALIZED";
        } else {
            return Integer.toString(state);
        }
    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    @Override
    public int getNumTokens() {
        return nTokens;
    }

    @Override
    public ResultToken getBestToken(int nth) throws IllegalArgumentException {
        if ((nth < 0) || (nth > (nTokens - 1))) {
            throw new IllegalArgumentException("Token index out of range.");
        }
        return tokens[nth];
    }

    @Override
    public ResultToken[] getBestTokens() {
        return tokens;
    }

    @Override
    public ResultToken[] getUnfinalizedTokens() {
        if (getResultState() == Result.ACCEPTED || getResultState() == Result.REJECTED) {
            return new ResultToken[0];
        }
        int numUnfinalizedTokens = getBestTokens().length - getNumTokens();

        ResultToken[] unfinalizedTokens = new ResultToken[numUnfinalizedTokens];

        for (int i = 0; i < numUnfinalizedTokens; ++i) {
            unfinalizedTokens[i] = getBestTokens()[i + getNumTokens()];
        }
        return unfinalizedTokens;
    }

    @Override
    public void addResultListener(ResultListener listener) {
        if (!resultListeners.contains(listener)) {
            resultListeners.add(listener);
        }
    }

    @Override
    public void removeResultListener(ResultListener listener) {
        resultListeners.remove(listener);
    }

    //
    // End Result Methods
    //

    //
    // Begin FinalResult Methods
    //

    @Override
    public boolean isTrainingInfoAvailable() throws ResultStateException {
        validateResultState(UNFINALIZED);
        return false;
    }

    @Override
    public void releaseTrainingInfo() throws ResultStateException {
        validateResultState(UNFINALIZED);
    }

    @Override
    public void tokenCorrection(String[] correctTokens, ResultToken fromToken, ResultToken toToken, int correctionType) throws ResultStateException, IllegalArgumentException {
        validateResultState(UNFINALIZED);
    }

    @Override
    public boolean isAudioAvailable() throws ResultStateException {
        validateResultState(UNFINALIZED);
        return false;
    }

    @Override
    public void releaseAudio() throws ResultStateException {
        validateResultState(UNFINALIZED);
    }

    @Override
    public AudioSegment getAudio() throws ResultStateException {
        validateResultState(UNFINALIZED);
        return null;
    }

    @Override
    public AudioSegment getAudio(ResultToken from, ResultToken to) throws ResultStateException {
        validateResultState(UNFINALIZED);
        return null;
    }

    //
    // End FinalResult Methods
    //

    //
    // Begin FinalRuleResult Methods
    //

    @Override
    public int getNumberAlternatives() throws ResultStateException {
        validateResultState(UNFINALIZED);
        return 1;
    }

    @Override
    public ResultToken[] getAlternativeTokens(int nBest) throws ResultStateException {
        validateResultState(UNFINALIZED);
        if (!(grammar instanceof RuleGrammar)) {
            throw new ResultStateException("Result is not a FinalRuleResult");
        }
        if (nBest == 0) {
            return getBestTokens();
        }
        // WDW - throw InvalidArgumentException?
        return null;
    }

    @Override
    public Grammar getGrammar(int nBest) throws ResultStateException {
        validateResultState(UNFINALIZED);
        if (!(grammar instanceof RuleGrammar)) {
            throw new ResultStateException("Result is not a FinalRuleResult");
        }
        if (nBest == 0) {
            return grammar;
        }
        // WDW - throw InvalidArgumentException?
        return null;
    }

    @Override
    public Object[] getTags(int nBest) throws IllegalArgumentException, ResultStateException {
        validateResultState(UNFINALIZED);
        if (!(grammar instanceof RuleGrammar)) {
            throw new ResultStateException("Result is not a FinalRuleResult");
        }
        return tags;
    }

    @Override
    public RuleReference getRuleReference(int nBest) throws ResultStateException, IllegalArgumentException, IllegalStateException {
        throw new RuntimeException("BaseResult.getRuleReference NOT IMPLEMENTED");
    }

    //
    // End FinalRuleResult Methods
    //

    /**
     * Utility function to generate result event and post it to the event queue.
     * Eventually fireAudioReleased will be called by dispatchSpeechEvent as a
     * result of this action.
     *
     * @param speechEventExecutor SpeechEventExecutor
     * @param event               ResultEvent
     */
    public void postResultEvent(SpeechEventExecutor speechEventExecutor, ResultEvent event) {
        try {
            speechEventExecutor.execute(() -> {
                if (logger.isLoggable(Level.DEBUG)) {
                    logger.log(Level.DEBUG, "notifying event " + event);
                }
                fireResultEvent(event);
            });
        } catch (RuntimeException ex) {
            logger.log(Level.WARNING, ex.getLocalizedMessage());
        }
    }

    /**
     * Utility function to send a result event to all result listeners.
     *
     * @param resultEvent the event to sned
     */
    public void fireResultEvent(ResultEvent resultEvent) {
        if (resultListeners != null) {
            for (ResultListener listener : resultListeners) {
                listener.resultUpdate(resultEvent);
            }
        }
    }

    /**
     * Utility function to set the number of finalized tokens in the Result.
     *
     * @param n int
     */
    public void setNumTokens(int n) {
        nTokens = n;
    }

    /**
     * Simple implementation of tags.
     * <p>
     * ATTENTION: This method changes the ResultToken content.
     * <p>
     * This implementation replaces each token text by tag information, if it
     * exists.
     * </p>
     * <p>
     * TODO the tag information can not be only text! It can be anything.
     *
     * @param token     the result tokens
     * @param component the rule component that will be analyzed
     * @param pos       the position in rt of next token that will be appeared in the graph
     * @return int
     */
    private int applyTags(ResultToken[] token, RuleComponent component, int pos) {
        if (component instanceof RuleReference) {
            return pos;
        } else if (component instanceof RuleToken) {
            return pos + 1;
        } else if (component instanceof RuleAlternatives) {
            return applyTags(token, ((RuleAlternatives) component).getRuleComponents()[0], pos);
        } else if (component instanceof RuleSequence) {
            for (RuleComponent r : ((RuleSequence) component).getRuleComponents()) {
                pos = applyTags(token, r, pos);
            }
            return pos;
        } else if (component instanceof RuleTag) {
            String tag = (String) ((RuleTag) component).getTag();

            // assumes that ruleTag component appears after RuleToken component
            token[pos - 1] = new BaseResultToken(token[pos - 1].getResult(), tag);
            return pos;
        } else if (component instanceof RuleCount) {
            return applyTags(token, ((RuleCount) component).getRuleComponent(), pos);
        }

        return pos;
    }

    /**
     * Utility function to set the resultTokens.
     *
     * @param rt the tokens
     */
    public void setTokens(ResultToken[] rt) {
        setTokens(rt, false);
    }

    /**
     * Utility function to set the resultTokens. Does nothing if no tokens are
     * provided.
     *
     * @param rt          the tokens
     * @param replaceTags if true, tokens must be replaced by tags content.
     */
    public void setTokens(ResultToken[] rt, boolean replaceTags) {
        tokens = new ResultToken[rt.length];
        System.arraycopy(rt, 0, tokens, 0, rt.length);
        if (replaceTags) {
            RuleParse ruleParse = parse(0);
            if (ruleParse != null) {
                applyTags(tokens, ruleParse.getParse(), 0);
            }
        }
    }

    /**
     * Concatenate the best tokens in the Result.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        if (nTokens == 0) {
            // TODO change this. Is it possible a result has 0 tokens??
            return super.toString();
        }

        StringBuilder sb = new StringBuilder(getBestToken(0).getText());
        for (int i = 1; i < getNumTokens(); i++) {
            sb.append(" ").append(getBestToken(i).getText());
        }
        return sb.toString();
    }

    /**
     * Utility function to set the result state.
     *
     * @param resultState the new state
     */
    public void setResultState(int resultState) {
        this.state = resultState;
    }

    /**
     * Validates that the result is in the given state.
     *
     * @param resultState the state to check for
     * @throws ResultStateException if the result is not in the given state.
     */
    protected void validateResultState(int resultState) throws ResultStateException {
        if (getResultState() == resultState) {
            String str = getResultStateAsString();
            throw new ResultStateException("Invalid result state: " + str);
        }
    }

    /**
     * Set the grammar that goes with this Result. NOT JSAPI.
     *
     * @param gram the grammar
     */
    public void setGrammar(Grammar gram) {
        grammar = gram;
    }

    /**
     * Try to set the Grammar and tokens of this result.
     *
     * @param gram   the related grammar
     * @param result the retrieved recognition result
     * @return {@code true} if the grammar matches the tokens.
     * @throws GrammarException error parsing the grammar
     */
    public boolean tryTokens(Grammar gram, String result) throws GrammarException {
        if ((result == null) || (gram == null)) {
            return false;
        }

        if (gram instanceof RuleGrammar rule) {
            return tryTokens(rule, result);
        }
        return false;
    }

    /**
     * Try to set the Grammar and tokens of this result.
     *
     * @param ruleGrammar the related grammar
     * @param result      the retrieved recognition result
     * @return {@code true} if the grammar matches the tokens.
     * @throws GrammarException error parsing the grammar
     */
    private boolean tryTokens(RuleGrammar ruleGrammar, String result) throws GrammarException {
        RuleParse parse = ruleGrammar.parse(result, null);
        if (parse == null) {
            return false;
        }

        // Adapt tags and rule names
        tags = parse.getTags();
        ruleName = parse.getRuleReference().getRuleName();

        // Copy the result tokens
        StringTokenizer tokenizer = new StringTokenizer(result);
        nTokens = tokenizer.countTokens();
        int i = 0;
        tokens = new ResultToken[nTokens];
        while (tokenizer.hasMoreTokens()) {
            // TODO information about startTime, endTime and
            // confidenceLevel
            tokens[i] = new BaseResultToken(this, tokenizer.nextToken());
            ++i;
        }
        return true;
    }

    @Override
    public RuleParse parse(int nBest) throws IllegalArgumentException, ResultStateException {
        ResultToken[] rt = getAlternativeTokens(0);
        String[] tokens = new String[rt.length];
        for (int i = 0; i < rt.length; ++i) {
            tokens[i] = rt[i].getText();
        }

        try {
            return ((RuleGrammar) getGrammar()).parse(tokens, ((BaseRuleGrammar) getGrammar()).getRoot());
        } catch (GrammarException e) {
            logger.log(System.Logger.Level.ERROR, e.getMessage(), e);
        }
        return null;
    }

    public void setConfidenceLevel(int confidenceLevel) {
        if (confidenceLevel < RecognizerProperties.MIN_CONFIDENCE || confidenceLevel > RecognizerProperties.MAX_CONFIDENCE)
            throw new IllegalArgumentException("Invalid confidenceThreshold: " + confidenceLevel);
        this.confidenceLevel = confidenceLevel;
    }

    @Override
    public int getConfidenceLevel() throws ResultStateException {
        return getConfidenceLevel(0);
    }

    @Override
    public int getConfidenceLevel(int nBest) throws IllegalArgumentException, ResultStateException {
        // uncommented - see JSAPI2/FinalResult#getConfidenceLevel
        // quote: "For a REJECTED result, a useful confidence level
        // MAY be returned, but this is application
        // and platform dependent."
        if (state != FinalResult.ACCEPTED && state != FinalResult.REJECTED && state != FinalResult.DONT_KNOW && state != FinalResult.MISRECOGNITION && state != FinalResult.USER_CHANGE)
            // above code could simply check for FinalResult.UNFINALIZED
            // but this would be more error prone
            // (e.g. if the ResultState was not correctly set to UNFINALIZED)
            throw new ResultStateException();

        if (nBest < 0 || nBest >= getNumberAlternatives()) {
            throw new IllegalArgumentException("nBest out of valid range!");
        }

        return confidenceLevel;
    }
}
