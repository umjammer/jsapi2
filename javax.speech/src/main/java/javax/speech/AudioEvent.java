/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
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

import java.util.List;

/**
 * Describes events associated with audio input/output for an Engine.
 * <p>
 * The event source is an Engine object.
 * <p>
 * Audio level values are suitable for display of a visual "VU meter"
 * (like the bar on stereo systems which goes up and down with the volume).
 * Different colors are often used to indicate the different levels:
 * for example, red for loud, green for normal, and blue for background.
 * <p>
 * Maintaining audio quality is important for reliable recognition.
 * A common problem is a user speaking too loudly or too quietly.
 * The color on a VU meter is one way to provide feedback to the user.
 * Note that a quiet level (AUDIO_LEVEL_QUIET) does not necessarily indicate that
 * the user is speaking too quietly.
 * The input is also quiet when the user is not speaking.
 * <p>
 * AUDIO_STARTED and AUDIO_STOPPED events indicate the state of the audio stream.
 *
 * @see javax.speech.AudioListener
 * @since 2.0.6
 */
public class AudioEvent extends SpeechEvent {

    /**
     * Event issued when the AudioManager has started the audio stream.
     * <p>
     * Applications may use this event to display visual feedback
     * to a user indicating that the audio stream is active.
     */
    public static final int AUDIO_STARTED = 0x8000001;

    /**
     * Event issued when the AudioManager has detected the end of an audio stream
     * that it previously indicated by a AUDIO_STARTED event.
     * <p>
     * This event always follows an AUDIO_STARTED event.
     */
    public static final int AUDIO_STOPPED = 0x8000002;

    public static final int AUDIO_CHANGED = 0x8000004;

    /**
     * Event issued when the next volume level value of the incoming audio becomes available.
     * <p>
     * The getAudioLevel method describes audio levels in more detail.
     */
    public static final int AUDIO_LEVEL = 0x8000008;

    /**
     * The default mask for events in this class.
     * <p>
     * The following events are delivered by default:
     * AUDIO_STARTED, and AUDIO_STOPPED.
     * <p>
     * The AUDIO_LEVEL event occurs relatively frequently and may be enabled
     * if needed.
     */
    public static final int DEFAULT_MASK = AUDIO_STARTED | AUDIO_CHANGED | AUDIO_STOPPED;

    /**
     * The minimum audio level.
     */
    public static final int AUDIO_LEVEL_MIN = 0;

    /**
     * The threshold for quiet audio.
     * <p>
     * Audio is considered quiet below this threshold.
     */
    public static final int AUDIO_LEVEL_QUIET = 250;

    /**
     * The threshold for loud audio.
     * <p>
     * Audio is considered loud above this threshold.
     */
    public static final int AUDIO_LEVEL_LOUD = 750;

    /**
     * The maximum audio level.
     */
    public static final int AUDIO_LEVEL_MAX = 1000;

    private int audioLevel;

    private String mediaLocator;

    /**
     * Constructs an AudioEvent with a specified event identifier.
     * <p>
     * The audioLevel is set to AUDIO_LEVEL_MIN.
     * @param source Engine that produced the event
     * @param id the identifier for the event type
     * @see javax.speech.AudioEvent#AUDIO_STARTED
     * @see javax.speech.AudioEvent#AUDIO_STOPPED
     */
    public AudioEvent(Engine source, int id) {
        super(source, id);
        if ((id != AUDIO_STARTED) && (id != AUDIO_CHANGED) && (id != AUDIO_STOPPED)) {
            throw new IllegalArgumentException("Id must be AUDIO_STARTED, AUDIO_CHANGED or AUDIO_STOPPED!");
        }
        audioLevel = AUDIO_LEVEL_MIN;
    }

    public AudioEvent(Engine source, int id, int audioLevel) {
        super(source, id);
        if (id != AUDIO_LEVEL) {
            throw new IllegalArgumentException("Id must be AUDIO_LEVEL!");
        }

        if ((audioLevel < AUDIO_LEVEL_MIN) || (audioLevel > AUDIO_LEVEL_MAX)) {
            throw new IllegalArgumentException("Audiolevel must be between AUDIO_LEVEL_MIN and AUDIO_LEVEL_MAX");
        }

        this.audioLevel = audioLevel;
    }

    public AudioEvent(Engine source, int id, String locator) {
        this(source, id);
        if (id != AUDIO_CHANGED) {
            throw new IllegalArgumentException("Id must be AUDIO_CHANGED!");
        }
        mediaLocator = locator;
        audioLevel = AUDIO_LEVEL_MIN;
    }

    /**
     * Gets the audio input level for this AudioEvent.
     * <p>
     * AUDIO_LEVEL_MIN represents complete silence.
     * A value below AUDIO_LEVEL_QUIET indicates quiet input.
     * A value above AUDIO_LEVEL_LOUD indicates loud input.
     * AUDIO_LEVEL_MAX represents the maximum level.
     * <p>
     * Values between AUDIO_LEVEL_QUIET and AUDIO_LEVEL_LOUD represent the
     * normal operating range.
     * <p>
     * This call back is called periodically.
     * The exact period is up to the implementation, but should be
     * approximately every 100 milliseconds to support a VU meter.
     * It is possible to receive the same value twice in a row.
     * <p>
     * The level is provided consistently for the AUDIO_LEVEL event.
     * If otherwise unavailable, the level is AUDIO_LEVEL_MIN for
     * AUDIO_STARTED and AUDIO_STOPPED events.
     * @return the audio input level
     * @see javax.speech.AudioEvent#AUDIO_LEVEL
     * @see javax.speech.AudioEvent#AUDIO_LEVEL_MIN
     * @see javax.speech.AudioEvent#AUDIO_LEVEL_QUIET
     * @see javax.speech.AudioEvent#AUDIO_LEVEL_LOUD
     * @see javax.speech.AudioEvent#AUDIO_LEVEL_MAX
     */
    public int getAudioLevel() {
        return audioLevel;
    }

    public String getMediaLocator() {
        return mediaLocator;
    }

    @Override
    protected void id2String(StringBuffer str) {
        maybeAddId(str, AUDIO_STARTED, "AUDIO_STARTED");
        maybeAddId(str, AUDIO_STOPPED, "AUDIO_STOPPED");
        maybeAddId(str, AUDIO_LEVEL, "AUDIO_LEVEL");
        super.id2String(str);
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        parameters.add(audioLevel);

        return parameters;
    }
}
