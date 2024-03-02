/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 61 $
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

import javax.speech.Engine;
import javax.speech.EngineStateException;

// Comp. 2.0.6

/**
 * Provides access to speech recognition capabilities.
 * <p>
 * The Recognizer interface extends the
 * <p>
 * {@link javax.speech.Engine}
 * interface and
 * so inherits the basic engine capabilities and provides additional
 * specialized capabilities.
 * <p>
 * The primary capabilities provided by a Recognizer are Grammar management
 * and Result handling.
 * An application is responsible for providing a recognizer with Grammars.
 * A
 * {@link javax.speech.recognition.Grammar}
 * defines a set of words (technically known as tokens)
 * and defines patterns in which the tokens may be spoken.
 * When a Grammar is
 * <I>active</I>
 * the Recognizer listens for speech in the
 * incoming audio which matches the Grammar.
 * When speech is detected, the Recognizer produces a Result.
 * The Result object is passed to the application and contains information
 * on which words were heard.
 * <p>
 * The types of Grammars, mechanisms for creating, modifying and
 * managing Grammars, types of Results, Result handling and other Recognizer
 * functions are described in more detail below.
 * <p>
 * <A href="#SkipDetail">Skip Detailed Description</A>
 * <p>
 * A Recognizer is created by a call to the
 * <p>
 * method
 * of the
 * class.
 * A Recognizer for the default Locale is create as follows:
 * <p>
 * Recognizer rec = (Recognizer)
 * EngineManager.engineCreate(RecognizerMode.DEFAULT);
 * <p>
 * The recognition package overview contains a
 * <p>
 * .
 * Detailed descriptions of the procedures for locating,
 * selecting, creating and initializing a Recognizer are provided in the
 * documentation for the
 * <p>
 * .
 * <p>
 * The creation, modification, enabling, activation and other management
 * of Grammars is a core function provided by any Recognizer.
 * A Recognizer must be provided with one or more Grammars that indicate
 * what words and word sequences it should listen for.
 * The basic process for dealing with a Grammar follows:
 * <p>
 * Create or load a new Grammar.
 * Attach a ResultListener to either the Recognizer or Grammar to
 * get Result events.
 * As necessary, setup or modify the Grammar according to
 * the application context.
 * Enable and disable the Grammar for recognition as required.
 * Commit changes to Grammar definitions and enabled status.
 * Repeat steps 1 through 5 as required.
 * Delete application-created Grammars when they are no longer needed.
 * <p>
 * A Recognizer provides several methods for the
 * creation and loading of RuleGrammars:
 * <p>
 * :
 * create a RuleGrammar programmatically
 * <p>
 * : create a RuleGrammar
 * from grammar text obtained in various ways.
 * <p>
 * Other important rule grammar functions are:
 * <p>
 * deletes a loaded grammar.
 * <p>
 * returns an array of
 * all Grammars loaded into a Recognizer.
 * <p>
 * In addition to RuleGrammars that an application creates,
 * some Recognizers have "built-in" Grammars.
 * Built-in Grammars are automatically loaded into the Recognizer
 * when the Recognizer is created and are accessible through the
 * listGrammars method.
 * Different Recognizers will have different built-in Grammars so applications
 * should not rely upon built-in Grammars if they need to be portable.
 * <p>
 * Each Recognizer object has a separate name-space.
 * Applications should be aware that changes to a Grammar object affect
 * all parts of the application that use that Grammar.
 * However, if two separate applications create a Recognizer and use a
 * Grammar with the same name, they are effectively independent - changes
 * by one application do not affect the operation of the other application.
 * <p>
 * The processing of Grammars (particularly large Grammars) can be
 * computationally expensive.
 * Loading a new Grammar can take appreciable time.
 * Updates to a Grammar may also be slow.
 * Therefore, applications should take precautions to build Grammars in
 * advance and to modify them only when necessary.
 * Furthermore, an application should minimize the number of Grammars active
 * at any point in time.
 * <p>
 * A Recognizer inherits two pairs of substates
 * from the
 * state from the Engine interface:
 * <p>
 * and
 * <p>
 * and
 * <p>
 * The initial states are PAUSED and DEFOCUSED.
 * An application should call
 * <p>
 * and
 * <p>
 * after loading Grammars.
 * <p>
 * The Recognizer interface adds one more independent substate system
 * to the ALLOCATED state:
 * <p>
 * {@link javax.speech.recognition.Recognizer#LISTENING}
 * ,
 * {@link javax.speech.recognition.Recognizer#PROCESSING}
 * , and
 * {@link javax.speech.recognition.Recognizer#RESUMED}
 *
 * The initial state is listening, but no processing takes place
 * until the Recognizer is in the RESUMED state.
 * <p>
 * <A/>
 * <p>
 * A Recognizer uses the two state substate-system for FOCUSED and
 * DEFOCUSED to indicate whether this instance of the Recognizer currently
 * has the speech focus.
 * Focus is important in a multi-application
 * environment because more than one application can connect to an
 * underlying speech recognition engine, but the user gives speech focus
 * to only one application at a time.
 * Since it is normal for an application to use only one Recognizer,
 * Recognizer focus and application focus normally mean the same thing.
 * (Multi-recognizer applications cannot make this assumption.)
 * <p>
 * Focus is not usually relevant in telephony applications or some
 * embedded applications in which there is a single input audio stream
 * to a single application.
 * <p>
 * The focus status is a key factor in determining the activation
 * of Grammars and therefore in determining when Results will and will
 * not be generated.
 * The activation conditions for Grammars and the role of focus are described
 * in the documentation for the
 * {@link javax.speech.recognition.Grammar}
 * interface.
 * <p>
 * When Recognizer focus is received,
 * an ENGINE_FOCUSED event is issued
 * to any RecognizerListeners attached to this Engine.
 * When Recognizer focus is released or otherwise lost,
 * an ENGINE_DEFOCUSED event is issued
 * to any RecognizerListeners attached to this Engine.
 * <p>
 * Applications can request speech focus for the Recognizer by calling
 * the
 * {@link javax.speech.recognition.Recognizer#requestFocus()}
 * method.
 * This asynchronous method may return before focus in received.
 * To determine when focus is on,
 * applications can check for ENGINE_FOCUSED events or
 * test the FOCUSED bit of engine state.
 * <p>
 * Applications can release speech focus from the Recognizer by calling
 * the
 * {@link javax.speech.recognition.Recognizer#releaseFocus()}
 * method.
 * This asynchronous method may return before focus is lost.
 * To determine whether focus is off,
 * applications can check for ENGINE_DEFOCUSED events or test
 * the DEFOCUSED bit of Engine state.
 * In GUI-based environments, it is normal (but not a requirement)
 * for speech focus to follow GUI focus.
 * Therefore, for example, it is common for the requestFocus method
 * to be called
 * on AWT or Swing events such as FocusEvent and WindowEvent.
 * <p>
 * A well-behaved application only requests focus when it knows that it has
 * the speech focus of the user (the user is talking to it and not to
 * other applications).
 * A well-behaved application will also release focus as soon as it
 * finishes with it.
 * <p>
 * A Recognizer adds the three state substate system for
 * LISTENING, PROCESSING and SUSPENDED to indicate the status of the
 * recognition of incoming speech.
 * These three states are loosely coupled with the PAUSED and RESUMED states
 * but only to the extent that turning on and off audio input will affect
 * the recognition process.
 * An ALLOCATED recognizer is always in one of these three states:
 * <p>
 * state:
 * the Recognizer is listening to incoming audio for
 * speech that may match an active Grammar
 * but has not detected speech yet.
 * <p>
 * state: the Recognizer is processing incoming speech
 * that may match an active Grammar to produce a result.
 * <p>
 * state: the Recognizer is temporarily suspended while
 * Grammars are updated.
 * While suspended, audio input is buffered for processing once
 * the Recognizer returns to the LISTENING and PROCESSING states.
 * <p>
 * The typical state cycle is as follows.
 * <p>
 * A Recognizer starts in the LISTENING state with a certain set of
 * Grammars enabled.
 * When incoming audio is detected that may match an active Grammar,
 * the Recognizer transitions to the PROCESSING state with a
 * RECOGNIZER_PROCESSING RecognizerEvent.
 * The Recognizer then creates a new Result and issues a RESULT_CREATED
 * event to provide it to the application.
 * The Recognizer remains in the PROCESSING state until it completes
 * recognition of the Result.
 * The Recognizer indicates completion of recognition by issuing
 * a RECOGNIZER_SUSPENDED RecognizerEvent to transition from the
 * PROCESSING state to the SUSPENDED state.
 * Once in that state, it issues a result finalization event to
 * ResultListeners (RESULT_ACCEPTED or RESULT_REJECTED event).
 * The Recognizer remains in the SUSPENDED state until processing of
 * the Result finalization event is completed.
 * Applications will usually make any necessary Grammar changes while
 * the Recognizer is SUSPENDED.
 * In this state the Recognizer buffers incoming audio.
 * This buffering allows a user to continue speaking without loss of
 * speech data.
 * Once the Recognizer returns to the LISTENING state the buffered audio
 * is processed to give the user the perception of real-time processing.
 * The Recognizer
 * <p>
 * ,
 * issues a CHANGES_COMMITTED event to any RecognizerListeners to return
 * to the LISTENING state. It also issues GRAMMAR_CHANGES_COMMITTED
 * events to GrammarListeners of changed grammars.
 * The commit applies all Grammar changes made at any point up to the
 * end of result finalization, typically including changes made in the
 * Result finalization events.
 * The Recognizer is back in the LISTENING state listening for speech
 * that matches the updated Grammars.
 * <p>
 * In this state cycle, the RECOGNIZER_PROCESSING and RECOGNIZER_SUSPENDED
 * events are triggered by user actions: starting and stopping speaking.
 * The third state transition -- CHANGES_COMMITTED -- is triggered
 * programmatically by the Recognizer after issuing events to application
 * listeners during Result finalization.
 * <p>
 * For applications that deal only with spoken input, the state cycle above
 * handles most normal speech interactions.
 * For applications that handle other asynchronous input,
 * additional state transitions are possible.
 * Other types of asynchronous input include graphical user interface events,
 * timer events, multi-threading events, socket events, etc.
 * <p>
 * When a non-speech event occurs which changes the application state
 * or application data,
 * it is often necessary to update the Recognizer's Grammars.
 * Furthermore, it is typically necessary to do this as if the change occurred
 * in real time - at exactly the point in time at which the event occurred.
 * <p>
 * The suspend and commitChanges methods of a Recognizer are used to handle
 * non-speech asynchronous events.
 * The typical cycle for updating grammars is as follows:
 * <p>
 * Assume that the Recognizer is in the LISTENING state
 * (the user is not currently speaking).
 * As soon as the event is received, the application calls
 * <p>
 * {@link javax.speech.recognition.Recognizer#pause()} ()}
 * to indicate that it is about to change Grammars.
 * The recognizer issues a RECOGNIZER_SUSPENDED event and transitions
 * from the LISTENING state to the SUSPENDED state.
 * The application makes all necessary changes to the Grammars.
 * Once all changes are completed the application calls the
 * commitChanges method.
 * The recognizer applies the updated Grammars, issues a
 * CHANGES_COMMITTED event to transition from the SUSPENDED state back
 * to the LISTENING state, and issues GRAMMAR_CHANGES_COMMITTED events
 * to all changed Grammars.
 * The Recognizer resumes recognition of any buffered audio and then live
 * audio with the updated Grammars.
 * <p>
 * Because audio is buffered from the time of the asynchronous event to
 * the time at which the CHANGES_COMMITTED occurs,
 * the audio is processed as if the updated Grammars were applied exactly at
 * the time of the asynchronous event.
 * The user has the perception of real-time processing.
 * <p>
 * Although audio is buffered in the SUSPENDED state,
 * applications should makes changes and commitChanges as quickly as possible.
 * This minimizes the possibility of a buffer overrun.
 * It also reduces delays in recognizing speech and responding to the user.
 * <p>
 * Note:
 * An application is not technically required to call suspend prior
 * to calling commitChanges.
 * If the suspend call is omitted, the Recognizer behaves as if suspend
 * had been called immediately prior to calling commitChanges.
 * <p>
 * There is no guarantee that a speech and non-speech events will not be mixed.
 * If a speech event occurs in the absence of non-speech events, the
 * <p>
 * takes place.
 * If a non-speech event occurs in the absence of any speech events, the
 * <p>
 * takes place.
 * <p>
 * Consider the two cases in which speech and non-speech events interact:
 * <p>
 * A non-speech event occurs during processing of a speech event:
 * Technically, this is the case in which a non-speech event is issued
 * while the Recognizer is in either the
 * PROCESSING state or the SUSPENDED state.
 * In both cases the event processing for the non-speech is no different
 * than normal. The non-speech event handler calls suspend to indicate it
 * is about to change Grammars, makes the Grammar changes, and then calls
 * commitChanges to apply the changes.
 * <p>
 * The effect is that the CHANGES_COMMITTED event that would normally occur
 * in the
 * <p>
 * <A href="#TypicalEventCycle">normal event cycle</A>
 * may be delayed until the commitChanges method
 * is explicitly called and that the commit applies changes made in response
 * to both the speech and non-speech events.
 * If the commitChanges call for the non-speech event is made before the end
 * of the result finalization event, there is no delay of the CHANGES_COMMITTED
 * event.
 * <p>
 * A speech event occurs during processing of a non-speech event:
 * This case is simpler. If the user starts speaking while a non-speech
 * event is being processed, then the Recognizer is in the SUSPENDED state,
 * that speech is buffered, and the speech event is actually delayed until
 * the Recognizer returns to the LISTENING state.
 * Once the Recognizer returns to the LISTENING state, the incoming speech
 * is processed with the
 * <p>
 * <A href="#TypicalEventCycle">normal event cycle</A>
 * .
 *
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
 * @see javax.speech.recognition.RecognizerEvent
 * @see javax.speech.recognition.RecognizerListener
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.FinalResult
 */
public interface Recognizer extends Engine {

    int BUFFER_MODE = 0x1000;

    /**
     * Bit of state that is set when an ALLOCATED Recognizer
     * is listening to incoming audio for speech that may match an active
     * Grammar but has not yet detected speech.
     * <p>
     * A RECOGNIZER_PROCESSING event is issued to indicate a transition out
     * of the LISTENING state and into the PROCESSING state.
     * <p>
     * A RECOGNIZER_SUSPENDED event is issued to indicate a transition out
     * of the LISTENING state and into the SUSPENDED state.
     * <p>
     * A CHANGES_COMMITTED event is issued to indicate a transition into
     * the LISTENING state from the SUSPENDED state.
     */
    long LISTENING = 0x1000;

    /**
     * Bit of state that is set when an ALLOCATED Recognizer
     * is producing a Result for incoming speech that may match an active
     * Grammar.
     * <p>
     * A RECOGNIZER_SUSPENDED event is issued to indicate a transition out of
     * the PROCESSING state and into the SUSPENDED state when recognition
     * of a Result is completed.
     * <p>
     * A RECOGNIZER_PROCESSING event is issued to indicate a transition into
     * the PROCESSING state from the LISTENING state when the start of a new
     * Result is detected.
     */
    long PROCESSING = 0x2000;

    long BUFFERING = 0x4000;

    long NOT_BUFFERING = 0x8000;

    /**
     * Requests notifications of RecognizerEvents related to this Recognizer.
     * <p>
     * An application can attach multiple listeners to this Recognizer.
     * A single listener can be attached to multiple Recognizers.
     * <p>
     * A RecognizerListener can be attached or removed in any state.
     * @param listener the listener that will receive RecognizerEvents
     * @see javax.speech.recognition.RecognizerEvent
     * @see javax.speech.recognition.RecognizerListener
     * @see javax.speech.recognition.Recognizer#removeRecognizerListener(javax.speech.recognition.RecognizerListener)
     */
    void addRecognizerListener(RecognizerListener listener);

    /**
     * Removes a RecognizerListener from this Recognizer.
     * <p>
     * A RecognizerListener can be removed in any state of an Engine.
     * @param listener the listener to be removed
     * @see javax.speech.recognition.Recognizer#addRecognizerListener(javax.speech.recognition.RecognizerListener)
     */
    void removeRecognizerListener(RecognizerListener listener);

    /**
     * Requests notifications of all events for all Results produced
     * by this Recognizer.
     * <p>
     * An application can attach multiple ResultListeners to a Recognizer.
     * A listener is removed with the removeResultListener method.
     * <p>
     * ResultListeners attached to a Recognizer are the only ResultListeners
     * to receive the RESULT_CREATED event and all subsequent events.
     * <p>
     * ResultListener objects can also be attached to any Grammar or to
     * any Result. A listener attached to the Grammar receives all events
     * that match that Grammar following a GRAMMAR_FINALIZED event.
     * A listener attached to a Result receives all events for that Result
     * from the time at which the listener is attached.
     * <p>
     * A ResultListener can be attached or removed in any Engine state.
     * @param listener the listener to attach to the recognizer
     * @see javax.speech.recognition.Grammar
     * @see javax.speech.recognition.Result
     * @see javax.speech.recognition.Recognizer#removeResultListener(javax.speech.recognition.ResultListener)
     * @see javax.speech.recognition.ResultEvent
     * @see javax.speech.recognition.ResultEvent#RESULT_CREATED
     * @see javax.speech.recognition.ResultEvent#GRAMMAR_FINALIZED
     */
    void addResultListener(ResultListener listener);

    /**
     * Removes a ResultListener from this Recognizer.
     * <p>
     * A ResultListener can be attached or removed in any Engine state.
     *
     * @param listener listener currently attached to this recognizer instance.
     * @see javax.speech.recognition.Recognizer#addResultListener(javax.speech.recognition.ResultListener)
     */
    void removeResultListener(ResultListener listener);

    /**
     * Returns an object which provides management of the speakers
     * for this Recognizer.
     * <p>
     * Returns null if the Recognizer does not store speaker data -
     * that is, if it is a speaker-independent recognizer in which all
     * speakers are handled the same.
     * <p>
     * A getSpeakerManager returns successfully in any state of a Recognizer.
     * The SpeakerManager methods that list speakers and set the current
     * speaker operate in any Recognizer state but only take effect in the
     * ALLOCATED state. This allows an application to set the initial
     * speaker prior to allocating the engine. Other methods of the
     * SpeakerManager only operate in the ALLOCATED state.
     * @return the SpeakerManager for this Recognizer
     * @throws java.lang.SecurityException if the application does not have permission
     */
    SpeakerManager getSpeakerManager();

    /**
     * Returns the properties for this Recognizer.
     * <p>
     * The RecognizerProperties are available in any state of this Recognizer.
     * However, changes only take effect once an engine reaches the ALLOCATED state.
     * @return the RecognizerProperties object for this Recognizer
     */
    RecognizerProperties getRecognizerProperties();

    GrammarManager getGrammarManager();

    void processGrammars() throws EngineStateException;

    void pause(int flags) throws IllegalArgumentException, EngineStateException;

    /**
     * Releases the speech focus from this Recognizer.
     * <p>
     * An ENGINE_DEFOCUSED event is issued to attached RecognizerListeners
     * once the focus
     * is released and the Recognizer state changes from FOCUSED to DEFOCUSED.
     * <p>
     * Since recognizer focus is a key factor in the activation policy for
     * grammars, an ENGINE_DEFOCUSED event is followed by a GRAMMAR_DEACTIVATED
     * event to the GrammarListeners of each Grammar that loses
     * activation.
     * The Grammar interface details the role of Recognizer focus and Grammar
     * <p>
     * <A href="Grammar.html#GrammarActivation">activation conditions</A>
     * .
     * <p>
     * Since only one application may have recognition focus at any time,
     * applications should release focus whenever it is not required.
     * Speech focus and other focus issues are discussed in more detail with
     * <p>
     * <A href="#focus">Recognizer focus states</A>
     * .
     * <p>
     * It is not an error for an application to release focus for a Recognizer
     * that does not have speech focus.
     * <p>
     * Focus is implicitly released when a Recognizer is deallocated.
     * <p>
     * The releaseFocus method operates as defined when the Recognizer is
     * in the ALLOCATED state. The call blocks if the Recognizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown if the Recognizer is in
     * the DEALLOCATED or DEALLOCATING_RESOURCES state.
     * @throws javax.speech.EngineStateException if called for a Recognizer in
     *          the DEALLOCATED or DEALLOCATING_RESOURCES states
     * @see javax.speech.recognition.Recognizer#requestFocus()
     * @see javax.speech.Engine#DEFOCUSED
     * @see javax.speech.EngineEvent#ENGINE_DEFOCUSED
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.recognition.GrammarEvent#GRAMMAR_DEACTIVATED
     * @see javax.speech.recognition.Grammar
     */
    void releaseFocus() throws EngineStateException;

    /**
     * Requests the speech focus for this Recognizer from the underlying
     * speech recognition engine.
     * <p>
     * When the focus is received, an ENGINE_FOCUSED event is issued to
     * attached RecognizerListeners and the Recognizer changes state from
     * DEFOCUSED to FOCUSED.
     * <p>
     * Since recognizer focus is a key factor in the activation policy for
     * grammars, an ENGINE_FOCUSED event is followed by a GRAMMAR_ACTIVATED
     * event to the GrammarListeners of each Grammar that is
     * activated.
     * The Grammar interface details the role of Recognizer focus and Grammar
     * <p>
     * <A href="Grammar.html#GrammarActivation">activation conditions</A>
     * .
     * <p>
     * Since only one application may have recognition focus at any time,
     * applications should only request focus when confident that the user
     * is speaking to that application.
     * Speech focus and other focus issues are discussed in more detail with
     * <p>
     * <A href="#focus">Recognizer focus states</A>
     * .
     * <p>
     * It is not an error for an application to request focus for a Recognizer
     * that already has speech focus.
     * <p>
     * The requestFocus method operates as defined when the Recognizer is
     * in the ALLOCATED state. The call blocks if the Recognizer is in the
     * ALLOCATING_RESOURCES state and completes when the Engine reaches
     * the ALLOCATED state.
     * An exception is thrown if the Recognizer is in
     * the DEALLOCATED or DEALLOCATING_RESOURCES state.
     * @throws javax.speech.EngineStateException if called for a Recognizer in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     * @see javax.speech.recognition.Recognizer#releaseFocus()
     * @see javax.speech.Engine#FOCUSED
     * @see javax.speech.EngineEvent#ENGINE_FOCUSED
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.recognition.GrammarEvent#GRAMMAR_ACTIVATED
     * @see javax.speech.recognition.Grammar
     */
    void requestFocus() throws EngineStateException;

    @Override
    boolean resume() throws EngineStateException;

    void setResultMask(int mask);

    int getResultMask();
}
