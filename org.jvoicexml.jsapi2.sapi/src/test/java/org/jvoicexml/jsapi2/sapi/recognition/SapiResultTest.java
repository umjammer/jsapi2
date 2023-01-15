/**
 * 
 */
package org.jvoicexml.jsapi2.sapi.recognition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Result;
import javax.speech.recognition.RuleGrammar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.recognition.BaseGrammarManager;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test methods for {@link SapiResult}.
 * 
 * @author Dirk Schnelle-Walka
 * 
 */
@EnabledOnOs(OS.WINDOWS)
public class SapiResultTest {

    private String readSml(final String resource) throws IOException {
        final InputStream in = SapiResultTest.class
                .getResourceAsStream(resource);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        do {
            int read = in.read(buffer);
            if (read < 0) {
                return out.toString();
            }
            out.write(buffer, 0, read);
            ;
        } while (true);
    }

    @Test
    void testSetSmlNoTags() throws Exception {
        final GrammarManager manager = new BaseGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "root");
        final SapiResult result = new SapiResult(grammar);
        result.setResultState(Result.ACCEPTED);
        final String sml = readSml("sml-simple.xml");
        result.setSml(sml);
        final Object[] tags = result.getTags(0);
        assertEquals(0, tags.length);
    }

    @Test
    void testSetSmlTag() throws Exception {
        final GrammarManager manager = new BaseGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "root");
        final SapiResult result = new SapiResult(grammar);
        result.setResultState(Result.ACCEPTED);
        final String sml = readSml("sml-tag.xml");
        result.setSml(sml);
        final Object[] tags = result.getTags(0);
        assertEquals(1, tags.length);
        assertEquals("Projectmanager", tags[0]);
    }

    @Test
    void testSetSmlMultipleTags() throws Exception {
        final GrammarManager manager = new BaseGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "root");
        final SapiResult result = new SapiResult(grammar);
        result.setResultState(Result.ACCEPTED);
        final String sml = readSml("sml-multiple-tags.xml");
        result.setSml(sml);
        final Object[] tags = result.getTags(0);
        assertEquals(2, tags.length);
        assertEquals("out.greet=\"general\";", tags[0]);
        assertEquals("out.who=\"Projectmanager\";", tags[1]);
    }
    @Test
    void testSetSmlCompound() throws Exception {
        final GrammarManager manager = new BaseGrammarManager();
        final RuleGrammar grammar = manager.createRuleGrammar("grammar:test",
                "root");
        final SapiResult result = new SapiResult(grammar);
        result.setResultState(Result.ACCEPTED);
        final String sml = readSml("sml-compound.xml");
        result.setSml(sml);
        final Object[] tags = result.getTags(0);
        for (Object o : tags) {
            System.out.println(o);
        }
        assertEquals(4, tags.length);
        assertEquals("out = new Object();", tags[0]);
        assertEquals("out.order = new Object();", tags[1]);
        assertEquals("out.order.size=\"small\";", tags[2]);
        assertEquals("out.order.topping=\"salami\";", tags[3]);
    }
}
