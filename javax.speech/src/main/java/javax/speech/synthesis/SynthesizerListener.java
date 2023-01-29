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

import javax.speech.EngineListener;
import javax.speech.SpeechEventListener;

// Comp 2.0.6

/**
 * The listener interface for receiving notification
 * of events associated with a Synthesizer.
 * <p>
 * SynthesizerListener objects are attached to and removed from a Synthesizer
 * by calling the addSynthesizerListener and removeSynthesizerListener methods.
 * <p>
 * The source for all SynthesizerEvents provided to a SynthesizerListener
 * is the Synthesizer.
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.synthesis.SynthesizerEvent
 * @see javax.speech.synthesis.Synthesizer#addSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
 * @see javax.speech.synthesis.Synthesizer#removeSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
 */
public interface SynthesizerListener extends EngineListener, SpeechEventListener {

    /**
     * Method called back to indicate a Synthesizer update.
     * @param e a SynthesizerEvent object
     * @see javax.speech.synthesis.SynthesizerEvent
     */
    void synthesizerUpdate(SynthesizerEvent e);
}
