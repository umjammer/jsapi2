/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 65 $
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

// Comp 2.0.6

/**
 * Identifies each enrollment by a user to a Recognizer.
 * <p>
 * SpeakerProfile objects are used in management of speaker data through
 * the SpeakerManager interface for a Recognizer and in selection of
 * Recognizers through the RecognizerMode class.
 * <p>
 * A user may have single or multiple profiles associated with a Recognizer.
 * Examples of multiple profiles include a user who enrolls and trains
 * the recognizer separately for different microphones or for different
 * application domains (e.g. name dialing and command and control).
 * <p>
 * Each SpeakerProfile object has a unique identifier
 * (unique to the Recognizer), plus a user name and optionally a variant
 * name that identifies each separate profile for a user (per-user unique).
 * All three identifying properties should be human-readable strings.
 * The identifier is often the concatenation of the user name and variant.
 * <p>
 * The user name may be the same as the "user.name" property stored in
 * the java.lang.System properties. However, access to system properties may be
 * restricted by security policies.
 * Appropriate naming of profiles is the joint responsibility of users
 * and recognizers.
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.RecognizerMode
 * @see javax.speech.recognition.SpeakerManager
 * @see java.lang.System
 */
public class SpeakerProfile {

    /**
     * The default SpeakerProfile used if no other speaker has been set.
     * @see javax.speech.recognition.SpeakerManager#setCurrentSpeaker(javax.speech.recognition.SpeakerProfile)
     */
    public static SpeakerProfile DEFAULT = new SpeakerProfile(null, null);

    private String name;

    private String variant;

    public SpeakerProfile(String name, String variant) {
        this.name = name;
        this.variant = variant;
    }

    /**
     * Returns the speaker name.
     * <p>
     * Should be unique for a user.
     * @return the speaker's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the variant description.
     * <p>
     * Should be unique for a user.
     * @return the variant description for the profile.
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Returns a hash code value for the object.
     * <p>
     * This method is supported for the benefit of hashtables.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    /**
     * Returns true if and only if the input parameter is a non-null
     * SpeakerProfile with equal values of all properties.
     * profile a SpeakerProfile object
     * @return true if the SpeakerProfiles are equal
     * @see javax.speech.recognition.SpeakerProfile#match(javax.speech.recognition.SpeakerProfile)
     */
    @Override
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
        SpeakerProfile other = (SpeakerProfile) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (variant == null) {
            if (other.variant != null) {
                return false;
            }
        } else if (!variant.equals(other.variant)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a String representation of this SpeakerProfile.
     * @return a String representation of this SpeakerProfile
     */
    public String toString() {
        String str = getClass().getName() +
                "[" +
                name +
                "," +
                variant +
                "]";

        return str;
    }

    /**
     * Returns true if this object matches the require object.
     * <p>
     * A match requires that each non-null or non-zero-length string
     * property of the required object be an exact string match to
     * the properties of this object.
     * @param require a profile with the required values
     * @return true if the profiles match
     * @see javax.speech.recognition.SpeakerProfile#equals(java.lang.Object)
     */
    public boolean match(SpeakerProfile require) {
        if (require == null) {
            return true;
        }

        String otherName = require.getName();
        boolean nameMatch;
        if (otherName == null) {
            nameMatch = true;
        } else {
            nameMatch = otherName.equals(name);
        }

        String otherVariant = require.getVariant();
        boolean variantMatch;
        if (otherVariant == null) {
            variantMatch = true;
        } else {
            variantMatch = otherVariant.equals(variant);
        }

        return nameMatch && variantMatch;
    }
}
