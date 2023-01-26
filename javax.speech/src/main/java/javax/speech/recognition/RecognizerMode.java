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
import javax.speech.JavaSpeechSecurity;
import javax.speech.SpeechLocale;

//Comp 2.0.6

public class RecognizerMode extends EngineMode {
    private static int hashCode(Object[] array) {
        int prime = 31;
        if (array == null)
            return 0;
        int result = 1;
        for (Object o : array) {
            result = prime * result
                    + (o == null ? 0 : o.hashCode());
        }
        return result;
    }

    public static RecognizerMode DEFAULT = new RecognizerMode();

    public static Integer SMALL_SIZE = 10;

    public static Integer MEDIUM_SIZE = 100;

    public static Integer LARGE_SIZE = 1000;

    public static Integer VERY_LARGE_SIZE = 10000;

    private Integer vocabSupport;

    private SpeechLocale[] locales;

    private SpeakerProfile[] profiles;

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
        super(engineName, modeName, running, supportsLetterToSound,
                supportsMarkup);
        this.vocabSupport = vocabSupport;
        this.locales = locales;
        this.profiles = profiles;
    }


    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + RecognizerMode.hashCode(locales);
        result = prime * result + RecognizerMode.hashCode(profiles);
        result = prime * result
                + ((vocabSupport == null) ? 0 : vocabSupport.hashCode());
        return result;
    }

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
            for (int i=0; i<locales.length; i++) {
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
            for (int i=0; i<profiles.length; i++) {
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

    public SpeakerProfile[] getSpeakerProfiles() {
        JavaSpeechSecurity.checkPermission(
                "javax.speech.recognition.SpeakerProfile");
        return profiles;
    }

    public Integer getVocabSupport() {
        return vocabSupport;
    }

    public boolean match(EngineMode require) {
        if (!super.match(require)) {
            return false;
        }

        if (require instanceof RecognizerMode) {
            RecognizerMode mode = (RecognizerMode) require;
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