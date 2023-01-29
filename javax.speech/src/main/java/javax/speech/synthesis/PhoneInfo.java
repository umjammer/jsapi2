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

package javax.speech.synthesis;

// Comp 2.0.6

/**
 * Provides dynamic information associated with a phoneme.
 * @see javax.speech.synthesis.SpeakableEvent
 * @see javax.speech.synthesis.SpeakableEvent#PHONEME_STARTED
 */
public class PhoneInfo {

    /**
     * A value signifying that the duration of a phone is not known.
     */
    public static int UNKNOWN_DURATION = -1;

    private String phoneme;

    private int duration;

    /**
     * Constructs a phone instance with associated information.
     * <p>
     * The name of the phoneme is specified using Unicode IPA.
     * The duration of the phoneme is specified in milliseconds.
     * The duration may be UNKNOWN_DURATION if it is unknown.
     * @param phoneme the name of the phoneme
     * @param duration the anticipated duration of this phoneme
     * @see javax.speech.synthesis.PhoneInfo#getPhoneme()
     * @see javax.speech.synthesis.PhoneInfo#getDuration()
     * @see javax.speech.synthesis.PhoneInfo#UNKNOWN_DURATION
     */
    public PhoneInfo(String phoneme, int duration) {
        this.phoneme = phoneme;
        this.duration = duration;
    }

    /**
     * Gets the anticipated duration of this phone realization.
     * <p>
     * The duration is measured in milliseconds.
     * The duration may be UNKNOWN_DURATION if it is unknown.
     * @return the anticipated duration
     * @see javax.speech.synthesis.PhoneInfo#UNKNOWN_DURATION
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the base phoneme name for this phone realization.
     * <p>
     * This is the name of the phoneme using Unicode IPA.
     * @return the phoneme name
     */
    public String getPhoneme() {
        return phoneme;
    }
}
