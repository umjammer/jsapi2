/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 52 $
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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The AudioManager is provided by subclasses of Engine
 * to allow an application to control audio input/output and to monitor audio-related events.
 * The AudioManager for an Engine is obtained by calling its getAudioManager method.
 * <p>
 * The following example shows how to redirect synthesizer output to a file.
 * Redirection to a recognizer is similar.
 * <p>
 * <pre>
 * Synthesizer synth = ...                            // see Synthesizer for creation options
 * AudioManager am = synth.getAudioManager();         // can throw SecurityException
 * am.setMediaLocator("file://user/smith/hello.wav"); // can throw AudioException
 * synth.allocate();                                  // after setMediaLocator to avoid error
 * synth.speak("hello world", null);                  // speak
 * </pre>
 * The media locator description is taken from JSR-135.
 * This JSR uses only the audio part.
 * <p>
 * Media locators are specified in
 * <p>
 * which is defined in the form:
 * <pre>
 * scheme
 * :
 * scheme-specific-part
 * </pre>
 * The "scheme" part of the locator string identifies the name
 * of the protocol being used to deliver the data.
 * <p>
 * The locator syntax for live-media capture and RTP streaming is defined as follows:
 * <p>
 * The locators for capturing live media are defined
 * by the following syntax in
 * notations:
 * <pre>
 * "capture://" device [ "?" params]
 * "playback://" device [ "?" params]
 * "segment://" device [ "?" params]
 * </pre>
 * The "segment" scheme supports directionless media locators associated with
 * audio segments.
 * <p>
 * A. Identifying the type or the specific
 * name of the device:
 * <pre>
 * device       = "audio" / dev_name / logical_name
 * dev_name     = alphanumeric
 * alphanumeric = 1*( ALPHA / DIGIT )
 * logical_name = "microphone" / "speaker" / "headset"
 * </pre>
 * B. Describing the media and capture parameters:
 * <pre>
 * params = audio_params / custom_params
 * </pre>
 * <p>
 * C. Describing the audio media format:
 * <pre>
 * audio_params = audio_param *( "&" audio_param )
 * audio_param  = 1*( "encoding=" audio_enc / "rate=" rate /
 * "bits=" bits / "channels=" channels /
 * "endian=" endian / "signed=" signed )
 * audio_enc    = "pcm" / "ulaw" / "alaw" / "gsm"
 * rate         = "96000" / "48000" / "44100" / "22050" / "16000" /
 * "11025" / "8000"
 * bits         = "8" / "16" / "24"
 * channels     = pos_integer
 * endian       = "little" / "big"
 * signed       = "signed" / "unsigned"
 * pos_integer  = 1*DIGIT
 * </pre>
 * Note: alaw is required for SSML, but is an extension of JSR-135.
 * Example:
 * <pre>
 * encoding=pcm&rate=11025&bits=16&channels=1
 * </pre>
 * D. Describing some custom media parameters:
 * <pre>
 * custom_params = param *( "&" param )
 * param         = key "=" value
 * key           = alphanumeric
 * value         = alphanumeric
 * </pre>
 * Examples:
 * <pre>
 * capture://audio  (default input audio)
 * playback://audio (default output audio, other capture examples apply)
 * capture://audio?encoding=pcm   (default audio audio in PCM format)
 * capture://devmic0?encoding=pcm&rate=11025&bits=16&channels=1
 * (audio from a specific device--devmic0)
 *
 * capture://mydev?myattr=123   (custom device with custom param)
 * </pre>
 * is a public standard for streaming media.  The locator
 * syntax for specifying RTP sessions is:
 * <pre>
 * "rtp://" address [ ":" port ] [ "/" type ]
 * </pre>
 * where:
 * <p>
 * address and port defines the RTP session.  The
 * address and port usage is similar to the host and port
 * usage as defined in the
 * .
 * <pre>
 * type = "audio"
 * </pre>
 *
 * @see javax.speech.Engine
 * @see javax.speech.Engine#getAudioManager()
 * @since 2.0.6
 */
public interface AudioManager {

    int getAudioMask();

    void setAudioMask(int mask);

    /**
     * Requests notifications of AudioEvents to an AudioListener.
     * <p>
     * An application can attach multiple AudioListeners to an AudioManager.
     * @param listener a listener for AudioEvents
     * @see javax.speech.AudioEvent
     * @see javax.speech.AudioListener
     * @see javax.speech.AudioManager#removeAudioListener(javax.speech.AudioListener)
     */
    void addAudioListener(AudioListener listener);

    /**
     * Removes an AudioListener from this AudioManager.
     * @param listener a listener for AudioEvents
     * @see javax.speech.AudioEvent
     * @see javax.speech.AudioListener
     * @see javax.speech.AudioManager#addAudioListener(javax.speech.AudioListener)
     */
    void removeAudioListener(AudioListener listener);

    /**
     * Starts the audio for this AudioManager instance.
     * <p>
     * Note that a speech engine will normally start and stop an audio channel.
     * This method provides additional control, especially when combined with
     * audioStop.
     * <p>
     * If successful, this method results in an AUDIO_STARTED event.
     * @throws javax.speech.AudioException if this method is not supported
     * @throws java.lang.SecurityException if the application does not have permission
     * @see javax.speech.AudioManager#audioStop()
     * @see javax.speech.AudioEvent#AUDIO_STARTED
     */
    void audioStart() throws SecurityException, AudioException, EngineStateException;

    /**
     * Stops the audio for this AudioManager instance.
     * <p>
     * This method may be used to effectively mute the audio.
     * Any buffered audio is discarded.
     * Any pending results are finalized.
     * For "live" audio, incoming audio is discarded while stopped.
     * <p>
     * If successful, this method results in an AUDIO_STOPPED event.
     * @throws javax.speech.AudioException if this method is not supported
     * @throws java.lang.SecurityException if the application does not have permission
     * @see javax.speech.AudioManager#audioStart()
     * @see javax.speech.AudioEvent#AUDIO_STOPPED
     */
    void audioStop() throws SecurityException, AudioException, EngineStateException;

    void setMediaLocator(String locator) throws AudioException,
            IllegalStateException, IllegalArgumentException, SecurityException;
    /**
     * Sets the locator for this Engine instance.
     * <p>
     * All engines must support the "file:" based locators to pass the TCK,
     * but may throw an exception in production.
     * <p>
     * The format description may be ignored for a "file:" based locator used
     * for audio input.
     * In this case, the file may contain its own format information
     * (e.g., within a header).
     * <p>
     * If null is passed as the locator, the default locator will be used.
     * This includes the device and format.
     * @param locator a media locator description.
     * @throws javax.speech.AudioException if this engine does not support the named
     *  locator.
     * @throws javax.speech.EngineStateException if the engine is not in the DEALLOCATED state.
     * @throws java.lang.SecurityException if the application does not have permission.
     * @see javax.speech.AudioManager#getMediaLocator()
     */

    void setMediaLocator(String locator, InputStream stream) throws
            AudioException, IllegalStateException, IllegalArgumentException, SecurityException;

    void setMediaLocator(String locator, OutputStream stream) throws
            AudioException, IllegalStateException, IllegalArgumentException, SecurityException;

    /**
     * Returns the media locator for the engine.
     * <p>
     * This locator specifies characteristics about the current audio stream
     * including the sample rate and encoding.
     *
     * @return A media locator description string.
     * @see javax.speech.AudioManager#setMediaLocator(java.lang.String)
     */
    String getMediaLocator();

    /**
     * Gets the set of supported audio media locator descriptions given a
     * locator template.
     * <p>
     * The locators are returned as strings that identify which locators
     * can be used with an associated engine. The locators indicate the scheme
     * and can indicate associated devices and media formats.
     * <p>
     * The argument provides a template that can specify a subset of
     * all possible schemes, devices, and audio media formats.
     * For example, if the given media locator is
     * "capture://audio?encoding=pcm", then the
     * supported media formats that can be used in an engine might be
     * <p>
     * "capture://audio?encoding=pcm&rate=11025&bits=16&channels=1" and
     * "capture://audio?encoding=pcm&rate=8000&bits=16&channels=1".
     * <p>
     * Some locator sources include the media format information internally.
     * For example, the audio source "file://user/smith/audio.wav" includes
     * information such as the sample rate within the header.
     * <p>
     * If null is passed in as the media locator, all the supported protocols
     * for this engine will be returned.
     * The returned array should be non-empty.
     * <p>
     * If the given media locator is invalid or unsupported,
     * then an empty array will be returned.
     *
     * @param mediaLocator a media locator description.
     * @return An array of supported audio media locator descriptions in the form of
     *  an array of URI locators.
     * @see javax.speech.AudioManager#isSupportedMediaLocator(java.lang.String)
     */
    String[] getSupportedMediaLocators(String mediaLocator) throws IllegalArgumentException;

    /**
     * Tests a media locator to see if it is supported.
     * <p>
     * This method returns true if a call to getSupportedMediaLocators with the
     * same media locator would return a non-empty list.
     * This provides a convenient way to test for supported features without
     * constructing a potentially large list.
     * @param mediaLocator a media locator description.
     * @return true if the media locator description is supported
     * @see javax.speech.AudioManager#getSupportedMediaLocators(java.lang.String)
     */
    boolean isSupportedMediaLocator(String mediaLocator) throws IllegalArgumentException;

    /**
     * Tests for equivalent audio sources or destinations.
     * <p>
     * This method returns true if this AudioManager represents
     * the same source or destination as the specified AudioManager.
     * In addition to counting equal mediaLocator Strings as equal,
     * AudioManagers may represent the same channel if
     * <p>
     * partially specified media locators are the same after considering
     * defaults,
     * an AudioManager is able to convert differences such as sample rate
     * for the same channel,
     * there is more than one logical name for the same physical device.
     *
     * @param audioManager the AudioManager to compare with this AudioManager
     * @return true if the AudioManagers represent the same source or destination
     */
    boolean isSameChannel(AudioManager audioManager);
}
