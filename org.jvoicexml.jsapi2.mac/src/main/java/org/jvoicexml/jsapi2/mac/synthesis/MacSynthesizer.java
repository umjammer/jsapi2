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
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.BaseAudioSegment;
import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;


/**
 * A SAPI compliant {@link javax.speech.synthesis.Synthesizer}.
 *
 * @author Dirk Schnelle-Walka
 * @author Josua Arndt
 */
public final class MacSynthesizer extends BaseSynthesizer {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(MacSynthesizer.class.getName());

    static {
        System.loadLibrary("Jsapi2MacBridge");
    }

    /** SAPI synthesizer handle. */
    private long synthesizerHandle;

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
            voice = null;
        } else {
            Voice[] voices = mode.getVoices();
            if (voices == null) {
                voice = null;
            } else {
                voice = voices[0];
            }
        }
        synthesizerHandle = macHandleAllocate(voice);
    }

    /**
     * Allocates a Mac synthesizer.
     *
     * @param voice the voice to use
     * @return synthesizer handle
     */
    private native long macHandleAllocate(Voice voice);

    @Override
    public boolean handleCancel() {
        return macHandleCancel(synthesizerHandle);
    }

    /**
     * Cancels the current output.
     *
     * @param handle the synthesizer handle
     * @return <code>true</code> if the current output has been canceled
     */
    private native boolean macHandleCancel(long handle);

    @Override
    protected boolean handleCancel(int id) {
        return macHandleCancel(synthesizerHandle, id);
    }

    /**
     * Cancels the output with the given id.
     *
     * @param handle the synthesizer handle
     * @param id     the item to cancel
     * @return <code>true</code> if the output with the given id has been
     * canceled
     */
    private native boolean macHandleCancel(long handle, int id);

    @Override
    protected boolean handleCancelAll() {
        return macHandleCancelAll(synthesizerHandle);
    }

    /**
     * Cancels all outputs.
     *
     * @param handle the synthesizer handle
     * @return <code>true</code> if at least one output has been canceled
     */
    private native boolean macHandleCancelAll(long handle);

    @Override
    public void handleDeallocate() {
        // Leave some time to let all resources detach
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return;
        }
        macHandlDeallocate(synthesizerHandle);
        synthesizerHandle = 0;
    }

    /**
     * Deallocates the SAPI synthesizer.
     *
     * @param handle the synthesizer handle
     */
    private native void macHandlDeallocate(long handle);

    @Override
    public void handlePause() {
        macHandlePause(synthesizerHandle);
    }

    /**
     * Pauses the synthesizer.
     *
     * @param handle the synthesizer handle
     *               the synthesizer handle
     */
    private native void macHandlePause(long handle);

    @Override
    public boolean handleResume() {
        return macHandlResume(synthesizerHandle);
    }

    /**
     * Resumes the synthesizer.
     *
     * @param handle the synthesizer handle
     *               the synthesizer handle
     * @return <code>true</code> if the synthesizer is resumed
     */
    private native boolean macHandlResume(long handle);

    @Override
    public AudioSegment handleSpeak(int id, String item) {
        byte[] bytes = macHandleSpeak(synthesizerHandle, id, item);
        AudioManager manager = getAudioManager();
        String locator = manager.getMediaLocator();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        AudioSegment segment;
        if (locator == null) {
            segment = new BaseAudioSegment(item, in);
        } else {
            segment = new BaseAudioSegment(locator, item, in);
        }
        return segment;
    }

    /**
     * Speaks the given item.
     *
     * @param handle synthesizer handle
     * @param id     id of the item
     * @param item   the item to speak
     * @return byte array of the synthesized speech
     */
    private native byte[] macHandleSpeak(long handle, int id, String item);

    @Override
    protected AudioSegment handleSpeak(int id, Speakable item) {
        throw new IllegalArgumentException("Synthesizer does not support" + " speech markup!");
    }

    /**
     * Speaks the given item.
     *
     * @param handle synthesizer handle
     * @param id     id of the item
     * @param ssml   the SSML markup to speak
     * @return byte array of the synthesized speech
     */
    private native byte[] macHandleSpeakSsml(long handle, int id, String ssml);

    @Override
    protected AudioFormat getEngineAudioFormat() {
        return macGetAudioFormat(synthesizerHandle);
    }

    /**
     * retrieves the default audio format.
     *
     * @param handle synthesizer handle.
     * @return native audio format
     */
    private native AudioFormat macGetAudioFormat(long handle);

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue,
            Object newValue) {
        LOGGER.warning("changing property '" + propName
                + "' to '" + newValue + "' ignored");
    }
}

