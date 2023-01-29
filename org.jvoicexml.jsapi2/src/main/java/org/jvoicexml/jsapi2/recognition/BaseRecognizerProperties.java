/*
 * File:    $HeadURL: https://jsapi.svn.sourceforge.net/svnroot/jsapi/trunk/org.jvoicexml.jsapi2/src/org/jvoicexml/jsapi2/BaseSpeechEventExecutor.java $
 * Version: $LastChangedRevision: 274 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2.recognition;

import javax.speech.recognition.RecognizerProperties;

import org.jvoicexml.jsapi2.BaseEngineProperties;


/**
 * Base implementation of {@link RecognizerProperties}.
 *
 * <p>
 * Actual JSAPI2 implementations may want to override this class to apply other
 * settings to the recognizer.
 * </p>
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 */
public class BaseRecognizerProperties extends BaseEngineProperties implements RecognizerProperties {

    /** Name of the training provided property in events. */
    public static final String TRAINING_PROVIDED = "trainingProvided";

    /** Name of the result audio provided property in events. */
    public static final String RESULT_AUDIO_PROVIDED = "resultAudioProvided";

    /** Name of the speed vs. accuracy property in events. */
    public static final String SPEED_VS_ACCURACY = "speedVsAccuracy";

    /** Name of the sensitivity property in events. */
    public static final String SENSITIVITY = "sensitivity";

    /** Name of the num result alternatives property in events. */
    public static final String NUM_RESULT_ALTERNATIVES = "numResultAlternatives";

    /** Name of the incomplete timeout property in events. */
    public static final String INCOMPLETE_TIMEOUT = "incompleteTimeout";

    /** Name of the endpoint style property in events. */
    public static final String ENDPOINT_STYLE = "endpointStyle";

    /** Name of the confidence threshold property in events. */
    public static final String CONFIDENCE_THRESHOLD = "confidenceThreshold";

    /** Name of the complete timeout property in events. */
    public static final String COMPLETE_TIMEOUT = "completeTimeout";

    /** Name of the adaptation property in events. */
    public static final String ADAPTATION = "adaptation";

    /**
     * Value used to control adaptation behavior. This value determines when a
     * Recognizer may adapt to the audio signal.
     */
    private int adaptation;

    /**
     * This timeout value, in milliseconds, determines the length of silence
     * required following user speech before the Recognizer finalizes a Result.
     */
    private int completeTimeout;

    /**
     * The confidenceThreshold value can vary between MIN_CONFIDENCE and
     * MAX_CONFIDENCE. A value of NORM_CONFIDENCE is the default for the
     * Recognizer.
     */
    private int confidenceThreshold;

    /**
     * This value determines how a Recognizer knows when an utterance begins and
     * ends.
     */
    private int endpointStyle;

    /**
     * The incompleteTimeout value, in milliseconds, determines the required
     * length of silence following user speech after which a Recognizer
     * finalizes a Result.
     */
    private int incompleteTimeout;

    /**
     * This property indicates the preferred maximum number of N-best
     * alternatives in FinalResult objects.
     */
    private int numResultAlternatives;

    /**
     * The sensitivity can vary between MIN_SENSITIVITY and MAX_SENSITIVITY. A
     * value of NORM_SENSITIVITY is the default for the Recognizer. A value of
     * MAX_SENSITIVITY makes the Recognizer more sensitive to quiet input, but
     * also more sensitive to noise. A value of MIN_SENSITIVITY may require the
     * user to speak louder, but makes the Recognizer less sensitive to
     * background noise.
     */
    private int sensitivity;

    /**
     * A value of NORM_ACCURACY is the default compromise between speed and
     * accuracy for the Recognizer. A value of MIN_ACCURACY minimizes response
     * time. A value of MAX_ACCURACY maximizes recognition accuracy.
     */
    private int speedVsAccuracy;

    /**
     * If set to true, the Recognizer is requested to provide audio with
     * FinalResult objects.
     */
    private boolean resultAudioProvided;

    /**
     * If true, request a Recognizer to provide training information for
     * FinalResult objects.
     */
    private boolean trainingProvided;

    /**
     * Constructs a new object.
     *
     * @param recognizer reference to the recognizer
     */
    public BaseRecognizerProperties(BaseRecognizer recognizer) {
        super(recognizer);
        adaptation = ADAPT_PAUSED | ADAPT_RESUMED;
        completeTimeout = 500;
        confidenceThreshold = NORM_CONFIDENCE;
        endpointStyle = ENDPOINT_SPEECH_DETECTION;
        incompleteTimeout = 1000;
        numResultAlternatives = 1;
        sensitivity = NORM_ACCURACY;
        speedVsAccuracy = NORM_ACCURACY;
        resultAudioProvided = false;
        trainingProvided = false;
    }

    @Override
    public final int getAdaptation() {
        return adaptation;
    }

    @Override
    public final int getCompleteTimeout() {
        return completeTimeout;
    }

    @Override
    public final int getConfidenceThreshold() {
        return confidenceThreshold;
    }

    @Override
    public final int getEndpointStyle() {
        return endpointStyle;
    }

    @Override
    public final int getIncompleteTimeout() {
        return incompleteTimeout;
    }

    @Override
    public final int getNumResultAlternatives() {
        return numResultAlternatives;
    }

    @Override
    public final int getSensitivity() {
        return sensitivity;
    }

    @Override
    public final int getSpeedVsAccuracy() {
        return speedVsAccuracy;
    }

    @Override
    public final boolean isResultAudioProvided() {
        return resultAudioProvided;
    }

    @Override
    public final boolean isTrainingProvided() {
        return trainingProvided;
    }

    @Override
    public void reset() {
        setAdaptation(ADAPT_PAUSED | ADAPT_RESUMED);
        setCompleteTimeout(500);
        setConfidenceThreshold(NORM_CONFIDENCE);
        setEndpointStyle(ENDPOINT_SPEECH_DETECTION);
        setIncompleteTimeout(1000);
        setNumResultAlternatives(1);
        setSensitivity(NORM_ACCURACY);
        setSpeedVsAccuracy(NORM_ACCURACY);
        setResultAudioProvided(false);
        setTrainingProvided(false);

        super.reset();
    }

    @Override
    public final void setAdaptation(int adapt) {
        if (adaptation == adapt) {
            return;
        }

        handlePropertyChangeRequest(ADAPTATION, adaptation, adapt);
    }

    @Override
    public final void setCompleteTimeout(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid completeTimeout: " + value);
        }

        if (completeTimeout == value) {
            return;
        }
        handlePropertyChangeRequest(COMPLETE_TIMEOUT, completeTimeout, value);
    }

    @Override
    public final void setConfidenceThreshold(int threshold) {
        if ((threshold > MAX_CONFIDENCE) || (threshold < MIN_CONFIDENCE)) {
            throw new IllegalArgumentException("Invalid confidenceThreshold: " + threshold);
        }
        if (confidenceThreshold == threshold) {
            return;
        }
        handlePropertyChangeRequest(CONFIDENCE_THRESHOLD, confidenceThreshold, threshold);
    }

    @Override
    public final void setEndpointStyle(int style) {
        if ((style != ENDPOINT_SPEECH_DETECTION)
                && (style != ENDPOINT_PUSH_TO_START)
                && (style != ENDPOINT_PUSH_TO_TALK)) {
            throw new IllegalArgumentException("Invalid endpointStyle: " + style);
        }
        if (endpointStyle == style) {
            return;
        }
        handlePropertyChangeRequest(ENDPOINT_STYLE, endpointStyle, style);
    }

    @Override
    public final void setIncompleteTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Invalid incompleteTimeout: " + timeout);
        }
        if (incompleteTimeout == timeout) {
            return;
        }
        handlePropertyChangeRequest(INCOMPLETE_TIMEOUT, incompleteTimeout, timeout);
    }

    @Override
    public final void setNumResultAlternatives(int num) {
        if (numResultAlternatives == num) {
            return;
        }
        handlePropertyChangeRequest(NUM_RESULT_ALTERNATIVES, numResultAlternatives, num);
    }

    @Override
    public final void setSensitivity(int value) {
        if ((value > MAX_CONFIDENCE) || (value < MIN_CONFIDENCE)) {
            throw new IllegalArgumentException("Invalid sensitivity: " + value);
        }
        if (sensitivity == value) {
            return;
        }
        handlePropertyChangeRequest(SENSITIVITY, sensitivity,
                value);
    }

    @Override
    public final void setSpeedVsAccuracy(int value) {
        if (value != MAX_ACCURACY && value != NORM_ACCURACY) {
            throw new IllegalArgumentException("Invalid speedVsAccuracy: " + value);
        }
        if (speedVsAccuracy == value) {
            return;
        }
        handlePropertyChangeRequest(SPEED_VS_ACCURACY, speedVsAccuracy, value);
    }

    @Override
    public final void setResultAudioProvided(boolean value) {
        if (resultAudioProvided == value) {
            return;
        }
        handlePropertyChangeRequest(RESULT_AUDIO_PROVIDED, resultAudioProvided, value);
    }

    @Override
    public final void setTrainingProvided(boolean value) {
        if (trainingProvided == value) {
            return;
        }
        handlePropertyChangeRequest(TRAINING_PROVIDED, trainingProvided, value);
    }

    @Override
    public final boolean setProperty(String propName, Object value) {
        switch (propName) {
        case TRAINING_PROVIDED: {
            Boolean boolVal = (Boolean) value;
            trainingProvided = boolVal;
            return true;
        }
        case RESULT_AUDIO_PROVIDED: {
            Boolean boolVal = (Boolean) value;
            resultAudioProvided = boolVal;
            return true;
        }
        case SPEED_VS_ACCURACY: {
            Integer intVal = (Integer) value;
            speedVsAccuracy = intVal;
            return true;
        }
        case SENSITIVITY: {
            Integer intVal = (Integer) value;
            sensitivity = intVal;
            return true;
        }
        case NUM_RESULT_ALTERNATIVES: {
            Integer intVal = (Integer) value;
            numResultAlternatives = intVal;
            return true;
        }
        case INCOMPLETE_TIMEOUT: {
            Integer intVal = (Integer) value;
            incompleteTimeout = intVal;
            return true;
        }
        case ENDPOINT_STYLE: {
            Integer intVal = (Integer) value;
            endpointStyle = intVal;
            return true;
        }
        case CONFIDENCE_THRESHOLD: {
            Integer intVal = (Integer) value;
            confidenceThreshold = intVal;
            return true;
        }
        case COMPLETE_TIMEOUT: {
            Integer intVal = (Integer) value;
            completeTimeout = intVal;
            return true;
        }
        case ADAPTATION: {
            Integer intVal = (Integer) value;
            adaptation = intVal;
            return true;
        }
        }
        return false;
    }
}
