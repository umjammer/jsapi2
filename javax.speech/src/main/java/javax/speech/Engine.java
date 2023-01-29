/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 53 $
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

package javax.speech;

// Comp. 2.0.6

/**
 * Parent interface for all speech Engines.
 * <p>
 * A speech engine is a generic entity that either processes speech
 * input or produces speech output.
 * Engine instances derive the following functionality
 * from the Engine interface:
 * <p>
 * allocate and deallocate methods.
 * pause and resume methods.
 * Access to a AudioManager and VocabularyManager.
 * Access to EngineProperties.
 * Access to the Engine's EngineMode.
 * Methods to add and remove EngineListener objects.
 * <p>
 * Engines are located, selected and created through methods of the
 * EngineManager class.
 * <p>
 * Each type of speech Engine has a well-defined set of states of operation,
 * and well-defined behavior for moving between states.
 * These states are defined by constants defined in the respective Engine
 * interfaces.
 * <p>
 * The Engine interface defines three methods for viewing and monitoring states:
 * getEngineState, waitEngineState and testEngineState.
 * An EngineEvent is issued to EngineListeners
 * each time an Engine changes state.
 * <p>
 * The basic states of any speech Engine are
 * DEALLOCATED, ALLOCATED, ALLOCATING_RESOURCES and DEALLOCATING_RESOURCES.
 * An Engine in the ALLOCATED state has acquired all the resources it requires
 * to perform its core functions.
 * <p>
 * Engines are created in the DEALLOCATED state and a call to allocate is
 * required to prepare them for usage.
 * The ALLOCATING_RESOURCES state is an intermediate state between DEALLOCATED
 * and ALLOCATED which an Engine occupies during the resource allocation process
 * (which may be a very short period or takes 10s of seconds).
 * <p>
 * Once an application finishes using a speech Engine
 * it should always explicitly
 * free system resources by calling the deallocate method.
 * This call transitions the Engine to the DEALLOCATED state after
 * some period of time in the DEALLOCATING_RESOURCES state.
 * <p>
 * The methods of an Engine instance perform differently
 * according to the Engine's allocation state.
 * Many methods cannot be performed when an Engine is in either the DEALLOCATED
 * or DEALLOCATING_RESOURCES state.
 * Many methods block (wait) for an Engine in the ALLOCATING_RESOURCES
 * state until the Engine reaches the ALLOCATED state.
 * This blocking/exception behavior is defined separately for
 * each subinterface of Engine.
 * <p>
 * The ALLOCATED state has substates.
 * (The DEALLOCATED, ALLOCATING_RESOURCES and DEALLOCATING_RESOURCES states
 * do not have any substates.)
 * <p>
 * Any ALLOCATED Engine (Recognizer or Synthesizer) is
 * either PAUSED or RESUMED.
 * These states indicates whether audio input/output is stopped or running.
 * Engine subclasses define additional specific substates.
 * <p>
 * The pause and resume methods are used
 * to transition an Engine instance between
 * the PAUSED and RESUMED states.
 * The PAUSED and RESUMED states of applications that use the same
 * underlying resources operate independently.
 * For instance, pausing a recognizer within an application will cause that
 * application to ignore audio during that time,
 * but the underlying resources and any other Engine instances may
 * continue to process audio.
 * <p>
 * The current state of an Engine is returned by the getEngineState method.
 * The waitEngineState method blocks the calling thread until
 * the Engine reaches a specified state.
 * The testEngineState tests whether an Engine is in a specified state.
 * <p>
 * <A/>
 * The state values can be bitwise OR'ed (using the Java "|" operator).
 * For example, for an allocated, resumed synthesizer with items in its speech
 * output queue, the state is
 * <p>
 * Engine.ALLOCATED | Engine.RESUMED | Synthesizer.QUEUE_NOT_EMPTY
 * <p>
 * The states and substates defined above put constraints
 * upon the state of an Engine.
 * The following are examples of illegal states:
 * <p>
 * Illegal Engine states:
 * <p>
 * Engine.DEALLOCATED | Engine.RESUMED
 * Engine.ALLOCATED | Engine.DEALLOCATED
 * <p>
 * Illegal Synthesizer states:
 * <p>
 * Engine.DEALLOCATED | Synthesizer.QUEUE_NOT_EMPTY
 * Synthesizer.QUEUE_EMPTY | Synthesizer.QUEUE_NOT_EMPTY
 * <p>
 * Illegal Recognizer states:
 * <p>
 * Engine.DEALLOCATED | Recognizer.PROCESSING
 * Recognizer.PROCESSING | Recognizer.SUSPENDED
 * <p>
 * Calls to the testEngineState and waitEngineState methods with illegal
 * state values cause an exception to be thrown.
 * @see javax.speech.EngineManager
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.recognition.Recognizer
 */
public interface Engine {

    /**
     * Bit of state that is set when an Engine is in the allocated state.
     * An Engine in the ALLOCATED state has acquired the resources required
     * for it to carry out its core functions.
     * <p>
     * The ALLOCATED state has substates for RESUMED and PAUSED.
     * Subinterfaces of Engine define additional substates of ALLOCATED.
     * <p>
     * An Engine is always created in the DEALLOCATED state.
     * It reaches the ALLOCATED state via the ALLOCATING_RESOURCES state with a
     * call to the allocate method.
     */
    long ALLOCATED = 0x001;

    /**
     * Bit of state that is set when an Engine is being allocated.
     * <p>
     * This is the transition state between DEALLOCATED and ALLOCATED following
     * a call to the allocate method.
     * <p>
     * The ALLOCATING_RESOURCES state has no substates.
     * In the ALLOCATING_RESOURCES state, many of the methods of Engine
     * (and subinterfaces)
     * will block until the Engine reaches the ALLOCATED
     * state and the action can be performed.
     */
    long ALLOCATING_RESOURCES = 0x002;

    /**
     * Bit of state that is set when an Engine is in the deallocated state.
     * A deallocated Engine does not have the resources necessary for it to
     * carry out its basic functions.
     * <p>
     * In the DEALLOCATED state, many of the methods of an Engine throw an
     * exception when called. The DEALLOCATED state has no substates.
     * <p>
     * An Engine is always created in the DEALLOCATED state.
     * A DEALLOCATED state can transition to the ALLOCATED state via the
     * ALLOCATING_RESOURCES state following a call to the allocate method.
     * An Engine returns to the DEALLOCATED state via the
     * DEALLOCATING_RESOURCES state with a call to the deallocate method.
     */
    long DEALLOCATED = 0x004;

    /**
     * Bit of state that is set when an Engine is being deallocated.
     * <p>
     * This is the transition state between ALLOCATED and DEALLOCATED following
     * a call to the deallocate method.
     * <p>
     * The DEALLOCATING_RESOURCES state has no substates.
     * In the DEALLOCATING_RESOURCES state, most methods of
     * Engine, Recognizer and Synthesizer throw an exception.
     */
    long DEALLOCATING_RESOURCES = 0x008;

    /**
     * Bit of state that is set when an Engine is in
     * the ALLOCATED state and is PAUSED.
     * <p>
     * In the PAUSED state, audio input or output is stopped.
     * <p>
     * An ALLOCATED Engine is always in either the PAUSED or RESUMED state.
     * The PAUSED and RESUMED states are substates of the ALLOCATED state.
     */
    long PAUSED = 0x010;

    /**
     * Bit of state that is set when an Engine is in
     * the ALLOCATED state and is RESUMED.
     * <p>
     * In the RESUMED state, audio input or output is active.
     * <p>
     * An ALLOCATED Engine is always in either the PAUSED or RESUMED state.
     * The PAUSED and RESUMED states are substates of the ALLOCATED state.
     */
    long RESUMED = 0x020;

    /**
     * Bit of state that is set when an ALLOCATED Engine
     * does not have the speech focus of the underlying resouces.
     * <p>
     * As focus is gained and lost, an ENGINE_FOCUSED or ENGINE_DEFOCUSED
     * event is issued to indicate the state change.
     * Focus management for a Recognizer is
     * explicit with requestFocus and releaseFocus methods.
     * Focus management for a Synthesizer is
     * implicitly obtained as described in that class.
     */
    long DEFOCUSED = 0x40;

    /**
     * Bit of state that is set when an ALLOCATED Engine
     * has the speech focus of the underlying resources.
     * <p>
     * As focus is gained and lost, an ENGINE_FOCUSED or ENGINE_DEFOCUSED
     * event is issued to indicate the state change.
     * Focus management for a Recognizer is
     * explicit with requestFocus and releaseFocus methods.
     * Focus management for a Synthesizer is
     * implicitly obtained as described in that class.
     */
    long FOCUSED = 0x080;

    /**
     * Bit of state that is set after an ENGINE_ERROR event occurs.
     * <p>
     * In the ERROR_OCCURRED state, many of the methods of an Engine throw an
     * exception when called. The ERROR_OCCURRED state has no substates.
     */
    long ERROR_OCCURRED = 0x100;

    /**
     * Constant value indicating asynchronous mode for a method.
     */
    int ASYNCHRONOUS_MODE = 0x1;

    int IMMEDIATE_MODE = 0x2;

    /**
     * Allocates the resources required for this Engine and puts it into
     * the ALLOCATED state.
     * <p>
     * When this method returns successfully
     * the ALLOCATED bit of Engine state is set,
     * and the testEngineState(Engine.ALLOCATED) method returns true.
     * During the processing of the method,
     * the Engine is temporarily in the ALLOCATING_RESOURCES state.
     * <p>
     * When the Engine reaches the ALLOCATED state
     * other Engine states are determined:
     * <p>
     * PAUSED or RESUMED: the pause state depends
     * upon the existing state of the Engine.
     * <p>
     * A Recognizer always starts in the LISTENING state when newly allocated
     * but may transition immediately to another state.
     * <p>
     * A Recognizer may be allocated in either the FOCUSED state or DEFOCUSED
     * state depending upon the activity of other applications.
     * <p>
     * A Synthesizer always starts in the QUEUE_EMPTY and DEFOCUSED states
     * when newly allocated.
     * <p>
     * While this method is being processed,
     * events are issued to any EngineListeners
     * attached to the Engine to indicate state changes
     * (see setEngineMask for event filtering).
     * First, as the Engine changes from the DEALLOCATED to
     * the ALLOCATING_RESOURCES
     * state, an ENGINE_ALLOCATING_RESOURCES event is issued.
     * As the allocation process completes, the Engine moves from the
     * ALLOCATING_RESOURCES state to the ALLOCATED state and
     * an ENGINE_ALLOCATED event is issued.
     * <p>
     * The allocate method should be called for an Engine in
     * the DEALLOCATED state.
     * The method has no effect for an Engine in either
     * the ALLOCATING_RESOURCES or ALLOCATED states.
     * The method throws an exception in the DEALLOCATING_RESOURCES state.
     * <p>
     * If any problems are encountered during the allocation process so that
     * the Engine cannot be allocated, the Engine returns to
     * the DEALLOCATED state
     * (with an ENGINE_DEALLOCATED event), and an EngineException is thrown.
     * <p>
     * Allocating the resources for an Engine may be fast (less than a second)
     * or slow (several 10s of seconds) depending upon a range of factors.
     * Since the allocate method does not return until allocation is completed
     * applications may want to perform allocation in a background thread
     * and proceed with other activities.
     * Use allocate with a flag for asynchronous operation
     * (see
     * {@link javax.speech.Engine#allocate(int)}
     * ).
     * @throws javax.speech.EngineException if an allocation error occurred or the
     *          Engine is not operational.
     * @throws javax.speech.EngineStateException if called for an Engine in the
     *          DEALLOCATING_RESOURCES state
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.Engine#allocate()
     * @see javax.speech.Engine#deallocate()
     * @see javax.speech.Engine#ALLOCATED
     * @see javax.speech.Engine#DEALLOCATED
     * @see javax.speech.EngineEvent
     * @see javax.speech.EngineEvent#ENGINE_ALLOCATED
     * @see javax.speech.EngineEvent#ENGINE_DEALLOCATED
     * @see javax.speech.Engine#setEngineMask(int)
     */
    void allocate() throws AudioException, EngineException,
            EngineStateException, SecurityException;

    void allocate(int mode) throws IllegalArgumentException, AudioException,
            EngineException, EngineStateException, SecurityException;

    /**
     * Deallocates the resources acquired for this Engine
     * and returns it to the DEALLOCATED state.
     * <p>
     * When this method returns the DEALLOCATED bit of Engine state is set
     * so the testEngineState(Engine.DEALLOCATED) method returns true.
     * During the processing of the method, the Engine is temporarily
     * in the DEALLOCATING_RESOURCES state.
     * <p>
     * A deallocated Engine can be restarted with a subsequent
     * call to allocate.
     * <p>
     * Engines should normally clean up current activities
     * before being deallocated.
     * If needed, deallocate will wait for pending operations to complete
     * appropriate for the type of Engine.
     * Use
     * {@link javax.speech.Engine#deallocate(int)}
     * with FLUSH_MODE
     * to deallocate without waiting for pending operations.
     * <p>
     * Deallocating resources for an Engine is not always immediate.
     * Use
     * {@link javax.speech.Engine#deallocate(int)}
     * with ASYNCHRONOUS_MODE for asynchronous operation.
     * The documentation for the allocate method shows an example of
     * asynchronous operation
     * (see
     * {@link javax.speech.Engine#allocate()}
     * ).
     * <p>
     * While this method is being processed, events are issued to any
     * EngineListeners attached to the Engine to indicate state changes.
     * First, as the Engine changes from the ALLOCATED to the
     * DEALLOCATING_RESOURCES state,
     * an ENGINE_DEALLOCATING_RESOURCES event is issued.
     * As the deallocation process completes, the Engine moves from the
     * DEALLOCATING_RESOURCES state to the DEALLOCATED state and an
     * ENGINE_DEALLOCATED event is issued.
     * <p>
     * The deallocate method should only be called for
     * an Engine in the ALLOCATED state.
     * The method has no effect for an Engine is
     * either the DEALLOCATING_RESOURCES
     * or DEALLOCATED states.
     * The method throws an exception in the ALLOCATING_RESOURCES state.
     * @throws javax.speech.EngineException if a deallocation error occurs
     * @throws javax.speech.EngineStateException if called for an Engine in
     *          the ALLOCATING_RESOURCES state
     * @see javax.speech.Engine#deallocate()
     * @see javax.speech.Engine#ASYNCHRONOUS_MODE
     * @see javax.speech.Engine#FLUSH_MODE
     * @see javax.speech.Engine#allocate()
     * @see javax.speech.EngineEvent#ENGINE_DEALLOCATING_RESOURCES
     * @see javax.speech.EngineEvent#ENGINE_DEALLOCATED
     * @see javax.speech.synthesis.Synthesizer#QUEUE_EMPTY
     */
    void deallocate() throws AudioException, EngineException,
            EngineStateException;

    void deallocate(int mode) throws IllegalArgumentException, AudioException,
            EngineException, EngineStateException;

    /**
     * Pauses the audio processing for this Engine and
     * puts it into the PAUSED state.
     * <p>
     * Pausing an Engine pauses the Engine instance and does not affect
     * other Engine instances in the current application or other applications,
     * even though they may use the same underlying resources.
     * Engines are typically paused and resumed by request from a user.
     * <p>
     * Applications may pause an Engine indefinitely.
     * When an Engine moves from the RESUMED state to the PAUSED state,
     * an ENGINE_PAUSED event is issued
     * to each EngineListener attached to the Engine.
     * The PAUSED bit of the Engine state is set to true when paused,
     * and can be tested by the getEngineState method and
     * other Engine state methods.
     * <p>
     * The PAUSED state is a substate of the ALLOCATED state.
     * An ALLOCATED Engine is always in either the PAUSED or the RESUMED state.
     * <p>
     * It is not an exception to pause an Engine that is already paused.
     * <p>
     * The pause method operates as defined for Engines in the ALLOCATED state.
     * When pause is called for an Engine in the ALLOCATING_RESOURCES state,
     * the method blocks (waits) until the ALLOCATED state is reached and then
     * operates normally.
     * An exception is thrown when pause is called for
     * an Engine that is in either the
     * DEALLOCATED is DEALLOCATING_RESOURCES states.
     * <p>
     * The pause method does not always return immediately.
     * Some applications may want to execute pause in a separate thread.
     * <p>
     * Operational detail depends on the particular Engine subinterface.
     * For Engines that produce audio,
     * pausing is analogous to pressing the "pause" button on
     * a tape or CD player -
     * audio output is paused and any unplayed audio is heard after resume.
     * For Engines that consume audio,
     * pausing is analogous to pressing a "mute" button on
     * a microphone -
     * audio input is ignored and permanently lost.
     * Audio processing does not begin again until after resume.
     * @throws javax.speech.EngineStateException if called for an Engine in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     * @see javax.speech.Engine#resume()
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.EngineEvent#ENGINE_PAUSED
     */
    void pause() throws EngineStateException;

    boolean resume() throws EngineStateException;

    /**
     * Returns true if the current Engine state matches the specified state.
     * <p>
     * The format of the state value is described above.
     * <p>
     * The test performed is not an exact match to the current state.
     * Only the specified states are tested.
     * <p>
     * <A/>
     * For example, the following returns true
     * only if the Synthesizer queue is empty,
     * irrespective of the pause/resume and allocation states.
     * <p>
     * if (synth.testEngineState(Synthesizer.QUEUE_EMPTY)) ...
     * <p>
     * The testEngineState method is equivalent to:
     * <p>
     * if ((engine.getEngineState() & state) == state)
     * <p>
     * The testEngineState method can be called successfully
     * in any Engine state.
     * @param state a set of states to test
     * @return true if the indicated states are true
     * @throws java.lang.IllegalArgumentException if the specified state is unreachable
     */
    boolean testEngineState(long state) throws IllegalArgumentException;

    long waitEngineState(long state)
            throws InterruptedException, IllegalArgumentException,
            IllegalStateException;

    long waitEngineState(long state, long timeout)
            throws InterruptedException, IllegalArgumentException,
            IllegalStateException;

    /**
     * Returns an object which provides management of the
     * audio input or output for this Engine.
     * <p>
     * Each Engine instance gets its own AudioManager.
     * <p>
     * The AudioManager is available in any state of an Engine.
     * @return the AudioManager for this Engine
     */
    AudioManager getAudioManager();

    /**
     * Returns the operating modes of this Engine.
     * <p>
     * For a Recognizer the return value is a RecognizerMode.
     * For a Synthesizer the return value is a SynthesizerMode.
     * <p>
     * The EngineMode is available in any state of an Engine.
     * @return an EngineMode for this Engine
     * @throws java.lang.SecurityException if the application does not have
     *             permission
     */
    EngineMode getEngineMode();

    /**
     * Returns an OR'ed set of flags indicating
     * the current state of this Engine.
     * <p>
     * The format of the returned state value is described above.
     * <p>
     * An EngineEvent is issued each time the Engine changes state.
     * <p>
     * The getEngineState method can be called successfully in any Engine state.
     * @return flags indicating the current state
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.Engine#waitEngineState(long)
     * @see javax.speech.EngineEvent#getNewEngineState()
     * @see javax.speech.EngineEvent#getOldEngineState()
     */
    long getEngineState();

    /**
     * Returns an object which provides management of
     * the vocabulary for this Engine.
     * <p>
     * Returns null if the Engine does not provide
     * vocabulary management capabilities.
     * Note that a VocabularyManager is not shared between Engines.
     * <p>
     * See the VocabularyManager documentation for a description of vocabularies
     * and their use with speech Engines.
     * <p>
     * The VocabularyManager is available for Engines in the ALLOCATED state.
     * The call blocks for Engines in the ALLOCATING_RESOURCES state.
     * An exception is thrown for Engines in the DEALLOCATED or
     * DEALLOCATING_RESOURCES states.
     * @return the VocabularyManager for the Engine or
     *          null if it does not have one
     * @throws javax.speech.EngineStateException if called for an Engine in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     * @throws java.lang.SecurityException if the Engine does not allow this method
     * @see javax.speech.Word
     */
    VocabularyManager getVocabularyManager();

    /**
     * Sets the mask to filter events for added EngineListeners.
     * <p>
     * It can improve efficiency to mask events that are not used.
     * Events of interest can be seen by using the logical AND of the
     * event constants defined in the EngineEvent class.
     * EngineEvent.DEFAULT_MASK is the initial value.
     * <p>
     * The EngineEvent mask can be set and retrieved in any state of an Engine.
     * @param mask the mask for EngineEvents
     * @see javax.speech.EngineEvent
     * @see javax.speech.EngineListener
     * @see javax.speech.Engine#getEngineMask()
     */
    void setEngineMask(int mask);

    /**
     * Gets the mask to filter events for added EngineListeners.
     * <p>
     * See setEngineMask for details.
     * The EngineListener mask can be set and retrieved
     * in any state of an Engine.
     * @return the current mask for Engine events
     * @see javax.speech.Engine#setEngineMask(int)
     */
    int getEngineMask();

    SpeechEventExecutor getSpeechEventExecutor();

    void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor);
}
