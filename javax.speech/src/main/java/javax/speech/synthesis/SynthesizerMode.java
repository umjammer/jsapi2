/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 66 $
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

package javax.speech.synthesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.speech.EngineMode;
import javax.speech.SpeechLocale;

// Comp 2.0.6

/**
 * Provides information about a specific operating mode of a Synthesizer.
 * <p>
 * SynthesizerMode extends EngineMode with features
 * specific to speech synthesizers.
 * <p>
 * Like EngineMode, there are two types of SynthesizerMode objects:
 * those created by the EngineManager or a Synthesizer instance and
 * those created by an application.
 * Engine-created SynthesizerMode objects are obtained from the EngineManager
 * through the availableEngines method.
 * They are also returned from a Synthesizer instance with the getEngineMode method.
 * For engine-created SynthesizerMode objects, all features will be set to non-null values.
 * <p>
 * Applications can create SynthesizerMode objects using the appropriate constructor.
 * Applications may leave any or all of the feature values null to indicate "don't care".
 * Typically, application-created SynthesizerModes are used to test the engine-created
 * SynthesizerModes to select an appropriate synthesizer for creation.
 * <p>
 * The EngineMode class describes additional detail on Engine selection.
 * <p>
 * Engine creation is described in the documentation for the EngineManager class.
 * @see javax.speech.Engine
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.Engine#getEngineMode()
 * @see javax.speech.EngineMode
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)
 * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
 */
public class SynthesizerMode extends EngineMode {

    /**
     * Returns a hash code value for the array
     *
     * @param array the array to create a hash code value for
     * @return a hash code value for the array
     */
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
     * SynthesizerMode with all mode features set to "don't care" values.
     * <p>
     * This can be used to select a default Synthesizer with
     * EngineManager.createEngine.
     * The documentation for the Synthesizer interface includes
     * an example of this.
     * @see javax.speech.synthesis.Synthesizer
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
     */
    public static final SynthesizerMode DEFAULT = new SynthesizerMode();

    private Voice[] voices;

    /**
     * Constructs a SynthesizerMode with all mode features set
     * to don't care values.
     */
    public SynthesizerMode() {
        super();
    }

    public SynthesizerMode(SpeechLocale locale) {
        super();

        voices = new Voice[1];
        voices[0] = new Voice(locale, null, Voice.GENDER_DONT_CARE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
    }

    /**
     * Constructs a SynthesizerMode with the specified mode features.
     * <p>
     * Sets engine name, mode name, running, generalDomain, and supportsMarkup
     * all to the specified values.
     * Any parameter may be null to signify "don't care".
     * <p>
     * The order of the voices determines the preference order of the
     * desired voices.
     * @param engineName the desired engine name
     * @param modeName the desired mode name
     * @param running a flag specifying a running engine
     * @param generalDomain a flag specifying general domain support
     * @param supportsMarkup a flag specifying markup support
     * @param voices the desired set of voices
     * @see javax.speech.EngineMode#getEngineName()
     * @see javax.speech.EngineMode#getModeName()
     * @see javax.speech.EngineMode#getRunning()
     * @see javax.speech.EngineMode#getGeneralDomain()
     * @see javax.speech.EngineMode#getSupportsMarkup()
     * @see javax.speech.synthesis.SynthesizerMode#getVoices()
     * @see javax.speech.synthesis.Voice
     * @see javax.speech.synthesis.SynthesizerMode#match(javax.speech.EngineMode)
     */
    public SynthesizerMode(String engineName, String modeName, Boolean running,
                           Boolean supportsLetterToSound, Boolean supportsMarkup, Voice[] voices) {
        super(engineName, modeName, running, supportsLetterToSound, supportsMarkup);

        this.voices = voices;
    }

    /**
     * Returns a hash code value for the object.
     * <p>
     * This method is supported for the benefit of hashtables.
     * @return a hash code value for the object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + SynthesizerMode.hashCode(voices);
        return result;
    }

    /**
     * Returns true if and only if the mode parameter
     * is not null and has equal values of all
     * mode features with this EngineMode.
     * mode an EngineMode to compare
     * @return true if mode is equal to this EngineMode
     */
    @Override
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
        SynthesizerMode other = (SynthesizerMode) obj;
        if (voices == null) {
            if (other.voices != null) {
                return false;
            }
        } else if (voices.length != other.voices.length) {
            return false;
        } else {
            for (int i = 0; i < voices.length; i++) {
                if (!voices[i].equals(other.voices[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean getMarkupSupport() {
        return super.getSupportsMarkup();
    }

    /**
     * Returns the list of voices specified in this SynthesizerMode.
     * <p>
     * Synthesizers use the order of the list as a preference order to
     * select voices.
     * <p>
     * In application generated EngineModes, a null value means "don't care".
     * Engine generated EngineModes never return null.
     * <p>
     * The class description describes how features may be used to
     * select an engine or request information about a specific engine.
     * @return list of available Voices
     */
    public Voice[] getVoices() {
        return voices;
    }

    /**
     * Determines whether a SynthesizerMode has all the features
     * specified by the require object.
     * <p>
     * Features in require which are null are not tested.
     * All string comparisons are exact (case-sensitive).
     * <p>
     * The parameters are used as follows:
     * <p>
     * First, all features of the EngineMode class are compared.
     * If any test fails, the method returns false.
     * <p>
     * If the require parameter is a SynthesizerMode (or sub-class)
     * then the required voice list is tested as follows.
     * Each voice defined in the required set must match one of the
     * voices in the tested object. (See Voice.match() for details.)
     * <p>
     * Note that it is possible to compare an EngineMode against a
     * SynthesizerMode and vice versa.
     * @param require an EngineMode to compare
     * @return true if all defined features match
     * @see javax.speech.synthesis.Voice
     * @see javax.speech.synthesis.Voice#match(javax.speech.synthesis.Voice)
     */
    public boolean match(EngineMode require) {
        if (!super.match(require)) {
            return false;
        }

        if (require instanceof SynthesizerMode) {
            SynthesizerMode mode = (SynthesizerMode) require;
            Voice[] otherVoices = mode.getVoices();
            if (otherVoices != null) {
                if (voices == null) {
                    return false;
                }

                for (Voice otherVoice : otherVoices) {
                    boolean voiceMatch = false;
                    for (int k = 0; (k < voices.length) && !voiceMatch; k++) {
                        Voice voice = voices[k];
                        if (otherVoice.match(voice)) {
                            voiceMatch = true;
                        }
                    }

                    if (!voiceMatch) {
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
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        if (voices == null) {
            parameters.add(null);
        } else {
            List<Object> col = new ArrayList<>();
            Collections.addAll(col, voices);
            parameters.add(col);
        }

        return parameters;
    }
}
