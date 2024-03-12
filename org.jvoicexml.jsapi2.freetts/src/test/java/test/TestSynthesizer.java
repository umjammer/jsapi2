/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package test;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.AudioSegment;
import javax.speech.EngineException;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;
import javax.speech.synthesis.Speakable;
import javax.speech.synthesis.SpeakableException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerListener;
import javax.speech.synthesis.SynthesizerProperties;


/**
 * TestSynthesizer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-12 nsano initial version <br>
 */
public class TestSynthesizer implements Synthesizer {

    @Override
    public void addSpeakableListener(SpeakableListener listener) {

    }

    @Override
    public void removeSpeakableListener(SpeakableListener listener) {

    }

    @Override
    public void addSynthesizerListener(SynthesizerListener listener) {

    }

    @Override
    public void removeSynthesizerListener(SynthesizerListener listener) {

    }

    @Override
    public boolean cancel() throws EngineStateException {
        return false;
    }

    @Override
    public boolean cancel(int id) throws EngineStateException {
        return false;
    }

    @Override
    public boolean cancelAll() throws EngineStateException {
        return false;
    }

    @Override
    public String getPhonemes(String text) throws EngineStateException {
        return null;
    }

    @Override
    public SynthesizerProperties getSynthesizerProperties() {
        return null;
    }

    @Override
    public void pause() throws EngineStateException {

    }

    @Override
    public boolean resume() throws EngineStateException {
        return false;
    }

    @Override
    public void setSpeakableMask(int mask) {

    }

    @Override
    public int getSpeakableMask() {
        return 0;
    }

    @Override
    public int speak(AudioSegment audio, SpeakableListener listener) throws SpeakableException, EngineStateException, IllegalArgumentException {
        return 0;
    }

    @Override
    public int speak(Speakable speakable, SpeakableListener listener) throws SpeakableException, EngineStateException {
        return 0;
    }

    @Override
    public int speak(String text, SpeakableListener listener) throws EngineStateException {
        return 0;
    }

    @Override
    public int speakMarkup(String synthesisMarkup, SpeakableListener listener) throws SpeakableException, EngineStateException {
        return 0;
    }

    @Override
    public void allocate() throws AudioException, EngineException, EngineStateException, SecurityException {

    }

    @Override
    public void allocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException, SecurityException {

    }

    @Override
    public void deallocate() throws AudioException, EngineException, EngineStateException {

    }

    @Override
    public void deallocate(int mode) throws IllegalArgumentException, AudioException, EngineException, EngineStateException {

    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        return false;
    }

    @Override
    public long waitEngineState(long state) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return 0;
    }

    @Override
    public long waitEngineState(long state, long timeout) throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return 0;
    }

    @Override
    public AudioManager getAudioManager() {
        return null;
    }

    @Override
    public EngineMode getEngineMode() {
        return null;
    }

    @Override
    public long getEngineState() {
        return 0;
    }

    @Override
    public VocabularyManager getVocabularyManager() {
        return null;
    }

    @Override
    public void setEngineMask(int mask) {

    }

    @Override
    public int getEngineMask() {
        return 0;
    }

    @Override
    public SpeechEventExecutor getSpeechEventExecutor() {
        return null;
    }

    @Override
    public void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor) {

    }
}
