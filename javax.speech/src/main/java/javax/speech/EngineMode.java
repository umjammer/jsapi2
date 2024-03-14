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

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.getLogger;

/**
 * Provides information about a specific operating
 * mode of a speech Engine.
 * <p>
 * The features defined in the EngineMode class apply to
 * all speech Engines. Subclasses of EngineMode
 * extend this class to define
 * specialized features for corresponding subclasses of Engine.
 * <p>
 * The features of EngineMode are all object references.
 * All features are defined so that a null value means
 * "don't care" when selecting an Engine or matching
 * EngineMode.  For example, a Boolean value
 * for a feature means that its three values are true,
 * false and null ("don't care").
 * <p>
 * EngineMode features follow the Java
 * Beans set/get property patterns.
 * <p>
 * There are two types of EngineMode objects: those
 * created by the EngineManager or an Engine
 * instance and those created by an application.
 * Engine-created EngineMode objects are
 * obtained from the EngineManager through the
 * availableEngines method.  They are also returned
 * from an Engine with the getEngineMode
 * method.  For Engine-created EngineMode
 * objects, all features will be set to non-null values.
 * <p>
 * Applications can create EngineMode objects using the
 * appropriate constructor.  Applications may leave any or all of the
 * feature values null to indicate "don't care".
 * Application-created EngineModes may be used to
 * test the Engine-created EngineModes to
 * select an appropriate Engine for creation.
 * <p>
 * The following example tests whether an Engine supports
 * Swiss German:
 * <pre>
 * SynthesizerMode fromEngine = ...;
 * // "de" is the ISO 639 language code for German
 * // "CH" is the ISO 3166 country code for Switzerland
 * // (see locale for details)
 * SynthesizerMode require = new SynthesizerMode(new Locale("de", "CH"));
 * // test whether the engine mode supports Swiss German.
 * if (fromEngine.match(require)) ...
 * </pre>
 * An application can create a subclass of EngineMode
 * and pass it to the createEngine
 * method of EngineManager.  In this common approach,
 * the EngineManager performs the Engine
 * selection.
 * <p>
 * The following example shows the creation of a French
 * Synthesizer:
 * <pre>
 * // Create a mode that requires French
 * SynthesizerMode mode = new SynthesizerMode(new Locale("fr"));
 * // Create a synthesizer that supports French
 * Synthesizer synth = EngineManager.createEngine(mode);
 * </pre>
 * The EngineManager class provides additional examples
 * of using EngineModes.
 * <p>
 * Applications that need advanced selection criterion will
 * <p>
 * Request an EngineMode list from
 * availableEngines,
 * <p>
 * Select an EngineMode from the list using the
 * methods of EngineList and
 * EngineMode,
 * <p>
 * Pass the selected EngineMode to the
 * createEngine method of
 * EngineManager.
 *
 * @see javax.speech.Engine
 * @see javax.speech.Engine#getEngineMode()
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)
 * @see javax.speech.EngineManager#createEngine(javax.speech.EngineMode)
 * @since 2.0.6
 */
public abstract class EngineMode {

    private static final Logger logger = getLogger(EngineMode.class.getName());

    public static final Integer FULL = Integer.MAX_VALUE;

    public static final Integer NONE = 0;

    private String engineName;

    private String modeName;

    private Boolean running;

    private Boolean supportsLetterToSound;

    private Boolean supportsMarkup;

    /**
     * Constructs an EngineMode with all mode features set
     * to don't care values.
     * @see javax.speech.EngineMode#EngineMode(String, String, Boolean, Boolean, Boolean)
     */
    public EngineMode() {
    }

    /**
     * Constructs an EngineMode with the specified mode features.
     * <p>
     * Sets the parameters to the specified values.
     * Any parameter may be null to signify "don't care".
     * @param engineName an engine name
     * @param modeName a mode name
     * @param running a flag specifying a running engine
     * @param supportsLetterToSound a flag specifying general domain support
     * @param supportsMarkup a flag specifying markup support
     * @see javax.speech.EngineMode#EngineMode()
     * @see javax.speech.EngineMode#getEngineName()
     * @see javax.speech.EngineMode#getModeName()
     * @see javax.speech.EngineMode#getRunning()
     * @see javax.speech.EngineMode#getSupportsLetterToSound()
     * @see javax.speech.EngineMode#getSupportsMarkup()
     */
    public EngineMode(String engineName, String modeName, Boolean running,
                      Boolean supportsLetterToSound, Boolean supportsMarkup) {
        this.engineName = engineName;
        this.modeName = modeName;
        this.running = running;
        this.supportsLetterToSound = supportsLetterToSound;
        this.supportsMarkup = supportsMarkup;
    }

    /**
     * Gets the Engine name.
     * <p>
     * The Engine name is a string that uniquely identifies a speech Engine
     * (e.g., "Acme Recognizer").
     * It should be a unique string across the provider company
     * and across companies.
     * <p>
     * In an application-generated EngineMode, a null value means "don't care".
     * An Engine-generated EngineMode never returns null.
     * <p>
     * The class description describes how features may be used to
     * select an Engine or request information about a specific Engine.
     * @return the name of the Engine
     */
    public String getEngineName() {
        return engineName;
    }

    /**
     * Gets the supports markup feature.
     * <p>
     * An Engine that supports markup can properly interpret markup passed
     * to the Engine (true).
     * The extent of markup support may vary from Engine to Engine.
     * Some Engines do not expect to receive markup at all (false).
     * <p>
     * In an application-generated EngineMode, a null value means "don't care".
     * An Engine-generated EngineMode never returns null.
     * <p>
     * The class description describes how features may be used to
     * select an Engine or request information about a specific Engine.
     * @return the value of the supports markup feature
     */
    public Boolean getSupportsMarkup() {
        return supportsMarkup;
    }

    /**
     * Gets the mode name.
     * <p>
     * The mode name uniquely identifies a single mode of
     * operation of a speech Engine (e.g., "Spanish Command and Control" ).
     * <p>
     * In an application-generated EngineMode, a null value means "don't care".
     * An Engine-generated EngineMode never returns null.
     * <p>
     * The class description describes how features may be used to
     * select an Engine or request information about a specific Engine.
     * @return the value of the mode name feature
     */
    public String getModeName() {
        return modeName;
    }

    /**
     * Gets the running feature for the resources underlying an Engine.
     * <p>
     * An Engine is considered running (true) if
     * its resources have been allocated before or
     * are in the process of being allocated.
     * Once all Engine instances sharing the underlying resources
     * begin deallocation or become deallocated,
     * the running feature becomes false.
     * <p>
     * This feature allows an application to request an
     * Engine whose underlying resources have already been loaded.
     * Using such an Engine can conserve resources.
     * <p>
     * In an application-generated EngineMode,
     * a null value means "don't care".
     * An Engine-generated EngineMode
     * never returns null.
     * <p>
     * The class description describes how features may be used to
     * select an Engine or request information about a specific
     * Engine.
     * @return the value of the running feature
     * @see javax.speech.Engine#allocate()
     * @see javax.speech.Engine#deallocate()
     */
    public Boolean getRunning() {
        return running;
    }

    public Boolean getSupportsLetterToSound() {
        return supportsLetterToSound;
    }

    /**
     * Determines whether an EngineMode has all the features defined in the require object.
     * <p>
     * Strings in require which are null are not tested (don't care).
     * All string comparisons are exact (case-sensitive).
     * @param require an EngineMode to compare
     * @return true if all defined features match
     */
    public boolean match(EngineMode require) {
        if (require == null) {
            return true;
        }
logger.log(Level.TRACE, "----- EngineMode MATCH: require: " + require + ", " + this);

        String otherEngineName = require.getEngineName();
        boolean namesMatch;
        if (otherEngineName == null) {
            namesMatch = true;
        } else {
            namesMatch = otherEngineName.equals(engineName);
        }
logger.log(Level.TRACE, "EngineMode MATCH: otherEngineName: " + otherEngineName + ", " + namesMatch);

        String otherModeName = require.getModeName();
        boolean modesMatch;
        if (otherModeName == null) {
            modesMatch = true;
        } else {
            modesMatch = otherModeName.equals(modeName);
        }
logger.log(Level.TRACE, "EngineMode MATCH: otherModeName: " + otherModeName + ", " + modesMatch);

        Boolean otherModeRunning = require.getRunning();
        boolean runningsMatch;
        if (otherModeRunning == null) {
            runningsMatch = true;
        } else {
            runningsMatch = otherModeRunning.equals(running);
        }
logger.log(Level.TRACE, "EngineMode MATCH: otherModeRunning: " + otherModeRunning + ", " + runningsMatch);

        Boolean otherSupportsLetterToSound = require.getSupportsLetterToSound();
        boolean supportsLetterToSoundMatch;
        if (otherSupportsLetterToSound == null) {
            supportsLetterToSoundMatch = true;
        } else {
            supportsLetterToSoundMatch = otherSupportsLetterToSound.equals(supportsLetterToSound);
        }
logger.log(Level.TRACE, "EngineMode MATCH: otherSupportsLetterToSound: " + otherSupportsLetterToSound + ", " + supportsLetterToSoundMatch);

        Boolean otherMarkupSupport = require.getSupportsMarkup();
        boolean markupSupportMatch;
        if (otherMarkupSupport == null) {
            markupSupportMatch = true;
        } else {
            markupSupportMatch = otherMarkupSupport.equals(supportsMarkup);
        }
logger.log(Level.TRACE, "EngineMode MATCH: otherMarkupSupport: " + otherMarkupSupport + ", " + markupSupportMatch);

logger.log(Level.TRACE, "EngineMode MATCH: total matches: " + (namesMatch && modesMatch && runningsMatch && supportsLetterToSoundMatch && markupSupportMatch));
        return namesMatch && modesMatch && runningsMatch && supportsLetterToSoundMatch && markupSupportMatch;
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
        int result = 1;
        result = prime * result + ((engineName == null) ? 0 : engineName.hashCode());
        result = prime * result + ((modeName == null) ? 0 : modeName.hashCode());
        result = prime * result + ((running == null) ? 0 : running.hashCode());
        result = prime * result + ((supportsLetterToSound == null) ? 0 : supportsLetterToSound.hashCode());
        result = prime * result + ((supportsMarkup == null) ? 0 : supportsMarkup.hashCode());
        return result;
    }

    /**
     * Returns true if and only if the mode parameter
     * is not null and has equal values of all
     * mode features with this EngineMode.
     * {@link #modeName} an EngineMode to compare
     * @return true if mode is equal to this EngineMode
     */
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

    /**
     * Returns a String representation of this EngineMode.
     * @return a String representation of this EngineMode
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + toListString(getParameters()) + "]";
    }
}
