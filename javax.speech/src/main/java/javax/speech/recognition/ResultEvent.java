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

import java.util.List;
import javax.speech.SpeechEvent;

// Comp 2.0.6

/**
 * Event issued to indicate changes in the
 * recognized tokens and changes in state.
 * <p>
 * A ResultEvent is issued to all appropriate ResultListener objects.
 * ResultListeners may be attached as described in the ResultListener documentation.
 * <p>
 * The three states of a recognition Result are UNFINALIZED, ACCEPTED, and REJECTED.
 * They are described in the documentation
 * for the Result interface.
 * The RESULT_CREATED, RESULT_ACCEPTED and RESULT_REJECTED
 * events indicate corresponding result state changes.
 * <p>
 * The sequence of ResultEvents associated with a recognition Result are
 * described in the documentation for the Result interface.
 * <p>
 * In brief, the events that occur depend upon the Result state:
 * <p>
 * A RESULT_CREATED event creates a Result object.
 * A new result starts in the UNFINALIZED state.
 * UNFINALIZED state: RESULT_UPDATED events indicate a change in
 * finalized and/or unfinalized tokens; a GRAMMAR_FINALIZED event
 * indicates that the Grammar matched by this result has been identified.
 * The RESULT_ACCEPTED event finalizes a result by indicating a change in
 * state from UNFINALIZED to ACCEPTED.
 * The RESULT_REJECTED event finalizes a result by indicating a change
 * in state from UNFINALIZED to REJECTED.
 * In the finalized states - ACCEPTED and REJECTED - the AUDIO_RELEASED
 * and TRAINING_INFO_RELEASED events may be issued.
 *
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.Result#UNFINALIZED
 * @see javax.speech.recognition.Result#ACCEPTED
 * @see javax.speech.recognition.Result#REJECTED
 * @see javax.speech.recognition.ResultListener
 * @see javax.speech.recognition.FinalResult
 * @see javax.speech.recognition.FinalRuleResult
 */
public class ResultEvent extends SpeechEvent {

    /**
     * Event issued when a new Result is created.
     * <p>
     * The event is received by each ResultListener attached to the Recognizer.
     * <p>
     * When a Result is created, it is in the UNFINALIZED state.
     * When created the result may have zero or more finalized tokens and zero
     * or more unfinalized tokens.
     * Changes in finalized and unfinalized tokens are indicated by the
     * isFinalizedChanged and isUnfinalizedChanged methods.
     * <p>
     * This event follows the RECOGNIZER_PROCESSING event which
     * transitions the Recognizer from the LISTENING state to the PROCESSING state.
     */
    public static final int RESULT_CREATED = 0x6000001;

    /**
     * Event issued when one or more tokens of a Result are finalized
     * or when the unfinalized tokens of a result are changed.
     * <p>
     * Changes in finalized and unfinalized tokens are indicated by the
     * isFinalizedChanged and isUnfinalizedChanged methods.
     * <p>
     * This event only occurs when a Result is in the UNFINALIZED state.
     */
    public static final int RESULT_UPDATED = 0x6000002;

    /**
     * Event issued when the Grammar matched by a Result is
     * identified and finalized.
     * <p>
     * Before this event, the getGrammar method of a Result returns null.
     * Following the event it is guaranteed to return non-null and the grammar
     * is guaranteed not to change.
     * <p>
     * This event only occurs for a Result in the UNFINALIZED state.
     * <p>
     * This event does not affect finalized or unfinalized tokens
     * as reflected by the isFinalizedChanged and isUnfinalizedChanged
     * methods.
     */
    public static final int GRAMMAR_FINALIZED = 0x6000004;

    /**
     * Event issued when a Result is successfully finalized
     * and indicates a state change from UNFINALIZED to ACCEPTED.
     * <p>
     * In the finalization transition, zero or more tokens may be finalized
     * and the unfinalized tokens are set to null.
     * Changes in finalized and unfinalized tokens are indicated by the
     * isFinalizedChanged and isUnfinalizedChanged methods.
     * <p>
     * Since the Result is finalized (accepted), the methods of FinalResult
     * and FinalRuleResult may be used.
     * The getGrammar method of Result may be used to determine the type of grammar matched.
     * Applications should use type casting to ensure that only the appropriate interfaces
     * and method are used.
     * <p>
     * <A/>
     * The following example illustrates proper casting of Results:
     * <p>
     * void resultUpdate(ResultEvent e) {
     * if (e.getId() == RESULT_ACCEPTED) {
     * Object source = e.getSource();
     * <p>
     * // always safe with RESULT_ACCEPTED
     * Result result = (Result) source;
     * FinalResult fr = (FinalResult) source;
     * <p>
     * // find the grammar-specific details
     * // the null test is only useful for rejected results
     * if (result.getGrammar() != null) {
     * if (result.getGrammar() instanceof RuleGrammar) {
     * FinalRuleResult frr = (FinalRuleResult) source;
     * ...
     * }
     * }
     * }
     * }
     * <p>
     * This event is issued after the Recognizer issues
     * a RECOGNIZER_SUSPENDED event to transition from the PROCESSING state
     * to the SUSPENDED state. Any changes made to grammars or the enabled
     * state of grammars during the processing of the RESULT_ACCEPTED event
     * are automatically committed once the RESULT_ACCEPTED event has been
     * processed by all ResultListeners. Once those changes have been committed,
     * the Recognizer returns to the LISTENING state with a CHANGES_COMMITTED event.
     * A call to commitChanges is not required. (Except, if there is a call to
     * suspend without a subsequent call to commitChanges, the Recognizer defers
     * the commit until the commitChanges call is received.)
     */
    public static final int RESULT_ACCEPTED = 0x6000008;

    /**
     * Event issued when a Result is unsuccessfully
     * finalized and indicates a change from
     * the UNFINALIZED state to the REJECTED state.
     * <p>
     * In the state transition, zero or more tokens may be finalized and the
     * unfinalized tokens are set to null.
     * Changes in finalized and unfinalized tokens are indicated by the
     * isFinalizedChanged and isUnfinalizedChanged methods.
     * However, because the Result is rejected, the tokens are
     * quite likely to be incorrect.
     * <p>
     * Since the Result is finalized (rejected), the methods of FinalResult
     * can be used.
     * If the grammar is known
     * (GRAMMAR_FINALIZED event was issued and
     * the getGrammar method returns non-null)
     * then the FinalRuleResult interface can also be used.
     * <p>
     * Other state transition behavior for RESULT_REJECTED is the same as for
     * the RESULT_ACCEPTED event.
     */
    public static final int RESULT_REJECTED = 0x6000010;

    /**
     * Event issued when the audio information
     * associated with a FinalResult object is released.
     * <p>
     * The release may have been requested by an application call to
     * releaseAudio in the FinalResult interface or may be initiated by
     * the recognizer to reclaim memory. The FinalResult.isAudioAvailable
     * method returns false after this event.
     * <p>
     * This event is only issued for results in a finalized
     * state (getResultState returns either ACCEPTED or REJECTED).
     */
    public static final int AUDIO_RELEASED = 0x6000020;

    /**
     * Event issued when the training information
     * for a finalized result is released.
     * <p>
     * The release may have been requested by an application call to the
     * releaseTrainingInfo method in the FinalResult interface or may be
     * initiated by the recognizer to reclaim memory.
     * The isTrainingInfoAvailable method of FinalResult returns false after this event.
     * <p>
     * The TRAINING_INFO_RELEASED event is only issued for results in a finalized
     * state (getResultState returns either ACCEPTED or REJECTED).
     */
    public static final int TRAINING_INFO_RELEASED = 0x6000040;

    /**
     * The default mask for events in this class.
     * <p>
     * The following events are delivered by default:
     * all except RESULT_CREATED and RESULT_UPDATED.
     * <p>
     * The events RESULT_CREATED and RESULT_UPDATED may be enabled if
     * intermediate results are desired.
     */
    public static final int DEFAULT_MASK = RESULT_CREATED | RESULT_UPDATED;

    private boolean tokensFinalized;

    private boolean unfinalizedTokensChanged;

    /**
     * Constructs a ResultEvent for the specified event identifier.
     * <p>
     * The isFinalizedChanged and isUnfinalizedChanged methods
     * return false.
     * @param source the Result object that issued the event
     * @param id the identifier for the event type
     * @see java.util.EventObject#getSource()
     * @see javax.speech.SpeechEvent#getId()
     * @see javax.speech.recognition.ResultEvent#AUDIO_RELEASED
     * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
     * @see javax.speech.recognition.ResultEvent#TRAINING_INFO_RELEASED
     * @see javax.speech.recognition.ResultEvent#ResultEvent(javax.speech.recognition.Result, int, boolean, boolean)
     * @see javax.speech.recognition.ResultEvent#isFinalizedChanged()
     * @see javax.speech.recognition.ResultEvent#isUnfinalizedChanged()
     */
    public ResultEvent(Result source, int id) throws IllegalArgumentException {
        super(source, id);
    }

    /**
     * Constructs a ResultEvent for the specified event identifier with flags
     * to indicate changes in token status and value.
     * <p>
     * The two boolean flags indicating changes in tokens
     * should be set appropriately for
     * RESULT_CREATED, RESULT_UPDATED, RESULT_ACCEPTED and RESULT_REJECTED
     * events.
     * For other event types, these flags should be false.
     * @param source the Result object that issued the event
     * @param id the identifier for the event type
     * @param tokensFinalized true if any tokens are finalized with this event
     * @param unfinalizedTokensChanged true if any unfinalized tokens have
     *         changed with this event
     * @see java.util.EventObject#getSource()
     * @see javax.speech.SpeechEvent#getId()
     * @see javax.speech.recognition.ResultEvent#isFinalizedChanged()
     * @see javax.speech.recognition.ResultEvent#isUnfinalizedChanged()
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#RESULT_UPDATED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     * @see javax.speech.recognition.ResultEvent#ResultEvent(javax.speech.recognition.Result, int)
     */
    public ResultEvent(Result source, int id, boolean tokensFinalized,
                       boolean unfinalizedTokensChanged) throws IllegalArgumentException {
        super(source, id);

        this.tokensFinalized = tokensFinalized;
        this.unfinalizedTokensChanged = unfinalizedTokensChanged;
    }

    /**
     * Returns true if any tokens were finalized.
     * <p>
     * This can occur for RESULT_CREATED, RESULT_UPDATED, RESULT_ACCEPTED
     * and RESULT_REJECTED events.
     * For other events, this method returns false.
     * <p>
     * If true, the number of tokens returned by getNumTokens and
     * getBestTokens has increased.
     * @return flag for finalized tokens changed
     * @see javax.speech.recognition.Result#getNumTokens()
     * @see javax.speech.recognition.Result#getBestTokens()
     * @see javax.speech.recognition.ResultEvent#isUnfinalizedChanged()
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#RESULT_UPDATED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     */
    public boolean isFinalizedChanged() {
        int id = getId();
        if ((id == RESULT_CREATED) || (id == RESULT_UPDATED)
                || (id == RESULT_ACCEPTED) || (id == RESULT_REJECTED)) {
            return tokensFinalized;
        }

        return false;
    }

    /**
     * Returns true if any unfinalized tokens have changed.
     * <p>
     * This can occur for RESULT_CREATED, RESULT_UPDATED, RESULT_ACCEPTED
     * and RESULT_REJECTED events.
     * For other events, this method returns false.
     * <p>
     * If true, the value returned by getUnfinalizedTokens has changed.
     * <p>
     * Note that there are no unfinalized tokens for both the
     * RESULT_ACCEPTED and RESULT_REJECTED events.
     * For these events, this method returns true only if
     * there were unfinalized tokens prior to finalization.
     * @return flag for unfinalized tokens changed
     * @see javax.speech.recognition.Result#getUnfinalizedTokens()
     * @see javax.speech.recognition.ResultEvent#isFinalizedChanged()
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#RESULT_UPDATED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     */
    public boolean isUnfinalizedChanged() {
        int id = getId();
        if ((id == RESULT_CREATED) || (id == RESULT_UPDATED)
                || (id == RESULT_ACCEPTED) || (id == RESULT_REJECTED)) {
            return tokensFinalized;
        }

        return false;
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        Boolean tokensFinalizedObject = tokensFinalized;
        parameters.add(tokensFinalizedObject);
        Boolean unfinalizedTokensChangedObject = unfinalizedTokensChanged;
        parameters.add(unfinalizedTokensChangedObject);

        return parameters;
    }
}
