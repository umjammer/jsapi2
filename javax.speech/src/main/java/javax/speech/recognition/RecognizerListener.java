/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 60 $
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

import javax.speech.EngineListener;

// Comp 2.0.6

/**
 * The listener interface for events associated with a Recognizer.
 * <p>
 * A RecognizerListener object is attached to and removed from a Recognizer
 * by calls to the addRecognizerListener and removeRecognizerListener methods.
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.RecognizerEvent
 * @see javax.speech.recognition.Recognizer#addRecognizerListener(javax.speech.recognition.RecognizerListener)
 * @see javax.speech.recognition.Recognizer#removeRecognizerListener(javax.speech.recognition.RecognizerListener)
 */
public interface RecognizerListener extends EngineListener {

    /**
     * Method called back to indicate a Recognizer update.
     * @param e a recognizer event
     * @see javax.speech.recognition.RecognizerEvent
     */
    void recognizerUpdate(RecognizerEvent e);
}
