/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 62 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
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

package javax.speech.recognition;

import javax.speech.EngineStateException;

// Comp 2.0.6

/**
 * Provides control of speaker-specific data for a Recognizer.
 * <p>
 * The SpeakerManager for a Recognizer is obtained through its
 * getSpeakerManager method.
 * Recognizers that do not maintain speaker profiles (e.g.,
 * speaker-independent recognizers) return null for this method.
 * <p>
 * Each speaker is identified with a SpeakerProfile that is associated with
 * information about an enrollment of a user with the Recognizer.
 * The speaker data allows the recognizer to adapt to
 * the characteristics of the speaker with the goal of improving
 * performance and recognition accuracy.
 * For example, the recognizer might adjust to vocabulary preferences and accent.
 * <p>
 * The SpeakerManager provides access to the known SpeakerProfiles,
 * enables storage and loading of the speaker data once a recognizer is running,
 * and provides other management functions (e.g., deletion etc).
 * The SpeakerManager has a "current speaker" to identify the speaker data
 * currently used by the recognizer.
 * <p>
 * Except for the properties of the SpeakerProfile,
 * speaker data is not accessible to an application.
 * Speaker data may include:
 * <p>
 * Speaker information: full name, age, gender etc.
 * Speaker preferences: including settings of the RecognizerProperties
 * Language models: data about the words and word patterns of the speaker
 * Word models: data about the pronunciation of words by the speaker
 * Acoustic models: data about the speaker's voice
 * Training information and usage history
 * <p>
 * Speaker data is typically persistent - a speaker will want their profile
 * to be available from session to session.
 * An application must explicitly request a recognizer to save
 * speaker data. It is good practice to check with a speaker before
 * storing their data in case it has become corrupted.
 * <p>
 * The SpeakerManager interface provides a revert method which requests
 * the engine to restore the speaker data last saved (possibly
 * loaded at the start of a session).
 * <p>
 * The speaker data is potentially large, so loading,
 * saving, reverting, and changing speaker data
 * may all be slow operations.
 * <p>
 * The SpeakerManager for a Recognizer can be obtained from the Recognizer
 * in any state of the recognizer.
 * However, some methods of the SpeakerManager operate correctly only
 * when the Recognizer is in the ALLOCATED state.
 * <p>
 * The getCurrentSpeaker, setCurrentSpeaker and listKnownSpeakers methods
 * operate in any Recognizer state. This allows the initial speaker
 * for a Recognizer to be loaded at allocation time.
 * @see javax.speech.recognition.Recognizer#getSpeakerManager()
 * @see javax.speech.recognition.SpeakerProfile
 * @see javax.speech.recognition.RecognizerMode#getSpeakerProfiles()
 * @see javax.speech.EngineManager
 * @see javax.speech.Engine#ALLOCATED
 * @see java.lang.System
 */
public interface SpeakerManager {

    /**
     * Creates speaker data for a new speaker for this Recognizer.
     * <p>
     * A speaker is identified with a SpeakerProfile.
     * This method creates recognizer-specific data to associate with the speaker.
     * This method does not change the current speaker.
     * <p>
     * The input speaker must be non-null and contain valid values.
     * In particular, the identifier must be specified.
     * @param speaker a profile for the new speaker
     * @throws java.lang.IllegalArgumentException if the speaker is null, invalid, or has already been created
     */
    void createSpeaker(SpeakerProfile speaker) throws IllegalArgumentException;

    /**
     * Deletes the speaker data for a speaker.
     * <p>
     * If the deleted speaker is the current speaker, the current speaker is set to null.
     * @param speaker the profile for a speaker
     * @throws java.lang.IllegalArgumentException if the speaker is not known
     */
    void deleteSpeaker(SpeakerProfile speaker) throws IllegalArgumentException;

    /**
     * Gets the current SpeakerProfile.
     * <p>
     * Returns null if there is no current speaker.
     * @return a profile for the current speaker.
     * @see javax.speech.recognition.SpeakerManager#setCurrentSpeaker(javax.speech.recognition.SpeakerProfile)
     */
    SpeakerProfile getCurrentSpeaker();

    /**
     * Returns a user interface object for managing speaker data and training.
     * <p>
     * If this Recognizer has no default user interface, the return value
     * is null and the application is responsible for providing an appropriate
     * user interface.
     * <p>
     * Note: because the interface is provided by the recognizer,
     * it may allow the management of additional properties that are not otherwise
     * accessible through the standard API.
     * @return a user interface object
     */
    SpeakerManagerUI getSpeakerManagerUI();

    /**
     * Lists the SpeakerProfiles known to this Recognizer.
     * <p>
     * Returns a zero-length array if there are no known speakers.
     * @return the profiles for known speakers
     */
    SpeakerProfile[] listKnownSpeakers();

    /**
     * Associates a new speaker with existing speaker data for this Recognizer.
     * <p>
     * This allows an update of the SpeakerProfile associated with the speaker data.
     * <p>
     * The data associated with oldSpeaker is now associated with newSpeaker.
     * This method does not change the current speaker.
     * <p>
     * The oldSpeaker and newSpeaker must be non-null and contain valid values.
     * In particular, the identifier must be specified in each case.
     * @param oldSpeaker a SpeakerProfile for existing speaker data
     * @param newSpeaker the new SpeakerProfile for the speaker data
     * @throws java.lang.IllegalArgumentException if either speaker is null, invalid,
     *          oldSpeaker is not associated with speaker data,
     *          or newSpeaker is already associated with speaker data
     */
    void renameSpeaker(SpeakerProfile oldSpeaker, SpeakerProfile newSpeaker) throws IllegalArgumentException;

    /**
     * Restores the speaker data for the current speaker to
     * the last saved version.
     * <p>
     * If the speaker data has not been saved during the session,
     * the restored version will be the version loaded
     * at the start of the session.
     * If speaker data has never been saved for the current speaker,
     * then default values are restored.
     * <p>
     * Because of the large potential size of the speaker data,
     * this may be a slow operation.
     * @throws javax.speech.EngineStateException if the engine is not ALLOCATED
     * @see javax.speech.recognition.SpeakerManager#saveCurrentSpeaker()
     * @see javax.speech.Engine#ALLOCATED
     */
    void restoreCurrentSpeaker() throws EngineStateException;

    /**
     * Saves the speaker data for the current speaker.
     * <p>
     * The speaker data is stored internally by the recognizer and should
     * be available for future sessions.
     * <p>
     * Use setCurrentSpeaker or
     * restoreCurrentSpeaker to load previously stored speaker data.
     * <p>
     * Because of the large potential size of the speaker data,
     * this may be a slow operation.
     * @throws javax.speech.EngineStateException if the engine is not ALLOCATED
     * @see javax.speech.recognition.SpeakerManager#restoreCurrentSpeaker()
     * @see javax.speech.recognition.SpeakerManager#setCurrentSpeaker(javax.speech.recognition.SpeakerProfile)
     * @see javax.speech.recognition.SpeakerManager#getCurrentSpeaker()
     * @see javax.speech.Engine#ALLOCATED
     */
    void saveCurrentSpeaker() throws EngineStateException;

    /**
     * Sets the current speaker.
     * <p>
     * The SpeakerProfile identifies the speaker and should be one of the
     * objects returned by the listKnownSpeakers method.
     * <p>
     * If set before an Engine allocate, then any speaker data for this
     * speaker is loaded during allocation.
     * Otherwise, restoreCurrentSpeaker may be
     * used to load the associated speaker data.
     * <p>
     * Because speaker data may store preferred user settings for the
     * RecognizerProperties, those properties may change as a result
     * of this call.
     * <p>
     * If a speaker has not previously been set,
     * the default is SpeakerProfile.DEFAULT.
     * @param speaker a speaker profile
     * @throws java.lang.IllegalArgumentException if the speaker is not known
     * @see javax.speech.recognition.SpeakerManager#getCurrentSpeaker()
     * @see javax.speech.recognition.SpeakerManager#listKnownSpeakers()
     * @see javax.speech.recognition.SpeakerManager#saveCurrentSpeaker()
     * @see javax.speech.recognition.SpeakerManager#restoreCurrentSpeaker()
     * @see javax.speech.recognition.RecognizerProperties
     * @see javax.speech.Engine#allocate()
     * @see javax.speech.recognition.SpeakerProfile#DEFAULT
     */
    void setCurrentSpeaker(SpeakerProfile speaker) throws IllegalArgumentException;
}
