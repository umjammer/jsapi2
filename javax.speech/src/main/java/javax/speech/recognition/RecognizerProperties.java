/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 62 $
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

import javax.speech.EngineProperties;
import javax.speech.EnginePropertyListener;

// Comp 2.0.6

/**
 * Enables control of the properties of a Recognizer.
 * <p>
 * The RecognizerProperties object is obtained by calling the
 * getRecognizerProperties method of the Recognizer.
 * <p>
 * RecognizerProperties inherits the following behavior from the
 * EngineProperties interface
 * (described in detail in the EngineProperties documentation):
 * <p>
 * Each property has a get and set method. (JavaBeans property method patterns)
 * <p>
 * Calls to set methods may be asynchronous (they may return before the
 * property change takes effect).
 * The Engine will apply a change as soon as possible.
 * <p>
 * A PropertyChangeListener may be attached to receive PropertyChangeEvents.
 * <p>
 * Engines may ignore changes to properties or apply maximum and minimum limits.
 * If an engine does not apply a property change request,
 * this is reflected in the corresponding PropertyChangeEvent.
 * <p>
 * The get methods return the current setting - not a pending value.
 * <p>
 * Where appropriate, property settings are persistent across sessions.
 * <p>
 * For recognizers that maintain speaker data (recognizers that implement
 * the SpeakerManager interface) the RecognizerProperties should be stored
 * and loaded as part of the speaker data. Thus, when the current speaker
 * is changed through the SpeakerManager interface, the properties of
 * the new speaker should be loaded.
 * @see javax.speech.recognition.Recognizer#getRecognizerProperties()
 * @see javax.speech.EngineProperties
 * @see javax.speech.recognition.SpeakerManager
 */
public interface RecognizerProperties extends EngineProperties {

    /**
     * A value signifying that the confidence value is unknown.
     */
    int UNKNOWN_CONFIDENCE = -1;

    /**
     * A value signifying that minimum confidence is required for results.
     */
    int MIN_CONFIDENCE = 0;

    /**
     * A value signifying that normal confidence is required for results.
     */
    int NORM_CONFIDENCE = 5;

    /**
     * A value signifying that maximum confidence is required for results.
     */
    int MAX_CONFIDENCE = 10;

    /**
     * A value signifying minimum sensitivity to noise during recognition.
     */
    int MIN_SENSITIVITY = 0;

    /**
     * A value signifying normal sensitivity to noise during recognition.
     */
    int NORM_SENSITIVITY = 5;

    /**
     * A value signifying maximum sensitivity to noise during recognition.
     */
    int MAX_SENSITIVITY = 10;

    /**
     * A value signifying minimum accuracy vs speed during recognition.
     */
    int MIN_ACCURACY = 0;

    /**
     * A value signifying normal accuracy vs speed during recognition.
     */
    int NORM_ACCURACY = 5;

    /**
     * A value signifying maximum accuracy vs speed during recognition.
     */
    int MAX_ACCURACY = 10;

    int ADAPT_PAUSED = 1;

    int ADAPT_RESUMED = 2;

    int ENDPOINT_AUTOMATIC = 0x01;

    int ENDPOINT_MANUAL = 0x02;

    int ENDPOINT_SPEECH_DETECTION = 0x04;

    int ENDPOINT_PUSH_TO_TALK = 0x08;

    int ENDPOINT_PUSH_TO_START = 0x10;

    void setAdaptation(int adapt) throws IllegalArgumentException;

    int getAdaptation();

    /**
     * Sets the "CompleteTimeout" property in milliseconds.
     * <p>
     * This timeout value determines the length of silence required following
     * user speech before the Recognizer finalizes a Result
     * (with a RESULT_ACCEPTED or RESULT_REJECTED event).
     * The complete timeout is used when the speech is a complete match to an
     * active RuleGrammar. By contrast, the incomplete timeout is used when
     * the speech is an incomplete match to an active Grammar.
     * <p>
     * A long complete timeout value delays the result completion and
     * therefore makes the computer's response slow. A short complete
     * timeout may lead to an utterance being broken up inappropriately.
     * Reasonable complete timeout values are typically in the range of
     * 300 milliseconds (0.3 seconds) to 1000 milliseconds (1.0 seconds),
     * depending on the application.
     * <p>
     * The default value of this property is implementation dependent.
     * Applications should set this value for interaction appropriate for
     * the task.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param timeout the timeout value in milliseconds
     * @see javax.speech.recognition.RecognizerProperties#getCompleteTimeout()
     * @see javax.speech.recognition.RecognizerProperties#setIncompleteTimeout(int)
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     */
    void setCompleteTimeout(int timeout) throws IllegalArgumentException;

    /**
     * Gets the "CompleteTimeout" property.
     * @return a timeout value in milliseconds
     * @see javax.speech.recognition.RecognizerProperties#setCompleteTimeout(int)
     */
    int getCompleteTimeout();

    /**
     * Sets the "ConfidenceThreshold" property.
     * <p>
     * The confidence threshold value can vary between
     * MIN_CONFIDENCE and MAX_CONFIDENCE.
     * A value of NORM_CONFIDENCE is the default for the Recognizer.
     * A value of MAX_CONFIDENCE requires the Recognizer to
     * have maximum confidence in its results or otherwise reject them.
     * A value of MIN_CONFIDENCE requires only low confidence so
     * fewer results are rejected.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param confidenceThreshold a value specifying the confidence threshold
     * @see javax.speech.recognition.RecognizerProperties#getConfidenceThreshold()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.recognition.RecognizerProperties#MIN_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#NORM_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#MAX_CONFIDENCE
     */
    void setConfidenceThreshold(int confidenceThreshold) throws IllegalArgumentException;

    /**
     * Gets the "ConfidenceThreshold" property.
     * @return an integer confidence threshold
     * @see javax.speech.recognition.RecognizerProperties#setConfidenceThreshold(int)
     */
    int getConfidenceThreshold();

    void setEndpointStyle(int endpointStyle) throws IllegalArgumentException;

    int getEndpointStyle();

    /**
     * Sets the "IncompleteTimeout" property in milliseconds.
     * <p>
     * The timeout value determines the required length of silence
     * following user speech after which a recognizer finalizes a result.
     * <p>
     * The incomplete timeout applies when the speech prior to the silence
     * is an incomplete match of the active RuleGrammars.
     * In this case, once the timeout is triggered, the partial result
     * is rejected (with a RESULT_REJECTED event).
     * <p>
     * The incomplete timeout also applies when the speech prior
     * to the silence is a complete match of an active grammar,
     * but where it is possible to speak further and still match the grammar.
     * For example, in a grammar for digit sequences for telephone numbers
     * it might be legal to speak either 7 or 10 digits.
     * If the user pauses briefly after speaking 7 digits then the
     * incomplete timeout applies because the user may then continue with
     * a further 3 digits.
     * <p>
     * By contrast, the complete timeout is used when the speech is a complete
     * match to an active RuleGrammar and no further words can be spoken.
     * <p>
     * A long incomplete timeout value delays the result completion and
     * therefore makes the computer's response slow.
     * A short incomplete timeout may
     * lead to an utterance being broken up inappropriately.
     * <p>
     * The incomplete timeout is usually longer than the complete timeout
     * to allow users to pause mid-utterance (for example, to breathe).
     * <p>
     * The default value of this property is implementation dependent.
     * Applications should set this value for interaction appropriate for
     * the task.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param timeout the timeout value in milliseconds
     * @see javax.speech.recognition.RecognizerProperties#getIncompleteTimeout()
     * @see javax.speech.recognition.RecognizerProperties#setCompleteTimeout(int)
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     */
    void setIncompleteTimeout(int timeout) throws IllegalArgumentException;

    /**
     * Gets the "IncompleteTimeout" property.
     * @return a timeout value in milliseconds
     * @see javax.speech.recognition.RecognizerProperties#setIncompleteTimeout(int)
     */
    int getIncompleteTimeout();

    /**
     * Sets the "NumResultAlternatives" property.
     * <p>
     * This property indicates the preferred maximum number of
     * N-best alternatives in FinalRuleResult objects.
     * A value of 1 indicates that the application wants only the best result.
     * A value of 0 indicates that the application wants all
     * available alternatives.
     * <p>
     * Recognizers are not required to provide this number of alternatives
     * for every result and the number of alternatives may
     * vary from result to result.
     * Recognizers should only provide alternative tokens which are considered
     * reasonable: that is, the alternatives should be above
     * the ConfidenceThreshold
     * set through this interface (unless the Result is rejected).
     * <p>
     * Providing alternatives requires additional computing power.
     * Applications should only request the number of alternatives that they
     * are likely to use.
     * <p>
     * The default value of this property is 1.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param num value of NumResultAlternatives property
     * @see javax.speech.recognition.RecognizerProperties#getNumResultAlternatives()
     * @see javax.speech.recognition.FinalResult#getAlternativeTokens(int)
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setNumResultAlternatives(int num) throws IllegalArgumentException;

    /**
     * Gets the "NumResultAlternatives" property.
     * @return the number of alternatives
     * @see javax.speech.recognition.RecognizerProperties#setNumResultAlternatives(int)
     */
    int getNumResultAlternatives();

    /**
     * Sets the "Priority" property.
     * <p>
     * Priorities help determine the order in which Recognizer instances interact.
     * For example, focus gain and loss may be affected by priority.
     * A trusted application could use a higher priority to avoid losing focus
     * during a user's answer to an important question.
     * <p>
     * Priorities are values between MIN_PRIORITY and MAX_PRIORITY.
     * Trusted applications may use the full range of priorities.
     * Untrusted applications have a maximum of MAX_UNTRUSTED_PRIORITY,
     * which is less than or equal to MAX_PRIORITY.
     * <p>
     * The default priorities for trusted and untrusted applications are
     * NORM_TRUSTED_PRIORITY and NORM_UNTRUSTED_PRIORITY, respectively.
     * <p>
     * Priority should be used with care to avoid starvation of other
     * applications.
     * <p>
     * Changing the priority of an Engine instance may cause it to
     * gain or lose focus.
     * Changing the priority may be asynchronous, so any resulting
     * focus change may not occur immediately.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param priority the priority for this engine
     * @see javax.speech.recognition.RecognizerProperties#getPriority()
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.EngineProperties#MIN_PRIORITY
     * @see javax.speech.EngineProperties#NORM_TRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#NORM_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_PRIORITY
     */
    void setPriority(int priority) throws IllegalArgumentException;

    /**
     * Gets the "Priority" property.
     * @return the priority value
     * @see javax.speech.recognition.RecognizerProperties#setPriority(int)
     */
    int getPriority();

    /**
     * Gets the "Sensitivity" property.
     * @return the sensitivity value
     * @see javax.speech.recognition.RecognizerProperties#setSensitivity(int)
     */
    int getSensitivity();

    /**
     * Sets the "Sensitivity" property.
     * <p>
     * The sensitivity can vary between MIN_SENSITIVITY and MAX_SENSITIVITY.
     * A value of NORM_SENSITIVITY is the default for the recognizer.
     * A value of MAX_SENSITIVITY gives maximum sensitivity and makes
     * the recognizer sensitive to quiet input but more sensitive to noise.
     * A value of MIN_SENSITIVITY gives minimum sensitivity requiring the
     * user to speak
     * loudly and making the recognizer less sensitive to background noise.
     * <p>
     * Note: some Recognizers set the gain automatically during use,
     * or through a setup "Wizard". On these Recognizers the sensitivity
     * adjustment should be used only in extreme cases where the automatic
     * settings are not adequate.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param sensitivity the sensitivity value
     * @see javax.speech.recognition.RecognizerProperties#getSensitivity()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.recognition.RecognizerProperties#MIN_SENSITIVITY
     * @see javax.speech.recognition.RecognizerProperties#NORM_SENSITIVITY
     * @see javax.speech.recognition.RecognizerProperties#MAX_SENSITIVITY
     */
    void setSensitivity(int sensitivity) throws IllegalArgumentException;

    /**
     * Sets the "SpeedVsAccuracy" property.
     * <p>
     * A value of NORM_ACCURACY is the default compromise between speed and
     * accuracy for the recognizer.
     * A value of MIN_ACCURACY minimizes response time.
     * A value of MAX_ACCURACY maximizes recognition accuracy.
     * <p>
     * Why are speed and accuracy a trade-off?
     * A recognizer determines what a user says by testing different possible
     * sequences of words
     * (with legal sequences being defined by the active grammars).
     * If the recognizer tests more sequences it is more likely to find the
     * correct sequence, but there is additional processing so it is slower.
     * Increasing Grammar complexity and decreasing the computer power both
     * make this trade-off more important.
     * Conversely, a simpler Grammar or more powerful computer
     * make the trade-off less important.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param speedVsAccuracy the value for this property
     * @see javax.speech.recognition.RecognizerProperties#getSpeedVsAccuracy()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see javax.speech.recognition.RecognizerProperties#MIN_ACCURACY
     * @see javax.speech.recognition.RecognizerProperties#NORM_ACCURACY
     * @see javax.speech.recognition.RecognizerProperties#MAX_ACCURACY
     */
    void setSpeedVsAccuracy(int speedVsAccuracy) throws IllegalArgumentException;

    /**
     * Gets the "SpeedVsAccuracy" property.
     * @return the value of this property
     * @see javax.speech.recognition.RecognizerProperties#setSpeedVsAccuracy(int)
     */
    int getSpeedVsAccuracy();

    /**
     * Sets the "ResultAudioProvided" property.
     * <p>
     * If set to true, the recognizer is requested to provide audio with
     * FinalResult objects.
     * If available, the audio is provided through the getAudio methods of
     * the FinalResult interface.
     * <p>
     * Some recognizers that can provide audio for a FinalResult cannot provide
     * audio for all results. Applications need to test audio availability
     * for each individual result.
     * <p>
     * A Recognizer that does not provide audio will limit the value to false.
     * The default value for this property is false.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param audioProvided the value for the ResultAudioProvided property
     * @see javax.speech.recognition.RecognizerProperties#isResultAudioProvided()
     * @see javax.speech.recognition.FinalResult
     * @see javax.speech.recognition.FinalResult#getAudio()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setResultAudioProvided(boolean audioProvided);

    /**
     * Gets the "ResultAudioProvided" property.
     * @return true if result audio is provided
     * @see javax.speech.recognition.RecognizerProperties#setResultAudioProvided(boolean)
     */
    boolean isResultAudioProvided();

    /**
     * Sets the "TrainingProvided" property.
     * <p>
     * If true, request a recognizer to provide training information for
     * FinalResult objects through the tokenCorrection method.
     * <p>
     * Not all recognizers support training.
     * Also, recognizers that do support training are not required to
     * support training data for all results.
     * <p>
     * A Recognizer that does not support training will limit the value to false.
     * The default value for this property is false.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param trainingProvided the value for the TrainingProvided property
     * @see javax.speech.recognition.RecognizerProperties#isTrainingProvided()
     * @see javax.speech.recognition.FinalResult#tokenCorrection(java.lang.String[], javax.speech.recognition.ResultToken, javax.speech.recognition.ResultToken, int)
     * @see javax.speech.EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setTrainingProvided(boolean trainingProvided);

    /**
     * Gets the "TrainingProvided" property.
     * @return true if training information is provided
     * @see javax.speech.recognition.RecognizerProperties#setTrainingProvided(boolean)
     */
    boolean isTrainingProvided();
}
