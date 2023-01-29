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

// Comp. 2.0.6

/**
 * Thrown when a Java Speech API exception occurs.
 * <p>
 * SpeechException is the super class of all exceptions in the javax.speech packages.
 * <p>
 * In addition to exceptions that inherit from SpeechException,
 * some methods
 * throw other Java Exceptions and Errors such as IllegalArgumentException
 * and SecurityException.
 */
public class SpeechException extends Exception {

    /**
     * Constructs a SpeechException with no detail message.
     */
    public SpeechException() {
    }

    /**
     * Constructs a SpeechException with the specified detail message.
     * <p>
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public SpeechException(String s) {
        super(s);
    }
}
