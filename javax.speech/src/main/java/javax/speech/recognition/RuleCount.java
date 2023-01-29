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
 * Attaches repeat counts to a contained RuleComponent object to indicate the
 * number of times it may occur.
 * <p>
 * The contained RuleComponent may occur optionally (zero or one times),
 * zero or more times, one or more times, up to a maximum, at least a minimum,
 * and within a range.
 * This class supports the following common cases from formal language theory:
 * "[]" (optional), "*" (zero or more - Kleene star)
 * and "+" (one or more - positive closure).
 * <p>
 * Any RuleComponent not contained by a RuleCount object occurs once only.
 * <p>
 * Probabilities normally in the range of 0 to 1.0
 * should be scaled to the range of 0 to MAX_PROBABILITY.
 */
public class RuleCount extends RuleComponent {

    /**
     * The maximum repeat probability for a RuleCount.
     */
    public static final int MAX_PROBABILITY = 0x7fffffff;

    public static final int REPEAT_INDEFINITELY = 0x7fffffff;

    private RuleComponent ruleComponent;

    private int repeatMin;

    private int repeatMax;

    private int repeatProbability;

    /**
     * Constructs a RuleCount specifying the minimum number
     * of times a RuleComponent must repeat.
     * <p>
     * repeatMin of 0 represents 0 or more (Kleene star) and
     * repeatMin of 1 represents the positive closure.
     * <p>
     * repeatMin must be greater than or equal to 0.
     * @param ruleComponent the RuleComponent to which the counts apply
     * @param repeatMin the minimum repeat count
     * @throws java.lang.IllegalArgumentException if repeatMin is less than 0
     * @see javax.speech.recognition.RuleCount#getRuleComponent()
     * @see javax.speech.recognition.RuleCount#getRepeatMin()
     */
    public RuleCount(RuleComponent ruleComponent, int repeatMin) throws IllegalArgumentException {
        if (repeatMin < 0) {
            throw new IllegalArgumentException("Repeat minimum must be greater or equal to 0!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMax = REPEAT_INDEFINITELY;
        this.repeatMin = repeatMin;
        this.repeatProbability = -1;
    }

    /**
     * Constructs a RuleCount specifying a range for repeating a RuleComponent.
     * <p>
     * A repeatMin of 0 and a repeatMax of 1 means that this
     * RuleComponent is optional.
     * <p>
     * repeatMin must be greater than or equal to 0 and
     * less than or equal to repeatMax.
     * @param ruleComponent the RuleComponent to which the counts apply
     * @param repeatMin the minimum repeat count
     * @param repeatMax the maximum repeat count
     * @throws java.lang.IllegalArgumentException if repeatMin is less than 0 or
     *          if repeatMin is greater than repeatMax
     * @see javax.speech.recognition.RuleCount#getRuleComponent()
     * @see javax.speech.recognition.RuleCount#getRepeatMin()
     * @see javax.speech.recognition.RuleCount#getRepeatMax()
     */
    public RuleCount(RuleComponent ruleComponent, int repeatMin, int repeatMax) throws IllegalArgumentException {
        if (repeatMin < 0 || (repeatMin > repeatMax)) {
            throw new IllegalArgumentException("Repeat minimum must be greater or equal to 0 and smaller "
                            + "than or equal to repeat maximum!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
        this.repeatProbability = -1;
    }

    /**
     * Constructs a RuleCount specifying a range for
     * repeating a RuleComponent with a weight.
     * <p>
     * repeatMin must be greater than or equal to 0 and
     * less than or equal to repeatMax.
     * @param ruleComponent the RuleComponent to which the counts apply
     * @param repeatMin the minimum repeat count
     * @param repeatMax the maximum repeat count
     * @param repeatProbability the repeat probability
     * @throws java.lang.IllegalArgumentException if repeatMin is greater than repeatMax
     *          or repeatWeigtht is not with range
     * @see javax.speech.recognition.RuleCount#getRuleComponent()
     * @see javax.speech.recognition.RuleCount#getRepeatMin()
     * @see javax.speech.recognition.RuleCount#getRepeatMax()
     * @see javax.speech.recognition.RuleCount#getRepeatProbability()
     */
    public RuleCount(RuleComponent ruleComponent, int repeatMin, int repeatMax, int repeatProbability)
            throws IllegalArgumentException {
        if (repeatMin < 0 || (repeatMin > repeatMax)) {
            throw new IllegalArgumentException("Repeat minimum must be greater or equal to 0 and smaller "
                            + "than or equal to repeat maximum!");
        }

        if (repeatProbability < 0) {
            throw new IllegalArgumentException("Repeat propability must be greater or equal to 0!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
        this.repeatProbability = repeatProbability;
    }

    /**
     * Returns the repeatMax for this RuleCount.
     * @return the maximum repeat count for this RuleCount
     */
    public int getRepeatMax() {
        return repeatMax;
    }

    /**
     * Returns the repeatMin for this RuleCount.
     * @return the minimum repeat count for this RuleCount
     */
    public int getRepeatMin() {
        return repeatMin;
    }

    /**
     * Returns the repeat probability for this RuleCount.
     * @return the repeatProbability for this RuleCount
     */
    public int getRepeatProbability() {
        if (repeatProbability < 0) {
            return REPEAT_INDEFINITELY;
        }

        return repeatProbability;
    }

    /**
     * Returns the RuleComponent to which the counts apply.
     * @return the RuleComponent to which the counts apply.
     */
    public RuleComponent getRuleComponent() {
        return ruleComponent;
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
        StringBuilder str = new StringBuilder();

        str.append("<item repeat=\"");
        str.append(repeatMin);
        if (repeatMin != repeatMax) {
            str.append("-");

            if (repeatMax != REPEAT_INDEFINITELY) {
                str.append(repeatMax);
            }
        }
        str.append("\"");

        if (repeatProbability >= 0) {
            // TODO we should divide by MAX_PROBABILTY but this is not
            // supported in CLDC 1.0
            str.append(" repeat-prop=\"");
            str.append(repeatProbability);
            str.append("\"");
        }

        str.append(">");

        // TODO: What to do with null rule components?
        if (ruleComponent != null) {
            str.append(ruleComponent);
        }

        str.append("</item>");

        return str.toString();
    }
}
