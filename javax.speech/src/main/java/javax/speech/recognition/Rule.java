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

// Comp 2.0.6

/**
 * Represents a Rule in a RuleGrammar.
 * <p>
 * Each Rule in a RuleGrammar defines a named rule in terms of RuleComponents.
 * <p>
 * In traditional grammar terminology,
 * the name of the rule represents the left-hand side (LHS) and
 * the RuleComponents represent the right-hand side (RHS).
 * At least one Rule within a RuleGrammar must have PUBLIC_SCOPE
 * to allow external reference.
 * @see javax.speech.recognition.RuleGrammar
 */
public class Rule {
    public static int PUBLIC = 0x1;

    public static int PRIVATE = 0x2;

    private String ruleName;

    private RuleComponent ruleComponent;

    private int scope;

    /**
     * Constructs a Rule with a name and RuleComponent expansion.
     * <p>
     * The Rule uses the default PRIVATE_SCOPE.
     * @param ruleName the name of this Rule
     * @param ruleComponent the expansion for this Rule
     * @throws java.lang.IllegalArgumentException if the name is not valid grammar text
     * @see javax.speech.recognition.Rule#getRuleName()
     * @see javax.speech.recognition.Rule#getRuleComponent()
     * @see javax.speech.recognition.Rule#getScope()
     */
    public Rule(String ruleName, RuleComponent ruleComponent) throws IllegalArgumentException {
        this(ruleName, ruleComponent, PRIVATE);
    }

    /**
     * Constructs a Rule with a name, RuleComponent expansion, and scope.
     * <p>
     * The scope is either PUBLIC_SCOPE or PRIVATE_SCOPE.
     * <p>
     * PUBLIC_SCOPE Rules may be referenced by other Rules both
     * locally within the same RuleGrammar and externally by other RuleGrammars.
     * PRIVATE_SCOPE Rules may only be referenced locally
     * within the RuleGrammar.
     * Rules are referenced with RuleReferences.
     * @param ruleName the name of this Rule
     * @param ruleComponent the expansion for this Rule
     * @param scope the scope of this Rule
     * @throws java.lang.IllegalArgumentException if the name is not valid grammar text
     *          or if the scope is invalid
     * @see javax.speech.recognition.Rule#getRuleName()
     * @see javax.speech.recognition.Rule#getRuleComponent()
     * @see javax.speech.recognition.Rule#getScope()
     * @see javax.speech.recognition.RuleGrammar
     * @see javax.speech.recognition.RuleReference
     */
    public Rule(String ruleName, RuleComponent ruleComponent, int scope) {
        RuleComponent.checkValidGrammarText(ruleName);

        if ((scope != PRIVATE) && (scope != PUBLIC)) {
            throw new IllegalArgumentException(
                    "Scope must be either PRIVATE or PUBLIC!");
        }
        if (ruleComponent == null) {
            throw new IllegalArgumentException(
                    "Rule component must not be null!");
        }
        this.ruleName = ruleName;
        this.ruleComponent = ruleComponent;
        this.scope = scope;
    }

    /**
     * Gets the RuleComponent expansion of this Rule.
     * <p>
     * The expansion defines how this Rule matches speech.
     * @return the RuleComponent expansion
     */
    public RuleComponent getRuleComponent() {
        return ruleComponent;
    }

    /**
     * Gets the name of this Rule.
     * <p>
     * The name is used to identify this Rule.
     * @return the name of this Rule
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Gets the scope of this Rule.
     * <p>
     * The scope is either PUBLIC_SCOPE or PRIVATE_SCOPE.
     * It is specified in the Rule constructor.
     * @return the scope value
     * @see javax.speech.recognition.Rule#PUBLIC
     * @see javax.speech.recognition.Rule#PRIVATE
     * @see javax.speech.recognition.Rule#Rule(java.lang.String, javax.speech.recognition.RuleComponent, int)
     */
    public int getScope() {
        return scope;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("<rule id=\"");
        str.append(ruleName);
        str.append("\" scope=\"");
        if (scope == PRIVATE) {
            str.append("private");
        } else {
            str.append("public");
        }
        str.append("\">");
        str.append(ruleComponent);
        str.append("</rule>");
        return str.toString();
    }
}
