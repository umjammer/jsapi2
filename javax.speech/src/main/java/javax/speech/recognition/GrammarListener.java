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

import javax.speech.SpeechEventListener;

// Comp 2.0.6

/**
 * The listener interface for receiving notifications of
 * status change events for a Grammar.
 * <p>
 * A GrammarListener is attached to and removed from a Grammar with its
 * addGrammarListener and removeGrammarListener methods.
 * Multiple grammars can share a GrammarListener object and one grammar can
 * have multiple GrammarListener objects attached.
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.GrammarEvent
 * @see javax.speech.recognition.Grammar#addGrammarListener(javax.speech.recognition.GrammarListener)
 * @see javax.speech.recognition.Grammar#removeGrammarListener(javax.speech.recognition.GrammarListener)
 */
public interface GrammarListener extends SpeechEventListener {

    /**
     * Method called back to indicate a Grammar update.
     * @param e the GrammarEvent
     * @see javax.speech.recognition.GrammarEvent
     * @see javax.speech.recognition.Grammar
     */
    void grammarUpdate(GrammarEvent e);
}
