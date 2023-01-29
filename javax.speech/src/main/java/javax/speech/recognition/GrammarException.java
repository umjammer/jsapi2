/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 59 $
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

import javax.speech.SpeechException;

// Comp 2.0.6

/**
 * Thrown if a problem is found with grammar text from resource or
 * with a RuleGrammar object derived from grammar text.
 * <p>
 * Grammar problems are typically identified and
 * fixed during application development.
 * This class provides information that allows a
 * debugging environment to handle the error.
 * <p>
 * The exception message is a printable String. Recognizers may optionally
 * provide details for each syntax problem.
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.Recognizer
 */
public class GrammarException extends SpeechException {

    private GrammarExceptionDetail[] details;

    /**
     * Constructs a GrammarException with no detail message.
     */
    public GrammarException() {
    }

    /**
     * Constructs a GrammarException with the specified detail message.
     * @param message a printable detail message
     * @see java.lang.Throwable#getMessage()
     */
    public GrammarException(String message) {
        super(message);
    }

    /**
     * Constructs a GrammarException with the specified detail message
     * and an optional programmatic description of each error.
     * @param message a printable detail message
     * @param details details of each error encountered or null
     * @see java.lang.Throwable#getMessage()
     * @see javax.speech.recognition.GrammarException#getDetails()
     */
    public GrammarException(String message, GrammarExceptionDetail[] details) throws IllegalArgumentException {
        super(message);
        if (details == null) {
            throw new IllegalArgumentException("Details must not be null!");
        }
        this.details = details;
    }

    /**
     * Gets the list of grammar syntax detail descriptions.
     * <p>
     * This provides additional detail for grammar syntax problems.
     * <p>
     * If no detail is available, a zero-length array is returned.
     * @return an array of GrammarExceptionDetail objects
     */
    public GrammarExceptionDetail[] getDetails() {
        return details;
    }
}
