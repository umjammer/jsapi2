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

/**
 * Defines a tag to be used within a RuleGrammar.
 * <p>
 * A tag is a special kind of RuleComponent that can be used
 * for semantic interpretation.
 * The tag does not affect the recognition of a RuleGrammar in which it is used.
 * Instead, tags are used to embed information into a RuleGrammar
 * that helps with processing of recognition results.
 * Tags are
 * <p>
 * Used in the definition of a RuleGrammar,
 * Included in parse output (RuleParse objects).
 *
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.RuleParse
 * @since 2.0.6
 */
public class RuleTag extends RuleComponent {

    private Object tag;

    public RuleTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    /**
     * Returns a String representing this RuleComponent as grammar text.
     * <p>
     * The String represents a portion of a grammar that could appear
     * on the right hand side of a Rule definition.
     * @return printable String representing grammar text.
     * @see javax.speech.recognition.RuleComponent
     * @see javax.speech.recognition.Rule
     */
    public String toString() {
        if (tag == null) {
            throw new IllegalArgumentException("null can not be represented in XML");
        }
        String str = "<tag>" + tag + "</tag>";

        return str;
    }
}
