/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 57 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package javax.speech.recognition;

import javax.speech.AudioSegment;

// Comp. 2.0.6

/**
 * Provides information about a Result that has been finalized -
 * that is, recognition is complete.
 * <p>
 * A finalized Result is a Result that has received a RESULT_ACCEPTED
 * or RESULT_REJECTED event that puts it in either the
 * ACCEPTED or REJECTED state as indicated by the getResultState method.
 * If any method of the FinalResult interface is called on a Result
 * in the UNFINALIZED state, a ResultException is thrown.
 * <p>
 * FinalResult provides
 * training/correction capabilities and access to audio data.
 * A FinalRuleResult additionally provides access to alternative results.
 * All three capabilities are optional because they are not all relevant
 * to all Results or all recognition environments,
 * and they are not universally supported by speech Recognizers.
 * <p>
 * Because speech recognizers are not always correct, applications need to
 * consider the possibility that a recognition error has occurred.
 * When an application detects an error (for example, a user updates a Result),
 * the application should inform the Recognizer so that it can learn from
 * the mistake and try to improve future performance.
 * The tokenCorrection method is provided for an application to provide feedback
 * from user correction to the Recognizer.
 * <p>
 * Sometimes, but certainly not always, the correct Result is selected by
 * a user from amongst the N-best alternatives for a Result obtained through
 * the FinalRuleResult interface. In other cases, a user may type the
 * correct Result or the application may infer
 * a correction from following user input.
 * <p>
 * Recognizers must store considerable information to support training
 * from Results. Applications need to be involved in the management
 * of that information so that it is not stored unnecessarily.
 * The isTrainingInfoAvailable method tests whether training information
 * is available for a finalized Result.
 * When an application/user has finished correction/training for a result
 * it should call releaseTrainingInfo to free up system resources.
 * Also, a Recognizer may choose at any time to free up training information.
 * In both cases, the application is notified of the release with a
 * TRAINING_INFO_RELEASED event to ResultListeners.
 * <p>
 * Audio data for a finalized Result is optionally provided by recognizers.
 * Audio data can be stored for future use by an application or user and in
 * certain circumstances can be provided by one Recognizer to another.
 * <p>
 * Since storing audio requires substantial system resources, audio
 * data requires special treatment.
 * If an application wants to use audio data, it should set the
 * setResultAudioProvided property of the RecognizerProperties to true.
 * <p>
 * Not all Recognizers provide access to audio data.
 * For those Recognizers, setResultAudioProvided has no effect,
 * isAudioAvailable always returns false, and the
 * getAudio methods always return null.
 * <p>
 * Recognizers that provide access to audio data cannot
 * always provide audio for every result.
 * Applications should test audio availability for every FinalResult and
 * should always test for null on the getAudio methods.
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.Result#getResultState()
 * @see javax.speech.recognition.Result#ACCEPTED
 * @see javax.speech.recognition.Result#REJECTED
 * @see javax.speech.recognition.Result#UNFINALIZED
 * @see javax.speech.recognition.ResultEvent
 * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
 * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
 * @see javax.speech.recognition.ResultEvent#TRAINING_INFO_RELEASED
 * @see javax.speech.recognition.ResultListener
 * @see javax.speech.recognition.FinalRuleResult
 * @see javax.speech.recognition.RecognizerProperties#setResultAudioProvided(boolean)
 */
public interface FinalResult extends Result {

    /**
     * Constant indicating that
     * the change is a correction of an error made by the Recognizer.
     */
    int MISRECOGNITION = 0x190;

    /**
     * Constant indicating that
     * the user has modified the text that was returned by the Recognizer
     * to something different from what they actually said.
     */
    int USER_CHANGE = 0x191;

    /**
     * Constant indicating that
     * the application does not know whether a change is
     * because of MISRECOGNITION or USER_CHANGE.
     */
    int DONT_KNOW = 0x192;

    /**
     * Gets the N-best token sequence for this Result.
     * <p>
     * The N-best value should be in the range of 0 to
     * (getNumberAlternatives()-1) inclusive.
     * <p>
     * If nBest == 0, the method returns the tokens for the best Result -
     * identical to the token sequence returned by the getBestTokens method
     * of the Result interface for the same object.
     * <p>
     * If nBest == 1 (or 2, 3...) the method returns the 1st (2nd, 3rd...)
     * alternative to the best Result.
     * <p>
     * The number of tokens for the best Result and the alternatives
     * need not be the same.
     * <p>
     * If the Result is in the ACCEPTED state (not rejected), then the best
     * Result and all the alternatives are accepted.
     * If the Result is in the REJECTED state (not accepted),
     * the Recognizer is not confident that the best Result or any of the
     * alternatives are what the user said.
     * @param nBest the N-best index
     * @return an array of ResultTokens
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @throws java.lang.IllegalArgumentException if nBest is not in range
     * @see javax.speech.recognition.FinalResult#getNumberAlternatives()
     * @see javax.speech.recognition.Result#ACCEPTED
     * @see javax.speech.recognition.Result#REJECTED
     */
    ResultToken[] getAlternativeTokens(int nBest) throws ResultStateException, IllegalArgumentException;

    /**
     * Gets the audio for the complete utterance of this Result.
     * <p>
     * Returns null if audio is not available or if it has been released.
     * @return the complete utterance audio if available or null
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.FinalResult#isAudioAvailable()
     * @see javax.speech.recognition.FinalResult#getAudio(javax.speech.recognition.ResultToken, javax.speech.recognition.ResultToken)
     */
    AudioSegment getAudio() throws ResultStateException;

    /**
     * Gets the audio for a token or sequence of tokens.
     * <p>
     * Recognizers make a best effort at determining
     * the start and end of tokens,
     * however, it is not unusual for chunks of surrounding audio to be included
     * or for the start or end token to be chopped.
     * <p>
     * Returns null if Result audio is not available or if it cannot be obtained
     * for the specified sequence of tokens.
     * <p>
     * If toToken is null or if fromToken and toToken are the same, the method
     * returns audio for fromToken. If both fromToken and toToken are null,
     * it returns the audio for the entire result (same as getAudio()).
     * <p>
     * Not all Recognizers can provide per-token audio, even if they can
     * provide audio for a complete utterance.
     * @param fromToken the beginning token for audio
     * @param toToken the ending token for audio
     * @return the specified audio if available or null
     * @throws java.lang.IllegalArgumentException one of the token parameters is
     *          not from this Result
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.FinalResult#isAudioAvailable()
     * @see javax.speech.recognition.FinalResult#getAudio()
     */
    AudioSegment getAudio(ResultToken fromToken, ResultToken toToken)
            throws ResultStateException, IllegalArgumentException;

    /**
     * Gets the confidence level for the best token sequence.
     * <p>
     * This indicates the Recognizer's confidence in this token sequence.
     * <p>
     * For an ACCEPTED result, the value should be at or above the current
     * confidence threshold as indicated in RecognizerProperties.
     * Values lie in the range between MIN_CONFIDENCE and MAX_CONFIDENCE.
     * A value of UNKNOWN_CONFIDENCE may be returned if the confidence is
     * unavailable or not supported
     * @return the confidence level for the best token sequence.
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.recognition.RecognizerProperties#setConfidenceThreshold(int)
     * @see javax.speech.recognition.RecognizerProperties#MIN_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#MAX_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#UNKNOWN_CONFIDENCE
     * @see javax.speech.recognition.Result#getResultState()
     * @see javax.speech.recognition.Result#ACCEPTED
     */
    int getConfidenceLevel() throws ResultStateException;

    /**
     * Gets the confidence level for the Nth best token sequence.
     * <p>
     * This indicates the Recognizer's confidence in this alternative.
     * <p>
     * For an ACCEPTED result, the value should be at or above the current
     * confidence threshold as indicated in RecognizerProperties.
     * Values lie in the range between MIN_CONFIDENCE and MAX_CONFIDENCE.
     * A value of UNKNOWN_CONFIDENCE may be returned if the confidence is
     * unavailable or not supported
     * <p>
     * The range for nBest is 0 to getNumberAlternatives()-1.
     * @param nBest the N-best index
     * @return the confidence level for this alternative.
     * @throws java.lang.IllegalArgumentException if nBest is not in range
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.recognition.RecognizerProperties#setConfidenceThreshold(int)
     * @see javax.speech.recognition.RecognizerProperties#MIN_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#MAX_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#UNKNOWN_CONFIDENCE
     * @see javax.speech.recognition.Result#getResultState()
     * @see javax.speech.recognition.FinalResult#getNumberAlternatives()
     * @see javax.speech.recognition.Result#ACCEPTED
     */
    int getConfidenceLevel(int nBest) throws ResultStateException, IllegalArgumentException;

    Grammar getGrammar(int nBest) throws ResultStateException, IllegalArgumentException;

    /**
     * Returns the number of alternatives for this Result.
     * <p>
     * The alternatives are numbered from 0 up.
     * The 0th alternative is the best result and provides the same
     * tokens as the getBestTokens method of the Result interface.
     * <p>
     * If only the best result is available (no alternatives) the
     * return value is 1.
     * If the result was rejected (REJECTED state), the return value may
     * be 0 if no tokens are available.
     * If the best Result plus alternatives are available, the return value is
     * greater than 1.
     * @return The number of alternatives for this Result.
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.Result#getBestTokens()
     * @see javax.speech.recognition.Result#REJECTED
     * @see javax.speech.recognition.FinalResult#getAlternativeTokens(int)
     */
    int getNumberAlternatives() throws ResultStateException;

    Object[] getTags(int nBest) throws ResultStateException, IllegalArgumentException, IllegalStateException;

    /**
     * Tests whether result audio data is available for this Result.
     * <p>
     * Result audio is only available if:
     * <p>
     * The ResultAudioProvided property of RecognizerProperties was set
     * to true when the Result was recognized.
     * The Recognizer was able to collect Result audio for this Result.
     * The Result audio has not yet been released.
     * <p>
     * The availability of audio for a Result does not mean that all getAudio
     * calls will return audio. For example, some recognizers might
     * provide audio data only for the entire Result, only for individual
     * tokens, or not for sequences of more than one token.
     * @return true if audio data is available for this Result
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.FinalResult#getAudio()
     * @see javax.speech.recognition.FinalResult#getAudio(javax.speech.recognition.ResultToken, javax.speech.recognition.ResultToken)
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.recognition.RecognizerProperties#setResultAudioProvided(boolean)
     */
    boolean isAudioAvailable() throws ResultStateException;

    /**
     * Returns true if the Recognizer has training information available
     * for this Result.
     * <p>
     * Training is available if the following conditions are met:
     * <p>
     * The "TrainingProvided" property of the RecognizerProperties
     * is set to true.
     * The training information for this Result has not been released
     * by the application or by the Recognizer.
     * This means that the TRAINING_INFO_RELEASED event has
     * not been issued.
     * <p>
     * Calls to tokenCorrection have no effect if
     * the training information is not available.
     * @return true if training information is available for this Result
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.recognition.RecognizerProperties#isTrainingProvided()
     * @see javax.speech.recognition.FinalResult#releaseTrainingInfo()
     * @see javax.speech.recognition.FinalResult#tokenCorrection(java.lang.String[], javax.speech.recognition.ResultToken, javax.speech.recognition.ResultToken, int)
     * @see javax.speech.recognition.ResultEvent#TRAINING_INFO_RELEASED
     */
    boolean isTrainingInfoAvailable() throws ResultStateException;

    /**
     * Releases the audio for this Result.
     * <p>
     * After audio is released, isAudioAvailable will return false.
     * This call is ignored if the audio is not available or
     * has already been released.
     * <p>
     * This method is asynchronous -
     * audio data is not necessarily released immediately.
     * An AUDIO_RELEASED event is issued to the ResultListener when the audio
     * is released by a call to this method.
     * An AUDIO_RELEASED event is also issued if the Recognizer releases
     * the audio for some other reason (for example, to reclaim memory).
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.ResultEvent#AUDIO_RELEASED
     * @see javax.speech.recognition.ResultListener
     */
    void releaseAudio() throws ResultStateException;

    /**
     * Releases the training information for this Result.
     * <p>
     * The release frees memory used for the training information --
     * this information can be substantial.
     * <p>
     * It is not an error to call the method when training information
     * is not available or has already been released.
     * <p>
     * This method is asynchronous - the training info is not necessarily
     * released when the call returns. A TRAINING_INFO_RELEASED event
     * is issued to the ResultListener once the information is released.
     * The TRAINING_INFO_RELEASED event is also issued if the Recognizer
     * releases the training information for any other reason
     * (for example, to reclaim memory).
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @see javax.speech.recognition.ResultEvent#TRAINING_INFO_RELEASED
     * @see javax.speech.recognition.ResultListener
     */
    void releaseTrainingInfo() throws ResultStateException;

    /**
     * Informs the Recognizer of a correction to one or more tokens in a
     * finalized Result so that the Recognizer can re-train itself.
     * <p>
     * Training the Recognizer from its mistakes allows it to improve
     * its performance and accuracy in future recognition.
     * <p>
     * Correction improvements apply to the Recognizer instance,
     * but may persist longer.
     * If the Recognizer uses a SpeakerManager,
     * improvements may only apply to the current speaker.
     * <p>
     * The fromToken and toToken parameters indicate the inclusive sequence
     * of tokens that are being trained or corrected.
     * If toToken is null or if fromToken and toToken are the same,
     * the training applies to a single recognized token.
     * <p>
     * The correctTokens token sequence may have the same or different
     * length than the token sequence being corrected.
     * Setting correctTokens
     * to null indicates the deletion of tokens.
     * <p>
     * The correctionType parameter must be one of
     * MISRECOGNITION, USER_CHANGE, DONT_KNOW.
     * <p>
     * Note: tokenCorrection does not change the result object.
     * So, future calls to the getBestToken,
     * getBestTokens and getAlternativeTokens
     * method return exactly the same values as before
     * the call to tokenCorrection.
     * @param correctTokens sequence of correct tokens to
     *         replace fromToken to toToken
     * @param fromToken first token in the sequence being corrected
     * @param toToken last token in the sequence being corrected
     * @param correctionType type of correction
     * @throws java.lang.IllegalArgumentException either token is not from
     *          this FinalResult
     * @throws javax.speech.recognition.ResultStateException if called before a Result is finalized
     * @throws java.lang.SecurityException if token correction is not allowed for the
     *          application
     * @see javax.speech.recognition.FinalResult#MISRECOGNITION
     * @see javax.speech.recognition.FinalResult#USER_CHANGE
     * @see javax.speech.recognition.FinalResult#DONT_KNOW
     * @see javax.speech.recognition.Result#getBestTokens()
     * @see javax.speech.recognition.SpeakerManager
     */
    void tokenCorrection(String[] correctTokens, ResultToken fromToken,
                         ResultToken toToken, int correctionType)
            throws ResultStateException, IllegalArgumentException, SecurityException;
}
