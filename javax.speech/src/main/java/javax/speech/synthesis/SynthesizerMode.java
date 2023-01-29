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

//Comp 2.0.6

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

    public static final SynthesizerMode DEFAULT = new SynthesizerMode();

    private Voice[] voices;

    public SynthesizerMode() {
        super();
    }

    public SynthesizerMode(SpeechLocale locale) {
        super();

        voices = new Voice[1];
        voices[0] = new Voice(locale, null, Voice.GENDER_DONT_CARE,
                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
    }

    public SynthesizerMode(String engineName, String modeName, Boolean running,
                           Boolean supportsLetterToSound, Boolean supportsMarkup, Voice[] voices) {
        super(engineName, modeName, running, supportsLetterToSound, supportsMarkup);

        this.voices = voices;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + SynthesizerMode.hashCode(voices);
        return result;
    }

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

    public Voice[] getVoices() {
        return voices;
    }

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
