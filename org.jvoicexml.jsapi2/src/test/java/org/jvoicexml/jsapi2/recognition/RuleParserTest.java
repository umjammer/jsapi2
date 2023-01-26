package org.jvoicexml.jsapi2.recognition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;

import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.recognition.MockRecognizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RuleParserTest {

    @Test
    void testParseStringGrammarManagerStringString() throws Exception {
        MockRecognizer recognizer = new MockRecognizer();
        GrammarManager manager = new BaseGrammarManager(recognizer);
        InputStream in = RuleParserTest.class
                .getResourceAsStream("pizza-de.xml");
        Reader reader = new InputStreamReader(in);
        Grammar grammar = manager.loadGrammar("test",
                "application/srgs+xml", reader);
        RuleParse parse = RuleParser.parse("eine kleine pizza mit salami",
                manager, "test", "order");
        System.out.println(parse);
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.recognition.BaseResult#getTags(int)}.
     *
     * @throws Exception test failed
     */
    @Test
    void testTags() throws Exception {
        BaseRecognizer recognizer = new MockRecognizer();
        GrammarManager manager = recognizer.getGrammarManager();
        RuleGrammar grammar =
                manager.createRuleGrammar("grammar:test", "test");
        RuleComponent[] components = new RuleComponent[] {
                new RuleToken("test"),
                new RuleTag("T")
        };
        RuleSequence sequence = new RuleSequence(components);
        Rule root = new Rule("test", sequence, Rule.PUBLIC);
        grammar.addRule(root);
        recognizer.processGrammars();
        RuleParse parse = RuleParser.parse("test",
                manager, "grammar:test", "test");
        Object[] tags = parse.getTags();
        assertNotNull(tags);
        assertEquals(1, tags.length);
        assertEquals("T", tags[0]);
    }
}
