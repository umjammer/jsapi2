/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 65 $
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

package javax.speech.synthesis;

import javax.speech.AudioSegment;
import javax.speech.Engine;
import javax.speech.EngineStateException;

// Comp 2.0.6

/**
 * Provides primary access to speech synthesis
 * capabilities. The Synthesizer interface extends the Engine interface.
 * Thus, any Synthesizer implements basic speech engine capabilities plus
 * the specialized capabilities required for speech synthesis.
 * <p>
 * The primary functions provided by the Synthesizer interface are the
 * ability to speak text, speak the Speech Synthesis Markup Language,
 * and control an output queue of objects to be spoken.
 * <p>
 * Creating a Synthesizer
 * <p>
 * A Synthesizer is created by a call to the
 * EngineManager.createEngine method.
 * A Synthesizer for the default Locale is create as follows:
 * <p>
 * Synthesizer synth = (Synthesizer)
 * EngineManager.engineCreate(SynthesizerMode.DEFAULT);
 * <p>
 * The synthesis package overview contains a
 * <p>
 * .
 * Detailed descriptions of the procedures for locating,
 * selecting, creating and initializing a Synthesizer are provided in the
 * documentation for the
 * <p>
 * .
 * <p>
 * Speaking Text
 * <p>
 * The basic function of a Synthesizer is to speak text provided to it
 * by an application. This text can be plain Unicode text in a String or
 * can be markup text using the
 * <p>
 * <A href="http://www.w3.org/TR/speech-synthesis/">Speech Synthesis Markup Language</A>
 * (SSML).
 * <p>
 * Plain text is spoken using one of the speak methods. Markup text is
 * spoken using the speakMarkup method. The other speak methods obtain
 * markup text from a Speakable object or from an AudioSegment
 * (if needed and provided).
 * <p>
 * Note that markup text provided programmatically (by a Speakable object
 * or a String) does not require the full XML header.
 * <p>
 * A synthesizer may be mono-lingual (it speaks a single language) in which case
 * the text should contain only the single language of the synthesizer.
 * An application requiring output of more than one language with a
 * mono-lingual synthesizer must
 * create multiple Synthesizer objects through EngineManager.
 * The language
 * of the Synthesizer should be selected at the time at which it is created.
 * The languages for a created Synthesizer can be checked through the Voices
 * of its SynthesizerMode (see
 * {@link javax.speech.Engine#getEngineMode()}
 * ).
 * <p>
 * Each object provided to a synthesizer is spoken independently.
 * Sentences, phrases and other structures should not span multiple calls
 * to the speak methods.
 * <p>
 * Synthesizer adds a pair of substates to the ALLOCATED state
 * to represent
 * the state of the speech output queue (queuing is described in more detail
 * below). For an ALLOCATED Synthesizer, the speech output queue is either
 * empty or not empty represented by the states
 * and
 * <p>
 * , respectively.
 * <p>
 * The queue status is independent of the pause/resume status.
 * Pausing or resuming a synthesizer does not affect the queue.
 * Adding or removing objects from the queue does not affect the
 * pause/resume status. The only form of interaction between these state
 * systems is that the Synthesizer only speaks in the RESUMED state and,
 * therefore, a transition from QUEUE_NOT_EMPTY to QUEUE_EMPTY because
 * of completion of speaking an object is only possible in the RESUMED state.
 * A transition from QUEUE_NOT_EMPTY to QUEUE_EMPTY is possible in the PAUSED
 * state only through a call to one of the cancel methods.
 * <p>
 * Speech Output Queue
 * <p>
 * A synthesizer implements a queue of items provided to it through the
 * speak and speakMarkup methods. The queue is "first-in, first-out" (FIFO)
 * - the objects are spoken in exactly the order in which they are received.
 * The object at the top of the queue is the object that is currently being
 * spoken or about to be spoken.
 * <p>
 * The QUEUE_EMPTY and QUEUE_NOT_EMPTY states of a Synthesizer indicate
 * the current state of of the speech output queue. The state handling methods
 * inherited from the Engine interface (getEngineState, waitEngineState
 * and testEngineState) can be used to test the queue state.
 * <p>
 * The cancel methods allows an application to (a) stop the output of the item
 * currently at the top of the speaking queue, (b) remove an arbitrary item
 * from the queue, or (c) remove all items from the output queue.
 * <p>
 * Applications requiring more complex queuing mechanisms
 * (e.g. a prioritized queue) can implement their own queuing objects
 * that control the synthesizer.
 * <p>
 * Pause and Resume
 * <p>
 * The pause and resume methods (inherited from the Engine
 * interface) provide behavior similar to a "tape player".
 * Pause stops audio output
 * as soon as possible. Resume restarts audio output from the point of the
 * pause. Pause and resume may occur within words, phrases or unnatural
 * points in the speech output.
 * A Synthesizer starts in the RESUMED state.
 * <p>
 * Pause and resume do not affect the speech output queue.
 * <p>
 * In addition to the ENGINE_PAUSED and ENGINE_RESUMED events issued
 * to the SynthesizerListener, SPEAKABLE_PAUSED and
 * SPEAKABLE_RESUMED events are issued to appropriate SpeakableListeners
 * for the Speakable object at the top of the speaking queue.
 * A SpeakableEvent is first issued to any SpeakableListener provided
 * with the speak method and then to each SpeakableListener attached to the
 * Synthesizer. Finally, the SynthesizerEvent is issued to each
 * SynthesizerListener attached to the Synthesizer.
 * <p>
 * Applications can determine the approximate point at which a pause
 * occurs by monitoring the WORD_STARTED events.
 * @see javax.speech.EngineManager
 * @see javax.speech.Engine
 * @see javax.speech.synthesis.SynthesizerEvent
 * @see javax.speech.synthesis.SynthesizerListener
 * @see javax.speech.synthesis.Speakable
 * @see javax.speech.synthesis.SpeakableEvent
 * @see javax.speech.synthesis.SpeakableListener
 */
public interface Synthesizer extends Engine {

    /**
     * Bit of state that is set when the speech output queue of a Synthesizer
     * is empty.
     * <p>
     * The QUEUE_EMPTY state is a substate of the ALLOCATED state.
     * An allocated Synthesizer is always in either the QUEUE_NOT_EMPTY
     * or QUEUE_EMPTY state.
     * <p>
     * A Synthesizer is always allocated in the QUEUE_EMPTY state.
     * The Synthesizer transitions from the QUEUE_EMPTY state to the
     * QUEUE_NOT_EMPTY state when a call to one of the speak methods places
     * an object on the speech output queue. A QUEUE_UPDATED event is issued
     * to indicate this change in state.
     * <p>
     * A Synthesizer returns from the QUEUE_NOT_EMPTY state to the QUEUE_EMPTY
     * state once the queue is emptied because of completion of speaking all
     * objects or because of a cancel.
     * <p>
     * The queue status can be tested with the waitQueueEmpty, getEngineState
     * and testEngineState methods.
     * <p>
     * <A/>
     * Example:
     * <p>
     * // Block a thread until the queue is empty
     * Synthesizer synth = ...;
     * synth.allocate();                          // allocate resources
     * synth.speak("hello world", null);          // speak
     * synth.waitEngineState(QUEUE_EMPTY);        // wait for finish
     */
    long QUEUE_EMPTY = 0x1000;

    /**
     * Bit of state that is set when the speech output queue of a Synthesizer
     * is not empty.
     * <p>
     * The QUEUE_NOT_EMPTY state is a substate of the ALLOCATED state.
     * An allocated Synthesizer is always in either the QUEUE_NOT_EMPTY or
     * QUEUE_EMPTY state.
     * <p>
     * A Synthesizer enters the QUEUE_NOT_EMPTY state from the QUEUE_EMPTY
     * state when one of the speak methods is called to place an object on
     * the speech output queue. A QUEUE_UPDATED event is issued to mark
     * this change in state.
     * <p>
     * A Synthesizer returns from the QUEUE_NOT_EMPTY state to the QUEUE_EMPTY
     * state once the queue is emptied because of completion of speaking all
     * objects or because of a cancel.
     */
    long QUEUE_NOT_EMPTY = 0x2000;

    /**
     * Request notifications of all SpeakableEvents for all speech output
     * objects for this Synthesizer.
     * <p>
     * An application can attach multiple SpeakableListeners to a Synthesizer.
     * A single listener can be attached to multiple synthesizers.
     * <p>
     * When an event affects more than one item in the speech output queue
     * (e.g. cancelAll), the SpeakableEvents are issued in the order
     * of the items in the queue starting with the top of the queue.
     * <p>
     * A SpeakableListener can also be provided for an individual speech
     * output item by providing it as a parameter to one of the speak
     * or speakMarkup methods.
     * <p>
     * A SpeakableListener can be attached or removed in any Engine state.
     * <p>
     * SpeakableEvents may be filtered with setSpeakableMask.
     * <p>
     * Use addSynthesizerListener for notifications related to general
     * operation of this Synthesizer.
     * @param listener the listener that will receive SpeakableEvents
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.synthesis.SpeakableListener
     * @see javax.speech.synthesis.Synthesizer#removeSpeakableListener(javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.Synthesizer#setSpeakableMask(int)
     * @see javax.speech.synthesis.Synthesizer#addSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
     */
    void addSpeakableListener(SpeakableListener listener);

    /**
     * Removes a SpeakableListener from this Synthesizer.
     * <p>
     * A SpeakableListener can be attached or removed in any Engine state.
     * @param listener the listener to remove
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     */
    void removeSpeakableListener(SpeakableListener listener);

    /**
     * Request notifications of events related to general operation of this Synthesizer.
     * The SynthesizerEvent class provides details about these events.
     * <p>
     * An application can attach multiple listeners to this Synthesizer.
     * A single listener can be attached to multiple Synthesizers.
     * <p>
     * A SynthesizerListener can be attached or removed in any state.
     * <p>
     * SynthesizerEvents may be filtered with setEngineMask.
     * <p>
     * Use addSpeakableListener for notifications related to individual
     * speech output objects.
     * @param listener the listener that will receive SynthesizerEvents
     * @see javax.speech.synthesis.SynthesizerEvent
     * @see javax.speech.synthesis.SynthesizerListener
     * @see javax.speech.synthesis.Synthesizer#removeSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
     * @see javax.speech.Engine#setEngineMask(int)
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     */
    void addSynthesizerListener(SynthesizerListener listener);

    /**
     * Removes a SynthesizerListener from this Synthesizer.
     * <p>
     * A SynthesizerListener can be removed in any state of an Engine.
     * @param listener the listener to remove
     * @see javax.speech.synthesis.Synthesizer#addSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
     */
    void removeSynthesizerListener(SynthesizerListener listener);

    boolean cancel() throws EngineStateException;

    boolean cancel(int id) throws EngineStateException;

    boolean cancelAll() throws EngineStateException;

    /**
     * Returns the phoneme string for a text string.
     * <p>
     * The return string uses the International Phonetic Alphabet subset
     * of Unicode. The input string is expected to be simple text
     * (for example, a word or phrase in English).
     * The text is not expected to contain any synthesis markup or
     * punctuation between words such as periods or commas.
     * The text may contain characters such as hyphens and apostrophes where
     * expected within words or synthesis markup.
     * Any normal text normalization will be performed on the string (e.g.,
     * the pronunciation for "100" is the same as for "one hundred").
     * <p>
     * If the Synthesizer does not support text-to-phoneme conversion or
     * cannot process the input text it will return null.
     * <p>
     * If the text has multiple pronunciations, there is no way to indicate
     * which pronunciation is preferred.
     * <p>
     * This method operates as defined only when a Synthesizer is in the
     * ALLOCATED state. The call blocks if the Synthesizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown for synthesizers in the
     * DEALLOCATED or DEALLOCATING_RESOURCES states.
     * @param text plain text to be converted to phonemes
     * @return phonemic representation of text or null
     * @throws javax.speech.EngineStateException if called for a synthesizer in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     */
    String getPhonemes(String text) throws EngineStateException;

    /**
     * Returns the properties of this Synthesizer.
     * <p>
     * The SynthesizerProperties are available in any state of an Engine.
     * However, changes only take effect once an engine reaches the
     * ALLOCATED state.
     * @return the SynthesizerProperties object for this Synthesizer
     */
    SynthesizerProperties getSynthesizerProperties();

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
     * <p>
     * For a Synthesizer,
     * the speaking queue is left intact and a subsequent resume continues
     * output from the point at which the pause took effect.
     * @throws javax.speech.EngineStateException if called for an Engine in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     * @see javax.speech.Engine#resume()
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.EngineEvent#ENGINE_PAUSED
     */
    void pause() throws EngineStateException;

    boolean resume() throws EngineStateException;

    /**
     * Sets the mask to filter events for added SpeakableListeners.
     * <p>
     * It can improve efficiency to mask events that are not used.
     * Events of interest can be seen by using the logical AND of the
     * event constants defined in the SpeakableEvent class.
     * SpeakableEvent.DEFAULT_MASK is the initial value.
     * <p>
     * The event mask can be set and retrieved in any state of an Engine.
     * @param mask the mask for SpeakableEvents
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.synthesis.Synthesizer#getSpeakableMask()
     * @see javax.speech.synthesis.SpeakableEvent#DEFAULT_MASK
     */
    void setSpeakableMask(int mask);

    /**
     * Gets the mask to filter events for added SpeakableListeners.
     * <p>
     * See setSpeakableMask for details.
     * <p>
     * The event mask can be set and retrieved in any state of an Engine.
     * @return the current mask for SpeakableEvents
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.synthesis.Synthesizer#setSpeakableMask(int)
     */
    int getSpeakableMask();

    /**
     * Speaks an AudioSegment.
     * <p>
     * The audio is placed at the end of the speaking queue
     * and will be spoken once it reaches the top of the queue and the
     * synthesizer is in the RESUMED state. In other respects it is similar
     * to the speak method that accepts a Speakable object.
     * <p>
     * The source of a SpeakableEvent issued to the SpeakableListener is
     * the AudioSegment.
     * <p>
     * The speak methods operate as defined only when a Synthesizer is
     * in the ALLOCATED state. The call blocks if the Synthesizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown for synthesizers in the
     * DEALLOCATED or DEALLOCATING_RESOURCES states.
     * @param audio an AudioSegment to be spoken
     * @param listener receives notification of events as synthesis output
     *  proceeds
     * @return a unique id for this request
     * @throws javax.speech.EngineStateException if called for a synthesizer in the
     *  DEALLOCATED or DEALLOCATING_RESOURCES states
     * @throws java.lang.IllegalArgumentException if the Synthesizer cannot interpret
     *  the audio segment
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.SpeakableEvent
     */
    int speak(AudioSegment audio, SpeakableListener listener)
            throws SpeakableException, EngineStateException, IllegalArgumentException;

    /**
     * Speaks an object that implements the Speakable interface and provides
     * synthesis markup text.
     * <p>
     * The Speakable object is added to the end of the speaking queue and
     * will be spoken once it reaches the top of the queue and the synthesizer
     * is in the RESUMED state.
     * <p>
     * The synthesizer first requests the text of the Speakable by calling
     * its getMarkupText method. It then checks the syntax of the synthesis markup
     * and throws a SynthesisException if any problems are found. If the synthesis markup text
     * is legal, the text is placed on the speech output queue.
     * <p>
     * When the speech output queue is updated, a QUEUE_UPDATE event is issued
     * to each attached SynthesizerListener.
     * <p>
     * Events associated with the Speakable object are issued to the
     * SpeakableListener object. The listener may be null. A listener
     * attached with this method cannot be removed with a subsequent
     * remove call.
     * The source for the SpeakableEvents is the speakable object.
     * <p>
     * SpeakableEvents can also be received by attaching a SpeakableListener
     * to the Synthesizer with the addSpeakableListener method.
     * A SpeakableListener attached to the Synthesizer receives all
     * SpeakableEvents for all speech output items of the synthesizer
     * (rather than for a single Speakable).
     * <p>
     * The speak call is asynchronous: it returns once the text for the
     * Speakable has been obtained, checked for syntax, and placed on the
     * synthesizer's speech output queue. An application needing to know
     * when the Speakable has been spoken should wait for the SPEAKABLE_ENDED
     * event to be issued to the SpeakableListener object. The getEngineState
     * and waitEngineState
     * methods can be used to determine
     * the speech output queue status.
     * <p>
     * An object placed on the speech output queue can be removed with one
     * of the cancel methods.
     * <p>
     * The speak methods operate as defined only when a Synthesizer is in
     * the ALLOCATED state. The call blocks if the Synthesizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown for synthesizers in the
     * DEALLOCATED or DEALLOCATING_RESOURCES states.
     * @param speakable object implementing the Speakable interface that
     *  provides synthesis markup to be spoken
     * @param listener receives notification of events as synthesis output
     *  proceeds
     * @return a unique id for this request
     * @throws javax.speech.synthesis.SpeakableException if any syntax errors are encountered in the
     *  synthesis markup
     * @throws javax.speech.EngineStateException if called for a synthesizer in the
     *  DEALLOCATED or DEALLOCATING_RESOURCES states
     * @throws java.lang.IllegalArgumentException if the engine does not support markup
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.EngineMode#getSupportsMarkup()
     */
    int speak(Speakable speakable, SpeakableListener listener) throws SpeakableException, EngineStateException;

    /**
     * Speaks a plain text String.
     * <p>
     * The text is not interpreted as containing synthesis markup so any
     * markup elements are ignored.
     * The text is placed at the end of the speaking queue
     * and will be spoken once it reaches the top of the queue and the
     * synthesizer is in the RESUMED state. In other respects it is similar
     * to the speak method that accepts a Speakable object.
     * <p>
     * The source of a SpeakableEvent issued to the SpeakableListener is
     * the String object.
     * <p>
     * To speak text from a URL, download the text and then use this method.
     * <p>
     * The speak methods operate as defined only when a Synthesizer is
     * in the ALLOCATED state. The call blocks if the Synthesizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown for synthesizers in the
     * DEALLOCATED or DEALLOCATING_RESOURCES states.
     * @param text String containing plain text to be spoken
     * @param listener receives notification of events as synthesis output
     *  proceeds
     * @return a unique id for this request
     * @throws javax.speech.EngineStateException if called for a synthesizer in the
     *  DEALLOCATED or DEALLOCATING_RESOURCES states
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.synthesis.Synthesizer#speakMarkup(java.lang.String, javax.speech.synthesis.SpeakableListener)
     */
    int speak(String text, SpeakableListener listener) throws EngineStateException;

    /**
     * Speaks a String containing text formatted with synthesis markup.
     * <p>
     * The synthesis markup is checked for formatting errors and a SynthesisException
     * is thrown if any are found. If there is not an exception, the text is placed at the end
     * of the speaking queue and will be spoken once it reaches the top of
     * the queue and the synthesizer is in the RESUMED state. In all other
     * respects is it identical to the speak method that accepts a Speakable
     * object.
     * <p>
     * Markup errors may also be encountered during synthesis in which case they
     * are reported in a MARKUP_FAILED event.
     * <p>
     * The source of a SpeakableEvent issued to the SpeakableListener is
     * the String.
     * <p>
     * To speak markup from a URL, download the markup first and then use this method.
     * <p>
     * The speak methods operate as defined only when a Synthesizer is in
     * the ALLOCATED state. The call blocks if the Synthesizer is in the
     * ALLOCATING_RESOURCES state and completes when the engine reaches
     * the ALLOCATED state.
     * An exception is thrown for synthesizers in the
     * DEALLOCATED or DEALLOCATING_RESOURCES states.
     * <p>
     * The following example includes emphasis and a change in rate:
     * <pre>
     * Synthesizer synth = ...
     * synth.speakMarkup("I
     * emphasis
     * really
     * /emphasis
     * like
     * prosody rate="slow"
     * speech
     * /prosody
     * ");
     * </pre>
     *
     * @param synthesisMarkup String containing synthesis markup to speak
     * @param listener receives notification of events as synthesis
     *         output proceeds
     * @return a unique id for this request
     * @throws javax.speech.synthesis.SpeakableException if any syntax errors are encountered in the synthesis markup
     * @throws javax.speech.EngineStateException if called for a synthesizer in the
     *          DEALLOCATED or DEALLOCATING_RESOURCES states
     * @throws java.lang.IllegalArgumentException if the engine does not support markup
     * @see javax.speech.synthesis.Synthesizer#speak(java.lang.String, javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.SpeakableEvent#SPEAKABLE_FAILED
     * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
     * @see javax.speech.synthesis.SpeakableEvent
     * @see javax.speech.EngineMode#getSupportsMarkup()
     */
    int speakMarkup(String synthesisMarkup, SpeakableListener listener)
            throws SpeakableException, EngineStateException;
}
