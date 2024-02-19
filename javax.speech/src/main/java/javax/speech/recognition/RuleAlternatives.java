/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
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
 * Represents a RuleComponent composed of a set of alternative RuleComponents.
 * <p>
 * RuleAlternatives are used to construct RuleGrammar objects.
 * A RuleAlternatives object is spoken by saying one,
 * and only one, of of its alternative RuleComponents.
 * <p>
 * A RuleAlternatives object contains a set of zero or more
 * RuleComponent objects.
 * A set of zero alternatives is equivalent to VOID (it is unspeakable).
 * <p>
 * Weights may be (optionally) assigned to each alternative RuleComponent.
 * The weights indicate the chance of each RuleComponent being spoken.
 * If no weights are defined, then all alternatives are considered
 * equally likely.
 * <p>
 * Traditional floating point weights may be converted to integer weights
 * as follows:
 * multiply by NORM_WEIGHT and then apply a ceiling of MAX_WEIGHT.
 * @see javax.speech.recognition.RuleSpecial#VOID
 * @see javax.speech.recognition.RuleAlternatives#NORM_WEIGHT
 * @see javax.speech.recognition.RuleAlternatives#MAX_WEIGHT
 */
public class RuleAlternatives extends RuleComponent {

    /**
     * The maximum weight for a RuleComponent.
     */
    public static final int MAX_WEIGHT = 0x7fffffff;

    /**
     * A value for the weight corresponding to 1.0.
     */
    public static final int NORM_WEIGHT = 0x3E8;

    public static final int MIN_WEIGHT = 0x0;

    private RuleComponent[] ruleComponents;

    private int[] weights;

    /**
     * Constructs a RuleAlternatives object with an array of
     * alternative RuleComponents.
     * <p>
     * All alternative RulesComponents have equal weight.
     * A zero-length or null set of alternatives is equivalent to VOID
     * (that is,  unspeakable).
     * @param ruleComponents the set of alternative RuleComponents
     * @see javax.speech.recognition.RuleSpecial#VOID
     */
    public RuleAlternatives(RuleComponent[] ruleComponents) throws IllegalArgumentException {
        if (ruleComponents == null) {
            throw new IllegalArgumentException("Rule components must not be null!");
        }
        this.ruleComponents = ruleComponents;
    }

    /**
     * Constructs a RuleAlternatives object with an array of
     * alternative RuleComponents and a corresponding array of weights.
     * <p>
     * The RuleComponents array and weights array may be zero-length or null.
     * <p>
     * A zero-length or null set of alternatives is equivalent to VOID
     * (that is, unspeakable).
     * If the weights array is non-null,
     * it must have identical length to the ruleComponents array.
     * If the weights array is null,
     * all alternative ruleComponents have equal weight.
     * @param ruleComponents the set of alternative RuleComponents
     * @param weights the weights for each RuleComponent or null
     * @throws java.lang.IllegalArgumentException if mismatched array lengths
     *          or bad weight values
     * @see javax.speech.recognition.RuleSpecial#VOID
     */
    public RuleAlternatives(RuleComponent[] ruleComponents, int[] weights) throws IllegalArgumentException {
        if (ruleComponents == null) {
            throw new IllegalArgumentException("Rule components must not be null!");
        }
        this.ruleComponents = ruleComponents;
        if (weights == null) {
            this.weights = new int[ruleComponents.length];
            for (int i = 0; i < ruleComponents.length; i++) {
                this.weights[i] = NORM_WEIGHT;
            }
        } else {
            if (ruleComponents.length != weights.length) {
                throw new IllegalArgumentException("Lengths of rule components and weights do not match!");
            }
            this.weights = weights;
        }
    }

    /**
     * Constructs a phrase list from an array of String objects.
     * <p>
     * Each string is used to create a single RuleToken object.
     * <p>
     * A string containing multiple words (e.g. "san francisco")
     * is treated as a single token.
     * If appropriate, an application should parse such strings
     * to produce separate tokens.
     * <p>
     * The phrase list may be zero-length or null.
     * This will produce an empty set
     * of alternatives which is equivalent to VOID (i.e. unspeakable).
     * @param tokens a set of alternative tokens
     * @see javax.speech.recognition.RuleToken
     * @see javax.speech.recognition.RuleSpecial#VOID
     */
    public RuleAlternatives(String[] tokens) {
        if (tokens != null) {
            ruleComponents = new RuleComponent[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                ruleComponents[i] = new RuleToken(token);
            }
        }
    }

    /**
     * Returns the array of alternative RuleComponents.
     * <p>
     * If a zero-length array is returned, this is treated the same as VOID.
     * @return the array of alternative RuleComponents.
     * @see javax.speech.recognition.RuleAlternatives#getWeights()
     * @see javax.speech.recognition.RuleSpecial#VOID
     */
    public RuleComponent[] getRuleComponents() {
        return ruleComponents;
    }

    /**
     * Returns the array of weights.
     * <p>
     * A zero-length array is returned if the weights were not
     * originally specified.
     * Otherwise, the length of the weights array is
     * guaranteed to be the same length as the array of RuleComponents.
     * @return an array of weights
     * @see javax.speech.recognition.RuleAlternatives#getRuleComponents()
     */
    public int[] getWeights() {
        return weights;
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
        if ((ruleComponents == null) || (ruleComponents.length == 0)) {
            return RuleSpecial.VOID.toString();
        }

        StringBuilder str = new StringBuilder();
        str.append("<one-of>");

        for (int i = 0; i < ruleComponents.length; i++) {
            str.append("<item");
            if (weights != null) {
                if (weights[i] != NORM_WEIGHT) {
                    // TODO we should divide by NORM_WEIGHT but this is not
                    // supported in CLDC 1.0
                    str.append(" weight=\"");
                    str.append(weights[i]);
                    str.append("\"");
                }
            }
            str.append('>');
            RuleComponent component = ruleComponents[i];
            if (component == null) {
                str.append(RuleSpecial.NULL.toString());
            } else {
                str.append(component);
            }
            str.append("</item>");
        }
        str.append("</one-of>");

        return str.toString();
    }
}
