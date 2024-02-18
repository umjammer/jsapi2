/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 58 $
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

// Comp. 2.0.6

/**
 * Parent interface supported by all recognition Grammars.
 * A Grammar defines a set of tokens (such as words) that may be spoken and the
 * pattern in which those tokens may be spoken.
 * Different Grammar types define the words and the
 * patterns in different ways.
 * <p>
 * The core functionality provided through the Grammar interface includes:
 * <p>
 * Naming: each Grammar of a Recognizer has a unique name.
 * Enabling: Grammars may be enabled or disabled for activation
 * of recognition.
 * Activation: the activation mode can be set and the
 * activation state tested.
 * Adding and removing GrammarListeners for Grammar-related events.
 * Adding and removing ResultListeners to receive result events for
 * results matching the Grammar.
 * Access the Recognizer that owns the grammar
 * <p>
 * Each Grammar is associated with a specific Recognizer.
 * All Grammars are located, created and managed through the
 * Recognizer interface.
 * The basic steps in using any Grammar are:
 * <p>
 * Create a new Grammar or get an existing Grammar
 * through the Recognizer interface.
 * Attach a ResultListener to get results.
 * As necessary, setup or modify the Grammar for the application's context.
 * Enable and disable recognition of the Grammar as required by the
 * application's context.
 * Commit grammar changes and enabled state into the recognition process.
 * Repeat the update, enable and commit steps as required.
 * For application-created grammars, delete the Grammar when it is
 * no longer needed.
 * <p>
 * <A/>
 * <p>
 * When a Grammar is active, the Recognizer listens to incoming audio for
 * speech that matches that Grammar.
 * To be activated, an application must first set the enabled property
 * to true (enable activation) and set the activationMode property to
 * indicate the activation conditions.
 * <p>
 * There are three activation modes (listed in priority order, from lowest
 * to highest):
 * RECOGNIZER_FOCUS, RECOGNIZER_MODAL and GLOBAL.
 * For each mode a certain set of activation conditions must be met for the
 * grammar to be activated for recognition.
 * The activation conditions are determined by Recognizer focus and
 * possibly by the activation of other grammars.
 * Recognizer focus is managed with the requestFocus and releaseFocus
 * methods of a Recognizer.
 * <p>
 * The specific activation conditions for each activation mode are
 * described with each mode.
 * Always use the lowest priority mode possible.
 * <p>
 * The current activation state of a Grammar can be tested with
 * the isActive method.
 * Whenever a grammar's activation changes either a
 * GRAMMAR_ACTIVATED or GRAMMAR_DEACTIVATED
 * event is issued to each attached GrammarListener.
 * <p>
 * An application may have zero, one or many Grammars enabled at any time.
 * As the conventions below indicate, well-behaved applications always minimize
 * the number of active grammars.
 * <p>
 * The activation and deactivation of grammars is independent of
 * PAUSED and RESUMED states of the Recognizer.
 * However, when a Recognizer is PAUSED, audio input to the Recognizer
 * is turned off, so speech can't be detected. Note that just after pausing a
 * recognizer there may be some remaining audio in the input buffer that could
 * contain recognizable speech and thus an active Grammar may continue
 * to receive result events.
 * <p>
 * Well-behaved applications adhere to the following conventions to minimize
 * impact on other applications and other components of the same application:
 * <p>
 * Always use the default activation mode RECOGNIZER_FOCUS unless
 * there is a good reason to use another mode.
 * Only use the RECOGNIZER_MODAL when certain that deactivating
 * RECOGNIZER_FOCUS Grammars will not adversely affect the user interface.
 * Minimize the complexity and the number of RuleGrammars with
 * GLOBAL activation mode.
 * Only enable a Grammar when it is appropriate for a user to say
 * something matching that Grammar.
 * Otherwise disable the Grammar to improve recognition response time
 * and recognition accuracy for other Grammars.
 * Only request focus when confident that the user's speech focus
 * (attention) is directed to Grammars of your application.
 * Release focus when it is not required.
 * <p>
 * The general principal underlying these conventions is that increasing the
 * number of active Grammars and/or increasing the complexity of those Grammars
 * can lead to slower response time, greater CPU load and reduced recognition
 * accuracy (more mistakes).
 * <p>
 * <A/>
 * <p>
 * Grammars can be dynamically changed and enabled and disabled.
 * Changes may be necessary as the application changes context,
 * as new information becomes available and so on.
 * As with graphical interface programming, most Grammar updates
 * occur during processing of events. Very often Grammars are
 * updated in response to speech input from a user (a ResultEvent).
 * Other asynchronous events (e.g., GUI events) are another common
 * trigger of Grammar changes. Changing Grammars during normal operation
 * of a recognizer is usually necessary to ensure natural and usable
 * speech-enabled applications.
 * <p>
 * Different Grammar types allow different types of run-time change.
 * RuleGrammars can be created and deleted during normal recognizer operation.
 * Also, many aspects of a created RuleGrammar can be redefined:
 * rules can be modified, deleted or added and so on.
 * <p>
 * For any Grammar changes to take effect they must be committed.
 * Committing changes builds the Grammars into a format that can be used
 * by the internal processes of the Recognizer and applies those changes
 * to the processing of incoming audio.
 * <p>
 * There are two ways in which a commit takes place:
 * <p>
 * An explicit call by an application to
 * the commitChanges method of the Recognizer.
 * The documentation for the method describes when and how changes
 * are committed when called.
 * This method is typically used in conjunction with the
 * suspend method of Recognizer.
 * <p>
 * Changes to all grammars are implicitly committed at the
 * completion of processing of a result finalization event
 * (either a RESULT_ACCEPTED or RESULT_REJECTED event).
 * This simplifies the common scenario in which Grammars are
 * changed during Result finalization process because the
 * user's input has changed the application's state.
 * (This implicit commit is deferred following a call to suspend
 * and until a call to commitChanges.)
 * <p>
 * The Recognizer issues a CHANGES_COMMITTED event to signal that changes
 * have been committed. This event signals a transition from the SUSPENDED
 * state to the LISTENING state. Once in the LISTENING state the Recognizer
 * resumes the processing of incoming audio with the newly committed grammars.
 * <p>
 * Also each changed Grammar receives a GRAMMAR_CHANGES_COMMITTED through
 * attached GrammarListeners whenever changes to it are committed.
 * <p>
 * The commit changes mechanism has two important properties:
 * <p>
 * Updates to Grammar definitions and the enabled property take effect
 * atomically (all changes take effect at once).
 * There are no intermediate states in which some, but not all,
 * changes have been applied.
 * It is a method of Recognizer so all changes to all Grammars are
 * committed at once. Again, there are no intermediate states in which
 * some, but not all, changes have been applied.
 * <p>
 * An application can attach one or more GrammarListeners to any Grammar.
 * The listener is notified of status changes for the Grammar: when changes
 * are committed and when activation changes.
 * <p>
 * An application can attach one or more ResultListener objects to each Grammar.
 * The listener is notified of all events for all results that match this
 * grammar. The listeners receive ResultEvents starting with the
 * GRAMMAR_FINALIZED event (not the preceding RESULT_CREATED or
 * RESULT_UPDATED events).
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.GrammarListener
 * @see javax.speech.recognition.GrammarEvent
 * @see javax.speech.recognition.GrammarEvent#GRAMMAR_ACTIVATED
 * @see javax.speech.recognition.GrammarEvent#GRAMMAR_DEACTIVATED
 * @see javax.speech.recognition.GrammarEvent#GRAMMAR_CHANGES_COMMITTED
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.Recognizer#requestFocus()
 * @see javax.speech.recognition.Recognizer#releaseFocus()
 * @see javax.speech.recognition.Recognizer#pause()
 * @see javax.speech.recognition.Recognizer#resume()
 * @see javax.speech.Engine#PAUSED
 * @see javax.speech.Engine#RESUMED
 * @see javax.speech.recognition.Recognizer#pause()
 * @see javax.speech.recognition.Recognizer#processGrammars()
 * @see javax.speech.recognition.RecognizerEvent
 * @see javax.speech.recognition.RecognizerEvent#CHANGES_COMMITTED
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.ResultListener
 * @see javax.speech.recognition.ResultEvent
 * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
 * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
 * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
 */
public interface Grammar {

    int ACTIVATION_INDIRECT = 0x384;

    int ACTIVATION_FOCUS = 0x385;

    int ACTIVATION_MODAL = 0x386;

    int ACTIVATION_GLOBAL = 0x387;

    /**
     * Requests notifications of events related to this Grammar.
     * <p>
     * An application can attach multiple listeners to a Grammar.
     * @param listener a listener for GrammarEvents
     * @see javax.speech.recognition.Grammar#removeGrammarListener(javax.speech.recognition.GrammarListener)
     * @see javax.speech.recognition.GrammarEvent
     */
    void addGrammarListener(GrammarListener listener);

    /**
     * Removes a GrammarListener from this Grammar.
     * @param listener the GrammarListener to remove
     * @see javax.speech.recognition.Grammar#addGrammarListener(javax.speech.recognition.GrammarListener)
     */
    void removeGrammarListener(GrammarListener listener);

    /**
     * Requests notifications of events from any Result that matches
     * this Grammar.
     * <p>
     * An application can attach multiple ResultListeners to a Grammar.
     * A listener is removed with the removeResultListener method.
     * <p>
     * A ResultListener attached to a Grammar receives result events starting
     * from the GRAMMAR_FINALIZED event - the event which indicates that
     * the matched grammar is known.
     * A ResultListener attached to a Grammar will never receive a
     * RESULT_CREATED event and does not receive any RESULT_UPDATED
     * events that occurred before the GRAMMAR_FINALIZED event.
     * A ResultListener attached to a Grammar is guaranteed to receive a
     * result finalization event - RESULT_ACCEPTED or RESULT_REJECTED - some
     * time after the GRAMMAR_FINALIZED event.
     * <p>
     * ResultListener objects can also be attached to a Recognizer and
     * to any Result. A listener attached to the Recognizer receives all
     * events for all results produced by that Recognizer.
     * A listener attached to a Result receives all events for that
     * result from the time at which the listener is attached.
     * @param listener a listener for ResultEvents
     * @see javax.speech.recognition.Grammar#removeResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.ResultEvent
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
     * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
     * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
     */
    void addResultListener(ResultListener listener);

    /**
     * Removes a ResultListener from this Grammar.
     * @param listener the ResultListener to remove
     * @see javax.speech.recognition.Grammar#addResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Recognizer#removeResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.Result#removeResultListener(javax.speech.recognition.ResultListener)
     */
    void removeResultListener(ResultListener listener);

    /**
     * Returns the current activation mode for a Grammar.
     * <p>
     * The default value for a grammar is RECOGNIZER_FOCUS.
     * @return the current activation mode
     * @see javax.speech.recognition.Grammar#setActivationMode(int)
     * @see javax.speech.recognition.Grammar#setActivatable(boolean)
     * @see javax.speech.recognition.Grammar#isActive()
     */
    int getActivationMode();

    GrammarManager getGrammarManager();

    /**
     * Gets the reference of this Grammar.
     * <p>
     * A Grammar's reference must be unique for a Recognizer.
     * <p>
     * Grammar references are used with a RuleGrammar for resolving
     * references between grammars.
     * The reference for a RuleGrammar is set when the Grammar is created
     * (either by loading grammar text or creating a new RuleGrammar).
     * @return the name of the Grammar
     * @see javax.speech.recognition.RuleGrammar
     */
    String getReference();

    /**
     * Sets the activation mode of this Grammar.
     * <p>
     * It may be set as RECOGNIZER_FOCUS, RECOGNIZER_MODAL, or GLOBAL.
     * The role of the activation mode in the activation conditions
     * for a Grammar are described with each activation mode.
     * The default activation mode - RECOGNIZER_FOCUS - should be used
     * unless there is a user interface design reason to use another mode.
     * <p>
     * The individual rules of a RuleGrammar can be separately
     * enabled and disabled.
     * However, all rules share the same ActivationMode since the mode is a
     * property of the complete Grammar. A consequence is that all enabled
     * rules of a RuleGrammar are activated and deactivated together.
     * <p>
     * A change in activation mode only takes effect once changes are committed.
     * For some recognizers changing the activation mode is
     * computationally expensive.
     * <p>
     * The activation mode of a grammar can be tested by the
     * getActivationMode method.
     * @param mode the activation mode to set
     * @throws java.lang.IllegalArgumentException if an inappropriate
     *          activation mode is used
     * @see javax.speech.recognition.Grammar#getActivationMode()
     * @see javax.speech.recognition.Grammar#ACTIVATION_FOCUS
     * @see javax.speech.recognition.Grammar#ACTIVATION_MODAL
     * @see javax.speech.recognition.Grammar#ACTIVATION_GLOBAL
     * @see javax.speech.recognition.Grammar#setActivatable(boolean)
     * @see javax.speech.recognition.Grammar#isActive()
     * @see javax.speech.recognition.Recognizer#processGrammars()
     */
    void setActivationMode(int mode) throws IllegalArgumentException;

    /**
     * Tests whether this Grammar is currently active for recognition.
     * <p>
     * When a grammar is active, the recognizer is matching incoming
     * audio against the grammar (and other active grammars) to detect
     * speech that matches the grammar.
     * <p>
     * A Grammar is activated for recognition if the enabled property
     * is set to true and the activation conditions are met
     * as described in each activation mode:
     * RECOGNIZER_FOCUS, RECOGNIZER_MODAL, and GLOBAL
     * Activation is not directly controlled by applications and so
     * can only be tested (there is no setActive method).
     * <p>
     * Rules of a RuleGrammar can be individually enabled and disabled.
     * However all rules share the same ActivationMode and the same
     * activation state. Thus, when a RuleGrammar is active,
     * all the enabled rules of the grammar are active for recognition.
     * <p>
     * Changes in the activation state are indicated by GRAMMAR_ACTIVATED
     * and GRAMMAR_DEACTIVATED events issued to the GrammarListener.
     * A change in activation state can follow these RecognizerEvents:
     * <p>
     * A CHANGES_COMMITTED event that applies a change in the
     * enabled state or ActivationMode of this or another Grammar.
     * <p>
     * An ENGINE_FOCUSED or ENGINE_DEFOCUSED event.
     *
     * @return the current state of the Grammar being active
     * @see javax.speech.recognition.Grammar#setActivatable(boolean)
     * @see javax.speech.recognition.Grammar#setActivationMode(int)
     * @see javax.speech.recognition.Grammar#ACTIVATION_FOCUS
     * @see javax.speech.recognition.Grammar#ACTIVATION_MODAL
     * @see javax.speech.recognition.Grammar#ACTIVATION_GLOBAL
     * @see javax.speech.recognition.GrammarEvent
     * @see javax.speech.recognition.GrammarEvent#GRAMMAR_ACTIVATED
     * @see javax.speech.recognition.GrammarEvent#GRAMMAR_DEACTIVATED
     * @see javax.speech.recognition.RecognizerEvent
     * @see javax.speech.recognition.RecognizerEvent#CHANGES_COMMITTED
     * @see javax.speech.EngineEvent#ENGINE_FOCUSED
     * @see javax.speech.EngineEvent#ENGINE_DEFOCUSED
     */
    boolean isActive();

    boolean isActivatable();

    void setActivatable(boolean activatable);
}
