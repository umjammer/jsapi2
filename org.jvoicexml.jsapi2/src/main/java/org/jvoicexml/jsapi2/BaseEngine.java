/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This class is based on work by SUN Microsystems and
 * Carnegie Mellon University
 *
 * Copyright 1998-2003 Sun Microsystems, Inc.
 *
 * Portions Copyright 2001-2004 Sun Microsystems, Inc.
 * Portions Copyright 1999-2001 Language Technologies Institute,
 * Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * Permission is hereby granted, free of charge, to use and distribute
 * this software and its documentation without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of this work, and to
 * permit persons to whom this work is furnished to do so, subject to
 * the following conditions:
 *
 * 1. The code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 * 2. Any modifications must be clearly marked as such.
 * 3. Original authors' names are not deleted.
 * 4. The authors' names are not used to endorse or promote products
 *    derived from this software without specific prior written
 *   permission.
 *
 * SUN MICROSYSTEMS, INC., CARNEGIE MELLON UNIVERSITY AND THE
 * CONTRIBUTORS TO THIS WORK DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
 * SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS, IN NO EVENT SHALL SUN MICROSYSTEMS, INC., CARNEGIE MELLON
 * UNIVERSITY NOR THE CONTRIBUTORS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF
 * USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package org.jvoicexml.jsapi2;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Collection;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.Engine;
import javax.speech.EngineEvent;
import javax.speech.EngineException;
import javax.speech.EngineListener;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;

import static java.lang.System.getLogger;


/**
 * Supports the JSAPI 2.0 {@link Engine} interface.
 * <p>
 * Actual JSAPI implementations might want to extend or modify this
 * implementation.
 * </p>
 * <p>
 * A base engine has to provide several factory methods:
 * <ul>
 * <li>{@link #createAudioManager()} to create an
 * {@link javax.speech.AudioManager}</li>
 * <li>{@link #createSpeechEventExecutor()} to create the
 * {@link javax.speech.SpeechEventExecutor}</li>
 * <li>{@link #createVocabularyManager()} to create the
 * {@link javax.speech.VocabularyManager}</li>
 * </ul>
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 */
public abstract class BaseEngine implements Engine {

    private static final Logger logger = getLogger(BaseEngine.class.getName());
    
    /**
     * A bitmask holding the current state of this <code>Engine</code>.
     */
    private long engineState;

    /**
     * An {@code Object} used for synchronizing access to {@link #engineState}.
     *
     * @see #engineState
     */
    private final Object engineStateLock;

    /**
     * A counter keeping track of nested calls to <code>pause</code> and
     * <code>resume</code> A value greater than 1 mean nested pauses. If the
     * value is 1 or lower, the Engine can be resumed immediately.
     *
     * @see #pause()
     * @see #resume()
     */
    private int pauses;

    /**
     * List of {@link EngineListener}s registered for {@link EngineEvent}s on
     * this {@link Engine}.
     * <p>
     * For a synthesizer this list will contain only
     * {@link javax.speech.synthesis.SynthesizerListener}s and for a recognizer
     * only {@link javax.speech.recognition.RecognizerListener}s.
     */
    private final Collection<EngineListener> engineListeners;

    /**
     * The {@link AudioManager} for this {@link Engine}.
     */
    private AudioManager audioManager;

    /**
     * The {@link VocabularyManager} for this {@link Engine}.
     */
    private VocabularyManager vocabularyManager;

    /**
     * The {@link EngineMode} for this {@link Engine}.
     */
    private EngineMode engineMode;

    /** The current speech event executor. */
    private SpeechEventExecutor speechEventExecutor;

    /**
     * Utility state for clearing the {@link #engineState}.
     */
    protected static final long CLEAR_ALL_STATE = ~(0L);

    /** The engine mask. */
    private int engineMask = EngineEvent.DEFAULT_MASK;

    /**
     * Creates a new {@link Engine} in the <code>DEALLOCATED</code> state.
     *
     * @param mode the operating mode of this <code>Engine</code>
     */
    public BaseEngine(EngineMode mode) {
        engineMode = mode;
        engineListeners = new java.util.ArrayList<>();
        engineState = DEALLOCATED;
        engineStateLock = new Object();
        pauses = 0;
    }

    /**
     * Returns a or'ed set of flags indicating the current state of this
     * <code>Engine</code>.
     *
     * <p>
     * An <code>EngineEvent</code> is issued each time this <code>Engine</code>
     * changes state.
     *
     * <p>
     * The <code>getEngineState</code> method can be called successfully in any
     * <code>Engine</code> state.
     *
     * @return the current state of this <code>Engine</code>
     * @see #getEngineState
     * @see #waitEngineState
     */
    public final long getEngineState() {
        return engineState;
    }

    @Override
    public final long waitEngineState(long state) throws InterruptedException {
        return waitEngineState(state, 0);
    }

    @Override
    public final long waitEngineState(long state, long timeout) throws InterruptedException {
        if (!isValid(state)) {
            throw new IllegalArgumentException("Cannot wait for impossible state: " + stateToString(state));
        }
        if (testEngineState(state)) {
            return state;
        }

        if (!isReachable(state)) {
            throw new IllegalStateException("State is not reachable: " + stateToString(state));
        }

        // Wait for a state change
        if (timeout > 0) {
            synchronized (engineStateLock) {
                while (!testEngineState(state)) {
                    engineStateLock.wait(timeout);
                }
            }
        } else {
            // Will wait forever to reach that state
            synchronized (engineStateLock) {
                while (!testEngineState(state)) {
                    engineStateLock.wait();
                }
            }
        }
        return getEngineState();
    }

    /**
     * Returns {@code true} if this state of this <code>Engine</code> matches
     * the specified state.
     *
     * <p>
     * The test performed is not an exact match to the current state. Only the
     * specified states are tested. For example the following returns true only
     * if the {@link javax.speech.synthesis.Synthesizer} queue is empty,
     * irrespective of the pause/resume and allocation states.
     *
     * <PRE>
     * if (synth.testEngineState(Synthesizer.QUEUE_EMPTY)) ...
     * </PRE>
     *
     * <p>
     * The <code>testEngineState</code> method is equivalent to:
     *
     * <PRE>
     * if ((engine.getEngineState() &amp; state) == state)
     * </PRE>
     * <p>
     * {@inheritDoc}
     */
    public final boolean testEngineState(long state) {
        return (getEngineState() & state) == state;
    }

    /**
     * Updates this {@link Engine} state by clearing defined bits, then setting
     * other specified bits.
     *
     * @param clear the flags to clear
     * @param set   the flags to set
     * @return a length-2 array with old and new state values.
     */
    public final long[] setEngineState(long clear, long set) {
        long[] states = new long[2];
        synchronized (engineStateLock) {
            states[0] = engineState;
            engineState = engineState & (~clear);
            engineState = engineState | set;
            states[1] = engineState;
            engineStateLock.notifyAll();
        }
        return states;
    }

    @Override
    public final void allocate() throws AudioException, EngineException, EngineStateException, SecurityException {
        // Validate current state
        if (testEngineState(ALLOCATED) || testEngineState(ALLOCATING_RESOURCES)) {
            return;
        }

        checkEngineState(DEALLOCATING_RESOURCES);

        // Update current state
        long[] states = setEngineState(CLEAR_ALL_STATE, ALLOCATING_RESOURCES);
        postStateTransitionEngineEvent(states[0], states[1], EngineEvent.ENGINE_ALLOCATING_RESOURCES);

        // Handle engine allocation
        try {
            pauses = 0;
            // Handle allocate
            baseAllocate();
        } finally {
            // Roll back if allocation failed for any reason
            if (!testEngineState(ALLOCATED)) {
                states = setEngineState(CLEAR_ALL_STATE, DEALLOCATED);
                postStateTransitionEngineEvent(states[0], states[1], EngineEvent.ENGINE_DEALLOCATED);
            }
        }
    }

    /**
     * Called from the {@link #allocate()} method.
     * <p>
     * Within this method, also the state transition to {@link Engine#ALLOCATED}
     * must be posted, since the successive state is engine dependent.
     * </p>
     *
     * @throws AudioException       if any audio request fails
     * @throws EngineException      if an allocation error occurred or the Engine is not
     *                              operational.
     * @throws EngineStateException if called for an Engine in the DEALLOCATING_RESOURCES state
     * @throws SecurityException    if the application does not have permission for this Engine
     */
    protected abstract void baseAllocate() throws AudioException,
            EngineException, EngineStateException, SecurityException;

    @Override
    public final void allocate(int mode)
            throws AudioException, EngineException, EngineStateException,
            SecurityException, IllegalArgumentException {
        if (mode == ASYNCHRONOUS_MODE) {
            Runnable runnable = () -> {
                try {
                    allocate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };
            SpeechEventExecutor executor = getSpeechEventExecutor();
            executor.execute(runnable);
        } else if (mode == 0) {
            allocate();
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    @Override
    public final void deallocate() throws AudioException, EngineException, EngineStateException {

        // Validate current state
        if (testEngineState(DEALLOCATED) || testEngineState(DEALLOCATING_RESOURCES)) {
            return;
        }

        checkEngineState(ALLOCATING_RESOURCES);

        // Update current state
        long[] states = setEngineState(CLEAR_ALL_STATE, DEALLOCATING_RESOURCES);
        postStateTransitionEngineEvent(states[0], states[1], EngineEvent.ENGINE_DEALLOCATING_RESOURCES);
        baseDeallocate();

        terminateSpeechEventExecutor();
    }

    @Override
    public final void deallocate(int mode)
            throws AudioException, EngineException {
        if (mode == ASYNCHRONOUS_MODE) {
            Runnable runnable = () -> {
                try {
                    deallocate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };
            SpeechEventExecutor executor = getSpeechEventExecutor();
            executor.execute(runnable);
        } else if ((mode == 0) || (mode == IMMEDIATE_MODE)) {
            deallocate();
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    @Override
    public final void pause() {
        // Validate current state
        if (testEngineState(PAUSED)) {
            // Increase internal state counter for nested pauses/resumes
            synchronized (this) {
                pauses++;
            }
            return;
        }

        checkEngineState(DEALLOCATED | DEALLOCATING_RESOURCES);

        while (testEngineState(ALLOCATING_RESOURCES)) {
            try {
                waitEngineState(ALLOCATED);
            } catch (InterruptedException ex) {
                return;
            }
        }

        synchronized (this) {
            // Increase internal state counter for nested pauses/resumes
            pauses++;
        }

        // Handle pause
        basePause();

        // Adapt the state
        long[] states = setEngineState(RESUMED, PAUSED);
        postStateTransitionEngineEvent(states[0], states[1], EngineEvent.ENGINE_PAUSED);
    }

    @Override
    public final boolean resume() throws EngineStateException {
        // Do nothing if we are already resumed
        if (testEngineState(RESUMED)) {
            return true;
        }

        checkEngineState(DEALLOCATED | DEALLOCATING_RESOURCES);

        while (testEngineState(ALLOCATING_RESOURCES)) {
            try {
                waitEngineState(ALLOCATED);
            } catch (InterruptedException ex) {
                return false;
            }
        }

        // Check, if we are ready to resume.
        // This depends on the number of calls to pause()
        boolean resumeNow = false;
        synchronized (this) {
            if (pauses <= 1) {
                resumeNow = true;
            }
            pauses--;
        }
        if (resumeNow) {
            // Handle resume
            if (baseResume()) {
                long[] states = setEngineState(PAUSED, RESUMED);
                postStateTransitionEngineEvent(states[0], states[1], EngineEvent.ENGINE_RESUMED);
                return true;
            } else {
                return false;
            }
        } else {
            // Every pause must be resumed separately.
            // If the code reaches this point, we have a nested resume hence we
            // do NOT actually resume, but only decreased the pause-counter
            // further above
            return false;
        }
    }

    @Override
    public final AudioManager getAudioManager() {
        if (audioManager == null) {
            audioManager = createAudioManager();
        }
        return audioManager;
    }

    /**
     * Creates a new audio manager for this engine.
     *
     * @return new audio manager for this engine.
     */
    protected abstract AudioManager createAudioManager();

    @Override
    public final VocabularyManager getVocabularyManager() {
        if (vocabularyManager == null) {
            vocabularyManager = createVocabularyManager();
        }
        return vocabularyManager;
    }

    /**
     * Creates a new vocabulary manager for this engine.
     *
     * @return new vocabulary manager for this engine.
     */
    protected abstract VocabularyManager createVocabularyManager();

    /**
     * Gets the current operating properties and mode of this
     * <code>Engine</code>.
     *
     * @return the operating mode of this <code>Engine</code>
     */
    public final EngineMode getEngineMode() {
        return engineMode;
    }

    /**
     * Sets the current operating properties and mode of this {@link Engine}.
     *
     * @param mode the new operating mode of this <code>Engine</code>
     */
    protected final void setEngineMode(EngineMode mode) {
        engineMode = mode;
    }

    @Override
    public final SpeechEventExecutor getSpeechEventExecutor() {
        if (speechEventExecutor == null) {
            speechEventExecutor = createSpeechEventExecutor();
        }
        return speechEventExecutor;
    }

    /**
     * Creates a new speech event executor.
     *
     * @return the created speech event executor
     */
    protected abstract SpeechEventExecutor createSpeechEventExecutor();

    @Override
    public final void setSpeechEventExecutor(SpeechEventExecutor executor) {
        // Terminate a previously running executor.
        terminateSpeechEventExecutor();
        speechEventExecutor = executor;
    }

    private void terminateSpeechEventExecutor() {
        if (speechEventExecutor instanceof TerminatableSpeechEventExecutor) {
            TerminatableSpeechEventExecutor baseExecutor = (TerminatableSpeechEventExecutor) speechEventExecutor;
            baseExecutor.terminate();
        }
    }

    /**
     * Requests notification of <code>EngineEvents</code> from this
     * <code>Engine</code>.
     *
     * @param listener the listener to add.
     */
    protected final void addEngineListener(EngineListener listener) {
        synchronized (engineListeners) {
            if (!engineListeners.contains(listener)) {
                engineListeners.add(listener);
            }
        }
    }

    /**
     * Removes an {@link EngineListener} from the list of
     * {@link EngineListener}s.
     *
     * @param listener the listener to remove.
     */
    protected final void removeEngineListener(EngineListener listener) {
        synchronized (engineListeners) {
            engineListeners.remove(listener);
        }
    }

    @Override
    public final void setEngineMask(int mask) {
        engineMask = mask;
    }

    @Override
    public final int getEngineMask() {
        return engineMask;
    }

    /**
     * Posts the given event using the current {@link SpeechEventExecutor}.
     *
     * @param event the engine event to post.
     */
    protected final void postEngineEvent(EngineEvent event) {
        // Filter all events which are not observable due to the engine mask
        int id = event.getId();
logger.log(Level.TRACE, "event filtered: %s, %s, &: %08x, i: %08x, m: %08x", (engineMask & id) != id, event, engineMask & id, id, engineMask);
        if ((engineMask & id) != id) {
            return;
        }

        // Post the event in the configured speech event executor
        Runnable runnable = () -> {
            Collection<EngineListener> listeners;
            synchronized (engineListeners) {
                listeners = new java.util.ArrayList<>(engineListeners);
            }
            fireEvent(listeners, event);
        };

        SpeechEventExecutor executor = getSpeechEventExecutor();
        executor.execute(runnable);
    }

    /**
     * Convenience method that throws an {@link EngineStateException} if any of
     * the bits in the past state are set in the {@code state}.
     *
     * @param state the <code>Engine</code> state to check
     * @throws EngineStateException state is not the expected state
     */
    protected final void checkEngineState(long state) throws EngineStateException {
        long currentState = getEngineState();
        if ((currentState & state) != 0) {
            throw new EngineStateException("Invalid EngineState: expected=("
                    + stateToString(state) + ") current state=("
                    + stateToString(currentState) + ")");
        }
    }

    /**
     * Returns a <code>String</code> of the names of all the {@link Engine}
     * states in the given {@link Engine} state.
     *
     * @param state the bitmask of states
     * @return a <code>String</code> containing the names of all the states set
     * in <code>state</code>
     */
    public String stateToString(long state) {
        StringBuilder buf = new StringBuilder();
        if ((state & Engine.DEALLOCATED) != 0) {
            buf.append("DEALLOCATED");
        }
        if ((state & Engine.ALLOCATING_RESOURCES) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("ALLOCATING_RESOURCES");
        }
        if ((state & Engine.ALLOCATED) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("ALLOCATED");
        }
        if ((state & Engine.DEALLOCATING_RESOURCES) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("DEALLOCATING_RESOURCES");
        }
        if ((state & Engine.PAUSED) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("PAUSED");
        }
        if ((state & Engine.RESUMED) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("RESUMED");
        }
        if ((state & Engine.FOCUSED) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("FOCUSED");
        }
        if ((state & Engine.DEFOCUSED) != 0) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append("DEFOCUSED");
        }
        return buf.toString();
    }

    /**
     * Returns the engine name and mode for debug purposes.
     *
     * @return the engine name and mode.
     */
    public String toString() {
        EngineMode mode = getEngineMode();
        if (mode == null) {
            return super.toString();
        }
        return mode.getEngineName() + ":" + mode.getModeName();
    }

    /**
     * Retrieves the bit mask of all possible states of this engine.
     *
     * @return bit mask
     */
    protected long getEngineStates() {
        return PAUSED | RESUMED | FOCUSED | DEFOCUSED | ALLOCATED
                | ALLOCATING_RESOURCES | DEALLOCATED | DEALLOCATING_RESOURCES;
    }

    /**
     * Checks if the given state is a valid state.
     *
     * @param state long
     * @return boolean
     */
    private boolean isValid(long state) {
        if (state == 0) {
            return false;
        }
        long states = getEngineStates();
        return (state | states) == states;
    }

    /**
     * Checks if the given engine state can be reached from the current engine
     * state.
     *
     * @param state the state to reach
     * @return {@code true} if the state can be reached
     */
    protected boolean isReachable(long state) {
        if ((state & ERROR_OCCURRED) == ERROR_OCCURRED) {
            return false;
        }

        if (!testEngineState(ALLOCATED)) {
            if (((state & RESUMED) == RESUMED) || ((state & PAUSED) == PAUSED)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Called from the {@link #deallocate()} method. Override this in
     * subclasses.
     *
     * @throws EngineException      if this <code>Engine</code> cannot be deallocated.
     * @throws EngineStateException if called for an {@link Engine} in the
     *                              {@link Engine#DEALLOCATING_RESOURCES} state
     * @throws AudioException       if an audio request fails
     */
    protected abstract void baseDeallocate() throws EngineStateException, EngineException, AudioException;

    /**
     * Called from the <code>pause</code> method. Override this in subclasses.
     *
     * @throws EngineStateException when not in the standard conditions for Operation
     */
    protected abstract void basePause() throws EngineStateException;

    /**
     * Called from the <code>resume</code> method. Override in subclasses.
     *
     * @return <code>true</code> if the {@link Engine} is in or is about to
     * enter the {@link Engine#RESUMED} state; or <code>false</code> if
     * it will remain {@link Engine#PAUSED} due to nested calls to
     * pause.
     * @throws EngineStateException when not in the standard conditions for Operation
     */
    protected abstract boolean baseResume() throws EngineStateException;

    /**
     * Notifies all listeners about the given event. This method is being called
     * using the currently configured {@link SpeechEventExecutor}.
     * <p>
     * This is needed since the event listeners for the synthesizer and the
     * recognizer have different notification signatures.
     * </p>
     *
     * @param listeners all listeners to notify
     * @param event     the event
     */
    protected abstract void fireEvent(Collection<EngineListener> listeners, EngineEvent event);

    /**
     * Notifies all registered listeners about a state transition.
     *
     * @param oldState the old engine state
     * @param newState the new engine state.
     * @param id       the event identifier.
     */
    protected final void postStateTransitionEngineEvent(long oldState, long newState, int id) {
        EngineEvent event = createStateTransitionEngineEvent(oldState, newState, id);
        postEngineEvent(event);
    }

    /**
     * Constructs an engine specific state transition event. Since
     * {@link EngineEvent}s are engine specific for
     * {@link javax.speech.synthesis.Synthesizer} and
     * {@link javax.speech.recognition.Recognizer} we need this factory method.
     *
     * @param oldState the old engine state
     * @param newState the new engine state.
     * @param id       the event identifier.
     * @return created event
     */
    protected abstract EngineEvent createStateTransitionEngineEvent(
            long oldState, long newState, int id);

    /**
     * Engines must override this method to apply a pending property change
     * request. After a pending request is handled, the engine implementation
     * must take care to call
     * {@link BaseEngineProperties#commitPropertyChange(String, Object, Object)}
     * to notify registered {@link javax.speech.EnginePropertyListener}s that
     * the change took effect.
     * <p>
     * Depending on the property, implementations may throw an
     * {@link IllegalArgumentException} in case the the value is not supported.
     * The requested value is considered to be a hint and may be ignored.
     * </p>
     *
     * @param properties the engine properties
     * @param propName   the name of the property
     * @param oldValue   the old value
     * @param newValue   the requested new value
     */
    protected abstract void handlePropertyChangeRequest(
            BaseEngineProperties properties, String propName, Object oldValue, Object newValue);
}
