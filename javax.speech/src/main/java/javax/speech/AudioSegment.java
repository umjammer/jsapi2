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

import java.io.IOException;
import java.io.InputStream;

// Comp. 2.0.6

/**
 * Associates audio data with a representation of the audio format.
 * <p>
 * The format of the audio data is described by a media locator String.
 * Use
 * {@link javax.speech.AudioSegment#getMediaLocator()}
 * to get the format.
 * Media locators are described in the AudioManager.
 * Examples of media locators include:
 * <p>
 * "segment://audio?encoding=pcm
 * rate=11025
 * bits=16
 * channels=1"
 * "file:///user/smith/audio.wav"
 * <p>
 * {@link javax.speech.AudioSegment#openInputStream()}
 * provides a means to get the audio data.
 * Some implementations may limit the capability to get the audio data.
 * The capabilities may depend on security settings on the device.
 * @see javax.speech.AudioSegment#openInputStream()
 * @see javax.speech.AudioSegment#getMediaLocator()
 * @see javax.speech.AudioManager
 */
public class AudioSegment {

    private final String locator;

    private final String markupText;

    /**
     * Constructs an AudioSegment from the resource indicated by the given media
     * locator.
     * <p>
     * The media locator defines the format and location of the data as described
     * in the AudioManager.
     * The markup text may be used in case there is a problem with the
     * audio data.
     * <p>
     * <A/>
     * The following example shows how to speak audio from a file:
     * <p>
     * Synthesizer synth = ...        // see Synthesizer for creation options
     * synth.allocate();
     * <p>
     * AudioSegment audio = new AudioSegment("file:///user/smith/audio.wav");
     * <p>
     * synth.speak(audio, null);      // speak
     *
     * @param locator a media locator description string
     * @param markupText the alternate markup text
     * @throws java.lang.IllegalArgumentException if the media locator is not supported.
     * @see javax.speech.AudioManager
     * @see javax.speech.AudioSegment#getMarkupText()
     */
    public AudioSegment(String locator, String markupText) throws IllegalArgumentException {
        if (locator == null) {
            throw new IllegalArgumentException("locator must not be null");
        }
        this.locator = locator;
        this.markupText = markupText;
    }

    /**
     * Returns the media locator for the segment.
     * <p>
     * This locator specifies characteristics about the audio segment
     * including the sample rate and encoding.  If the audio segment was
     * constructed without a stream, the locator may also encode information
     * used to determine how to obtain the data (e.g., a "file:" locator).
     * @return A media locator description string.
     * @see javax.speech.AudioSegment#AudioSegment(java.lang.String, java.lang.String)
     */
    public String getMediaLocator() {
        return locator;
    }

    /**
     * Returns the alternate markup text that may be used in case there is
     * a problem with the audio for this AudioSegment.
     * @return the alternate markup text for this AudioSegment
     */
    public String getMarkupText() {
        return markupText;
    }

    public InputStream openInputStream() throws IOException, SecurityException {
        if (!isGettable()) {
            throw new SecurityException("The platform does not allow to access the input stream!");
        }
        return null;
    }

    /**
     * Indicates that a getInputStream request will not throw a SecurityException.
     * @return true if the platform allows access to this audio data
     * @see javax.speech.AudioSegment#AudioSegment(java.lang.String, java.lang.String)
     * @see javax.speech.AudioSegment#getMediaLocator()
     * @see javax.speech.AudioSegment#openInputStream()
     */
    public boolean isGettable() {
        return true;
    }

    public String toString() {

        String str = getClass().getName() +
                "[" +
                locator +
                ',' +
                markupText +
                "]";

        return str;
    }
}
