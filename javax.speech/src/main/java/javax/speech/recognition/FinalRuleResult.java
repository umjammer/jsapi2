/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 57 $
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

// Comp. 2.0.6

/**
 * Provides information on a finalized Result for
 * an utterance that matches a RuleGrammar.
 * <p>
 * A finalized Result is in either the ACCEPTED or REJECTED state
 * as returned by the getResultState method.
 * <p>
 * The methods of FinalRuleResult should only be called on a Result object if
 * the Result.getGrammar method returns a RuleGrammar and the Result
 * has been finalized with either a RESULT_ACCEPTED or RESULT_REJECTED event.
 * Inappropriate calls will cause a ResultException.
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.Result#ACCEPTED
 * @see javax.speech.recognition.Result#REJECTED
 * @see javax.speech.recognition.Result#getResultState()
 * @see javax.speech.recognition.Result#getGrammar()
 * @see javax.speech.recognition.ResultEvent#RESULT_ACCEPTED
 * @see javax.speech.recognition.ResultEvent#RESULT_REJECTED
 */
public interface FinalRuleResult extends FinalResult {
    RuleReference getRuleReference(int nBest) throws ResultStateException,
            IllegalArgumentException, IllegalStateException;

    Object[] getTags(int nBest) throws ResultStateException,
            IllegalArgumentException, IllegalStateException;

    RuleParse parse(int nBest) throws ResultStateException;
}
