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
 * Thrown if a problem is found while trying to create, access or use an
 * EngineFactory, the EngineManager, or an Engine.
 *
 * @see javax.speech.spi.EngineFactory
 * @see javax.speech.EngineManager
 * @see javax.speech.Engine
 * @since 2.0.6
 */
public class EngineException extends SpeechException {

    /**
     * Constructs an EngineException with no detail message.
     */
    public EngineException() {
    }

    /**
     * Constructs an EngineException with a detail message.
     * <p>
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public EngineException(String s) {
        super(s);
    }
}
