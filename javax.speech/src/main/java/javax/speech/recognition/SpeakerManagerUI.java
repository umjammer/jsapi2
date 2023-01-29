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

// Comp 2.0.6

/**
 * Provides access to a user interface for managing speaker and training data.
 * <p>
 * This interface allows a user to interactively provide additional information
 * and training data to help improve performance.  The interface also provides
 * a means to store and retrieve this data.
 * <p>
 * The documentation for the Thread class contains examples of how to use this
 * interface in combination with the methods of Thread.
 * <p>
 * Not all implementations support this interface.
 * @see java.lang.Thread
 * @see java.lang.Runnable
 * @see javax.speech.recognition.SpeakerManager
 * @see javax.speech.recognition.SpeakerManager#getSpeakerManagerUI()
 */
public interface SpeakerManagerUI {

    Object getUI(String uiClassName);

    String[] getUIClassNames();
}
