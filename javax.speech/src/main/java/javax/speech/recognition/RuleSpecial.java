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
 * Defines special RuleReferences defined as constants.
 * <p>
 * The special RuleReferences NULL, VOID and GARBAGE may be used within
 * RuleGrammar definitions as RuleComponents.
 * <p>
 * Special Rule references for SRGS are
 * <p>
 * ruleref special="NULL"/
 * <p>
 * ruleref special="VOID"/
 * <p>
 * ruleref special="GARBAGE"/
 *
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.RuleReference
 * @since 2.0.6
 */
public class RuleSpecial extends RuleComponent {

    /**
     * Special GARBAGE RuleComponent that may match any speech up
     * until the next RuleComponent match, the next token or until the
     * end of spoken input.
     * <p>
     * A RuleGrammar processor must accept RuleGrammars that contain GARBAGE.
     * However, the behavior of GARBAGE is implementation-specific.
     * A user agent should be capable of matching arbitrary spoken input up
     * to the next token, but may treat GARBAGE as equivalent to
     * NULL (match no spoken input).
     * @see javax.speech.recognition.RuleSpecial#NULL
     * @see javax.speech.recognition.RuleGrammar
     */
    public static RuleSpecial GARBAGE = new RuleSpecial("GARBAGE");

    /**
     * Special NULL RuleComponent that always matches.
     * <p>
     * Unlike GARBAGE, the match succeeds independent of speech input.
     * @see javax.speech.recognition.RuleSpecial#GARBAGE
     */
    public static RuleSpecial NULL = new RuleSpecial("NULL");

    /**
     * Special VOID RuleComponent that never matches.
     */
    public static RuleSpecial VOID = new RuleSpecial("VOID");

    private String special;

    private RuleSpecial(String special) {
        this.special = special;
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
        return "<ruleref special=\"" + special + "\"/>";
    }
}
