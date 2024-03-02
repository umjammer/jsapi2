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

package javax.speech.mock;

import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.Engine;
import javax.speech.EngineException;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;


/**
 * Engine for test purposes.
 *
 * @author Dirk Schnelle-Walka
 */
public class MockEngine implements Engine {

    @Override
    public void allocate() throws AudioException, EngineException,
            EngineStateException, SecurityException {
    }

    @Override
    public void allocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException,
            SecurityException {
    }

    @Override
    public void deallocate() throws AudioException, EngineException,
            EngineStateException {
    }

    @Override
    public void deallocate(int mode) throws IllegalArgumentException,
            AudioException, EngineException, EngineStateException {
    }

    @Override
    public void pause() throws EngineStateException {
    }

    @Override
    public boolean resume() throws EngineStateException {
        return false;
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
        return new MockSpeechEventExecutor();
    }

    @Override
    public void setSpeechEventExecutor(SpeechEventExecutor speechEventExecutor) {
    }
}
