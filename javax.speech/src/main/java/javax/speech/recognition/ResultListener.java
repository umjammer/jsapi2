/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
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
 * The listener interface for ResultEvents associated with a Result.
 * <p>
 * A ResultListener may be attached to any of three entities:
 * <p>
 * Result: a ResultListener attached to a Result receives all
 * events for that result starting from the time at which the listener is attached.
 * It never receives a RESULT_CREATED event because a listener can only
 * be attached to a Result once a RESULT_CREATED event has already been issued.
 * Grammar: a ResultListener attached to a Grammar receives
 * all events for all results that have been finalized for that Grammar.
 * Specifically, it receives the GRAMMAR_FINALIZED event and following events.
 * It never receives a RESULT_CREATED event because that event always
 * precedes the GRAMMAR_FINALIZED event.
 * Recognizer: a ResultListener attached to a Recognizer receives
 * notification of all ResultEvents for all Results produced by that recognizer.
 * <p>
 * The events are issued to the listeners in the order of most specific to
 * least specific: ResultListeners attached to the Result are notified first,
 * then listeners attached to the matched Grammar, and finally to listeners
 * attached to the Recognizer.
 * <p>
 * A single ResultListener may be attached to multiple objects or types of objects
 * (Recognizer, Grammar or Result), and multiple ResultListeners may
 * be attached to a single object.
 * <p>
 * The source for all ResultEvents issued to a ResultListener is the Result
 * that generated the event. The Result documentation provides more detail.
 * @see javax.speech.recognition.ResultEvent
 * @see javax.speech.recognition.Result
 * @see javax.speech.recognition.Result#addResultListener(javax.speech.recognition.ResultListener)
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.Grammar#addResultListener(javax.speech.recognition.ResultListener)
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.Recognizer#addResultListener(javax.speech.recognition.ResultListener)
 */
public interface ResultListener extends SpeechEventListener {

    /**
     * Method called back to indicate a Result update.
     * @param e the ResultEvent
     * @see javax.speech.recognition.Result
     */
    void resultUpdate(ResultEvent e);
}
