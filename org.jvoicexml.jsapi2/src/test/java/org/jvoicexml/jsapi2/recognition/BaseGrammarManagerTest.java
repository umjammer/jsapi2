/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jsapi/code/trunk/org.jvoicexml.jsapi2/unittests/org/jvoicexml/jsapi2/ThreadSpeechEventExecutorTest.java $
 * Version: $LastChangedRevision: 782 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2.recognition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.recognition.MockRecognizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test cases for the {@link GrammarManager}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class BaseGrammarManagerTest {

    /** The related recognizer. */
    private Recognizer recognizer;

    /** The object to test. */
    private GrammarManager manager;

    @BeforeEach
    public void setUp() throws Exception {
        recognizer = new MockRecognizer();
        manager = recognizer.getGrammarManager();
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.recognition.BaseGrammarManager#createRuleGrammar(java.lang.String, java.lang.String)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testCreateRuleGrammarStringString() throws Exception {
        final String name = "test";
        RuleGrammar grammar =
                manager.createRuleGrammar(name, name);
        RuleToken token = new RuleToken("hello world");
        Rule rule = new Rule("test", token, Rule.PUBLIC);
        grammar.addRule(rule);
        recognizer.processGrammars();
        Grammar retrievedGrammar = manager.getGrammar(name);
        assertNotNull(retrievedGrammar);
        assertEquals(grammar.toString(), retrievedGrammar.toString());
    }

    /**
     * Test method for {@link GrammarManager#loadGrammar(String, String, Reader)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testCreateRuleGrammarReader() throws Exception {
        InputStream in =
                BaseGrammarManagerTest.class.getResourceAsStream(
                        "pizza-de.xml");
        Reader reader = new InputStreamReader(in);
        Grammar grammar = manager.loadGrammar("test",
                "application/srgs+xml", reader);
        assertInstanceOf(RuleGrammar.class, grammar);
    }
}
