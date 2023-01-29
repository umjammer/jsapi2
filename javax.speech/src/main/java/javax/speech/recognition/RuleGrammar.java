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

// Comp. 2.0.6

/**
 * Defines a Grammar that specifies what users may
 * say by a set of Rules.
 * <p>
 * The rules may be defined as Rule objects that represent the rule in a
 * data structure or defined in grammar text (SRGS).
 * The two representations are isomorphic and the toString method returns
 * the markup text representation of a RuleGrammar.
 * <p>
 * The descriptions below use the element and attribute terminology borrowed
 * from XML as used in SRGS.
 * The SRGS specification contains examples and definitions.
 * <p>
 * All RuleGrammars are created and managed through the Recognizer interface.
 * A RuleGrammar may be created
 * programmatically using the createRuleGrammar method or
 * created from grammar text with the one of the loadRuleGrammar methods.
 * @see <A href="http://www.w3.org/TR/speech-grammar/" target="_blank">
 *       SRGS specification</A>,
 * @see javax.speech.recognition.Rule
 * @see javax.speech.recognition.Recognizer
 * @see javax.speech.recognition.Recognizer#createRuleGrammar(java.lang.String, java.lang.String)
 * @see javax.speech.recognition.Recognizer#loadRuleGrammar(java.lang.String, java.io.Reader)
 */
public interface RuleGrammar extends Grammar {

    boolean isActivatable(String ruleName);

    void setActivatable(String ruleName, boolean activatable);

    /**
     * Specifies an element within this RuleGrammar.
     * <p>
     * Elements may be specified for "lexicon", "meta", "metadata", and "tag".
     * Except for "metadata", these elements may be specified more than once.
     * None of these are required.
     * <p>
     * If a lexicon is specified, the pronunciations affect only this grammar.
     * The VocabularyManager does not return these pronunciations.
     * <p>
     * The following example shows how to add a meta element to a grammar:
     * <p>
     * ruleGrammar.addElement("
     * meta name=\"author\" content=\"Scott\"
     * ");
     *
     * @param element the element to add to this RuleGrammar
     * @throws java.lang.IllegalArgumentException if the element is not supported
     *          or is malformed
     * @see javax.speech.recognition.RuleGrammar#getElements(java.lang.String)
     * @see javax.speech.recognition.RuleGrammar#removeElement(java.lang.String)
     * @see javax.speech.recognition.RuleGrammar#setAttribute(java.lang.String, java.lang.String)
     * @see javax.speech.recognition.RuleGrammar#setDoctype(java.lang.String)
     * @see javax.speech.VocabularyManager
     */
    void addElement(String element) throws GrammarException;

    void removeElement(String element);

    /**
     * Returns a Rule object for the specified name.
     * <p>
     * Returns null if the Rule is unknown.
     * <p>
     * The addRule method is used to add Rules to this RuleGrammar.
     * The most recently added Rule is returned,
     * even if it has not yet been committed by commitChanges.
     * <p>
     * The Rule.toString method can be used to convert the return object
     * to a printable String in grammar text.
     * @param ruleName the name of the Rule to find
     * @return the Rule with the specified name or null
     * @see javax.speech.recognition.RuleGrammar#addRule(javax.speech.recognition.Rule)
     * @see javax.speech.recognition.Recognizer#commitChanges()
     * @see javax.speech.recognition.RuleGrammar#toString()
     */
    Rule getRule(String ruleName);

    /**
     * Adds a Rule to this RuleGrammar.
     * <p>
     * If the RuleGrammar already contains a Rule with the same name,
     * then it is replaced.
     * The change in the RuleGrammar takes effect when
     * changes are committed.
     * <p>
     * If the added Rule is
     * PUBLIC_SCOPE, then it may be enabled and active
     * for recognition and/or referenced by other RuleGrammars.
     * PRIVATE_SCOPE Rules may only be referenced within this RuleGrammar.
     *
     * @param rule the Rule to add to this RuleGrammar
     * @throws java.lang.IllegalArgumentException if rule is null or not valid
     * @see javax.speech.recognition.RuleGrammar#getRule(java.lang.String)
     * @see javax.speech.recognition.RuleGrammar#removeRule(java.lang.String)
     * @see javax.speech.recognition.Rule#PUBLIC
     * @see javax.speech.recognition.Recognizer#processGrammars()
     * @see javax.speech.recognition.RuleReference
     */
    void addRule(Rule rule);

    /**
     * Adds a Rule represented in grammar text to this RuleGrammar.
     * <p>
     * Once the grammar text is correctly converted to a Rule
     * this method behaves the same as the addRule(Rule) method.
     * <p>
     * The following example adds a Rule from grammar text:
     * <p>
     * ruleGrammar.addRule(
     * "
     * rule id=\"object\"
     * " +
     * "
     * item repeat="0-1"
     * " +
     * "
     * ruleref uri="determiner"/
     * " +
     * "
     * /item
     * " +
     * "
     * one-of
     * " +
     * "
     * item
     * window
     * /item
     * " +
     * "
     * item
     * door
     * /item
     * " +
     * "
     * /one-of
     * " +
     * "
     * /rule
     * ";
     * <p>
     * This is equivalent to adding a Rule programmatically as follows:
     * <p>
     * ruleGrammar.addRule(
     * new Rule("object",
     * new RuleSequence(new RuleComponent[] {
     * new RuleCount(
     * new RuleReference("determiner"),
     * 0, 1),
     * new RuleAlternatives(
     * new String[] {"window", "door"})})));
     *
     * @param ruleText grammar text for a Rule to add
     * @throws javax.speech.recognition.GrammarException if the grammar text contains any errors
     * @see javax.speech.recognition.RuleGrammar#addRule(javax.speech.recognition.Rule)
     */
    void addRule(String ruleText) throws GrammarException;

    /**
     * Adds an array of Rules to this RuleGrammar.
     * <p>
     * This method adds each Rule to the RuleGrammar in
     * the order in which they appear in the array.
     * The addRule method describes more detail.
     * @param rules the Rules to add to this RuleGrammar
     * @throws java.lang.IllegalArgumentException if rules is null or not valid
     * @see javax.speech.recognition.RuleGrammar#addRule(javax.speech.recognition.Rule)
     */
    void addRules(Rule[] rules);

    /**
     * Remove a named Rule from this RuleGrammar.
     * <p>
     * The removal only takes effect when the changes are next committed
     * with commitChanges.
     * @param ruleName name of the Rule to be removed
     * @throws java.lang.IllegalArgumentException if ruleName is unknown
     * @see javax.speech.recognition.Recognizer#processGrammars()
     */
    void removeRule(String ruleName) throws IllegalArgumentException;

    /**
     * Lists the names of all Rules defined in this RuleGrammar.
     * <p>
     * If any Rules are pending removal, they are not listed
     * (between a call to removeRule and a commitChanges taking effect).
     * @return names of all Rules defined in this grammar
     * @see javax.speech.recognition.RuleGrammar#removeRule(java.lang.String)
     * @see javax.speech.recognition.Recognizer#processGrammars()
     */
    String[] listRuleNames();

    /**
     * Sets the value for the specified attribute in the grammar element.
     * <p>
     * Possible attributes for the grammar element include:
     * "version", "xmlns", "xml:lang", "mode", "root", "tag-format", and
     * "xml:base".
     * <p>
     * Many of these values are optional and a default value will always
     * be supplied.
     * <p>
     * The default value for xml:lang is the current Locale.
     * <p>
     * Specifying a root rule allows
     * <I>implicit</I>
     * reference to a RuleGrammar:
     * a RuleReference need only include the grammar reference.
     * The root Rule of the RuleGrammar should be PUBLIC_SCOPE to be enabled.
     * The default root is the first PUBLIC_SCOPE rule specified.
     * @param attribute the desired attribute to set
     * @param value the value for the desired attribute
     * @throws java.lang.IllegalArgumentException if the attribute or
     *          value is not supported.
     * @see javax.speech.recognition.RuleGrammar#getAttribute(java.lang.String)
     * <A href="../../../javax/speech/recognition/RuleGrammar.html#addElement(java.lang.String)">#addElement</A>,
     * @see javax.speech.recognition.RuleGrammar#setDoctype(java.lang.String)
     * @see javax.speech.recognition.RuleReference
     * @see javax.speech.recognition.Rule#PUBLIC
     */
    void setAttribute(String attribute, String value) throws IllegalArgumentException;

    /**
     * Gets the value for the specified attribute in the grammar element.
     * <p>
     * The implementation default is returned if not set.
     * @param attribute the desired attribute from the grammar element
     * @return the value for the specified attribute
     * @throws java.lang.IllegalArgumentException if an illegal attribute is specified.
     * @see javax.speech.recognition.RuleGrammar#setAttribute(java.lang.String, java.lang.String)
     */
    String getAttribute(String attribute) throws IllegalArgumentException;

    String[] getElements();

    /**
     * Parses a sequence of tokens against this RuleGrammar.
     * <p>
     * Parsing is the process of matching the tokens against the Rules that are
     * defined in the RuleGrammar.
     * <p>
     * The tokens parsed against the Rule identified by ruleName,
     * which may identify any defined Rule of this RuleGrammar
     * (PUBLIC_SCOPE or PRIVATE_SCOPE, enabled or disabled).
     * <p>
     * The method returns a RuleParse object. The RuleParse
     * relates parse structure corresponding to the grammar structure.
     * <p>
     * If parse fails, then the return value is null.
     * To succeed, the parse must match the complete sequence of tokens.
     * <p>
     * For some RuleGrammars and token sequences, multiple parses are legal.
     * The parse method returns only one parse.
     * It is not defined which of the legal parses should be returned.
     * Development tools can help to analyze grammars for such ambiguities.
     * @param tokens a sequence of tokens
     * @param ruleName the name of the Rule from which the parse begins
     * @return a RuleParse object corresponding to the tokens or null
     * @throws javax.speech.recognition.GrammarException if an error is found in the definition of the RuleGrammar
     * @throws java.lang.IllegalArgumentException if ruleName is null or not defined
     * @see javax.speech.recognition.RuleGrammar#parse(java.lang.String, java.lang.String)
     */
    RuleParse parse(String[] tokens, String ruleName) throws IllegalArgumentException, GrammarException;

    RuleParse parse(String text, String ruleName) throws IllegalArgumentException, GrammarException;

    /**
     * Resolves a RuleReference within this RuleGrammar.
     * <p>
     * If the RuleReference refers to a Rule within this RuleGrammar,
     * a RuleReference is return that includes the grammarReference.
     * If not found, null is returned.
     * <p>
     * If the input RuleReference includes a grammarReference,
     * it must agree with the grammarReference for this grammar.
     * If the grammarReference is not defined or is relative,
     * the grammarReference in the output RuleReference will be absolute.
     * <p>
     * The following grammar shows the result of resolving a RuleReference
     * against a RuleGrammar loaded from a URI that contains a Rule named
     * "number":
     * <p>
     * String uri = "http://grammar.example.com/NumberGrammar.grxml";
     * RuleGrammar ruleGrammar = recognizer.loadRuleGrammar(uri);
     * RuleReference ruleReference =
     * ruleGrammar.resolve(new RuleReference("number"));
     * // now ruleReference.getGrammarReference is the URI
     *
     * @param ruleReference RuleReferene to resolve
     * @return a more fully specified RuleReference or null if not found
     * @throws javax.speech.recognition.GrammarException if an error is found in the definition
     *          of the RuleGrammar
     * @see javax.speech.recognition.RuleReference#getGrammarReference()
     * @see javax.speech.recognition.Grammar#getReference()
     */
    RuleReference resolve(RuleReference ruleReference) throws GrammarException;

    /**
     * Sets the root rulename for this RuleGrammar.
     * <p>
     * The Rule identified by the root rulename specifies where
     * processing begins for this RuleGrammar.
     * This Rule should have PUBLIC_SCOPE to allow external reference.
     * <p>
     * If the root rulename is not explicitly set with setRoot,
     * then it defaults to the name of the first PUBLIC_SCOPE Rule added.
     * The root rulename may be set to null to use the default.
     * <p>
     * The change in the RuleGrammar takes effect when
     * changes are committed.
     * A GrammarException is issued if no Rule has the rootName
     * at that time.
     * @param rootName the name of the root Rule for this RuleGrammar
     * @see javax.speech.recognition.RuleGrammar#getRoot()
     * @see javax.speech.recognition.RuleGrammar#addRule(javax.speech.recognition.Rule)
     * @see javax.speech.recognition.GrammarException
     * @see javax.speech.recognition.Rule#PUBLIC
     */
    void setRoot(String rootName);

    /**
     * Gets the root rulename for this RuleGrammar.
     * <p>
     * The root rulename may be null if none was set and there are no Rules.
     * @return the name of the root Rule for this RuleGrammar
     * @see javax.speech.recognition.RuleGrammar#setRoot(java.lang.String)
     * @see javax.speech.recognition.RuleGrammar#addRule(javax.speech.recognition.Rule)
     */
    String getRoot();

    /**
     * Returns a String containing a specification for this RuleGrammar in
     * grammar text.
     * <p>
     * The string includes the document information, the grammar element,
     * the grammar attributes, and all contained elements,
     * including all of the rule definitions.
     * @return the grammar text for this RuleGrammar
     */
    String toString();
}
