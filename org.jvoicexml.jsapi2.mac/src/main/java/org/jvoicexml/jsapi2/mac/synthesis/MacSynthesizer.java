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
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;
import org.rococoa.Foundation;
import org.rococoa.ObjCBlocks.BlockLiteral;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSError;
import org.rococoa.cocoa.foundation.NSObject;
import vavix.rococoa.avfoundation.AVAudioFile;
import vavix.rococoa.avfoundation.AVAudioFormat;
import vavix.rococoa.avfoundation.AVAudioPCMBuffer;
import vavix.rococoa.avfoundation.AVSpeechSynthesisVoice;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer;
import vavix.rococoa.avfoundation.AVSpeechSynthesizer.AVSpeechSynthesizerBufferCallback;
import vavix.rococoa.avfoundation.AVSpeechUtterance;

import static org.rococoa.ObjCBlocks.block;


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
    private AVSpeechSynthesizer synthesizer;

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
        if (getSynthesizerProperties().getVoice() == null) {
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
        }

//logger.log(Level.TRACE, "default voice2: " + NSSpeechSynthesizer.defaultVoice().getName());
        synthesizer = AVSpeechSynthesizer.newInstance();
//        delegate = new SynthesizerDelegate(synthesizer);

        //
        long newState = ALLOCATED | RESUMED;
        newState |= (getQueueManager().isQueueEmpty() ? QUEUE_EMPTY : QUEUE_NOT_EMPTY);
        setEngineState(CLEAR_ALL_STATE, newState);
    }

    /** */
    private AVSpeechSynthesisVoice toNativeVoice(Voice voice) {
logger.log(Level.TRACE, "toNativeVoice: " + getSynthesizerProperties().getVoice());
        if (voice == null) {
logger.log(Level.TRACE, "voice not set");
            return AVSpeechSynthesisVoice.speechVoices().get(0);
        }
        AVSpeechSynthesisVoice nativeVoice = null;
        for (NSObject object : AVSpeechSynthesisVoice.speechVoices()) {
            AVSpeechSynthesisVoice nv = Rococoa.cast(object, AVSpeechSynthesisVoice.class);
            if (nv.name().equals(voice.getName())) {
                nativeVoice = nv;
            }
        }
logger.log(Level.TRACE, "toNativeVoice: " + nativeVoice);
        return nativeVoice != null ? nativeVoice : AVSpeechSynthesisVoice.speechVoices().get(0);
    }

    @Override
    public boolean handleCancel() {
        return true;
    }

    @Override
    protected boolean handleCancel(int id) {
        return true;
    }

    @Override
    protected boolean handleCancelAll() {
        return true;
    }

    @Override
    public void handleDeallocate() {
        setEngineState(CLEAR_ALL_STATE, DEALLOCATED);
        getQueueManager().cancelAllItems();
        getQueueManager().terminate();

        // Leave some time to let all resources detach
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
        }
        synthesizer.release();
    }

    @Override
    public void handlePause() {
    }

    @Override
    public boolean handleResume() {
        return true;
    }

    @Override
    public AudioSegment handleSpeak(int id, String item) {
logger.log(Level.TRACE, "handleSpeak");
        AudioManager manager = getAudioManager();
        String locator = manager.getMediaLocator();
        InputStream in = synthesize(item);
        AudioSegment segment;
        if (locator == null) {
            segment = new BaseAudioSegment(item, in);
        } else {
            segment = new BaseAudioSegment(locator, item, in);
        }
        return segment;
    }

    /** */
    private AudioInputStream synthesize(String text) {
logger.log(Level.TRACE, "text: " + text);
        try {
logger.log(Level.TRACE, "voice: " + getSynthesizerProperties().getVoice());
            Path path = Files.createTempFile(getClass().getName(), ".wav");
            BlockLiteral bufferCallback = null;
            try {
                AVSpeechUtterance utterance = AVSpeechUtterance.of(text);
                var voice = toNativeVoice(getSynthesizerProperties().getVoice());
logger.log(Level.TRACE, "nativeVoice: " + voice);
                utterance.setVoice(voice);
                utterance.setVolume(getSynthesizerProperties().getVolume() / 100f);

                CountDownLatch cdl = new CountDownLatch(1);
                AtomicReference<AVAudioFile> audioFile = new AtomicReference<>();

                bufferCallback = block((AVSpeechSynthesizerBufferCallback) (block, audioBufferId) -> {
                    try {
                        AVAudioPCMBuffer audioBuffer = Rococoa.wrap(audioBufferId, AVAudioPCMBuffer.class);
                        if (audioBuffer == null) {
logger.log(Level.WARNING, "audioBuffer is null");
                            cdl.countDown();
                            throw new IllegalStateException("buffer is not pcm");
                        }
                        if (audioBuffer.frameLength() == 0) {
                            // done
                            cdl.countDown();
                        } else {
                            if (audioFile.get() == null) {
                                AVAudioFormat format16 = AVAudioFormat.init(3, audioBuffer.format().sampleRate(), 1, true);
                                audioFile.set(AVAudioFile.init(path.toUri(), format16.settings(), audioBuffer.format().commonFormat(), audioBuffer.format().isInterleaved()));
                                if (audioFile.get() == null) {
                                    cdl.countDown();
                                    throw new IllegalStateException("file creation failed");
                                }
                            }
                            ObjCObjectByReference outError = new ObjCObjectByReference();
                            audioFile.get().writeFromBuffer_error(audioBuffer, outError);
                            NSError error = outError.getValueAs(NSError.class);
                            if (error != null) {
logger.log(Level.WARNING, "writeFromBuffer: " + error.description());
                                cdl.countDown();
                                throw new IllegalStateException(error.description());
                            }
                        }
                    } catch (IOException e) {
logger.log(Level.ERROR, e.getMessage(), e);
                        cdl.countDown();
                        throw new UncheckedIOException(e);
                    }
                });

                synthesizer.writeUtterance_toBufferCallback(utterance, bufferCallback);
                cdl.await();

                if (audioFile.get() != null) {
                    audioFile.get().close();
                }

                return AudioSystem.getAudioInputStream(new ByteArrayInputStream(Files.readAllBytes(path)));
            } finally {
                Files.deleteIfExists(path);
                if (bufferCallback != null)
                    Foundation.getRococoaLibrary().releaseObjCBlock(bufferCallback.getPointer());
            }
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
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
