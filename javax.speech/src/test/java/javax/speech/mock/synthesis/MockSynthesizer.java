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

    public void allocate() throws AudioException, EngineException,
            EngineStateException, SecurityException {
    }

    public void allocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException, SecurityException {
    }

    public void deallocate() throws AudioException, EngineException, EngineStateException {
    }

    public void deallocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException {
    }

    public boolean testEngineState(long state) throws IllegalArgumentException {
        return false;
    }

    public long waitEngineState(long state) throws InterruptedException,
            IllegalArgumentException, IllegalStateException {
        return 0;
    }

    public long waitEngineState(long state, long timeout)
            throws InterruptedException, IllegalArgumentException, IllegalStateException {
        return 0;
    }

    public AudioManager getAudioManager() {
        return null;
    }

    public EngineMode getEngineMode() {
        return null;
    }

    public long getEngineState() {
        return 0;
    }

    public VocabularyManager getVocabularyManager() {
        return null;
    }

    public void setEngineMask(int mask) {
    }

    public int getEngineMask() {
        return 0;
    }

    public SpeechEventExecutor getSpeechEventExecutor() {
        return null;
    }

    public void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor) {
    }

    public void addSpeakableListener(SpeakableListener listener) {
    }

    public void removeSpeakableListener(SpeakableListener listener) {
    }

    public void addSynthesizerListener(SynthesizerListener listener) {
    }

    public void removeSynthesizerListener(SynthesizerListener listener) {
    }

    public boolean cancel() throws EngineStateException {
        return false;
    }

    public boolean cancel(int id) throws EngineStateException {
        return false;
    }

    public boolean cancelAll() throws EngineStateException {
        return false;
    }

    public String getPhonemes(String text) throws EngineStateException {
        return null;
    }

    public SynthesizerProperties getSynthesizerProperties() {
        return null;
    }

    public void pause() throws EngineStateException {
    }

    public boolean resume() throws EngineStateException {
        return false;
    }

    public void setSpeakableMask(int mask) {
    }

    public int getSpeakableMask() {
        return 0;
    }

    public int speak(AudioSegment audio, SpeakableListener listener)
            throws SpeakableException, EngineStateException, IllegalArgumentException {
        return 0;
    }

    public int speak(Speakable speakable, SpeakableListener listener)
            throws SpeakableException, EngineStateException {
        return 0;
    }

    public int speak(String text, SpeakableListener listener) throws EngineStateException {
        return 0;
    }

    public int speakMarkup(String synthesisMarkup, SpeakableListener listener)
            throws SpeakableException, EngineStateException {
        return 0;
    }
}
