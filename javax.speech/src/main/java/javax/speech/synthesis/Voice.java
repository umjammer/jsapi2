/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 74 $
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

import javax.speech.SpeechLocale;

//Comp 2.0.6

public class Voice {

    public static final int AGE_CHILD = 0x80010000;

    public static final int AGE_TEENAGER = 0x80020000;

    public static final int AGE_YOUNGER_ADULT = 0x80040000;

    public static final int AGE_MIDDLE_ADULT = 0x80080000;

    public static final int AGE_OLDER_ADULT = 0x80100000;

    public static final int AGE_SPECIFIC = 0xC0000000;

    public static final int AGE_DONT_CARE = -1;

    public static final int GENDER_FEMALE = 0x1;

    public static final int GENDER_MALE = 0x2;

    /** Neutral voice that is neither male or female. Examples include artificial voices and robotic voices. */
    public static final int GENDER_NEUTRAL = 0x4;

    public static final int GENDER_DONT_CARE = -1;

    /** The default variant for a Voice. */
    public static final int VARIANT_DEFAULT = 1;

    /** Ignore the variant when performing a match of voices. */
    public static final int VARIANT_DONT_CARE = -1;

    private final SpeechLocale locale;

    private final String name;

    private final int gender;

    private final int age;

    private final int variant;

    public Voice() {
        gender = GENDER_DONT_CARE;
        age = AGE_DONT_CARE;
        variant = VARIANT_DONT_CARE;
        locale = null;
        name = null;
    }

    public Voice(SpeechLocale locale, String name, int gender, int age, int variant) throws IllegalArgumentException {
        if ((age < 0) && (age != AGE_DONT_CARE)) {
            if ((age & 0xFFFF0000) != AGE_SPECIFIC) {
                if ((age & 0x7FE0FFFF) != 0) {
                    throw new IllegalArgumentException("Age must be a positive integer or AGE_DONT_CARE!");
                }
            }
        }
        if ((variant < 0) && (variant != VARIANT_DONT_CARE)) {
            throw new IllegalArgumentException("Variant must be a positive integer or VARIANT_DONT_CARE!");
        }
        if ((gender != GENDER_DONT_CARE) && (gender != GENDER_FEMALE)
                && (gender != GENDER_MALE) && (gender != GENDER_NEUTRAL)) {
            throw new IllegalArgumentException(gender + " is not a valid value for gender!");
        }
        this.locale = locale;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.variant = variant;
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender;
    }

    public SpeechLocale getSpeechLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    public int getVariant() {
        return variant;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + gender;
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + variant;
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
        Voice other = (Voice) obj;
        if (age != other.age) {
            return false;
        }
        if (gender != other.gender) {
            return false;
        }
        if (locale == null) {
            if (other.locale != null) {
                return false;
            }
        } else if (!locale.equals(other.locale)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (variant != other.variant) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        String str = getClass().getName() +
                "[" +
                locale +
                "," +
                name +
                "," +
                age +
                "," +
                gender +
                "," +
                variant +
                "]";

        return str;
    }

    /**
     * Determines whether a Voice has all the features defined in the require object. Strings in require which are
     * null are ignored. All string comparisons are exact matches (case-sensitive). A null locale in the require object
     * is ignored. Otherwise, all Strings in the require locale must match.
     * <code>GENDER_DONT_CARE</code>, <code>AGE_DONT_CARE</code>, and <code>VARIANT_DONT_CARE</code> values in the
     * require object are ignored. The gender parameters are OR'able. The age parameter finds the closest one.
     * <p>
     * The following example shows how to test for a male and/or neutral Voice:
     * </p>
     * <pre>
     *  Voice aVoice = ...;
     *  Voice maleishVoice =
     *      new Voice(null, null, Voice.GENDER_MALE | Voice.GENDER_NEUTRAL,
     *                Voice.AGE_DONT_CARE, Voice.VARIANT_DONT_CARE);
     *  if (aVoice.match(maleishVoice)) ...
     * </pre>
     *
     * @param require the required features to match this voice
     * @return true if all the required features match
     * @see #GENDER_DONT_CARE
     * @see #AGE_DONT_CARE
     * @see #VARIANT_DONT_CARE
     */
    public boolean match(Voice require) {
        if (require == null) {
            return true;
        }

        boolean namesMatch;
        String requiredName = require.getName();
        if ((requiredName == null) || (requiredName.length() == 0)) {
            namesMatch = true;
        } else {
            namesMatch = requiredName.equals(name);
        }

        boolean genderMatch;
        int requiredGender = require.getGender();
        if (requiredGender == GENDER_DONT_CARE) {
            genderMatch = true;
        } else {
            genderMatch = (gender == requiredGender);
        }
        boolean localeMatch;
        SpeechLocale requiredLocale = require.getSpeechLocale();
        if (requiredLocale == null) {
            localeMatch = true;
        } else {
            localeMatch = requiredLocale.equals(locale);
        }

        boolean ageMatch;
        int requiredAge = require.getAge();
        if (requiredAge == AGE_DONT_CARE) {
            ageMatch = true;
        } else {
            int closestAge = getClosestAge(requiredAge);
            ageMatch = (age == closestAge);
        }

        boolean variantMatch;
        int requiredVariant = require.getVariant();
        if (requiredVariant == VARIANT_DONT_CARE) {
            variantMatch = true;
        } else {
            variantMatch = (variant == requiredVariant);
        }

        return namesMatch && genderMatch && localeMatch && ageMatch && variantMatch;
    }

    /**
     * Determines the age closest to the given age.
     *
     * @param age the given age.
     * @return Age defined as a constant closest to the given age.
     */
    private int getClosestAge(int age) {
        if (age <= AGE_CHILD + (AGE_TEENAGER - AGE_CHILD) / 2) {
            return AGE_CHILD;
        }

        if (age <= AGE_TEENAGER + (AGE_YOUNGER_ADULT - AGE_TEENAGER) / 2) {
            return AGE_TEENAGER;
        }

        if (age <= AGE_YOUNGER_ADULT + (AGE_MIDDLE_ADULT - AGE_YOUNGER_ADULT) / 2) {
            return AGE_YOUNGER_ADULT;
        }

        if (age <= AGE_MIDDLE_ADULT + (AGE_OLDER_ADULT - AGE_MIDDLE_ADULT) / 2) {
            return AGE_MIDDLE_ADULT;
        }

        return AGE_OLDER_ADULT;
    }
}
