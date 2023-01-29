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

import javax.speech.EngineManager;
import javax.speech.EngineProperties;
import javax.speech.EnginePropertyListener;

//Comp 2.0.6

public interface SynthesizerProperties extends EngineProperties {

    /** The maximum volume level. */
    int MAX_VOLUME = 100;

    int MEDIUM_VOLUME = 50;

    int MIN_VOLUME = 0;

    int NORM_VOLUME = 100;

    int WORD_LEVEL = 1;

    int OBJECT_LEVEL = 2;

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
     * Synthesizer instances of higher priority interrupt immediately. Synthesizer instances of the same or
     * lower priority wait for the specified interruptibility level to complete. If a Speakable is interrupted,
     * then a <code>SPEAKABLE_CANCELLED</code> event is issued.
     * </p><p>
     * The possible values are <code>WORD_LEVEL</code>, <code>OBJECT_LEVEL</code>, and <code>QUEUE_LEVEL</code>.
     * With <code>WORD_LEVEL</code>, the current word will complete. With </ode>OBJECT_LEVEL</code>, the current
     * Speakable object will complete. With </ode>QUEUE_LEVEL</code>, the entire queue will be spoken.
     * </p><p>
     * The default interruptibility level depends on the system implementation. The level set may be less than
     * the level requested depending on the implementation.
     * </p><p>
     * The system property values Synthesizer.defaultTrustedInterruptibility and
     * Synthesizer.defaultUntrustedInterruptibility determine the defaults (default is <code>OBJECT_LEVEL</code>).
     * </p><p>
     * The system property value Synthesizer.maximumUntrustedInterruptibility determines the default maximum
     * interruptibility level for untrusted applications (default is <code>QUEUE_LEVEL</code>).
     * </p><p>
     * Making a Synthesizer more interruptible (e.g., <code>WORD_LEVEL</code>) may cause it to lose output focus
     * if another Synthesizer instance is waiting to speak. For example, if this Synthesizer instance changes
     * from <code>QUEUE_LEVEL</code> to <code>WORD_LEVEL</code> interruptibility and another Synthesizer instance
     * of equal or higher priority is waiting to speak, then this Synthesizer becomes eligible for interruption
     * at the end of the current Word.
     * </p><p>
     * Changing the interruptibility may be asynchronous, so any focus change may not occur immediately.
     * </p><p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param level the interruptibility level for synthesis
     * @see #WORD_LEVEL
     * @see #OBJECT_LEVEL
     * @see #QUEUE_LEVEL
     * @see #getInterruptibility
     * @see #setPriority
     * @see SpeakableEvent#SPEAKABLE_CANCELLED
     * @see javax.speech.EngineManager
     * @see #addEnginePropertyListener
     */
    void setInterruptibility(int level) throws IllegalArgumentException;

    int getInterruptibility();

    /**
     * Set the baseline pitch property. Different voices have different natural sounding ranges of pitch.
     * Typical male voices are between 80 and 180 Hertz. Female pitches typically vary from 150 to 300 Hertz.
     * <p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param hertz the pitch in Hertz
     */
    void setPitch(int hertz) throws IllegalArgumentException;

    int getPitch();

    /**
     * Sets the pitch range property. Set the pitch range for the current synthesis voice. A narrow pitch range
     * provides monotonous output while wide range provide a more lively voice. This setting is a hint to the
     * synthesis engine. Engines may choose to ignore unreasonable requests. Some synthesizers may not support
     * pitch variability.
     * <p>
     * The pitch range is typically between 20% and 80% of the baseline pitch.
     * </p><p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param hertz the pitch range in Hertz
     * @see #getPitchRange
     * @see EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setPitchRange(int hertz) throws IllegalArgumentException;

    int getPitchRange();

    /**
     * Sets the target speaking rate for the synthesis in words per minute. Reasonable speaking rates depend
     * upon the Synthesizer and the current Voice (some voices sound better at higher or lower speed than others).
     * Speaking rate is also dependent upon the language because of different conventions for what is a "word".
     * A reasonable speaking rate for English is 200 words per minute.
     * <p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param wpm words per minute
     * @see #getSpeakingRate
     * @see EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setSpeakingRate(int wpm) throws IllegalArgumentException;

    int getSpeakingRate();

    /**
     * Sets the current Voice property. The list of available voices for a Synthesizer is returned by the getVoices
     * method of the synthesizer's SynthesizerMode. Any one of the voices returned by that method can be passed to
     * setVoice to set the current speaking voice.
     * Alternatively, the voice parameter may be an application-created partially specified Voice object.
     * If there is no matching voice, the voice remains unchanged.
     * <p>
     * The following example sets a teen-aged, female voice:
     * </p>
     * <pre><code>
     *  Voice voice = new Voice(null, null,
     *                          GENDER_FEMALE, AGE_TEENAGER,
     *                          VARIANT_DONT_CARE);
     *  synthesizerProperties.setVoice(voice);
     * </code></pre>
     * The requested value may be rejected or limited.
     *
     * @param voice a voice to match in the current synthesizer
     * @see #getVoice
     * @see EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setVoice(Voice voice) throws IllegalArgumentException;

    Voice getVoice();

    /**
     * Sets the current volume property. The volume is a value between <code>MIN_VOLUME</code> and
     * <code>MAX_VOLUME</code>. A value of <code>MIN_VOLUME</code> indicates silence and <code>MAX_VOLUME</code>
     * indicates maximum available volume. The default is <code>NORM_VOLUME</code>.
     * A synthesizer may change the voice's style with volume. For example, a quiet volume might produce
     * whispered output and loud might produce shouting. Many synthesizers do not make this type of change.
     * <p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param volume value between MAX_VOLUME and MIN_VOLUME
     * @see #getVolume
     * @see #MIN_VOLUME
     * @see #MAX_VOLUME
     * @see #NORM_VOLUME
     * @see EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     */
    void setVolume(int volume) throws IllegalArgumentException;

    int getVolume();

    int getPriority();

    /**
     * Sets the priority property.
     * <p>
     * Priorities are values between <code>MIN_PRIORITY</code> and <code>MAX_PRIORITY</code>. The priority used may be
     * lower than the priority requested. In this case, the value set will differ from the value requested.
     * Trusted applications may use the full range of priorities. Untrusted applications have a maximum of
     * <code>MAX_UNTRUSTED_PRIORITY</code>, which is less than MAX_PRIORITY.
     * </p><p>
     * The default priorities for trusted and untrusted applications are <code>NORM_TRUSTED_PRIORITY</code> and
     * <code>NORM_UNTRUSTED_PRIORITY</code>, respectively.
     * </p><p>
     * Along with queue interruptibility, the priorities determine the order in which speakable objects are
     * synthesized.
     * </p><p>
     * Changing the priority of a Synthesizer instance may cause it to gain or lose focus. For example,
     * if the new priority value for this Synthesizer instance is higher than that of the speaking Synthesizer
     * instance, then that Synthesizer will be interrupted and this one will speak instead. Conversely if the
     * priority of a Synthesizer instance is lowered, another instance may interrupt it.
     * </p><p>
     * Changing the priority may be asynchronous, so any focus change may not occur immediately.
     * </p><p>
     * Priority should be used with care to avoid starvation of other applications.
     * </p><p>
     * The requested value may be rejected or limited.
     * </p>
     *
     * @param priority the priority for the synthesizer
     * @see #getPriority
     * @see #setInterruptibility
     * @see EngineManager
     * @see EngineProperties#addEnginePropertyListener(EnginePropertyListener)
     * @see EngineProperties#MIN_PRIORITY
     * @see EngineProperties#NORM_TRUSTED_PRIORITY
     * @see EngineProperties#NORM_UNTRUSTED_PRIORITY
     * @see EngineProperties#MAX_UNTRUSTED_PRIORITY
     * @see EngineProperties#MAX_PRIORITY
     */
    @Override
    void setPriority(int priority) throws IllegalArgumentException;
}
