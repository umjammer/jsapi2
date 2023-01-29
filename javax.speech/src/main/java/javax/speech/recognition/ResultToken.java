/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
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

// Comp 2.0.6

/**
 * A token (usually a word) contained by a Result representing something
 * heard by a recognizer.
 * <p>
 * For a Result from a RuleGrammar, a ResultToken contains the same string as
 * the defining RuleToken in the RuleGrammar.
 * <p>
 * For any Result, best finalized tokens are obtained from the
 * getBestToken and getBestTokens methods of the Result interface.
 * For a finalized result, alternatives may be available through
 * the getAlternativeTokens method of the FinalResult interface.
 * <p>
 * The ResultToken provides the following information:
 * <p>
 * Required: the token text
 * Required: a reference to the Result that contains this token
 * Optional: Start and end time
 * Optional: the confidence level
 *
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.Result#getBestToken(int)
 * @see javax.speech.recognition.Result#getBestTokens()
 * @see javax.speech.recognition.FinalResult
 * @see javax.speech.recognition.FinalResult#getAlternativeTokens(int)
 */
public interface ResultToken {

    /**
     * Gets the approximate start time for this token in milliseconds.
     * <p>
     * The value is matched to the System.currentTimeMillis() time.
     * <p>
     * The start time of a token is always greater than or equal to the
     * the end time of a preceding token.
     * The values will be different if the tokens are separated by a pause.
     * <p>
     * Returns -1 if timing information is not available.
     * Not all recognizers provide timing information.
     * Timing information is not usually available for unfinalized or
     * finalized tokens in a Result that is not yet finalized.
     * Even if timing information is available for tokens in the best result,
     * it might not be available for tokens from other alternatives.
     * @return approximate start time for token or -1 if not available
     * @see java.lang.System#currentTimeMillis()
     * @see javax.speech.recognition.ResultToken#getEndTime()
     */
    long getStartTime();

    /**
     * Gets the approximate end time for this token in milliseconds.
     * <p>
     * The value is matched to the System.currentTimeMillis() time.
     * <p>
     * The end time of a token is always less than or equal to the
     * start time of a succeeding token.
     * The values will be different if the tokens are separated by a pause.
     * <p>
     * Returns -1 if timing information is not available.
     * Not all recognizers provide timing information.
     * Timing information is not usually available for unfinalized
     * or finalized tokens in a Result that is not yet finalized.
     * Even if timing information is available for tokens in the best result,
     * it might not be available for tokens in other alternatives.
     * @return approximate end time for token or -1 if not available.
     * @see java.lang.System#currentTimeMillis()
     * @see javax.speech.recognition.ResultToken#getStartTime()
     */
    long getEndTime();

    /**
     * Returns a reference to the result that contains this token.
     * @return a Result object that contains this token.
     */
    Result getResult();

    /**
     * Gets the confidence level of this token.
     * <p>
     * This indicates the recognizer's confidence in the token.
     * Values lie in the range between MIN_CONFIDENCE and MAX_CONFIDENCE.
     * Not all engines support confidence levels for tokens.
     * <p>
     * Confidence levels may only be available in the ACCEPTED state.
     * A value of UNKNOWN_CONFIDENCE may be returned if the confidence is
     * unavailable or not supported
     * @return the confidence level for this token.
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.recognition.RecognizerProperties#setConfidenceThreshold(int)
     * @see javax.speech.recognition.RecognizerProperties#MIN_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#MAX_CONFIDENCE
     * @see javax.speech.recognition.RecognizerProperties#UNKNOWN_CONFIDENCE
     * @see javax.speech.recognition.Result#getResultState()
     * @see javax.speech.recognition.Result#ACCEPTED
     */
    int getConfidenceLevel();

    /**
     * Gets the text of this token.
     * @return the text of this token.
     * @see javax.speech.recognition.RuleToken#getText()
     * @see javax.speech.Word#getText()
     */
    String getText();
}
