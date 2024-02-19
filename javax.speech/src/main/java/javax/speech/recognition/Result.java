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

// Comp 2.0.6

/**
 * A Result is issued by a Recognizer as it recognizes an incoming
 * utterance that matches an active Grammar.
 * <p>
 * It is obtained by casting ResultEvent.getSource to a Result:
 * <p>
 * Result result = (Result) resultEvent.getSource();
 * <p>
 * The Result interface provides the application with access to the following
 * information about a recognized utterance:
 * <p>
 * A sequence of finalized tokens (words) that have been recognized,
 * A sequence of unfinalized tokens,
 * Reference to the Grammar matched by the Result,
 * The result state: UNFINALIZED, ACCEPTED or REJECTED.
 * <p>
 * Every Result implements the FinalResult and FinalRuleResult interfaces.
 * <p>
 * These multiple interfaces are designed to explicitly indicate
 * (a) what information is available at what times in the Result life-cycle and
 * (b) what information is available for different types of Results.
 * Appropriate casting of Results allows compile-time checking of
 * Result-handling code and fewer bugs.
 * <p>
 * The FinalResult extends the Result interface.
 * It provides access to the additional information about a result that is
 * available once it has been finalized
 * (once it is in either of the ACCEPTED or REJECTED states).
 * Calling any method of the FinalResult interface for a Result in the
 * UNFINALIZED state causes a ResultException.
 * <p>
 * The FinalRuleResult extends the FinalResult interface. It provides access
 * to the additional information about a finalized Result that matches a
 * RuleGrammar.
 * Calling any method of the FinalRuleResult interface for a non-finalized
 * Result causes a ResultException.
 * <p>
 * The separate interfaces determine what information is available for a
 * result in the different stages of its life-cycle.
 * The state of a Result is determined by calling the getResultState method.
 * The three possible states are UNFINALIZED, ACCEPTED and REJECTED.
 * <p>
 * A new result starts in the UNFINALIZED state.
 * When the result is finalized it moves to either the ACCEPTED or REJECTED state.
 * An accepted or rejected Result is termed a finalized Result.
 * All values and information regarding a finalized Result are fixed
 * (except that audio and training information may be released).
 * <p>
 * The descriptions for each state contain more information.
 * <p>
 * The state system of a Recognizer is linked to the state of recognition
 * of the current Result.
 * The Recognizer interface documents the
 * <p>
 * <A href="Recognizer.html#TypicalEventCycle">normal event cycle</A>
 * for a Recognizer and for Results.
 * The following is an overview of the ways in which the two
 * state systems are linked:
 * <p>
 * The ALLOCATED state of a Recognizer has three substates.
 * In the LISTENING state, the recognizer is listening to background audio
 * and there is no result being produced.
 * In the SUSPENDED state, the recognizer is temporarily buffering audio
 * input while its Grammars are updated.
 * In the PROCESSING state, the recognizer has detected incoming audio
 * that may match an active Grammar and produce a Result.
 * The Recognizer moves from the LISTENING state to the PROCESSING
 * state with a RECOGNIZER_PROCESSING event immediately prior to issuing
 * a RESULT_CREATED event.
 * The RESULT_UPDATED and GRAMMAR_FINALIZED events are produced while
 * the Recognizer is in the PROCESSING state.
 * The Recognizer finalizes a Result with
 * RESULT_ACCEPTED or RESULT_REJECTED
 * event immediately after it transitions from the PROCESSING state to the
 * SUSPENDED state with a RECOGNIZER_SUSPENDED event.
 * Unless there is a pending suspend, the Recognizer commits Grammar
 * changes with a CHANGES_COMMITTED event as soon as the
 * result finalization event is processed.
 * The TRAINING_INFO_RELEASED and AUDIO_RELEASED events can occur
 * in any state of an ALLOCATED Recognizer.
 * <p>
 * Rejection of a Result indicates that the Recognizer is not confident
 * that it has accurately recognized what a user said.
 * Rejection can be controlled through the RecognizerProperties
 * interface with the setConfidenceLevel method.
 * Increasing the confidence level requires the recognizer to have
 * greater confidence to accept a result, so more results are likely
 * to be rejected.
 * <p>
 * An ACCEPTED (vs. REJECTED) Result does not mean the Result is correct.
 * Instead, it implies that the Recognizer has a sufficient level
 * of confidence that the Result is correct.
 * <p>
 * It is difficult for Recognizers to reliably determine when they
 * make mistakes. Applications need to determine the cost of
 * incorrect recognition of any particular results and take appropriate actions.
 * For example, confirm with a user that they said "delete all files" before
 * deleting anything.
 * <p>
 * ResultEvents are issued when a new Result is created and
 * when there is any change
 * in the state or information content of a Result.
 * The following describes the event sequence for an ACCEPTED Result.
 * It provides the same information as above for Result states, but focuses on
 * legal event sequences.
 * <p>
 * Before a new Result is created for incoming speech, a Recognizer usually
 * issues a SPEECH_STARTED event.
 * <p>
 * A newly created Result is provided to the application through the
 * RESULT_CREATED event by calling the ResultEvent.getSource method.
 * The new result may or may not have any finalized
 * tokens or unfinalized tokens.
 * <p>
 * At any time following the RESULT_CREATED event, an application may attach a
 * ResultListener to an individual result. That listener will receive all
 * subsequent events associated with that Result.
 * <p>
 * A new Result is created in the UNFINALIZED state.
 * In this state, zero or more RESULT_UPDATED events may be issued to each
 * ResultListener attached to the Recognizer and to each ResultListener
 * attached to that Result.
 * The RESULT_UPDATED indicates that one or more tokens have been finalized,
 * or that the unfinalized tokens have changed, or both.
 * <p>
 * When the recognizer determines which Grammar is
 * the best match for incoming speech,
 * it issues a GRAMMAR_FINALIZED event.
 * This event is issued to each ResultListener
 * attached to the Recognizer and
 * to each ResultListener attached to that Result.
 * <p>
 * The GRAMMAR_FINALIZED event is also issued to each ResultListener attached
 * to the matched Grammar.
 * This is the first ResultEvent received by ResultListeners
 * attached to the Grammar.
 * All subsequent result events are issued to all
 * attached ResultListeners.
 * <p>
 * Zero or more RESULT_UPDATED events may be issued after the GRAMMAR_FINALIZED
 * event but before the result is finalized.
 * <p>
 * Once the recognizer completes recognition of the Result that it chooses to
 * accept, it finalizes the result with a RESULT_ACCEPTED event that is issued
 * to all attached ResultListeners.
 * This event may also indicate finalization of zero or more tokens, and/or the
 * reseting of the unfinalized tokens to null.
 * The result finalization event occurs immediately after the Recognizer makes
 * a transition from the PROCESSING state to the SUSPENDED state with
 * a RECOGNIZER_SUSPENDED event.
 * <p>
 * A finalized result (accepted or rejected state) may issue an AUDIO_RELEASED
 * or TRAINING_INFO_RELEASED event. These events may be issued in response
 * to relevant release methods of FinalResult or may be issued when the
 * recognizer independently determines to release audio or training information.
 * <p>
 * When a result is rejected, some of the events described above may be skipped.
 * At any time after a RESULT_CREATED event, a result may be rejected with
 * the RESULT_REJECTED event instead of a RESULT_ACCEPTED event.
 * A result may be rejected with or without any unfinalized or finalized tokens
 * being created (no RESULT_UPDATED events), and with or without a
 * GRAMMAR_FINALIZED event.
 * <p>
 * A new result object is created when a Recognizer has detected possible
 * incoming speech which may match an active Grammar.
 * <p>
 * To accept the Result (that is, to issue a RESULT_ACCEPTED event),
 * the token sequence from the best Result must match the token patterns
 * defined by the matched Grammar.
 * For a RuleGrammar this implies that a call to the parse method of the
 * matched RuleGrammar must return successfully.
 * (Note: the parse is not guaranteed if the grammar has been changed.)
 * <p>
 * The following conditions apply to all finalized Results:
 * <p>
 * N-best alternatives available through the
 * FinalResult interface cannot cross result boundaries.
 * Correction/training is only possible within a single result object.
 *
 * @see javax.speech.recognition.FinalResult
 * @see javax.speech.recognition.FinalRuleResult
 * @see javax.speech.recognition.ResultEvent
 * @see javax.speech.recognition.ResultListener
 * @see javax.speech.recognition.ResultStateException
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.RecognizerEvent
 */
public interface Result {

    /**
     * A state indicating that a Result is still being recognized.
     * <p>
     * A Result is in the UNFINALIZED state when
     * the RESULT_CREATED event is issued.
     * getResultState returns UNFINALIZED while
     * a Result is still being recognized.
     * A Result remains in the UNFINALIZED state until it is finalized by
     * either a RESULT_ACCEPTED or RESULT_REJECTED event.
     * <p>
     * In the UNFINALIZED state:
     * <p>
     * Calls to the methods of FinalResult and FinalRuleResult interfaces
     * result in ResultExceptions.
     * Zero or more RESULT_UPDATED events may be issued as
     * <p>
     * tokens are finalized, or
     * as the unfinalized tokens change.
     * <p>
     * One GRAMMAR_FINALIZED event must be issued in the
     * UNFINALIZED state before result finalization by
     * a RESULT_ACCEPTED event.
     * The GRAMMAR_FINALIZED event is optional if the Result is
     * finalized by a RESULT_REJECTED event -
     * it is not always possible for a recognizer to identify a best-match
     * grammar for a rejected Result.
     * getNumTokens returns the number of finalized tokens and
     * this number may increase as RESULT_UPDATED events are issued.
     * getUnfinalizedTokens may return zero or more tokens and these may
     * change at any time
     * when a ResultEvent.RESULT_UPDATED event is issued.
     */
    int UNFINALIZED = 0x12c;

    /**
     * A state indicating that recognition of the Result is completed
     * and the Result object has been finalized by being accepted.
     * <p>
     * When a Result changes to the ACCEPTED state,
     * a RESULT_ACCEPTED event is issued.
     * <p>
     * In the ACCEPTED state:
     * <p>
     * Recognition of the Result is complete and
     * the Recognizer is confident
     * it has the correct Result (not a rejected Result).
     * Non-rejection is not a guarantee of a correct result -
     * only sufficient confidence that the result is correct.
     * AUDIO_RELEASED and TRAINING_INFO_RELEASED events may occur
     * optionally (once).
     * getNumTokens will return 1 or greater (there must be at least one
     * finalized token) and the number of finalized tokens will not change.
     * Each token within the best finalized token sequence is
     * available through getBestToken.
     * These tokens are guaranteed not to change
     * through the remaining life of the Result.
     * getUnfinalizedTokens returns null.
     * getGrammar returns the Grammar matched by this Result.
     * The methods of FinalResult and FinalRuleResult may be used.
     */
    int ACCEPTED = 0x12d;

    /**
     * A state indicating that recognition of the Result is complete
     * and the Result object has been finalized by being rejected.
     * <p>
     * When a Result changes to the REJECTED state,
     * a RESULT_REJECTED event is issued.
     * <p>
     * In the REJECTED state
     * <p>
     * The Results are most likely incorrect.
     * AUDIO_RELEASED and TRAINING_INFO_RELEASED events may occur
     * optionally (once).
     * getNumTokens will return 0 or greater and
     * the number of finalized tokens will not change.
     * Each token within the best finalized token sequence is
     * available through getBestToken.
     * These tokens are guaranteed not to change
     * through the remaining life of the Result.
     * getUnfinalizedTokens method returns null.
     * If GRAMMAR_FINALIZED was issued, getGrammar returns
     * the Grammar matched by this Result.
     * If available, the Grammar is most likely not the correct one.
     * The methods of FinalResult may be used.
     * If the grammar is known, the FinalRuleResult method may be used, but
     * the information is unlikely to be reliable.
     */
    int REJECTED = 0x12e;

    /**
     * Requests notifications of ResultEvents related to this Result.
     * <p>
     * An application can attach multiple listeners to a Result.
     * A listener can be removed with the removeResultListener method.
     * <p>
     * ResultListener objects can also be attached to
     * a Recognizer and to any Grammar.
     * A listener attached to the Recognizer receives all events for all results
     * produced by that Recognizer.
     * A listener attached to a Grammar receives all
     * events for all results that have been finalized for that Grammar
     * (all events starting with and including the GRAMMAR_FINALIZED event).
     * <p>
     * A ResultListener attached to a Result only receives events following the
     * point in time at which the listener is attached. Because the listener
     * can only be attached during or after the RESULT_CREATED event,
     * it will not receive the RESULT_CREATED event. Only ResultListeners
     * attached to the Recognizer receive RESULT_CREATED events.
     * @param listener a ResultListener
     * @see javax.speech.recognition.ResultEvent
     * @see javax.speech.recognition.Result#removeResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Recognizer#addResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Grammar#addResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     */
    void addResultListener(ResultListener listener);

    /**
     * Removes a ResultListener from this Result.
     * @param listener a ResultListener
     * @see javax.speech.recognition.Result#addResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Recognizer#removeResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Grammar#removeResultListener(javax.speech.recognition.ResultListener)
     */
    void removeResultListener(ResultListener listener);

    /**
     * Returns the Grammar matched by the finalized tokens of the best
     * Result or null if the Grammar is not known.
     * <p>
     * The return value is null before a GRAMMAR_FINALIZED event and non-null
     * afterward.
     * <p>
     * The Grammar is guaranteed to be non-null for an accepted result.
     * The Grammar may be null or non-null for a rejected result,
     * depending upon whether a GRAMMAR_FINALIZED event was issued prior to
     * finalization.
     * <p>
     * For a finalized Result, an application should determine the type of
     * matched Grammar with an instanceof test.
     * For a Result that matches a RuleGrammar, the methods of FinalRuleResult
     * can be used (the methods of other possible interfaces would throw an
     * exception).
     * The methods of FinalResult can be used for a Result matching any kind of
     * Grammar.
     * <p>
     * <A/>
     * Example of testing the Grammar for the Result type:
     * <p>
     * Result result;
     * if (result.getGrammar() instanceof RuleGrammar)
     * FinalRuleResult frr = (FinalRuleResult) result;
     * ...
     * }
     *
     * @return the Grammar matched by the finalized tokens of the best Result
     *          or null
     * @see javax.speech.recognition.Result#getResultState()
     * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
     * @see javax.speech.recognition.FinalResult
     * @see javax.speech.recognition.FinalRuleResult
     */
    Grammar getGrammar();

    /**
     * Returns the current state of the Result object.
     * <p>
     * The set of possible states are UNFINALIZED, ACCEPTED or REJECTED.
     * <p>
     * The details of a Result in each state are described in the
     * documentation for each state.
     * @return the current state of this object
     * @see javax.speech.recognition.Result#UNFINALIZED
     * @see javax.speech.recognition.Result#ACCEPTED
     * @see javax.speech.recognition.Result#REJECTED
     */
    int getResultState();

    /**
     * Returns the Nth token from the best Result.
     * <p>
     * tokNum must be in the range 0 to getNumTokens()-1.
     * <p>
     * If the Result has zero tokens (possible in both the UNFINALIZED
     * and REJECTED states) an exception is thrown.
     * <p>
     * If the Result is in the REJECTED state, then the returned tokens are
     * likely to be incorrect.
     * In the ACCEPTED state (not rejected) the recognizer is confident that
     * the tokens are correct but applications should consider the possibility
     * that the tokens are incorrect.
     * <p>
     * The FinalRuleResult interface provides a
     * getAlternativeTokens method that returns alternative token sequences
     * for finalized results.
     * @param tokNum index for the desired token.
     * @return the Nth token from the best result
     * @throws java.lang.IllegalArgumentException if tokNum is out of range
     * @see javax.speech.recognition.Result#getNumTokens()
     * @see javax.speech.recognition.Result#getUnfinalizedTokens()
     * @see javax.speech.recognition.Result#getBestTokens()
     * @see javax.speech.recognition.FinalResult#getAlternativeTokens(int)
     * @see javax.speech.recognition.Result#UNFINALIZED
     * @see javax.speech.recognition.Result#REJECTED
     * @see javax.speech.recognition.Result#ACCEPTED
     */
    ResultToken getBestToken(int tokNum) throws IllegalArgumentException;

    /**
     * Returns the token sequence for the best Result.
     * <p>
     * If the Result has zero tokens,
     * the return value is a zero-length sequence.
     * @return the token sequence for the best Result
     * @see javax.speech.recognition.Result#getBestToken(int)
     */
    ResultToken[] getBestTokens();

    /**
     * Returns the number of finalized tokens in a Result.
     * <p>
     * Tokens are numbered from 0 to getNumTokens()-1 and are obtained through
     * the getBestToken and getBestTokens methods of this
     * interface and the getAlternativeTokens method of the FinalRuleResult
     * interface for a finalized Result.
     * <p>
     * Starting from the RESULT_CREATED event and while the result remains in
     * the UNFINALIZED state, the number of finalized tokens may be zero or
     * greater and can increase as tokens are finalized.
     * When one or more tokens are finalized in the UNFINALIZED state, a
     * RESULT_UPDATED event is issued with the tokenFinalized flag set true.
     * The RESULT_ACCEPTED and RESULT_REJECTED events which finalize a result
     * can also indicate that one or more tokens have been finalized.
     * <p>
     * In the ACCEPTED and REJECTED states, getNumTokens indicates the
     * total number of tokens that were finalized.
     * The number of finalized tokens never changes in these states.
     * An ACCEPTED result must have one or more finalized tokens.
     * A REJECTED result may have zero or more tokens.
     * @return the number of finalized tokens in this Result
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#RESULT_UPDATED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     * @see javax.speech.recognition.Result#UNFINALIZED
     * @see javax.speech.recognition.Result#ACCEPTED
     * @see javax.speech.recognition.Result#REJECTED
     * @see javax.speech.recognition.Result#getBestToken(int)
     * @see javax.speech.recognition.Result#getBestTokens()
     * @see javax.speech.recognition.FinalResult#getAlternativeTokens(int)
     */
    int getNumTokens();

    /**
     * Returns the current unfinalized tokens following
     * the current finalized tokens.
     * <p>
     * Unfinalized tokens provide an indication of what a recognizer is
     * considering as possible recognition tokens for speech following
     * the finalized tokens.
     * <p>
     * Unfinalized tokens can provide users with feedback on
     * the recognition process.
     * The array may be any length (zero or more tokens),
     * the length may change at any time,
     * and successive calls to getUnfinalizedTokens may return different tokens
     * or even different numbers of tokens.
     * When the unfinalized tokens are changed, a RESULT_UPDATED event is
     * issued to the ResultListener.
     * The RESULT_ACCEPTED and RESULT_REJECTED events finalize a Result and
     * always guarantee that the return value is a zero-length array.
     * A new Result created with a RESULT_CREATED event
     * may contain zero or more tokens.
     * <p>
     * The returned array has length zero
     * if there are currently no unfinalized tokens,
     * if the recognizer does not support unfinalized tokens, or
     * after a Result is finalized
     * (in the ACCEPTED or REJECTED state rather than the UNFINALIZED state).
     * @return current unfinalized tokens following the finalized tokens
     * @see javax.speech.recognition.ResultEvent#isUnfinalizedChanged()
     * @see javax.speech.recognition.ResultListener
     * @see javax.speech.recognition.ResultEvent#RESULT_UPDATED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     * @see javax.speech.recognition.Result#ACCEPTED
     * @see javax.speech.recognition.Result#REJECTED
     * @see javax.speech.recognition.Result#UNFINALIZED
     */
    ResultToken[] getUnfinalizedTokens();
}
