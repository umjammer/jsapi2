/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 54 $
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

package javax.speech;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//Comp. 2.0.6

public abstract class EngineMode {

    public static final Integer FULL = Integer.MAX_VALUE;

    public static final Integer NONE = 0;

    private String engineName;

    private String modeName;

    private Boolean running;

    private Boolean supportsLetterToSound;

    private Boolean supportsMarkup;

    public EngineMode() {
    }

    public EngineMode(String engineName, String modeName, Boolean running,
            Boolean supportsLetterToSound, Boolean supportsMarkup) {
        this.engineName = engineName;
        this.modeName = modeName;
        this.running = running;
        this.supportsLetterToSound = supportsLetterToSound;
        this.supportsMarkup = supportsMarkup;
    }

    public String getEngineName() {
        return engineName;
    }

    public Boolean getSupportsMarkup() {
        return supportsMarkup;
    }

    public String getModeName() {
        return modeName;
    }

    public Boolean getRunning() {
        return running;
    }

    public Boolean getSupportsLetterToSound() {
        return supportsLetterToSound;
    }

    public boolean match(EngineMode require) {
        if (require == null) {
            return true;
        }

        String otherEngineName = require.getEngineName();
        boolean namesMatch;
        if (otherEngineName == null) {
            namesMatch = true;
        } else {
            namesMatch = otherEngineName.equals(engineName);
        }

        String otherModeName = require.getModeName();
        boolean modesMatch;
        if (otherModeName == null) {
            modesMatch = true;
        } else {
            modesMatch = otherModeName.equals(modeName);
        }

        Boolean otherModeRunning = require.getRunning();
        boolean runningsMatch;
        if (otherModeRunning == null) {
            runningsMatch = true;
        } else {
            runningsMatch = otherModeRunning.equals(running);
        }

        Boolean otherSupportsLetterToSound = require
                .getSupportsLetterToSound();
        boolean supportsLetterToSoundMatch;
        if (otherSupportsLetterToSound == null) {
            supportsLetterToSoundMatch = true;
        } else {
            supportsLetterToSoundMatch = otherSupportsLetterToSound
                    .equals(supportsLetterToSound);
        }

        Boolean otherMarkupSupport = require.getSupportsMarkup();
        boolean markupSupportMatch;
        if (otherMarkupSupport == null) {
            markupSupportMatch = true;
        } else {
            markupSupportMatch = otherMarkupSupport.equals(supportsMarkup);
        }

        return namesMatch && modesMatch && runningsMatch
                && supportsLetterToSoundMatch && markupSupportMatch;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((engineName == null) ? 0 : engineName.hashCode());
        result = prime * result
                + ((modeName == null) ? 0 : modeName.hashCode());
        result = prime * result + ((running == null) ? 0 : running.hashCode());
        result = prime
                * result
                + ((supportsLetterToSound == null) ? 0 : supportsLetterToSound
                        .hashCode());
        result = prime * result
                + ((supportsMarkup == null) ? 0 : supportsMarkup.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EngineMode other = (EngineMode) obj;
        if (engineName == null) {
            if (other.engineName != null) {
                return false;
            }
        } else if (!engineName.equals(other.engineName)) {
            return false;
        }
        if (modeName == null) {
            if (other.modeName != null) {
                return false;
            }
        } else if (!modeName.equals(other.modeName)) {
            return false;
        }
        if (running == null) {
            if (other.running != null) {
                return false;
            }
        } else if (!running.equals(other.running)) {
            return false;
        }
        if (supportsLetterToSound == null) {
            if (other.supportsLetterToSound != null) {
                return false;
            }
        } else if (!supportsLetterToSound.equals(other.supportsLetterToSound)) {
            return false;
        }
        if (supportsMarkup == null) {
            if (other.supportsMarkup != null) {
                return false;
            }
        } else if (!supportsMarkup.equals(other.supportsMarkup)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a collection of all parameters.
     * 
     * @return collection of all parameters.
     */
    protected List<Object> getParameters() {
        List<Object> parameters = new ArrayList<>();

        parameters.add(engineName);
        parameters.add(modeName);
        parameters.add(running);
        parameters.add(supportsLetterToSound);
        parameters.add(supportsMarkup);

        return parameters;
    }

    private static String toListString(List<Object> col) {
        return col.stream().map(parameter -> {
            if (parameter instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> subList = (List<Object>) parameter;
                return "[" + toListString(subList) + "]";
            } else {
                return String.valueOf(parameter);
            }
        }).collect(Collectors.joining(","));
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + toListString(getParameters()) + "]";
    }
}
