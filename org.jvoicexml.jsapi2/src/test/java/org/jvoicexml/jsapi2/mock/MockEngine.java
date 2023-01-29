/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
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

package org.jvoicexml.jsapi2.mock;

import java.util.Collection;
import javax.speech.AudioException;
import javax.speech.AudioManager;
import javax.speech.EngineEvent;
import javax.speech.EngineException;
import javax.speech.EngineListener;
import javax.speech.EngineStateException;
import javax.speech.SpeechEventExecutor;
import javax.speech.VocabularyManager;

import org.jvoicexml.jsapi2.BaseEngine;
import org.jvoicexml.jsapi2.BaseEngineProperties;


/**
 * An engine for test purposes.
 *
 * @author Dirk Schnelle-Walka
 */
public class MockEngine extends BaseEngine {
    public MockEngine() {
        super(null);
    }

    @Override
    protected void baseAllocate() throws AudioException, EngineException,
            EngineStateException, SecurityException {
    }

    @Override
    protected AudioManager createAudioManager() {
        return new MockAudioManager();
    }

    @Override
    protected VocabularyManager createVocabularyManager() {
        return null;
    }

    @Override
    protected SpeechEventExecutor createSpeechEventExecutor() {
        return new MockSpeechEventExecutor();
    }

    @Override
    protected void baseDeallocate() throws EngineStateException,
            EngineException, AudioException {
    }

    @Override
    protected void basePause() throws EngineStateException {
    }

    @Override
    protected boolean baseResume() throws EngineStateException {
        return false;
    }

    @Override
    public final void fireEvent(Collection<EngineListener> listeners,
                                EngineEvent event) {
    }

    @Override
    protected EngineEvent createStateTransitionEngineEvent(long oldState, long newState,
                                                           int id) {
        return new MockEngineEvent(this, id, oldState, newState, null);
    }

    @Override
    protected void handlePropertyChangeRequest(BaseEngineProperties properties,
                                               String propName, Object oldValue, Object newValue) {
        properties.commitPropertyChange(propName, oldValue, newValue);
    }
}
