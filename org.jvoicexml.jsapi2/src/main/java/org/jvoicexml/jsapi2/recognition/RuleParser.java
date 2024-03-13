/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.recognition;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Stack;
import javax.speech.EngineStateException;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleCount;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.RuleReference;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;


/**
 * Implementation of the parse method(s) on
 * {@link javax.speech.recognition.RuleGrammar}.
 *
 * @version $Revision: 1370 $
 */
public class RuleParser {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(RuleParser.class.getName());

    /** the grammarManager that contains all the grammars. */
    private final GrammarManager grammarManager;

    /**
     * Represents the current position of the input array, during the parse
     * algorithm.
     */
    private int position;

    /**
     * This stack helps the construction of the RuleParse in Deepth-search
     * algorithm.
     */
    private Stack<Object> grammarElements;

    /**
     * Creates a Rule Parser.
     *
     * @param manager GrammarManager
     * @param pos     int
     */
    public RuleParser(GrammarManager manager, int pos) {
        grammarManager = manager;
        position = pos;
        grammarElements = new Stack<>();
    }

    /**
     * Parse a text string against a particular rule from a particular grammar
     * returning a RuleParse data structure is successful and null otherwise.
     *
     * @param text             text to search
     * @param grammarManager   the grammar manager
     * @param grammarReference the grammar reference
     * @param ruleName         the start rule name
     * @return RuleParse
     */
    public static RuleParse parse(String text, GrammarManager grammarManager, String grammarReference,
                                  String ruleName) {
        String[] inputTokens = text.split(" ");
        return parse(inputTokens, grammarManager, grammarReference, ruleName);
    }

    /**
     * Parse a set of tokens against a particular rule from a particular grammar
     * returning a RuleParse data structure is successful and null otherwise.
     *
     * @param inputTokens      String[]
     * @param grammarManager   GrammarManager
     * @param grammarReference String
     * @param ruleName         String
     * @return RuleParse the start rule name
     */
    public static RuleParse parse(String[] inputTokens,
                                  GrammarManager grammarManager, String grammarReference, String ruleName) {
        RuleParse[] parse = mparse(inputTokens, grammarManager, grammarReference, ruleName);
        if (parse == null) {
            return null;
        }
        return parse[0];
    }

    /**
     * Parse a text against a particular rule from a particular grammar
     * returning a RuleParse data structure is successful and null otherwise.
     *
     * @param text             String
     * @param grammarManager   GrammarManager
     * @param grammarReference String
     * @param ruleName         String
     * @return RuleParse[]
     */
    public static RuleParse[] mparse(String text,
                                     GrammarManager grammarManager, String grammarReference, String ruleName) {
        String[] inputTokens = text.split(" ");
        return mparse(inputTokens, grammarManager, grammarReference, ruleName);
    }

    /**
     * Parse a set of tokens against a particular rule from a particular grammar
     * returning a RuleParse data structure is successful and null otherwise.
     *
     * @param inputTokens      String[]
     * @param grammarManager   GrammarManager
     * @param grammarReference String
     * @param ruleName         String
     * @return RuleParse[]
     */
    public static RuleParse[] mparse(String[] inputTokens,
                                     GrammarManager grammarManager, String grammarReference, String ruleName) {
        RuleParser parse = new RuleParser(grammarManager, 0);
        Grammar gram = grammarManager.getGrammar(grammarReference);
        RuleGrammar grammar;
        if (gram instanceof RuleGrammar) {
            grammar = (RuleGrammar) gram;
        } else {
            return null;
        }
        String[] ruleNames;
        if (ruleName != null) {
            ruleNames = new String[1];
            ruleNames[0] = ruleName;
        } else {
            ruleNames = grammar.listRuleNames();
        }
        List<RuleParse> parsed = new java.util.ArrayList<>();
        for (String currentRuleName : ruleNames) {
            if ((ruleName == null) && !(grammar.isActivatable(currentRuleName))) {
                continue;
            }
            Rule currentRule = grammar.getRule(currentRuleName);
            RuleComponent startRule = currentRule.getRuleComponent();
            if (startRule == null) {
                logger.log(Level.ERROR, "Bad rulename '" + currentRuleName + "'");
                continue;
            }
            GrammarGraph grammarGraph = parse.buildGrammarGraph(grammar,
                    currentRuleName);
            GrammarNode node = grammarGraph.getStartNode();
            if (parse.parse(node, inputTokens)) {
                RuleParse element = (RuleParse) parse.grammarElements.pop();
                parsed.add(element);
            }
        }
        if (parsed.isEmpty()) {
            // No parse is available
            return null;
        }
        return parsed.toArray(new RuleParse[0]);
    }

    /**
     * this method starts the recursively of the parse.
     *
     * @param currentNode the start node
     * @param input       the text
     * @return RuleComponent
     */
    public final RuleComponent parse(GrammarNode currentNode, String input) {
        String[] in = input.split(" ");
        position = 0;
        grammarElements = new Stack<>();
        if (parse(currentNode, in) && !grammarElements.empty()) {
            return (RuleComponent) grammarElements.pop();
        } else {
            return null;
        }
    }

    /**
     * Creates a grammar graph, from a rule grammar and a start rule name.
     *
     * @param grammar       the rule grammar
     * @param startRuleName the start rule name
     * @return GrammarGraph the graph that represents this grammar
     */
    public final GrammarGraph buildGrammarGraph(RuleGrammar grammar, String startRuleName) {
        RuleComponent startRuleComponent = grammar.getRule(startRuleName).getRuleComponent();
        RuleReference startRule = new RuleReference(startRuleName);

        GrammarNode startNode = new GrammarNode(false, GrammarNode.START_REFERENCE, startRule);
        GrammarNode endNode = new GrammarNode(true, GrammarNode.END_REFERENCE);
        GrammarGraph newNodes = buildGrammarGraph(grammar, startRuleComponent);

        startNode.addArc(newNodes.getStartNode());
        newNodes.getEndNode().addArc(endNode);

        return new GrammarGraph(startNode, endNode);
    }

    /**
     * this method processes recursively a rule component. It returns the
     * GrammarGraph
     *
     * @param grammar   the rule grammar
     * @param component the rule component
     * @return GrammarGraph
     */
    private GrammarGraph buildGrammarGraph(RuleGrammar grammar, RuleComponent component) {
        if (component instanceof RuleReference) {
            return buildGrammarGraph(grammar, (RuleReference) component);
        } else if (component instanceof RuleToken) {
            return buildGrammarGraph(grammar, (RuleToken) component);
        } else if (component instanceof RuleAlternatives) {
            return buildGrammarGraph(grammar, (RuleAlternatives) component);
        } else if (component instanceof RuleSequence) {
            return buildGrammarGraph(grammar, (RuleSequence) component);
        } else if (component instanceof RuleTag) {
            return buildGrammarGraph(grammar, (RuleTag) component);
        } else if (component instanceof RuleCount) {
            return buildGrammarGraph(grammar, (RuleCount) component);
//        } else if (r instanceof RuleSpecial) {
//            //____________________________________
//            return buildGrammarGraph(rg, (RuleSpecial) r);
        } else {
            return null;
        }
    }

    /**
     * Creates a sub-graph that represents a rule reference.
     *
     * @param grammar   the rule grammar
     * @param reference the rule reference
     * @return GrammarGraph
     */
    private GrammarGraph buildGrammarGraph(RuleGrammar grammar, RuleReference reference) {
        RuleGrammar currentRuleGrammar = grammar;
        GrammarNode startNode = new GrammarNode(false, GrammarNode.START_REFERENCE, reference);
        GrammarNode endNode = new GrammarNode(false, GrammarNode.END_REFERENCE);

        String simpleName = reference.getRuleName();
        RuleComponent ruleref;
        if (currentRuleGrammar.getRule(simpleName) == null) {
            ruleref = null;
        } else {
            ruleref = currentRuleGrammar.getRule(simpleName).getRuleComponent();
        }
        if (ruleref == null) {
            String gname = reference.getGrammarReference();
            if ((gname != null) && (!gname.isEmpty())) {
                RuleGrammar rg1 = null;
                try {
                    rg1 = (RuleGrammar) grammarManager.getGrammar(gname);
                } catch (EngineStateException ex) {
                    logger.log(Level.ERROR, ex.getMessage(), ex);
                }
                if (rg1 != null) {
                    ruleref = rg1.getRule(simpleName).getRuleComponent();
                    currentRuleGrammar = rg1;
                } else {
                    logger.log(Level.ERROR, "ERROR: UNKNOWN GRAMMAR " + gname);
                }
            }
            if (ruleref == null) {
                logger.log(Level.ERROR, "ERROR: UNKNOWN RULE NAME " + reference.getRuleName() + " " + reference);
                return null;
            }
        }

        GrammarGraph rc = buildGrammarGraph(currentRuleGrammar, ruleref);
        if (rc == null) {
            return null;
        }
        startNode.addArc(rc.getStartNode());
        rc.getEndNode().addArc(endNode);

        return new GrammarGraph(startNode, endNode);
    }

    /**
     * Creates a sub-graph that represents a rule token.
     *
     * @param grammar the rule grammar
     * @param token   the rule token
     * @return GrammarGraph
     */
    private static GrammarGraph buildGrammarGraph(RuleGrammar grammar, RuleToken token) {
        GrammarNode startNode = new GrammarNode(false, GrammarNode.TOKEN, token);
        return new GrammarGraph(startNode, startNode);
    }

    /**
     * Creates a sub-graph that represents a rule alternative.
     *
     * @param grammar      the rule grammar
     * @param alternatives the rule alternative
     * @return GrammarGraph
     */
    private GrammarGraph buildGrammarGraph(RuleGrammar grammar, RuleAlternatives alternatives) {
        GrammarNode startNode = new GrammarNode(false, GrammarNode.START_ALTERNATIVE, alternatives);
        GrammarNode endNode = new GrammarNode(false, GrammarNode.END_ALTERNATIVE);

        RuleComponent[] rules = alternatives.getRuleComponents();
        int[] weights = alternatives.getWeights();
        // TODO implement it in jsapi2/srgsrulegrammarparser
        //  normalizeWeights(weights);

        // expand each alternative, and connect them in parallel
        for (int i = 0; i < rules.length; i++) {
            RuleComponent rule = rules[i];
            float weight = 0.0f;
            if (weights != null) {
                weight = weights[i];
            }
            GrammarGraph newNodes = buildGrammarGraph(grammar, rule);

            if (newNodes.getStartNode() != null) {
                startNode.addArc(newNodes.getStartNode()); // TODO ??weight?
                newNodes.getEndNode().addArc(endNode);
            }
        }

        return new GrammarGraph(startNode, endNode);
    }

    /**
     * Creates a sub-graph that represents a rule sequence.
     *
     * @param rg the rule grammar
     * @param r  the rule sequence
     * @return GrammarGraph
     */
    private GrammarGraph buildGrammarGraph(RuleGrammar rg, RuleSequence r) {
        GrammarNode startNode = new GrammarNode(false, GrammarNode.START_SEQUENCE, r);
        GrammarNode endNode = new GrammarNode(false, GrammarNode.END_SEQUENCE);

        RuleComponent[] rules = r.getRuleComponents();

        GrammarNode lastGrammarNode = null;

        // expand and connect each rule in the sequence serially
        for (int i = 0; i < rules.length; i++) {
            RuleComponent rule = rules[i];
            GrammarGraph newNodes = buildGrammarGraph(rg, rule);

            // first node
            if (i == 0) {
                startNode.addArc(newNodes.getStartNode());
            }

            // last node
            if (i == (rules.length - 1)) {
                newNodes.getEndNode().addArc(endNode);
            }

            if (i > 0) {
                lastGrammarNode.addArc(newNodes.getStartNode());
            }
            lastGrammarNode = newNodes.getEndNode();
        }

        return new GrammarGraph(startNode, endNode);
    }

    /**
     * Creates a sub-graph that represents a rule tag.
     *
     * @param rg the rule grammar
     * @param r  the rule tag
     * @return GrammarGraph
     */
    private static GrammarGraph buildGrammarGraph(RuleGrammar rg, RuleTag r) {
        GrammarNode startNode = new GrammarNode(false, GrammarNode.TAG, r);
        return new GrammarGraph(startNode, startNode);
    }

    /**
     * Creates a sub-graph that represents a rule count.
     *
     * @param rg the rule grammar
     * @param r  the rule count
     * @return GrammarGraph
     */
    private GrammarGraph buildGrammarGraph(RuleGrammar rg, RuleCount r) {
        GrammarNode startNode = new GrammarNode(false, GrammarNode.START_COUNT, r);
        GrammarNode endNode = new GrammarNode(false, GrammarNode.END_COUNT);

        int minRepeat = r.getRepeatMin();
        int maxRepeat = r.getRepeatMax();
        GrammarGraph newNodes = buildGrammarGraph(rg, r.getRuleComponent());
        int countNodes = 1;

        GrammarGraph lastNode = newNodes;

        if (minRepeat > 1) {
            GrammarGraph tmpGraph;
            while (countNodes < minRepeat) {
                countNodes++;
                // TODO how can i copy a graph
                tmpGraph = buildGrammarGraph(rg, r.getRuleComponent());
                lastNode = tmpGraph;
                newNodes.getEndNode().addArc(tmpGraph.getStartNode());
                // TODO review this
                newNodes.setEndNode(tmpGraph.getEndNode());
            }
        }

        if (maxRepeat != RuleCount.REPEAT_INDEFINITELY) {
            GrammarGraph tmpGraph;
            List<GrammarNode> v = new java.util.ArrayList<>();
            lastNode = newNodes;
            while (countNodes < maxRepeat) {
                ++countNodes;
                // TODO how can i copy a graph
                tmpGraph = buildGrammarGraph(rg, r.getRuleComponent());
                v.add(lastNode.getEndNode());
                newNodes.getEndNode().addArc(tmpGraph.getStartNode());
                // TODO review this
                newNodes.setEndNode(tmpGraph.getEndNode());
                lastNode = tmpGraph;
            }

            // set this nodes optional
            for (GrammarNode g : v) {
                g.addArc(endNode);
            }
        }

        startNode.addArc(newNodes.getStartNode());
        newNodes.getEndNode().addArc(endNode);

        // if this is optional, add a bypass arc
        if (minRepeat == 0) {
            startNode.addArc(endNode);
        }

        // if this can possibly occur indefinitely add a loopback
        if (maxRepeat == RuleCount.REPEAT_INDEFINITELY) {
            if (lastNode != null) {
                newNodes.getEndNode().addArc(lastNode.getStartNode());
            }
        }
        return new GrammarGraph(startNode, endNode);
    }

    /**
     * This method parses a tag.
     *
     * @param currentNode the current node of this grammar
     * @param input       the set of tokens
     * @return <code>true</code> if these nodes accepts the current (iPos) input
     */
    private boolean parseTag(GrammarNode currentNode, String[] input) {
        if (parse(currentNode.getArcs().get(0).getGrammarNode(), input)) {
            RuleComponent tag = currentNode.getRuleComponent();
            grammarElements.push(tag);
            return true;
        }
        return false;
    }

    /**
     * This method parses a token.
     *
     * @param currentNode the current node of this grammar
     * @param input       the set of tokens
     * @return <code>true</code> if these nodes accepts the current (iPos) input
     */
    private boolean parseToken(GrammarNode currentNode, String[] input) {
        if (position >= input.length) {
            return false;
        }
        RuleToken token = (RuleToken) currentNode.getRuleComponent();
        String text = token.getText().toLowerCase();
        if (text.equals(input[position]) || (input[position].equals("%")) || (input[position].equals("*"))) {
            position++;
            if (parse(currentNode.getArcs().get(0).getGrammarNode(), input)) {
                grammarElements.push(new RuleToken(text));
                return true;
            } else {
                return false;
            }
        } else {
            if (text.indexOf(' ') < 0) {
                return false;
            }
            if (!text.startsWith(input[position])) {
                return false;
            }
            String[] ta = text.split(" ");
            int j = 0;
            StringBuilder buffer = new StringBuilder();
            // this while is necessary because a token
            // can contain more than a single word
            while (j < ta.length) {
                if (position >= input.length) {
                    return false;
                }
                if (!ta[j].equals(input[position])) {
                    return false;
                }
                if (j > 0) {
                    buffer.append(" ");
                }
                buffer.append(ta[j]);
                position++;
                j++;
            }
            if (parse(currentNode.getArcs().get(0).getGrammarNode(), input)) {
                grammarElements.push(new RuleToken(buffer.toString()));
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * This method constructs an RuleSequence for the rule parser.
     */
    private void posParseStartSequence() {
        List<RuleComponent> arSeq = new java.util.ArrayList<>();
        while (true) {
            if (grammarElements.empty()) {
                break;
            }
            Object topElement = grammarElements.pop();
            if (topElement instanceof GrammarNode
                    && ((GrammarNode) topElement).getNodeType() == GrammarNode.END_SEQUENCE) {
                RuleComponent[] components = new RuleComponent[arSeq.size()];
                arSeq.toArray(components);
                grammarElements.push(new RuleSequence(components));
                break;
            } else if (topElement instanceof RuleComponent) {
                arSeq.add((RuleComponent) topElement);
            }
        }
    }

    /**
     * This method constructs an RuleCount for the rule parser.
     *
     * @param currentNode the current node
     */
    private void posParseStartCount(GrammarNode currentNode) {
        List<RuleComponent> ruleComponents = new java.util.ArrayList<>();
        int count = 0;
        while (true) {
            if (grammarElements.empty()) {
                break;
            }
            Object topElement = grammarElements.pop();
            if (topElement instanceof GrammarNode
                    && ((GrammarNode) topElement).getNodeType() == GrammarNode.END_COUNT) {
                RuleComponent[] copy = new RuleComponent[ruleComponents.size()];
                ruleComponents.toArray(copy);

                int repeatProb = ((RuleCount) currentNode.getRuleComponent()).getRepeatProbability();
                if (repeatProb != RuleCount.MAX_PROBABILITY) {
                    grammarElements.push(new RuleCount(new RuleSequence(copy), count, count, repeatProb));
                } else {
                    grammarElements.push(new RuleCount(new RuleSequence(copy), count, count));
                }
                break;
            } else if (topElement instanceof RuleComponent) {
                ruleComponents.add((RuleComponent) topElement);
                count++;
            }
        }
    }

    /**
     * This method constructs an RuleReference for the rule parser.
     *
     * @param currentNode the current node
     */
    private void posParseStartReference(GrammarNode currentNode) {
        RuleComponent reference = null;
        while (true) {
            if (grammarElements.empty()) {
                break;
            }
            Object topElement = grammarElements.pop();
            if (topElement instanceof GrammarNode
                    && ((GrammarNode) topElement).getNodeType() == GrammarNode.END_REFERENCE) {
                String ruleName = ((RuleReference) currentNode.getRuleComponent()).getRuleName();
                grammarElements.push(new RuleParse(new RuleReference(ruleName), reference));
                break;
            } else if (topElement instanceof RuleComponent) {
                if (reference == null) {
                    reference = (RuleComponent) topElement;
                }
            }
        }
    }

    /**
     * This method constructs an RuleAlternative for the rule parser.
     */
    private void posParseStartAlternative() {
        List<RuleComponent> alternatives = new java.util.ArrayList<>();
        while (true) {
            if (grammarElements.empty()) {
                break;
            }
            Object topElement = grammarElements.pop();
            if (topElement instanceof GrammarNode
                    && ((GrammarNode) topElement).getNodeType() == GrammarNode.END_ALTERNATIVE) {
                RuleComponent[] component = new RuleComponent[alternatives.size()];
                alternatives.toArray(component);
                grammarElements.add(new RuleAlternatives(component));
                break;
            } else if (topElement instanceof RuleComponent) {
                alternatives.add((RuleComponent) topElement);
            }
        }
    }

    /**
     * This method parses recursively a grammar graph, to check if a set of
     * tokens belongs at this grammar.
     *
     * @param currentNode the current node of this grammar
     * @param input       the set of tokens
     * @return <code>true</code> if starting in the current node exists a way
     * in the graph to ends in a final node.
     */
    private boolean parse(GrammarNode currentNode, String[] input) {
        int currentPosition = position;
        if (currentNode.isFinalNode()) {
            if (position == input.length) {
                grammarElements.push(currentNode);
                return true;
            } else {
                return false;
            }
        }

        int type = currentNode.getNodeType();
        if (type == GrammarNode.TOKEN) {
            return parseToken(currentNode, input);
        } else if (type == GrammarNode.TAG) {
            return parseTag(currentNode, input);
        } else {
            for (GrammarArc arc : currentNode.getArcs()) {
                GrammarNode nextNode = arc.getGrammarNode();
                if (parse(nextNode, input)) {
                    switch (type) {
                    case GrammarNode.END_ALTERNATIVE:
                    case GrammarNode.END_COUNT:
                    case GrammarNode.END_REFERENCE:
                    case GrammarNode.END_SEQUENCE:
                        grammarElements.push(currentNode);
                        break;
                    case GrammarNode.START_SEQUENCE:
                        posParseStartSequence();
                        break;
                    case GrammarNode.START_COUNT:
                        posParseStartCount(currentNode);
                        break;
                    case GrammarNode.START_REFERENCE:
                        posParseStartReference(currentNode);
                        break;
                    case GrammarNode.START_ALTERNATIVE:
                        posParseStartAlternative();
                        break;
                    default:
                        break;
                    }
                    return true;
                }
            }
            position = currentPosition;
            return false;
        }
    }
}
