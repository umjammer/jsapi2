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

package org.jvoicexml.jsapi2.mock.synthesis;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.sound.sampled.AudioFormat;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SynthesizerEvent;

import org.jvoicexml.jsapi2.BaseEngineProperties;
import org.jvoicexml.jsapi2.mock.MockAudioManager;
import org.jvoicexml.jsapi2.mock.MockSpeechEventExecutor;
import org.jvoicexml.jsapi2.synthesis.BaseSynthesizer;


/**
 * Dummy implementation of a {@link javax.speech.synthesis.Synthesizer}
 * for test purposes.
 *
 * @author Dirk Schnelle-Walka
 */
public final class MockSynthesizer extends BaseSynthesizer {

    public MockSynthesizer() {
        super(null);
    }

    public MockSynthesizer(boolean queueUpdatedEnabled) {
        super(null);
        setEngineMask(getEngineMask() | (queueUpdatedEnabled ? SynthesizerEvent.QUEUE_UPDATED : 0));
    }

    /** user defined speaking routine */
    private Consumer<Integer> speakHandler;

    public void setSpeakHandler(Consumer<Integer> speakHandler) {
        this.speakHandler = speakHandler;
    }

    /** user defined speaking routine */
    private BiFunction<Integer, String, AudioSegment> audioSegmentMaker;

    public void setAudioSegmentMaker(BiFunction<Integer, String, AudioSegment> audioSegmentMaker) {
        this.audioSegmentMaker = audioSegmentMaker;
    }

    @Override
    protected void handleAllocate() throws EngineStateException,
            EngineException, AudioException, SecurityException {
    }

    @Override
    protected boolean handleCancel() {
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
    protected void handleDeallocate() {
    }

    @Override
    protected void handlePause() {
    }

    @Override
    protected boolean handleResume() {
        return true;
    }

    @Override
    protected AudioSegment handleSpeak(int id, String item) {
        if (audioSegmentMaker != null) return audioSegmentMaker.apply(id, item);
        if (speakHandler != null) speakHandler.accept(id);
        return new AudioSegment(
                "stream://audio?encoding=pcm&rate=11025&bits=16&channels=1",
                item);
    }

    @Override
    protected AudioSegment handleSpeak(int id, Speakable item) {
        if (audioSegmentMaker != null) return audioSegmentMaker.apply(id, item.getMarkupText());
        if (speakHandler != null) speakHandler.accept(id);
        String text = item.getMarkupText();
        return new AudioSegment(
                "stream://audio?encoding=pcm&rate=11025&bits=16&channels=1",
                text);
    }

    @Override
    protected AudioManager createAudioManager() {
        return new MockAudioManager();
    }

    @Override
    protected SpeechEventExecutor createSpeechEventExecutor() {
        return new MockSpeechEventExecutor();
    }

    @Override
    protected VocabularyManager createVocabularyManager() {
        return null;
    }

    @Override
    protected void handlePropertyChangeRequest(
            BaseEngineProperties properties,
            String propName, Object oldValue, Object newValue) {
        properties.commitPropertyChange(propName, oldValue, newValue);
    }

    @Override
    protected AudioFormat getEngineAudioFormat() {
        return null;
    }

    @Override
    public String toString() { return "MockSynthesizer"; }
}
