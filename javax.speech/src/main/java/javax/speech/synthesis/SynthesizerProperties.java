/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 66 $
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

import javax.speech.EngineProperties;

// Comp 2.0.6

/**
 * Provides run-time control of the properties of a Synthesizer.
 * <p>
 * The SynthesizerProperties object is obtained by calling the
 * getSynthesizerProperties method of the Synthesizer.
 * <p>
 * SynthesizerProperties inherits the following behavior from the
 * EngineProperties interface
 * (described in detail in the EngineProperties documentation):
 * <p>
 * Each property has a get and set method (JavaBeans property method patterns).
 * <p>
 * Engines may ignore calls to change properties, for example by applying
 * maximum and minimum settings.
 * <p>
 * Calls to set methods may be asynchronous (they may return before the
 * property change takes effect). The Engine will apply a change as soon
 * as possible. A PropertyChangeEvent is issued when the change takes effect.
 * For example, a change in the speaking rate might take place immediately,
 * or at the end of the current word, sentence or paragraph.
 * <p>
 * The get methods return the current setting - not a pending value.
 * <p>
 * A PropertyChangeListener may be attached to receive property change events.
 * <p>
 * Where appropriate, property settings are persistent across sessions.
 * <p>
 * The primary properties of a synthesizer are:
 * <pre>
 * Speaking voice,
 * Baseline pitch,
 * Pitch range,
 * Speaking rate,
 * Volume.
 * </pre>
 * Setting these properties should be considered as a hint to the synthesizer.
 * A synthesizer may choose to ignore out-of-range values. A synthesizer
 * may have some properties that are unchangeable (e.g. a single voice
 * synthesizer). Reasonable values for baseline pitch, pitch range and
 * speaking rate may vary between synthesizers, between languages and
 * or between voices.
 * <p>
 * A change in voice may lead to change in other properties. For example,
 * female and young voices typically have higher pitches than male voices.
 * When a change in voice leads to changes in other properties, a separate
 * PropertyChangeEvent is issued for each property changed.
 * <p>
 * Whenever possible, property changes should be persistent for a voice.
 * For instance, after changing from voice A to voice B and back, the previous
 * property settings for voice A should return.
 * <p>
 * Changes in pitch, speaking rate and so on in markup text provided to the
 * synthesizer do not affect the baseline values reflected in this interface.
 * These changes may be tracked through SpeakableEvents.
 * <p>
 * Additional properties include
 * <p>
 * Interruptibility level,
 * Priority.
 * <p>
 * Setting these values helps determine the speaking order of items between
 * synthesizer instances.  Priority may differ between trusted applications
 * (such as those installed directly on the device) and untrusted applications
 * (such as those downloaded from the Web).
 *
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.synthesis.Synthesizer#getSynthesizerProperties()
 * @see javax.speech.EngineProperties
 * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
 * @see javax.speech.EngineProperties#removeEnginePropertyListener(javax.speech.EnginePropertyListener)
 * @see javax.speech.synthesis.SpeakableEvent
 */
public interface SynthesizerProperties extends EngineProperties {

    /**
     * The maximum volume level.
     */
    int MAX_VOLUME = 100;

    int MEDIUM_VOLUME = 50;

    /**
     * The minimum volume level.
     */
    int MIN_VOLUME = 0;

    /**
     * The normal volume level.
     */
    int NORM_VOLUME = 100;

    /**
     * The value for word-level interruptibility.
     */
    int WORD_LEVEL = 1;

    /**
     * The value for object-level interruptibility.
     */
    int OBJECT_LEVEL = 2;

    /**
     * The value for queue-level interruptibility.
     */
    int QUEUE_LEVEL = 3;

    int DEFAULT_RATE = 0;

    int X_SLOW_RATE = -40;

    int SLOW_RATE = -70;

    int MEDIUM_RATE = -100;

    int FAST_RATE = -130;

    int X_FAST_RATE = -160;

    /**
     * Sets the interruptibility level property.
     * <p>
     * Synthesizer instances of higher priority interrupt immediately.
     * Synthesizer instances of the same or lower priority wait for the specified
     * interruptibility level to complete. If a Speakable is interrupted,
     * then a <code>SPEAKABLE_CANCELLED</code> event is issued.
     * </p><p>
     * The possible values are <code>WORD_LEVEL</code>, <code>OBJECT_LEVEL</code>,
     * and <code>QUEUE_LEVEL</code>. With <code>WORD_LEVEL</code>, the current word
     * will complete. With </ode>OBJECT_LEVEL</code>, the current Speakable object
     * will complete. With </ode>QUEUE_LEVEL</code>, the entire queue will be spoken.
     * </p><p>
     * The default interruptibility level depends on the system implementation.
     * The level set may be less than the level requested depending on the
     * implementation.
     * </p><p>
     * The system property values Synthesizer.defaultTrustedInterruptibility and
     * Synthesizer.defaultUntrustedInterruptibility determine the defaults
     * (default is OBJECT_LEVEL).
     * <p>
     * The system property value Synthesizer.maximumUntrustedInterruptibility
     * determines the default maximum interruptibility level for untrusted
     * applications (default is QUEUE_LEVEL).
     * <p>
     * Making a Synthesizer more interruptible (e.g., WORD_LEVEL)
     * may cause it to lose output focus if another Synthesizer instance
     * is waiting to speak.
     * For example, if this Synthesizer instance changes from QUEUE_LEVEL to
     * WORD_LEVEL interruptibility and another Synthesizer instance of
     * equal or higher priority is waiting to speak, then this Synthesizer
     * becomes eligible for interruption at the end of the current Word.
     * <p>
     * Changing the interruptibility may be asynchronous, so any focus change
     * may not occur immediately.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param level the interruptibility level for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#WORD_LEVEL
     * @see javax.speech.synthesis.SynthesizerProperties#OBJECT_LEVEL
     * @see javax.speech.synthesis.SynthesizerProperties#QUEUE_LEVEL
     * @see javax.speech.synthesis.SynthesizerProperties#getInterruptibility()
     * @see javax.speech.synthesis.SynthesizerProperties#setPriority(int)
     * @see javax.speech.synthesis.SpeakableEvent#SPEAKABLE_CANCELLED
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setInterruptibility(int level) throws IllegalArgumentException;

    /**
     * Gets the interruptibility level property.
     * @return the interruptibility level for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#setInterruptibility(int)
     */
    int getInterruptibility();

    /**
     * Set the baseline pitch property.
     * <p>
     * Different voices have different natural sounding ranges of pitch.
     * Typical male
     * voices are between 80 and 180 Hertz. Female pitches typically vary from
     * 150 to 300 Hertz.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param hertz the pitch in Hertz
     * @see javax.speech.synthesis.SynthesizerProperties#getPitch()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setPitch(int hertz) throws IllegalArgumentException;

    /**
     * Gets the baseline pitch property.
     * @return the baseline pitch in Hertz for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#setPitch(int)
     */
    int getPitch();

    /**
     * Sets the pitch range property.
     * <p>
     * Set the pitch range for the current synthesis voice. A narrow pitch
     * range provides monotonous output while wide range provide a more
     * lively voice. This setting is a hint to the synthesis engine.
     * Engines may choose to ignore unreasonable requests. Some synthesizers
     * may not support pitch variability.
     * <p>
     * The pitch range is typically between 20% and 80% of the baseline pitch.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param hertz the pitch range in Hertz
     * @see javax.speech.synthesis.SynthesizerProperties#getPitchRange()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setPitchRange(int hertz) throws IllegalArgumentException;

    /**
     * Gets the pitch range property.
     * @return the pitch range in Hertz for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#setPitchRange(int)
     */
    int getPitchRange();

    /**
     * Sets the target speaking rate for the synthesis in words per minute.
     * <p>
     * Reasonable speaking rates depend upon the Synthesizer and the current
     * Voice (some voices sound better at higher or lower speed than others).
     * <p>
     * Speaking rate is also dependent upon the language because of different
     * conventions for what is a "word". A reasonable speaking rate for
     * English is 200 words per minute.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param wpm words per minute
     * @see javax.speech.synthesis.SynthesizerProperties#getSpeakingRate()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setSpeakingRate(int wpm) throws IllegalArgumentException;

    /**
     * Gets the current target speaking rate property.
     * <p>
     * The speaking rate is measured in Words Per Minute (WPM).
     * @return the current target speaking rate in WPM
     * @see javax.speech.synthesis.SynthesizerProperties#setSpeakingRate(int)}
     */
    int getSpeakingRate();

    /**
     * Sets the current Voice property.
     * <p>
     * The list of available voices for a Synthesizer is returned by the
     * getVoices method of the synthesizer's SynthesizerMode. Any one
     * of the voices returned by that method can be passed to setVoice to
     * set the current speaking voice.
     * <p>
     * Alternatively, the voice parameter may be an application-created
     * partially specified Voice object. If there is no matching voice,
     * the voice remains unchanged.
     * <p>
     * The following example sets a teen-aged, female voice:
     * <pre>
     * Voice voice = new Voice(null, null,
     * GENDER_FEMALE, AGE_TEENAGER,
     * VARIANT_DONT_CARE);
     * synthesizerProperties.setVoice(voice);
     * </pre>
     * The requested value may be rejected or limited.
     *
     * @param voice a voice to match in the current synthesizer
     * @see javax.speech.synthesis.SynthesizerProperties#getVoice()
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setVoice(Voice voice) throws IllegalArgumentException;

    /**
     * Gets the current Voice property.
     * <p>
     * The Voice may be changed using setVoice.
     * @return the current  Voice for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#setVoice(javax.speech.synthesis.Voice)
     */
    Voice getVoice();

    /**
     * Sets the current volume property.
     * <p>
     * The volume is a value between MIN_VOLUME and MAX_VOLUME. A value of
     * MIN_VOLUME indicates silence
     * and MAX_VOLUME indicates maximum available volume.
     * The default is NORM_VOLUME.
     * <p>
     * A synthesizer may change the voice's style with volume. For example,
     * a quiet volume might produce whispered output and loud might produce
     * shouting. Many synthesizers do not make this type of change.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param volume value between MAX_VOLUME and MIN_VOLUME
     * @see javax.speech.synthesis.SynthesizerProperties#getVolume()
     * @see javax.speech.synthesis.SynthesizerProperties#MIN_VOLUME
     * @see javax.speech.synthesis.SynthesizerProperties#MAX_VOLUME
     * @see javax.speech.synthesis.SynthesizerProperties#NORM_VOLUME
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     */
    void setVolume(int volume) throws IllegalArgumentException;

    /**
     * Gets the current volume property.
     * <p>
     * See setVolume for a full description of volume values.
     * @return the current volume setting for synthesis
     * @see javax.speech.synthesis.SynthesizerProperties#setVolume(int)}
     */
    int getVolume();

    @Override
    int getPriority();

    /**
     * Sets the priority property.
     * <p>
     * Priorities are values between MIN_PRIORITY and MAX_PRIORITY.
     * The priority used may be lower than the priority requested.
     * In this case, the value set will differ from the value requested.
     * <p>
     * Trusted applications may use the full range of priorities.
     * Untrusted applications have a maximum of MAX_UNTRUSTED_PRIORITY,
     * which is less than MAX_PRIORITY.
     * <p>
     * The default priorities for trusted and untrusted applications are
     * NORM_TRUSTED_PRIORITY and NORM_UNTRUSTED_PRIORITY, respectively.
     * <p>
     * Along with queue interruptibility,
     * the priorities determine the order in which speakable objects are
     * synthesized.
     * <p>
     * Changing the priority of a Synthesizer instance may cause it to
     * gain or lose focus.
     * For example, if the new  priority value for this Synthesizer instance
     * is higher than that of the speaking Synthesizer instance,
     * then that Synthesizer will be interrupted and this one will speak instead.
     * Conversely if the priority of a Synthesizer instance is lowered, another
     * instance may interrupt it.
     * <p>
     * Changing the priority may be asynchronous, so any focus change
     * may not occur immediately.
     * <p>
     * Priority should be used with care to avoid starvation of other
     * applications.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param priority the priority for the synthesizer
     * @see javax.speech.EngineProperties#getPriority()
     * @see javax.speech.synthesis.SynthesizerProperties#setInterruptibility(int)
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     * @see javax.speech.EngineProperties#MIN_PRIORITY
     * @see javax.speech.EngineProperties#NORM_TRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#NORM_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_PRIORITY
     */
    @Override
    void setPriority(int priority) throws IllegalArgumentException;
}
