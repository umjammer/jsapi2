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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the parse structure of a Result or String against a RuleGrammar.
 * <p>
 * The RuleParse object indicates how the Result or String matches to the Rules
 * of the RuleGrammar and also any Rules referenced by that RuleGrammar.
 * <p>
 * A RuleParse is obtained using the parse methods of RuleGrammar.
 * The RuleParse structure returned by parse closely matches the structure
 * of the grammar it is parsed against: if the grammar contains RuleTag,
 * RuleSequence, RuleToken objects and so on, then the returned RuleParse will
 * contain corresponding objects.
 * <p>
 * The mapping from RuleComponent objects in a RuleGrammar to
 * RuleComponent objects in a RuleParse follows:
 * <p>
 * <ul>
 * <li>RuleAlternatives: maps to a RuleAlternatives object containing a
 * single RuleComponent object for the one entity in the
 * set of alternatives that is matched.</li>
 * <li>RuleCount: the RuleComponent within the RuleCount maps to a RuleSequence
 * containing a RuleComponent for each match of the
 * RuleComponent contained by RuleCount.
 * The sequence may contain zero, one or multiple RuleComponent matches
 * depending on the count.
 * The min and max repeats are set to the count.
 * <li>RuleReference: maps to a RuleParse indicating how the RuleReference
 * was matched with a parse.</li>
 * <li>RuleSequence: maps to a RuleSequence with a matching RuleComponent
 * for each RuleComponent in the original sequence.</li>
 * <li>RuleTag, RuleToken, and RuleSpecial: map to themselves.</li>
 * </ul>
 * <p>
 * The RuleParse object is not used in defining a RuleGrammar so it
 * is never matched.
 * <p>
 * This example shows the resulting parse structure given a RuleGrammar,
 * a RuleReference, and an array of Strings representing tokens.
 * <p>
 * Consider the following RuleGrammar defined in SRGS:
 * <pre>
 * grammar root="command"
 * meta name="id" content="CommandGrammar"/
 * rule id="command" scope="public"
 * ruleref uri="#action"/
 * ruleref uri="#object"/
 * item repeat="0-1"
 * ruleref uri="#polite"/
 * /item
 * /rule
 * rule id="action"
 * one-of
 * item
 * open
 * tag
 * OP
 * /tag
 * /item
 * item
 * close
 * tag
 * CL
 * /tag
 * /item
 * /one-of
 * /rule
 * rule id="object"
 * item repeat="0-1"
 * ruleref uri="determiner"/
 * /item
 * one-of
 * item
 * window
 * /item
 * item
 * door
 * /item
 * /one-of
 * /rule
 * rule id="determiner"
 * one-of
 * item
 * a
 * /item
 * item
 * the
 * /item
 * /one-of
 * /rule
 * rule id="polite"
 * one-of
 * item
 * please
 * /item
 * item
 * kindly
 * /item
 * /one-of
 * /rule
 * /grammar
 * </pre>
 * This same grammar may be defined programmatically as follows:
 * <pre>
 * RuleSequence commandRHS =
 *   new RuleSequence(new RuleComponent[] {
 *     new RuleReference("action"),
 *     new RuleReference("object"),
 *     new RuleCount(new RuleReference("polite"), 0, 1)});
 * RuleAlternatives actionRHS =
 *   new RuleAlternatives(new RuleComponent[] {
 *     new RuleSequence(new RuleComponent[] {
 *       new RuleToken("open"),
 *       new RuleTag("OP")}),
 *   new RuleSequence(new RuleComponent[] {
 *     new RuleToken("close"),
 *     new RuleTag("CL")})});
 * RuleSequence objectRHS =
 *   new RuleSequence(new RuleComponent[] {
 *     new RuleCount(new RuleReference("determiner"), 0, 1),
 *     new RuleAlternatives(new String[] {"window", "door"})});
 * RuleAlternatives determinerRHS =
 *   new RuleAlternatives(new String[] {"a", "the"});
 * RuleAlternatives politeRHS =
 *   new RuleAlternatives(new String[] {"please", "kindly"});
 *
 * Recognizer r = ...
 * RuleGrammar rg = r.createRuleGrammar("CommandGrammar", "command");
 * rg.addRules(new Rules[] {
 *   new Rule("command", commandRHS, RuleGrammar.PUBLIC_SCOPE);
 *   new Rule("action", actionRHS);
 *   new Rule("object", objectRHS);
 *   new Rule("determiner", determinerRHS);
 *   new Rule("polite", politeRHS)});
 * </pre>
 * Now we can create a parse for the sentence "close the door please"
 * beginning with the root Rule as follows:
 * <p>
 * RuleParse ruleParse =
 * rg.parse(new String[] {"close", "the", "door", "please"}, "command");
 * </pre>
 * Now the parse will have the same structure as follows:
 * <pre>
 * RuleParse ruleParse =
 *   new RuleParse(
 *     new RuleReference("command"),
 *     new RuleSequence(new RuleComponent[] {            // Parse for "command"
 *       new RuleParse(
 *         new RuleReference("action"),
 *         new RuleAlternatives(new RuleComponent[] {    // Parse for "action"
 *           new RuleSequence(new RuleComponent[] {      // The chosen alternative
 *             new RuleToken("close"),
 *             new RuleTag("CL")})})),
 *       new RuleParse(
 *         new RuleReference("object"),
 *         new RuleSequence(new RuleComponent[] {        // Parse for "object"
 *           new RuleCount(                              // RuleComponent argument
 *             new RuleSequence(new RuleComponent[] {    //   becomes RuleSequence
 *               new RuleParse(                          // One item per match
 *                 new RuleReference("determiner"),
 *            new RuleAlternatives(new RuleComponent[] { // "determiner" parse
 *                   new RuleToken("the")}))}),          // RuleToken from String
 *             1, 1),                                    // 1 and only 1
 *           new RuleAlternatives(new RuleComponent[] {
 *             new RuleToken("door")})})),               // The chosen alternative
 *       new RuleCount(                                  // Option chosen
 *         new RuleSequence(new RuleComponent[] {        //   with one repeat
 *           new RuleParse(
 *             new RuleReference("polite"),
 *            new RuleAlternatives(new RuleComponent[] { // Parse for "polite"
 *               new RuleToken("please")}))}),           // The chosen alternative
 *         1, 1)}));                                     // 1 and only 1
 * <pre>
 * Completing the cycle, ruleParse.toString will take the parse structure back
 * to a markup string.  In this case, the information represented by each
 * RuleParse instance is represented by a corresponding
 * ruleref element
 * with the corresponding parse included as the content of the element.
 * The ruleref element does not normallly include content, so the
 * resulting String only represents a parse, not valid grammar text.
 * <p>
 * ruleref uri="#command"
 * <p>
 * ruleref uri="#action"
 * <p>
 * one-of
 * <p>
 * item
 * close
 * tag
 * CL
 * /tag
 * /item
 * <p>
 * /one-of
 * <p>
 * /ruleref
 * <p>
 * ruleref uri="#object"
 * <p>
 * item repeat="1-1"
 * <p>
 * ruleref uri="#determiner"
 * <p>
 * one-of
 * <p>
 * item
 * the
 * /item
 * <p>
 * /one-of
 * <p>
 * /ruleref
 * <p>
 * /item
 * <p>
 * one-of
 * <p>
 * item
 * door
 * /item
 * <p>
 * /one-of
 * <p>
 * /ruleref
 * <p>
 * item repeat="1-1"
 * <p>
 * ruleref uri="#polite"
 * <p>
 * one-of
 * <p>
 * item
 * please
 * /item
 * <p>
 * /one-of
 * <p>
 * /ruleref
 * <p>
 * /item
 * <p>
 * /ruleref
 *
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.RuleGrammar#parse(java.lang.String, java.lang.String)
 * @see javax.speech.recognition.RuleComponent
 * @see javax.speech.recognition.RuleAlternatives
 * @see javax.speech.recognition.RuleCount
 * @see javax.speech.recognition.RuleReference
 * @see javax.speech.recognition.RuleSequence
 * @see javax.speech.recognition.RuleSpecial
 * @see javax.speech.recognition.RuleTag
 * @see javax.speech.recognition.RuleToken
 * @since 2.0.6
 */
public class RuleParse extends RuleComponent {

    private RuleReference ruleReference;

    private RuleComponent parse;

    /**
     * Constructs a RuleParse object associating a RuleReference with
     * a parse structure for that RuleReference.
     * <p>
     * Viewing a parse as a tree, the RuleParse created by this constructor
     * represents the entire tree with RuleReference as the root and the parse
     * parameter as the subtrees below the root.
     * @param ruleReference a RuleReference within a Grammar
     * @param parse a parse for that ruleReference
     * @see javax.speech.recognition.RuleParse#getRuleReference()
     * @see javax.speech.recognition.RuleParse#getParse()
     * @see javax.speech.recognition.RuleGrammar#parse(java.lang.String, java.lang.String)
     */
    public RuleParse(RuleReference ruleReference, RuleComponent parse) {
        this.ruleReference = ruleReference;
        this.parse = parse;
    }

    public Object[] getTags() {
        if (parse == null) {
            return null;
        }

        List<Object> parseTags = new ArrayList<>();
        addTags(parseTags, parse);

        return parseTags.toArray(new Object[0]);
    }

    private void addTags(List<Object> tags, RuleComponent component) {
        if (component instanceof RuleTag tag) {
            Object tagName = tag.getTag();
            tags.add(tagName);
        } else if (component instanceof RuleAlternatives alternatives) {
            RuleComponent[] components = alternatives.getRuleComponents();
            for (RuleComponent actComponent : components) {
                addTags(tags, actComponent);
            }
        } else if (component instanceof RuleCount count) {
            RuleComponent actComponent = count.getRuleComponent();
            addTags(tags, actComponent);
        } else if (component instanceof RuleParse parse) {
            RuleComponent actComponent = parse.getParse();
            addTags(tags, actComponent);
        } else if (component instanceof RuleSequence sequence) {
            RuleComponent[] components = sequence.getRuleComponents();
            for (RuleComponent actComponent : components) {
                addTags(tags, actComponent);
            }
        }
    }

    /**
     * Returns the parse associated with (or below) the RuleReference.
     * @return the associated parse
     * @see javax.speech.recognition.RuleParse#getRuleReference()
     */
    public RuleComponent getParse() {
        return parse;
    }

    /**
     * Returns the matched RuleReference (or root) for this RuleParse.
     * <p>
     * This RuleReference represents the root of this parse tree.
     * The rest of this parse (subparse) is returned by getParse.
     * @return the matched RuleReference
     * @see javax.speech.recognition.RuleParse#getParse()
     */
    public RuleReference getRuleReference() {
        return ruleReference;
    }

    /**
     * Converts a RuleParse to a string with a similar style
     * to the grammar text.
     * <p>
     * Note that a parse for a RuleAlternative will
     * contain only the one matching alternative from within that
     * RuleAlternative.  A singleton RuleAlternative is not normally useful
     * other than for this purpose.
     * @return a parse structure represented using the grammar text
     * @see javax.speech.recognition.RuleComponent
     * @see javax.speech.recognition.Rule
     */
    public String toString() {
        if (parse == null) {
            return "";
        }

        StringBuffer str = new StringBuffer();

        if (ruleReference != null) {
            ruleReference.appendStartTag(str);
            str.append('>');
        }

        str.append(parse.toString());

        if (ruleReference != null) {
            str.append("</ruleref>");
        }

        return str.toString();
    }
}
