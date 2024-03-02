/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.synthesis.freetts;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.SynthesizerProperties;

import com.sun.speech.freetts.FreeTTSSpeakableImpl;
import com.sun.speech.freetts.audio.AudioPlayer;
import org.jvoicexml.jsapi2.BaseAudioManager;
import org.jvoicexml.jsapi2.BaseAudioSegment;
import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;
import org.w3c.dom.Document;


/**
 * Provides partial support for a JSAPI 2.0 synthesizer for the FreeTTS speech
 * synthesis system.
 *
 * @author Dirk Schnelle-Walka
 */
public class FreeTTSSynthesizer extends BaseSynthesizer {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(FreeTTSSynthesizer.class.getName());

    /**
     * The currently active voice for this synthesizer.
     */
    private FreeTTSVoice curVoice;

    /** The audio player to use. */
    private AudioPlayer audioPlayer;

    /** The ssml to jsml transformer. */
    private final Ssml2JsmlTransformer transformer = new Ssml2JsmlTransformer();

    /**
     * Creates a new Synthesizer in the DEALLOCATED state.
     *
     * @param desc describes the allowed mode of operations for this synthesizer.
     */
    public FreeTTSSynthesizer(FreeTTSSynthesizerMode desc) {
        super(desc);
        audioPlayer = null;
    }

    @Override
    protected SynthesizerProperties createSynthesizerProperties() {
        return new FreeTTSEngineProperties(this);
    }

    @Override
    public void handleAllocate() throws EngineStateException, EngineException, AudioException, SecurityException {
        boolean ok = false;
        SynthesizerMode synthesizerMode = (SynthesizerMode) getEngineMode();

        if (synthesizerMode.getVoices().length > 0) {
            FreeTTSVoice freettsVoice = (FreeTTSVoice) synthesizerMode.getVoices()[0];
            ok = setCurrentVoice(freettsVoice);
        }

        if (ok) {
            BaseAudioManager manager = (BaseAudioManager) getAudioManager();
            audioPlayer = new FreeTTSAudioPlayer(manager);
            long newState = ALLOCATED | RESUMED;
            newState |= (getQueueManager().isQueueEmpty() ? QUEUE_EMPTY : QUEUE_NOT_EMPTY);
            setEngineState(CLEAR_ALL_STATE, newState);
        } else {
            throw new AudioException("Can't allocate FreeTTS synthesizer");
        }
    }

    /**
     * Sets the given voice to be the current voice. If the voice cannot be
     * loaded, this call has no affect.
     *
     * @param voice the new voice.
     * @return {@code true} if the voice was loaded
     */
    protected boolean setCurrentVoice(FreeTTSVoice voice) {
        com.sun.speech.freetts.Voice freettsVoice = voice.getVoice();
        boolean ok = false;

        // Load the voice if it is not loaded.
        if (!freettsVoice.isLoaded()) {
            freettsVoice.allocate();
            freettsVoice.setAudioPlayer(audioPlayer);
        }

        if (freettsVoice.isLoaded()) {
            curVoice = voice;
            ok = true;
        }
        return ok;
    }

    /**
     * Handles a deallocation request. Cancels all pending items, terminates the
     * output handler, and posts the state changes.
     */
    @Override
    public void handleDeallocate() throws EngineStateException, EngineException, AudioException {
        setEngineState(CLEAR_ALL_STATE, DEALLOCATED);
        getQueueManager().cancelAllItems();
        getQueueManager().terminate();

        // Close the audio. This should flush out any queued audio data
        if (audioPlayer != null) {
            try {
                audioPlayer.close();
            } catch (Exception e) {
                throw new AudioException(e.getMessage());
            }
        }
    }

    /**
     * Pauses the output.
     */
    @Override
    public void handlePause() {
        audioPlayer.pause();
    }

    /**
     * Resumes the output.
     */
    @Override
    public boolean handleResume() {
        audioPlayer.resume();
        return true;
    }

    protected Speakable parseMarkup(String synthesisMarkup) {
        return null;
    }

    /**
     * Outputs the given queue item to the current voice
     *
     * @param id the item to output
     */
    private AudioSegment handleSpeak(int id, FreeTTSSpeakableImpl speakElement) {
        com.sun.speech.freetts.Voice voice = curVoice.getVoice();
        voice.setAudioPlayer(audioPlayer);

        voice.speak(speakElement);

        if (audioPlayer instanceof FreeTTSAudioPlayer player) {
            InputStream in;
            try {
                in = player.getAudioBytes();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getLocalizedMessage());
                return null;
            } finally {
                player.reset();
            }
            AudioManager manager = getAudioManager();
            String locator = manager.getMediaLocator();
            String markupText = speakElement.getText();
            AudioSegment segment;
            if (locator == null) {
                segment = new BaseAudioSegment(markupText, in);
            } else {
                segment = new BaseAudioSegment(locator, markupText, in);
            }
            return segment;
        }
        return null;
    }

    @Override
    public AudioSegment handleSpeak(int id, Speakable item) {
        String markup = item.getMarkupText();
        Document document = transformer.transform(markup);
        FreeTTSSpeakableImpl speakable = new FreeTTSSpeakableImpl(document);
        return handleSpeak(id, speakable);
    }

    @Override
    public AudioSegment handleSpeak(int id, String text) {
        FreeTTSSpeakableImpl speakable = new FreeTTSSpeakableImpl(text);
        return handleSpeak(id, speakable);
    }

    @Override
    public boolean handleCancelAll() {
        return false;
    }

    @Override
    public boolean handleCancel(int id) {
        return false;
    }

    @Override
    public boolean handleCancel() {
        return false;
    }

    @Override
    protected AudioFormat getEngineAudioFormat() {
        return new AudioFormat(8000f, 16, 1, true, true);
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties, String propName,
            Object oldValue, Object newValue) {
//        if (propName.equals(BaseSynthesizerProperties.VOICE)) {
//            if (curVoice == null) {
//                return;
//            }
//            com.sun.speech.freetts.Voice freettsVoice = curVoice.getVoice();
//        }
        properties.commitPropertyChange(propName, oldValue, newValue);
//        logger.log(Level.WARNING, "changing property '" + propName + "' to '" + newValue + "' ignored");
    }
}
