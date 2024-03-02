/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 61 $
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.speech.EngineMode;
import javax.speech.SpeechLocale;

// Comp 2.0.6

/**
 * Provides information about a specific operating mode of a Recognizer.
 * <p>
 * RecognizerMode extends EngineMode with features
 * specific to speech Recognizers.
 * <p>
 * Like EngineMode, there are two types of RecognizerMode objects:
 * those created by the EngineManager or a Recognizer instance and
 * those created by an application.
 * Engine-created RecognizerMode objects are obtained from the EngineManager
 * through the availableEngines method.
 * They are also returned from a Recognizer instance with the getEngineMode method.
 * For engine-created RecognizerMode objects, all features will be set to non-null values.
 * <p>
 * Applications can create RecognizerMode objects using the appropriate constructor.
 * Applications may leave any or all of the feature values null to indicate "don't care".
 * Typically, application-created RecognizerModes are used to test the engine-created
 * RecognizerModes to select an appropriate recognizer for creation.
 * <p>
 * The EngineMode class describes additional detail on Engine selection.
 * <p>
 * Engine creation is described in the documentation for the EngineManager class.
 * @see javax.speech.Engine
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.Engine#getEngineMode()
 * @see javax.speech.EngineMode
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)
 * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
 * @see javax.speech.recognition.SpeakerManager
 * @see javax.speech.recognition.SpeakerProfile
 */
public class RecognizerMode extends EngineMode {

    private static int hashCode(Object[] array) {
        int prime = 31;
        if (array == null)
            return 0;
        int result = 1;
        for (Object o : array) {
            result = prime * result + (o == null ? 0 : o.hashCode());
        }
        return result;
    }

    /**
     * RecognizerMode with all mode features set to "don't care" values.
     * <p>
     * This can be used to select a default Recognizer with
     * EngineManager.createEngine.
     * The documentation for the Recognizer interface includes
     * an example of this.
     * @see javax.speech.recognition.Recognizer
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
     */
    public static RecognizerMode DEFAULT = new RecognizerMode();

    public static Integer SMALL_SIZE = 10;

    public static Integer MEDIUM_SIZE = 100;

    public static Integer LARGE_SIZE = 1000;

    public static Integer VERY_LARGE_SIZE = 10000;

    private Integer vocabSupport;

    private SpeechLocale[] locales;

    private SpeakerProfile[] profiles;

    /**
     * Constructs a RecognizerMode with all mode features set to don't care values.
     */
    public RecognizerMode() {
    }

    public RecognizerMode(SpeechLocale locale) {
        locales = new SpeechLocale[1];

        locales[0] = locale;
    }

    public RecognizerMode(String engineName, String modeName, Boolean running,
                          Boolean supportsLetterToSound, Boolean supportsMarkup,
                          Integer vocabSupport, SpeechLocale[] locales,
                          SpeakerProfile[] profiles) {
        super(engineName, modeName, running, supportsLetterToSound, supportsMarkup);
        this.vocabSupport = vocabSupport;
        this.locales = locales;
        this.profiles = profiles;
    }

    /**
     * Returns a hash code value for the object.
     * <p>
     * This method is supported for the benefit of hashtables.
     * @return a hash code value for the object
     */
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + RecognizerMode.hashCode(locales);
        result = prime * result + RecognizerMode.hashCode(profiles);
        result = prime * result + ((vocabSupport == null) ? 0 : vocabSupport.hashCode());
        return result;
    }

    /**
     * Returns true if and only if the mode parameter
     * is not null and has equal values of all
     * mode features with this EngineMode.
     * mode an EngineMode to compare
     * @return true if mode is equal to this EngineMode
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RecognizerMode other = (RecognizerMode) obj;
        if (locales == null) {
            if (other.locales != null) {
                return false;
            }
        } else if (locales.length != other.locales.length) {
            return false;
        } else {
            for (int i = 0; i < locales.length; i++) {
                if (!locales[i].equals(other.locales[i])) {
                    return false;
                }
            }
        }
        if (profiles == null) {
            if (other.profiles != null) {
                return false;
            }
        } else if (profiles.length != other.profiles.length) {
            return false;
        } else {
            for (int i = 0; i < profiles.length; i++) {
                if (!profiles[i].equals(other.profiles[i])) {
                    return false;
                }
            }
        }
        if (vocabSupport == null) {
            if (other.vocabSupport != null) {
                return false;
            }
        } else if (!vocabSupport.equals(other.vocabSupport)) {
            return false;
        }
        return true;
    }

    public SpeechLocale[] getSpeechLocales() {
        return locales;
    }

    /**
     * Gets the list of SpeakerProfiles specified in this RecognizerMode.
     * <p>
     * The list of SpeakerProfiles is the same as returned by the listKnownSpeakers
     * method of SpeakerManager if this Engine is running.
     * <p>
     * In an application-generated RecognizerMode, a null value means "don't care".
     * An Engine-generated RecognizerMode returns null if speaker training
     * is not supported (i.e., the SpeakerManager is not implemented).
     * It returns a zero-length array if speaker training is supported but no
     * speaker profiles have been constructed yet.
     * <p>
     * The class description describes how features may be used to select an
     * Engine or request information about a specific Engine.
     * @return speaker profiles known to this mode
     * @throws java.lang.SecurityException if the application does not have accessSpeakerProfiles permission
     * @see javax.speech.recognition.SpeakerManager
     * @see javax.speech.recognition.SpeakerManager#listKnownSpeakers()
     */
    public SpeakerProfile[] getSpeakerProfiles() {
        return profiles;
    }

    /**
     * Gets the vocabulary support feature.
     * <p>
     * Values may be SMALL, MEDIUM, LARGE, VERYLARGE, and null for don't care.
     * <p>
     * This feature gives an application an indication of the capabilities
     * of a given engine on a given platform.
     * Vocabulary size support varies by engine implementation and platform
     * capabilities.
     * <p>
     * The same engine running on one platform may return a different value
     * on another platform due to platform resource constraints.
     * Also, two different implementations on the same platform may return
     * different values due to differences in implementation resource
     * requirements.
     * <p>
     * The values of SMALL, MEDIUM, LARGE, and VERYLARGE correspond to
     * vocabulary support for engines supporting approximately 10s, 100s, 1000s, and 10,000s
     * of words.  This is as measured for a nominal perplexity of 10.
     * <p>
     * When using this feature to request an engine, the size supported may be
     * larger than the size requested.
     * An application may decide not to use such an engine due to
     * resource considerations.
     * The getRunning feature may be used to determine if the
     * larger engine is already running.
     * <p>
     * This value may be a fixed value for a given engine on a platform or
     * may take current resources into account.
     * <p>
     * In an application-generated RecognizerMode, a null value means "don't care".
     * An Engine-generated RecognizerMode never returns null.
     * <p>
     * The class description describes how features may be used to select an
     * engine or request information about a specific engine.
     * @return the value of the vocabulary support feature
     * @see javax.speech.recognition.RecognizerMode#SMALL_SIZE
     * @see javax.speech.recognition.RecognizerMode#MEDIUM_SIZE
     * @see javax.speech.recognition.RecognizerMode#LARGE_SIZE
     * @see javax.speech.recognition.RecognizerMode#VERY_LARGE_SIZE
     * @see javax.speech.recognition.RecognizerMode#getVocabSupport()
     * @see javax.speech.EngineMode#getRunning()
     */
    public Integer getVocabSupport() {
        return vocabSupport;
    }

    /**
     * Determines whether a RecognizerMode has all the features defined
     * in the require object.
     * <p>
     * Features in require which are null are not tested.
     * All string comparisons are exact (case-sensitive).
     * <p>
     * The parameters are used as follows:
     * <p>
     * If the require parameter is an EngineMode then only the
     * EngineMode features are tested.
     * If the require parameter is a RecognizerMode (or sub-class)
     * then the required locales and required speakers are tested
     * as follows.
     * <p>
     * Every locale in the required set must be matched by
     * a locale in the tested object.
     * A null require value for locales is ignored.
     * Every speaker profile in the required set must be matched by
     * a profile in the tested object.
     * A null require value for speakers speaker profiles is ignored.
     * <p>
     * Note that it is possible to compare an EngineMode against
     * a RecognizerMode and vice versa.
     * @param require an EngineMode to compare
     * @return true if all defined features match
     */
    @Override
    public boolean match(EngineMode require) {
        if (!super.match(require)) {
            return false;
        }

        if (require instanceof RecognizerMode mode) {
            SpeechLocale[] otherLocales = mode.getSpeechLocales();
            if (otherLocales != null) {
                if (locales == null) {
                    return false;
                }

                boolean match = false;
                for (int i = 0; (i < otherLocales.length) && !match; i++) {
                    SpeechLocale otherLocale = otherLocales[i];

                    for (SpeechLocale locale : locales) {
                        if (locale.equals(otherLocale)) {
                            match = true;
                        }
                    }

                    if (!match) {
                        return false;
                    }
                }
            }

            SpeakerProfile[] otherProfiles = mode.getSpeakerProfiles();
            if (otherProfiles != null) {
                if (profiles == null) {
                    return false;
                }

                boolean match = false;
                for (int i = 0; (i < otherProfiles.length) && !match; i++) {
                    SpeakerProfile otherProfile = otherProfiles[i];

                    for (SpeakerProfile profile : profiles) {
                        if (profile.equals(otherProfile)) {
                            match = true;
                        }
                    }

                    if (!match) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Creates a collection of all parameters.
     *
     * @return collection of all parameters.
     */
    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        parameters.add(vocabSupport);
        if (locales == null) {
            parameters.add(null);
        } else {
            List<Object> vec = new ArrayList<>();
            Collections.addAll(vec, locales);
            parameters.add(vec);
        }

        if (profiles == null) {
            parameters.add(null);
        } else {
            List<Object> vec = new ArrayList<>();
            Collections.addAll(vec, profiles);
            parameters.add(vec);
        }

        return parameters;
    }
}
