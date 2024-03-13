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

import java.util.List;
import javax.speech.SpeechEvent;

/**
 * Event issued during spoken output of text.
 * <p>
 * SpeakableEvents are issued to SpeakableListeners. A single
 * SpeakableListener can be provided to any of the speak and speakMarkup
 * methods of a Synthesizer to monitor progress of a single item on the
 * speech output queue. Any number of SpeakableListener objects can be
 * attached to a Synthesizer with the addSpeakableListener method.
 * These listeners receive every SpeakableEvent for every item on the
 * speech output queue of the Synthesizer. The SpeakableListener attached
 * to an individual item on the speech output queue is notified before
 * the SpeakableListeners attached to the Synthesizer.
 * <p>
 * The source for a SpeakableEvent is the object from which the
 * synthesis markup text was obtained: a Speakable object, String,
 * or AudioSegment.
 * <p>
 * The normal sequence of events during output of the item of the top
 * of the synthesizer's speech output is:
 * <code>
 * TOP_OF_QUEUE
 * SPEAKABLE_STARTED
 * Any number of ELEMENT_REACHED, VOICE_CHANGED,
 * PROSODY_UPDATED, MARKER_REACHED,
 * WORD_STARTED, PHONEME_STARTED,
 * and MARKUP_FAILED events
 * SPEAKABLE_ENDED
 * </code>
 * A SPEAKABLE_PAUSED may occur any time after the TOP_OF_QUEUE but before
 * the SPEAKABLE_ENDED event. A SPEAKABLE_PAUSED event can only be followed
 * by a SPEAKABLE_RESUMED or SPEAKABLE_CANCELLED event.
 * <p>
 * A SPEAKABLE_CANCELLED event can occur at any time before an
 * SPEAKABLE_ENDED (including before a TOP_OF_QUEUE event). No other
 * events can follow the SPEAKABLE_CANCELLED event since the item has been
 * removed from the speech output queue.
 * <p>
 * A SPEAKABLE_CANCELLED event can be issued for items that are not at
 * the top of the speech output queue. The other events are only issued
 * for the top-of-queue item.
 *
 * @see javax.speech.synthesis.Speakable
 * @see javax.speech.synthesis.SpeakableListener
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.synthesis.Synthesizer#addSpeakableListener(javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#removeSpeakableListener(javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speak(javax.speech.synthesis.Speakable, javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speak(java.lang.String, javax.speech.synthesis.SpeakableListener)
 * @see javax.speech.synthesis.Synthesizer#speakMarkup(java.lang.String, javax.speech.synthesis.SpeakableListener)
 * @since 2.0.6
 */
public class SpeakableEvent extends SpeechEvent {

    // Events.

    /**
     * Event issued when an item on the synthesizer's speech output queue reaches
     * the top of the queue.
     * <p>
     * If the Synthesizer is not paused, the TOP_OF_QUEUE event will be
     * followed immediately by the SPEAKABLE_STARTED event. If the Synthesizer
     * is paused, the SPEAKABLE_STARTED event will be delayed until the
     * Synthesizer is resumed.
     * <p>
     * A QUEUE_UPDATED is also issued when the speech output queue changes
     * (e.g. a new item at the top of the queue). The SpeakableEvent is
     * issued prior to the SynthesizerEvent.
     */
    public static final int TOP_OF_QUEUE = 0x7000001;

    /**
     * Event issued at the start of audio output of an item on the speech output
     * queue.
     * <p>
     * This event immediately follows the TOP_OF_QUEUE unless the Synthesizer
     * is paused when the speakable text is promoted to the top of the output
     * queue.
     */
    public static final int SPEAKABLE_STARTED = 0x7000002;

    /**
     * Event issued with the completion of audio output of an object on the
     * speech output queue as the object is removed from the queue.
     * <p>
     * A QUEUE_UPDATED or QUEUE_EMPTIED event is also issued when the speech
     * output queue changes because the speech output of the item at the top
     * of queue is completed. The SpeakableEvent is issued prior to the
     * SynthesizerEvent.
     */
    public static final int SPEAKABLE_ENDED = 0x7000004;

    /**
     * Event issued when audio output of the item at the top of a synthesizer's
     * speech output queue is paused.
     * <p>
     * The SPEAKABLE_PAUSED SpeakableEvent is issued prior to the ENGINE_PAUSED
     * event that is issued to the SynthesizerListener.
     */
    public static final int SPEAKABLE_PAUSED = 0x7000008;

    /**
     * Event issued when audio output of the item at the top of a synthesizer's
     * speech output queue is resumed after a previous pause.
     * <p>
     * The SPEAKABLE_RESUMED SpeakableEvent is issued prior to the
     * ENGINE_RESUMED event that is issued to the SynthesizerListener.
     */
    public static final int SPEAKABLE_RESUMED = 0x7000010;

    /**
     * Event issued when an item on the synthesizer's speech output queue
     * is cancelled and removed from the queue.
     * <p>
     * A speech output queue item may be cancelled at any time following
     * a call to speak. An item can be cancelled even if it is not at the
     * top of the speech output queue (other SpeakableEvents are issued only
     * to the top-of-queue item). Once cancelled, the listener for the
     * cancelled object receives no further SpeakableEvents.
     * <p>
     * The SPEAKABLE_CANCELLED event is issued prior to the
     * QUEUE_UPDATED or QUEUE_EMPTIED SynthesizerEvents.
     * <p>
     * This event may be caused by one of the cancel methods of Synthesizer.
     * It may also be caused by focus changes due to Synthesizer priority
     * or interruptibility.
     */
    public static final int SPEAKABLE_CANCELLED = 0x7000020;

    /**
     * Event issued when a synthesis engine starts the audio output of a word
     * in the speech output queue item.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * the next word to be spoken
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextBegin()}
     * character index of the word start within the speakable's string
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextEnd()}
     * character index of the word end within the speakable's string
     * <p>
     * The text of the next word to be spoken may differ from the
     * text defined by getWordStart and getWordEnd in the original
     * speakable's string being spoken.
     * When speaking the phrase "$100",
     * for example,
     * for the first WORD_STARTED event getTextInfo might return "one"
     * (for "one hundred dollars") and getWordStart and getWordEnd might return
     * 0 and 4 respectively to define the segment of the speakable's string
     * ("$100").
     */
    public static final int WORD_STARTED = 0x7000040;

    /**
     * Event issued when a synthesis engine starts the audio output of a phoneme
     * in the speech output queue item.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * returns the text of the word being spoken
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getPhones()}
     * returns an array of phones corresponding to the current text
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getIndex()}
     * returns the index of the next phone within the phone array
     * <p>
     * This may not be supported by all synthesizers.
     * Durations may have a value of UNKNOWN_DURATION if not supported.
     * When supported, the timing supplied by the durations may not be exact.
     * <p>
     * PHONEME_STARTED events follow the associated WORD_STARTED event.
     */
    public static final int PHONEME_STARTED = 0x7000080;

    /**
     * Event issued when audio output reaches a marker contained in markup
     * text.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * returns the string value of the
     * NAME attribute in the MARK tag.
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getAudioPosition()}
     * returns the position of the mark
     * in the output stream.
     * <p>
     * The following example shows how to include the MARK tag in markup text:
     * <pre>
     * Go from
     * mark name="HERE"/
     * here, to
     * mark name="THERE"/
     * there!
     * </pre>
     * For this example, a MARKER_REACHED SpeakableEvent
     * with the marker value "HERE"
     * will be sent to attached listeners as the word "here"
     * is about to be played
     * on the output device and similarly for the marker value "THERE"
     * as "there" is about to be played.
     */
    public static final int MARKER_REACHED = 0x7000100;

    /**
     * Event issued when audio output begins for a voice change specified
     * in markup text.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * returns the text of the VOICE element
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getOldVoice()}
     * returns the voice before the change
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getOldVoice()}
     * returns the voice after the change
     */
    public static final int VOICE_CHANGED = 0x7000200;

    public static final int SPEAKABLE_FAILED = 0x7000400;

    /**
     * Event issued when a prosody value changes due to markup.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * returns
     * the markup text that caused the change
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getType()}
     * returns
     * the type of prosody change requested:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#PROSODY_RATE}
     *
     * {@link javax.speech.synthesis.SpeakableEvent#PROSODY_PITCH}
     *
     * {@link javax.speech.synthesis.SpeakableEvent#PROSODY_VOLUME}
     *
     * {@link javax.speech.synthesis.SpeakableEvent#getRequestedValue()}
     * returns
     * the prosody value requested by the markup
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getRealizedValue()}
     * returns
     * the realized prosody value for the request
     */
    public static final int PROSODY_UPDATED = 0x7000800;

    /**
     * Event issued when audio output for an element of markup text
     * begins or ends.
     * <p>
     * The following methods provide additional information:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getTextInfo()}
     * returns
     * the tag name
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#getType()}
     * returns
     * the type of element reached:
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#ELEMENT_OPEN}
     * ,
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#ELEMENT_CLOSE}
     * , or
     * <p>
     * {@link javax.speech.synthesis.SpeakableEvent#ELEMENT_CLOSE}
     *
     * {@link javax.speech.synthesis.SpeakableEvent#getAttributes()}
     * returns
     * an array of any attribute/value pairs for this element
     * <p>
     * For example, if the markup text is
     * <pre>
     * prosody rate="-10%" volume="loud"
     * really
     * /prosody
     * </pre>
     * then for the type ELEMENT_OPEN, getTextInfo and
     * getAttributes will return values corresponding to
     * <pre>
     * String text = "prosody";
     * String[] attributes = {"rate=\"-10%\"", "volume=\"loud\""};
     * </pre>
     * Because different synthesizers will render markup text differently,
     * this event helps an application align SpeakableEvents
     * with the corresponding markup text.
     * <p>
     * This event can also be used to align other multimedia queues
     * (e.g., font and color changes) with the rendered audio.
     */
    public static final int ELEMENT_REACHED = 0x7001000;

    /**
     * The default mask for events in this class.
     * <p>
     * The mask is set with setSpeakableMask.
     * The following events are delivered by default:
     * {@code MARKER_REACHED, MARKUP_FAILED,
     * SPEAKABLE_CANCELLED,
     * SPEAKABLE_STARTED, SPEAKABLE_ENDED,
     * SPEAKABLE_PAUSED, SPEAKABLE_RESUMED,
     * AND VOICE_CHANGED}.
     * <p>
     * The {@code ELEMENT_REACHED, PROSODY_UPDATED, WORD_STARTED, PHONEME_STARTED,
     * and TOP_OF_QUEUE}
     * events may be enabled if desired.
     */
    public static final int DEFAULT_MASK = MARKER_REACHED | SPEAKABLE_FAILED
            | SPEAKABLE_CANCELLED | SPEAKABLE_STARTED | SPEAKABLE_ENDED
            | SPEAKABLE_PAUSED | SPEAKABLE_RESUMED | VOICE_CHANGED;

    // Types.

    /**
     * Type of ELEMENT_REACHED event.
     * <p>
     * The output for the element is beginning.
     */
    public static final int ELEMENT_OPEN = 1;

    /**
     * Type of ELEMENT_REACHED event.
     * <p>
     * The output for the element has completed.
     */
    public static final int ELEMENT_CLOSE = 2;

    public static final int SPEAKABLE_FAILURE_RECOVERABLE = 0x10;

    public static final int SPEAKABLE_FAILURE_UNRECOVERABLE = 0x20;

    /**
     * Type of PROSODY_UPDATED event issued when a prosody value changes.
     */
    public static final int PROSODY_RATE = 0x01;

    /**
     * Type of PROSODY_UPDATED event issued when a prosody value changes.
     * <p>
     * This applies to baseline pitch changes.
     * Changes to F0 or contour are not reported as events,
     * even if specified in markup.
     */
    public static final int PROSODY_PITCH = 0x02;

    /**
     * Type of PROSODY_UPDATED event issued when a prosody value changes.
     */
    public static final int PROSODY_VOLUME = 0x04;

    public static final int PROSODY_PITCH_RANGE = 0x08;

    public static final int PROSODY_CONTOUR = 0x10;

    /**
     * Value returned when the getAudioPosition method is not supported.
     */
    public static final int UNKNOWN_AUDIO_POSITION = -1;

    public static final int UNKNOWN_INDEX = -1;

    public static final int UNKNOWN_TYPE = -1;

    public static final int UNKNOWN_VALUE = -1;

    private int requestId;

    private String textInfo;

    private int audioPosition;

    private int textBegin;

    private int textEnd;

    private int type;

    private int requested;

    private int realized;

    private SpeakableException exception;

    private String[] attributes;

    private PhoneInfo[] phones;

    private int index;

    private Voice newVoice;

    private Voice oldVoice;

    public SpeakableEvent(Object source, int id, int requestId) throws IllegalArgumentException {
        super(source, id);
        if ((id != TOP_OF_QUEUE) && (id != SPEAKABLE_STARTED)
                && (id != SPEAKABLE_ENDED) && (id != SPEAKABLE_PAUSED)
                && (id != SPEAKABLE_RESUMED) && (id != SPEAKABLE_CANCELLED)
                && (id != ELEMENT_REACHED) && (id != VOICE_CHANGED)
                && (id != PROSODY_UPDATED) && (id != MARKER_REACHED)
                && (id != WORD_STARTED) && (id != PHONEME_STARTED)
                && (id != SPEAKABLE_FAILED)) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }
        this.requestId = requestId;
    }

    public SpeakableEvent(Object source, int id, int requestId, String textInfo, int audioPosition)
            throws IllegalArgumentException {
        this(source, id, requestId);
        if (id != MARKER_REACHED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.audioPosition = audioPosition;
    }

    public SpeakableEvent(Object source, int id, int requestId, String textInfo, int textBegin, int textEnd)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != WORD_STARTED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.textBegin = textBegin;
        this.textEnd = textEnd;
    }

    public SpeakableEvent(Object source, int id, int requestId,
                          String textInfo, int type, int requested, int realized)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != PROSODY_UPDATED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.type = type;
        this.requested = requested;
        this.realized = realized;
    }

    public SpeakableEvent(Object source, int id, int requestId,
                          String textInfo, int type, SpeakableException exception)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != SPEAKABLE_FAILED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.type = type;
        this.exception = exception;
    }

    public SpeakableEvent(Object source, int id, int requestId,
                          String textInfo, int type, String[] attributes)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != ELEMENT_REACHED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.type = type;
        this.attributes = attributes;
    }

    public SpeakableEvent(Object source, int id, int requestId,
                          String textInfo, PhoneInfo[] phones, int index)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != PHONEME_STARTED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.phones = phones;
        this.index = index;
    }

    public SpeakableEvent(Object source, int id, int requestId,
                          String textInfo, Voice oldVoice, Voice newVoice)
            throws IllegalArgumentException {
        this(source, id, requestId);

        if (id != VOICE_CHANGED) {
            StringBuffer str = new StringBuffer();
            id2String(str);
            throw new IllegalArgumentException("Invalid event identifier " + str + "!");
        }

        this.textInfo = textInfo;
        this.newVoice = newVoice;
        this.oldVoice = oldVoice;
    }

    public String[] getAttributes() {
        int id = getId();
        if (id == ELEMENT_REACHED) {
            return attributes;
        }

        return new String[0];
    }

    public int getAudioPosition() {
        int id = getId();
        if (id == MARKER_REACHED) {
            return audioPosition;
        }

        return UNKNOWN_AUDIO_POSITION;
    }

    public SpeakableException getSpeakableException() {
        return exception;
    }

    /**
     * Gets the index of the phone associated with a PHONEME_STARTED event.
     * <p>
     * The getPhones method returns the array of
     * phones corresponding to the text.
     * @return the index of the current phone within the phoneme context
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#getPhones()
     * @see javax.speech.synthesis.SpeakableEvent#PHONEME_STARTED
     */
    public int getIndex() {
        int id = getId();
        if (id == PHONEME_STARTED) {
            return index;
        }

        return UNKNOWN_INDEX;
    }

    /**
     * Gets the new Voice associated with a VOICE_CHANGED event.
     * @return the voice after a VOICE_CHANGED event
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#VOICE_CHANGED
     */
    public Voice getNewVoice() {
        int id = getId();
        if (id == VOICE_CHANGED) {
            return newVoice;
        }

        return null;
    }

    /**
     * Gets the old Voice associated with a VOICE_CHANGED event.
     * @return the voice before a VOICE_CHANGED event
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#VOICE_CHANGED
     */
    public Voice getOldVoice() {
        int id = getId();
        if (id == VOICE_CHANGED) {
            return oldVoice;
        }

        return null;
    }

    /**
     * Returns an array of phone information associated with a
     * PHONEME_STARTED event.
     * <p>
     * The array represents a set of phones corresponding to the text.
     * Each PhoneInfo instance includes the base phoneme along with
     * anticipated duration.
     * @return set of phones corresponding to the current text
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#getIndex()
     * @see javax.speech.synthesis.SpeakableEvent#PHONEME_STARTED
     */
    public PhoneInfo[] getPhones() {
        int id = getId();
        if (id == PHONEME_STARTED) {
            return phones;
        }

        return new PhoneInfo[0];
    }

    /**
     * Gets the prosody value realized by markup text with a PROSODY_UPDATED event.
     * @return the realized prosody value
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#PROSODY_UPDATED
     * @see javax.speech.synthesis.SpeakableEvent#getRequestedValue()
     */
    public int getRealizedValue() {
        int id = getId();
        if (id == PROSODY_UPDATED) {
            return realized;
        }

        return UNKNOWN_VALUE;
    }

    /**
     * Gets the prosody value requested by markup text with a PROSODY_UPDATED event.
     * @return the requested prosody value
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#PROSODY_UPDATED
     * @see javax.speech.synthesis.SpeakableEvent#getRealizedValue()
     */
    public int getRequestedValue() {
        int id = getId();
        if (id == PROSODY_UPDATED) {
            return requested;
        }

        return UNKNOWN_VALUE;
    }

    public int getRequestId() {
        return requestId;
    }

    /**
     * Gets the text associated with this SpeakableEvent.
     * <p>
     * The description for each event contains more detail about the
     * text returned in each case.
     * @return the text associated with this event
     * @see javax.speech.synthesis.SpeakableEvent#ELEMENT_REACHED
     * @see javax.speech.synthesis.SpeakableEvent#MARKER_REACHED
     * @see javax.speech.synthesis.SpeakableEvent#SPEAKABLE_FAILURE_UNRECOVERABLE
     * @see javax.speech.synthesis.SpeakableEvent#PHONEME_STARTED
     * @see javax.speech.synthesis.SpeakableEvent#PROSODY_UPDATED
     * @see javax.speech.synthesis.SpeakableEvent#VOICE_CHANGED
     * @see javax.speech.synthesis.SpeakableEvent#WORD_STARTED
     */
    public String getTextInfo() {
        return textInfo;
    }

    /**
     * Gets the type of this SpeakableEvent event.
     * <p>
     * The ELEMENT_REACHED, MARKUP_FAILED, MARKER_REACHED, and PROSODY_UPDATED
     * events include a type.
     * @return the type of the SpeakableEvent
     * @throws java.lang.IllegalStateException if called for an inappropriate event
     * @see javax.speech.synthesis.SpeakableEvent#ELEMENT_REACHED
     * @see javax.speech.synthesis.SpeakableEvent#MARKER_REACHED
     * @see javax.speech.synthesis.SpeakableEvent#SPEAKABLE_FAILURE_UNRECOVERABLE
     * @see javax.speech.synthesis.SpeakableEvent#PROSODY_UPDATED
     */
    public int getType() {
        // TODO Check if there is an error in the specification.
        // The MARKER_REACHED does not provide a type.
        int id = getId();
        if ((id == ELEMENT_REACHED) || (id == SPEAKABLE_FAILED) || (id == PROSODY_UPDATED)) {
            return type;
        }

        return UNKNOWN_TYPE;
    }

    public int getTextEnd() {
        int id = getId();
        if (id == WORD_STARTED) {
            return textEnd;
        }

        return UNKNOWN_INDEX;
    }

    public int getTextBegin() {
        int id = getId();
        if (id == WORD_STARTED) {
            return textBegin;
        }

        return UNKNOWN_INDEX;
    }

    @Override
    protected void id2String(StringBuffer str) {
        maybeAddId(str, TOP_OF_QUEUE, "TOP_OF_QUEUE");
        maybeAddId(str, SPEAKABLE_STARTED, "SPEAKABLE_STARTED");
        maybeAddId(str, ELEMENT_REACHED, "ELEMENT_REACHED");
        maybeAddId(str, VOICE_CHANGED, "VOICE_CHANGED");
        maybeAddId(str, PROSODY_UPDATED, "PROSODY_UPDATED");
        maybeAddId(str, MARKER_REACHED, "MARKER_REACHED");
        maybeAddId(str, WORD_STARTED, "WORD_STARTED");
        maybeAddId(str, PHONEME_STARTED, "PHONEME_STARTED");
        maybeAddId(str, SPEAKABLE_PAUSED, "SPEAKABLE_PAUSED");
        maybeAddId(str, SPEAKABLE_RESUMED, "SPEAKABLE_RESUMED");
        maybeAddId(str, SPEAKABLE_CANCELLED, "SPEAKABLE_CANCELLED");
        maybeAddId(str, SPEAKABLE_ENDED, "SPEAKABLE_ENDED");
        maybeAddId(str, SPEAKABLE_FAILED, "SPEAKABLE_FAILED");
        super.id2String(str);
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        int id = getId();

        Integer typeObject = type;
        parameters.add(typeObject);
        Integer requestIdObject = requestId;
        parameters.add(requestIdObject);
        if (id == PROSODY_UPDATED) {
            Integer requestedObject = requested;
            parameters.add(requestedObject);
        }
        parameters.add(textInfo);
        if (id == MARKER_REACHED) {
            Integer audioPositionObject = audioPosition;
            parameters.add(audioPositionObject);
        }
        if (id == WORD_STARTED) {
            Integer wordStartObject = textBegin;
            parameters.add(wordStartObject);
            Integer wordEndObject = textEnd;
            parameters.add(wordEndObject);
            parameters.add(newVoice);
            parameters.add(oldVoice);
        }
        parameters.add(exception);
        if (id == ELEMENT_REACHED) {
            parameters.add(attributes);
        }
        if (id == PHONEME_STARTED) {
            parameters.add(phones);
            Integer indexObject = index;
            parameters.add(indexObject);
        }

        return parameters;
    }
}
