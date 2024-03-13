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

/**
 * Thrown when there is an illegal call to a method of a speech engine.
 * <p>
 * For example, it is illegal to request a deallocated Synthesizer to speak,
 * to request a deallocated Recognizer to create a new grammar, or
 * to request any deallocated engine to pause or resume.
 * @see javax.speech.SpeechException
 * @since 2.0.6
 */
public class EngineStateException extends IllegalStateException {

    /**
     * Constructs an EngineStateException with no detail message.
     */
    public EngineStateException() {
    }

    /**
     * Constructs an EngineStateException with the specified detail message.
     * <p>
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public EngineStateException(String s) {
        super(s);
    }
}
