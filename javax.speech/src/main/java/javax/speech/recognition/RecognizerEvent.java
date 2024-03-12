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

package javax.speech.recognition;

import java.util.List;
import javax.speech.EngineEvent;

/**
 * Event issued by a Recognizer to indicate a change in state or other activity.
 * <p>
 * A RecognizerEvent is issued to each RecognizerListener attached to a
 * Recognizer using the addRecognizerListener method in the Recognizer
 * interface.
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.RecognizerListener
 * @see javax.speech.recognition.Recognizer#addRecognizerListener(javax.speech.recognition.RecognizerListener)
 * @see javax.speech.recognition.Recognizer#removeRecognizerListener(javax.speech.recognition.RecognizerListener)
 * @since 2.0.6
 */
public class RecognizerEvent extends EngineEvent {

    /**
     * Event issued when a Recognizer changes from
     * the LISTENING state to the PROCESSING state to indicate that
     * it is actively processing a recognition Result.
     * <p>
     * The transition is triggered when the recognizer detects speech in
     * the incoming audio stream that may match an active grammar.
     * The transition occurs immediately before the RESULT_CREATED event
     * is issued to ResultListeners.
     */
    public static final int RECOGNIZER_PROCESSING = 0x2000800;

    public static final int RECOGNIZER_LISTENING = 0x2001000;

    /**
     * Event issued when a Recognizer changes
     * from the SUSPENDED state to the LISTENING state.
     * <p>
     * This state transition takes place when changes to the definition and
     * enabled state of all of a Recognizer's Grammars have been applied.
     * The new Grammar definitions are used as incoming speech is recognized
     * in the LISTENING and PROCESSING states of the Recognizer.
     * <p>
     * Immediately following the CHANGES_COMMITTED event,
     * GRAMMAR_CHANGES_COMMITTED events are issued to the GrammarListeners
     * of each changed Grammar.
     * <p>
     * If any errors are detected in any Grammar's definition during
     * the commit, a GrammarException is provided with this event.
     * The GrammarException is also included with the GRAMMAR_CHANGES_COMMITTED
     * event to the Grammar with the error.
     * The GrammarException has the same function as the GrammarException
     * thrown on the commitChanges method.
     * <p>
     * The causes and timing of the CHANGES_COMMITTED event are described in the
     * <p>
     * <A href="Recognizer.html#TypicalEventCycle">normal event cycle</A>
     * for a Recognizer and with the
     * <p>
     * <A href="Grammar.html#commit">committing changes</A>
     * documentation for a Grammar.
     */
    public static final int CHANGES_COMMITTED = 0x2002000;

    public static final int CHANGES_REJECTED = 0x2040000;

    /**
     * Event issued when a Recognizer detects the possible start
     * of speech in the incoming audio.
     * <p>
     * Applications may use this event to display visual feedback
     * to a user indicating that the Recognizer is listening.
     * <p>
     * It is sometimes difficult to quickly distinguish between speech
     * and other noises (e.g. coughs, microphone bumps), so the SPEECH_STARTED
     * event is not always followed by a Result.
     * <p>
     * If a RESULT_CREATED event is issued for the detected speech,
     * it will usually occur soon after
     * the SPEECH_STARTED event but may be delayed for
     * a number of reasons including:
     * <p>
     * The Recognizer may be slower than real time and lag audio input.
     * <p>
     * The Recognizer may defer issuing a RESULT_CREATED until it is confident
     * that it has detected speech that matches one of the active grammars -
     * in some cases the RESULT_CREATED event may be issued at the end
     * of the spoken sentence.
     * <p>
     * Some Recognizers will allow a user to speak
     * more than one command without a break.
     * In these cases,
     * a single SPEECH_STARTED event may be followed by more than
     * one RESULT_CREATED event and result finalization before
     * the SPEECH_STOPPED event is issued.
     * <p>
     * In longer speech,
     * short pauses in the user's speech may lead to a SPEECH_STOPPED
     * event followed by a SPEECH_STARTED event as the user resumes speaking.
     * These events do not always indicate that
     * the current Result will be finalized.
     */
    public static final int SPEECH_STARTED = 0x2004000;

    /**
     * Event issued when a Recognizer detects the possible end of speech
     * or noise in the incoming audio that it previously
     * indicated by a SPEECH_STARTED event.
     * <p>
     * This event always follows a SPEECH_STARTED event.
     * See that event description for more detail.
     */
    public static final int SPEECH_STOPPED = 0x2008000;

    public static final int RECOGNIZER_NOT_BUFFERING = 0x2010000;

    public static final int RECOGNIZER_BUFFERING = 0x2020000;

    public static final int RECOGNIZER_STOPPED = 0x2040000;

    /**
     * Value returned when the getAudioPosition method is not supported.
     */
    public static final int UNKNOWN_AUDIO_POSITION = -1;

    public static final int DEFAULT_MASK = EngineEvent.DEFAULT_MASK
            | CHANGES_COMMITTED | CHANGES_REJECTED | RECOGNIZER_LISTENING
            | RECOGNIZER_PROCESSING | SPEECH_STARTED | SPEECH_STOPPED;
    /**
     * The default mask for events in this class.
     * <p>
     * The following events, in addition to events in
     * <p>
     * {@link javax.speech.EngineEvent#DEFAULT_MASK}
     * , are delivered by default:
     * CHANGES_COMMITTED,
     * RECOGNIZER_PROCESSING, RECOGNIZER_SUSPENDED,
     * SPEECH_STARTED, and SPEECH_STOPPED.
     */

    private GrammarException grammarException;

    private long audioPosition;

    /**
     * Constructs a RecognizerEvent to indicate a
     * change in state or other activity.
     * <p>
     * The old and new states are zero if the
     * engine states are unknown or undefined.
     * For an ENGINE_ERROR, problem should be non-null.
     * For CHANGES_COMMITTED,
     * grammarException should be non-null if a grammar
     * exception is detected.
     * For SPEECH_STARTED and SPEECH_STOPPED,
     * the audioPosition should be available.
     * @param source the Recognizer that issued the event
     * @param id the identifier for the event type
     * @param oldEngineState the Engine state prior to this event
     * @param newEngineState the Engine state following this event
     * @param problem non-null Engine problem if this is an ENGINE_ERROR event
     * @param grammarException non-null if a GrammarException is
     *         detected during CHANGES_COMMITTED
     * @param audioPosition the audio position for this event
     * @see java.util.EventObject#getSource()
     * @see javax.speech.SpeechEvent#getId()
     * @see javax.speech.EngineEvent#getOldEngineState()
     * @see javax.speech.EngineEvent#getNewEngineState()
     * @see javax.speech.EngineEvent#getEngineError()
     * @see javax.speech.recognition.RecognizerEvent#getGrammarException()
     * @see javax.speech.recognition.RecognizerEvent#getAudioPosition()
     * @see javax.speech.EngineEvent#ENGINE_ERROR
     * @see javax.speech.recognition.RecognizerEvent#CHANGES_COMMITTED
     * @see javax.speech.recognition.RecognizerEvent#SPEECH_STARTED
     * @see javax.speech.recognition.RecognizerEvent#SPEECH_STOPPED
     */
    public RecognizerEvent(Recognizer source, int id, long oldEngineState,
                           long newEngineState, Throwable problem,
                           GrammarException grammarException, long audioPosition) throws IllegalArgumentException {
        super(source, id, oldEngineState, newEngineState, problem);
        if ((id != CHANGES_REJECTED) && (grammarException != null)) {
            throw new IllegalArgumentException("A grammar exception can only be specified for CHANGES_REJECTED");
        }
        if ((id == SPEECH_STARTED) || (id == SPEECH_STOPPED)
                || (id == RECOGNIZER_BUFFERING)
                || (id == RECOGNIZER_NOT_BUFFERING)) {
            if (audioPosition < 0) {
                throw new IllegalArgumentException("Audio position must be a non-negative integer!");
            }
        } else {
            if (audioPosition != UNKNOWN_AUDIO_POSITION) {
                StringBuffer str = new StringBuffer();
                id2String(str);
                throw new IllegalArgumentException("Audio position must be UNKNOWN_AUDIO_POSITION for"
                                + " the given event id (" + str + ")!");
            }
        }
        this.grammarException = grammarException;
        this.audioPosition = audioPosition;
    }

    /**
     * Gets the audio position in the audio stream.
     * <p>
     * The audio position is the time in milliseconds measuring the amount
     * of audio processed since the audio began.
     * <p>
     * Valid values are zero and above.
     * If the value is not known, a value of UNKNOWN_AUDIO_POSITION is returned.
     * @return the audio position
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.recognition.RecognizerEvent#SPEECH_STARTED
     * @see javax.speech.recognition.RecognizerEvent#SPEECH_STOPPED
     */
    public long getAudioPosition() {
        return audioPosition;
    }

    /**
     * Returns non-null for a CHANGES_COMMITTED event if an error is found
     * in a Grammar definition.
     * <p>
     * The exception serves the same functional role as the GrammarException
     * thrown on the commitChanges method.
     * @return a GrammarException object
     * @see javax.speech.recognition.RecognizerEvent#CHANGES_COMMITTED
     * @see javax.speech.recognition.GrammarException
     * @see javax.speech.recognition.Recognizer#processGrammars()
     */
    public GrammarException getGrammarException() {
        int id = getId();
        if (id == CHANGES_REJECTED) {
            return grammarException;
        }

        return null;
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        Long audioPositionObject = audioPosition;
        parameters.add(audioPositionObject);
        parameters.add(grammarException);

        return parameters;
    }
}
