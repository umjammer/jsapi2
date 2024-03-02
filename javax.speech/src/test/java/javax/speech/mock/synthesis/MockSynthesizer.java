/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
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

package javax.speech.mock.synthesis;

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
 * Synthesizer for test purposes.
 *
 * @author Dirk Schnelle-Walka
 */
public class MockSynthesizer implements Synthesizer {

    @Override
    public void allocate() throws AudioException, EngineException,
            EngineStateException, SecurityException {
    }

    @Override
    public void allocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException, SecurityException {
    }

    @Override
    public void deallocate() throws AudioException, EngineException, EngineStateException {
    }

    @Override
    public void deallocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException {
    }

    @Override
    public boolean testEngineState(long state) throws IllegalArgumentException {
        return false;
    }

    @Override
    public long waitEngineState(long state) throws InterruptedException,
            IllegalArgumentException, IllegalStateException {
        return 0;
    }

    @Override
    public long waitEngineState(long state, long timeout)
            throws InterruptedException, IllegalArgumentException, IllegalStateException {
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
    public int speak(AudioSegment audio, SpeakableListener listener)
            throws SpeakableException, EngineStateException, IllegalArgumentException {
        return 0;
    }

    @Override
    public int speak(Speakable speakable, SpeakableListener listener)
            throws SpeakableException, EngineStateException {
        return 0;
    }

    @Override
    public int speak(String text, SpeakableListener listener) throws EngineStateException {
        return 0;
    }

    @Override
    public int speakMarkup(String synthesisMarkup, SpeakableListener listener)
            throws SpeakableException, EngineStateException {
        return 0;
    }
}
