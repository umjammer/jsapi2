/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 296 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.mac.synthesis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.BaseAudioSegment;
import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.mac.SynthesizerDelegate;
import org.jvoicexml.jsapi2.mac.rococoa.NSSpeechSynthesizer;
import org.jvoicexml.jsapi2.mac.rococoa.NSVoice;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;


/**
 * A SAPI compliant {@link Synthesizer}.
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 */
public final class MacSynthesizer extends BaseSynthesizer {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(MacSynthesizer.class.getName());

    /** */
    private NSSpeechSynthesizer synthesizer;

    /** */
    private SynthesizerDelegate delegate;

    /**
     * Constructs a new synthesizer object.
     *
     * @param mode the synthesizer mode
     */
    MacSynthesizer(MacSynthesizerMode mode) {
        super(mode);
    }

    @Override
    protected void handleAllocate() throws EngineStateException, EngineException, AudioException, SecurityException {
        Voice voice;
        MacSynthesizerMode mode = (MacSynthesizerMode) getEngineMode();
        if (mode == null) {
            throw new EngineException("engine mode is null");
        } else {
            Voice[] voices = mode.getVoices();
logger.log(Level.INFO, "voices: " + voices.length);
            if (voices.length == 0) {
                throw new EngineException("no voice");
            } else {
                voice = voices[0];
            }
        }
logger.log(Level.TRACE, "voice: " + voice.getName());
        getSynthesizerProperties().setVoice(voice);

//logger.log(Level.TRACE, "default voice2: " + NSSpeechSynthesizer.defaultVoice().getName());
        synthesizer = NSSpeechSynthesizer.synthesizerWithVoice(toNativeVoice(voice));
        delegate = new SynthesizerDelegate(synthesizer); // delegate is implemented in vavi-speech

        //
        long newState = ALLOCATED | RESUMED;
        newState |= (getQueueManager().isQueueEmpty() ? QUEUE_EMPTY : QUEUE_NOT_EMPTY);
        setEngineState(CLEAR_ALL_STATE, newState);
    }

    /** */
    private NSVoice toNativeVoice(Voice voice) {
//logger.log(Level.TRACE, "vioce2: " + getSynthesizerProperties().getVoice());
        if (voice == null) {
            return null;
        }
        Optional<NSVoice> result = NSSpeechSynthesizer.availableVoices().stream().filter(v -> v.getName().equals(voice.getName())).findFirst();
        return result.orElse(null);
    }

    @Override
    public boolean handleCancel() {
//        synthesizer.stopSpeaking();
        return false;
    }

    @Override
    protected boolean handleCancel(int id) {
//        synthesizer.stopSpeaking();
        return false;
    }

    @Override
    protected boolean handleCancelAll() {
//        synthesizer.stopSpeaking();
        return false;
    }

    @Override
    public void handleDeallocate() {
        // Leave some time to let all resources detach
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
        }
        synthesizer.release();

        //
        setEngineState(CLEAR_ALL_STATE, DEALLOCATED);
        getQueueManager().cancelAllItems();
        getQueueManager().terminate();
    }

    @Override
    public void handlePause() {
//        synthesizer.pauseSpeakingAtBoundary(NSSpeechBoundary.ImmediateBoundary);
    }

    @Override
    public boolean handleResume() {
//        synthesizer.continueSpeaking();
        return false;
    }

    @Override
    public AudioSegment handleSpeak(int id, String item) {
        AudioManager manager = getAudioManager();
        String locator = manager.getMediaLocator();
        InputStream in = synthe(item);
        AudioSegment segment;
        if (locator == null) {
            segment = new BaseAudioSegment(item, in);
        } else {
            segment = new BaseAudioSegment(locator, item, in);
        }
        return segment;
    }

    /** */
    private AudioInputStream synthe(String text) {
        try {
//logger.log(Level.TRACE, "vioce: " + getSynthesizerProperties().getVoice());
            synthesizer.setVoice(toNativeVoice(getSynthesizerProperties().getVoice()));
            Path path = Files.createTempFile(getClass().getName(), ".aiff");
            synthesizer.startSpeakingStringToURL(text, path.toUri());
            // wait to finish writing whole data
            delegate.waitForSpeechDone(10000, true);
            byte[] wav = Files.readAllBytes(path);
            ByteArrayInputStream bais = new ByteArrayInputStream(wav);
            // you should pass bytes to BaseAudioSegment as AudioInputStream or causes crackling!
            AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
            Files.delete(path);
            return ais;
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected AudioSegment handleSpeak(int id, Speakable item) {
        throw new IllegalArgumentException("Synthesizer does not support" + " speech markup!");
    }

    @Override
    protected AudioFormat getEngineAudioFormat() {
        // new AudioFormat(format.nSamplesPerSec, format.wBitsPerSample, format.nChannels, true, true);
        return new AudioFormat(22050.0f, 16, 1, true, false);
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue,
            Object newValue) {
        properties.commitPropertyChange(propName, oldValue, newValue);
    }
}
