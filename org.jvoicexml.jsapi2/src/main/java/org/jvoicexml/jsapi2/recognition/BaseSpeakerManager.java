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

package org.jvoicexml.jsapi2.recognition;

import java.util.Collection;
import javax.speech.EngineStateException;
import javax.speech.recognition.SpeakerManager;
import javax.speech.recognition.SpeakerManagerUI;
import javax.speech.recognition.SpeakerProfile;


/**
 * Basic implementation of a speaker manager.
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 */
public class BaseSpeakerManager implements SpeakerManager {

    /** Known speaker profiles. */
    private Collection<SpeakerProfile> speakerProfiles;

    /** The current speaker profile. */
    private SpeakerProfile currentSpeaker;

    /**
     * Constructs a new object.
     */
    public BaseSpeakerManager() {
        speakerProfiles = new java.util.ArrayList<>();
        currentSpeaker = null;
    }

    @Override
    public void createSpeaker(SpeakerProfile speaker) {
        speakerProfiles.add(speaker);
    }

    @Override
    public void deleteSpeaker(SpeakerProfile speaker) {
        speakerProfiles.remove(speaker);
    }

    @Override
    public SpeakerProfile getCurrentSpeaker() {
        return currentSpeaker;
    }

    @Override
    public SpeakerManagerUI getSpeakerManagerUI() {
        return null;
    }

    @Override
    public SpeakerProfile[] listKnownSpeakers() {
        SpeakerProfile[] profiles = new SpeakerProfile[speakerProfiles.size()];
        speakerProfiles.toArray(profiles);
        return profiles;
    }

    @Override
    public void renameSpeaker(SpeakerProfile oldSpeaker, SpeakerProfile newSpeaker) {
    }

    @Override
    public void restoreCurrentSpeaker() throws EngineStateException {
    }

    @Override
    public void saveCurrentSpeaker() throws EngineStateException {
    }

    @Override
    public void setCurrentSpeaker(SpeakerProfile speaker) {
        if (!speakerProfiles.contains(speaker)) {
            createSpeaker(speaker);
        }
        currentSpeaker = speaker;
    }
}
