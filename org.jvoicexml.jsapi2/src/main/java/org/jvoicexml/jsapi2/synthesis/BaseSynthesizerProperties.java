/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2.synthesis;

import javax.speech.Engine;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.SynthesizerProperties;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.BaseEngineProperties;


/**
 * Base implementation of {@link SynthesizerProperties}.
 *
 * <p>
 * Actual JSAPI2 implementations may want to override this class to
 * apply the settings to the synthesizer.
 * </p>
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public class BaseSynthesizerProperties extends BaseEngineProperties implements SynthesizerProperties {

    /** Name of the interruptibility property in events. */
    public static final String INTERRUPTIBILITY = "interruptibility";

    /** Name of the pitch property in events. */
    public static final String PITCH = "pitch";

    /** Name of the pitch range property in events. */
    public static final String PITCH_RANGE = "pitchRange";

    /** Name of the speaking rate property in events. */
    public static final String SPEAKING_RATE = "speakingRate";

    /** Name of the speaking rate property in events. */
    public static final String VOICE = "voice";

    /** Name of the volume property in events. */
    public static final String VOLUME = "volume";

    /** Current value for the interruptibility. */
    private int interruptibility;

    /** Current value for the pitch. */
    private int pitch;

    /** Current value for the pitch range. */
    private int pitchRange;

    /** Current value for the speaking rate. */
    private int speakingRate;

    /** The current voice. */
    private Voice voice;

    /** Current value for the volume. */
    private int volume;

    /**
     * Constructs a new Object.
     *
     * @param synthesizer reference to the synthesizer.
     */
    public BaseSynthesizerProperties(BaseSynthesizer synthesizer) {
        super(synthesizer);

        interruptibility = OBJECT_LEVEL;
        pitch = 160;
        pitchRange = (int) (160 * 0.60);
        speakingRate = DEFAULT_RATE;
        volume = MEDIUM_VOLUME;
        //Set default voice
        Engine engine = getEngine();
        SynthesizerMode mode = (SynthesizerMode) engine.getEngineMode();
        if (mode == null) {
            voice = null;
        } else {
            Voice[] voices = mode.getVoices();
            if ((voices != null) && (voices.length > 0)) {
                voice = voices[0];
            } else {
                voice = null;
            }
        }
    }

    @Override
    public final int getInterruptibility() {
        return interruptibility;
    }

    @Override
    public final void setInterruptibility(int level) {
        if ((level != WORD_LEVEL) && (level != OBJECT_LEVEL) && (level != QUEUE_LEVEL)) {
            throw new IllegalArgumentException("Invalid interruptibility level :" + level);
        }
        if (level == interruptibility) {
            return;
        }
        handlePropertyChangeRequest(INTERRUPTIBILITY, interruptibility, level);
    }

    @Override
    public final int getPitch() {
        return pitch;
    }

    @Override
    public final void setPitch(int hertz) {
        if (hertz <= 0) {
            throw new IllegalArgumentException("Pitch is not a positive integer:" + hertz);
        }
        if (pitch == hertz) {
            return;
        }
        handlePropertyChangeRequest(PITCH, pitch, hertz);
    }

    @Override
    public final int getPitchRange() {
        return pitchRange;
    }

    @Override
    public final void setPitchRange(int hertz) {
        if (hertz < 0) {
            throw new IllegalArgumentException("Pitch is a negative integer:" + hertz);
        }
        if (pitchRange == hertz) {
            return;
        }
        handlePropertyChangeRequest(PITCH_RANGE, pitchRange, hertz);
    }

    @Override
    public final int getSpeakingRate() {
        return speakingRate;
    }

    @Override
    public final void setSpeakingRate(int wpm) {
        if (wpm < 0) {
            throw new IllegalArgumentException("Speaking rate is not a positive integer:" + wpm);
        }
        if (speakingRate == wpm) {
            return;
        }
        handlePropertyChangeRequest(SPEAKING_RATE, speakingRate, wpm);
    }

    @Override
    public final Voice getVoice() {
        return voice;
    }

    @Override
    public void setVoice(Voice voice) {
        Engine synthesizer = getEngine();
        SynthesizerMode mode = (SynthesizerMode) synthesizer.getEngineMode();
        if (mode == null) {
            return;
        }
        Voice[] voices = mode.getVoices();
        for (Voice current : voices) {
            if (current.match(voice)) {
                handlePropertyChangeRequest(VOICE, this.voice, current);
                return;
            }
        }
    }

    @Override
    public final int getVolume() {
        return volume;
    }

    @Override
    public final void setVolume(int vol) {
        if ((vol < MIN_VOLUME) || (vol > MAX_VOLUME)) {
            throw new IllegalArgumentException("Volume is out of range: " + vol);
        }
        if (volume == vol) {
            return;
        }
        handlePropertyChangeRequest(VOLUME, volume, vol);
    }

    @Override
    public void reset() {
        setInterruptibility(OBJECT_LEVEL);
        setPitch(160);
        setPitchRange((int) (160 * 0.60));
        setSpeakingRate(DEFAULT_RATE);
        setVolume(MEDIUM_VOLUME);
        // Set default voice
        Engine engine = getEngine();
        SynthesizerMode mode = (SynthesizerMode) engine.getEngineMode();
        if (mode == null) {
            setVoice(null);
        } else {
            Voice[] voices = mode.getVoices();
            if ((voices != null) && (voices.length > 0)) {
                setVoice(voices[0]);
            } else {
                setVoice(null);
            }
        }

        super.reset();
    }

    @Override
    protected boolean setProperty(String propName, Object value) {
        return switch (propName) {
            case INTERRUPTIBILITY -> {
                interruptibility = (Integer) value;
                yield true;
            }
            case PITCH -> {
                pitch = (Integer) value;
                yield true;
            }
            case PITCH_RANGE -> {
                pitchRange = (Integer) value;
                yield true;
            }
            case SPEAKING_RATE -> {
                speakingRate = (Integer) value;
                yield true;
            }
            case VOICE -> {
                voice = (Voice) value;
                yield true;
            }
            case VOLUME -> {
                volume = (Integer) value;
                yield true;
            }
            default -> false;
        };
    }
}
