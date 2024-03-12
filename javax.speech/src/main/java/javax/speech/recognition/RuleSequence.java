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
 * Specifies a RuleComponent composed of a sequence of sub-RuleComponents
 * that must each be spoken in order.
 * <p>
 * If there are zero RuleComponents in the sequence,
 * the sequence behaves the same as NULL.
 *
 * @see javax.speech.recognition.RuleSpecial#NULL
 * @since 2.0.6
 */
public class RuleSequence extends RuleComponent {

    private RuleComponent[] ruleComponents;

    /**
     * Constructs a RuleSequence with a sequence of RuleComponents.
     * <p>
     * The RuleComponents may have zero-length or be null.
     * This produces a zero-length
     * sequence which behaves the same as NULL.
     * @param ruleComponents a sequence of RuleComponent
     * @see javax.speech.recognition.RuleSpecial#NULL
     * @see javax.speech.recognition.RuleSequence#getRuleComponents()
     */
    public RuleSequence(RuleComponent[] ruleComponents) throws IllegalArgumentException {
        if (ruleComponents == null) {
            throw new IllegalArgumentException("Rule components must not be null!");
        }
        this.ruleComponents = ruleComponents;
    }

    /**
     * Constructs a RuleSequence that converts a sequence of strings to RuleTokens.
     * <p>
     * A string containing multiple words (e.g. "San Francisco") is treated
     * as a single token.
     * If appropriate, an application should parse such strings
     * to produce separate tokens.
     * <p>
     * The token list may be zero-length or null.
     * This produces a zero-length
     * sequence which behaves the same as NULL.
     * @param tokens a sequence of Strings converted to RuleTokens
     * @see javax.speech.recognition.RuleSpecial#NULL
     * @see javax.speech.recognition.RuleToken
     * @see javax.speech.recognition.RuleSequence#getRuleComponents()
     */
    public RuleSequence(String[] tokens) throws IllegalArgumentException {
        if (tokens == null) {
            throw new IllegalArgumentException("Tokens must not be null!");
        }
        ruleComponents = new RuleComponent[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            ruleComponents[i] = new RuleToken(token);
        }
    }

    /**
     * Returns the array of RuleComponents that define this sequence.
     * @return the RuleComponents in this sequence
     */
    public RuleComponent[] getRuleComponents() {
        return ruleComponents;
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
        if (ruleComponents == null) {
            return "";
        }

        StringBuilder str = new StringBuilder();
        for (RuleComponent component : ruleComponents) {
            if (component == null) {
                str.append(RuleSpecial.NULL.toString());
            } else {
                str.append(component);
            }
        }

        return str.toString();
    }

}
