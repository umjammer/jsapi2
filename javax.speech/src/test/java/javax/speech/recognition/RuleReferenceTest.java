/**
 *
 */

package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Test case for {@link javax.speech.recognition.RuleReference}.
 *
 * @author Dirk Schnelle
 */
public class RuleReferenceTest {
    /**
     * Test method for {@link javax.speech.recognition.RuleReference}
     */
    @Test
    void testNewRuleReference() {
        RuleReference reference1 = new RuleReference("ruleNameOne");
        assertEquals("ruleNameOne", reference1.getRuleName());

        Exception failure = null;
        try {
            RuleReference reference2 = new RuleReference(null);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            RuleReference reference3 = new RuleReference("#ruleNameThree");
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        RuleReference reference4 = new RuleReference(
                "grammarReferenceFour", "ruleNameFour");
        assertEquals("ruleNameFour", reference4.getRuleName());

        failure = null;
        try {
            RuleReference reference5 = new RuleReference(null,
                    "ruleNameFive");
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            RuleReference reference6 = new RuleReference(
                    "file://grammarReference6", null);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleReference#toString()}.
     */
    @Test
    void testToString() {
        RuleReference reference1 = new RuleReference("ruleNameOne");
        assertEquals("<ruleref uri=\"#ruleNameOne\"/>", reference1.toString());

        RuleReference reference2 = new RuleReference(
                "grammarReferenceTwo", "ruleNameTwo");
        assertEquals("<ruleref uri=\"grammarReferenceTwo#ruleNameTwo\"/>",
                reference2.toString());

        RuleReference reference3 = new RuleReference(
                "grammarReferenceThree", "ruleNameThree", "mediaTypeThree");
        assertEquals("<ruleref uri=\"grammarReferenceThree#ruleNameThree\" "
                + "type=\"mediaTypeThree\"/>", reference3.toString());

        RuleReference reference4 = new RuleReference(
                "grammarReferenceFour", "ruleNameFour", "mediaTypeFour");
        assertEquals(
                "<ruleref uri=\"grammarReferenceFour#ruleNameFour\" type=\"mediaTypeFour\"/>",
                reference4.toString());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleReference#getGrammarReference()}.
     */
    @Test
    void testGetGrammarReference() {
        RuleReference reference1 = new RuleReference("ruleNameOne");
        assertNull(reference1.getGrammarReference());

        RuleReference reference2 = new RuleReference(
                "grammarReferenceTwo", "ruleNameTwo");
        assertEquals("grammarReferenceTwo", reference2.getGrammarReference());

        RuleReference reference3 = new RuleReference(
                "grammarReferenceThree", "ruleNameThree", "mediaTypeThree");
        assertEquals("grammarReferenceThree", reference3.getGrammarReference());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleReference#getMediaType()}.
     */
    @Test
    void testGetMediaType() {
        RuleReference reference1 = new RuleReference("ruleNameOne");
        assertEquals("application/srgs+xml", reference1.getMediaType());

        RuleReference reference2 = new RuleReference(
                "grammarReferenceTwo", "ruleNameTwo");
        assertEquals("application/srgs+xml", reference1.getMediaType());

        RuleReference reference3 = new RuleReference(
                "grammarReferenceThree", "ruleNameThree", "application/x-jsgf");
        assertEquals("application/x-jsgf", reference3.getMediaType());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleReference#getRuleName()}.
     */
    @Test
    void testGetRuleName() {
        RuleReference reference1 = new RuleReference("ruleNameOne");
        assertEquals("ruleNameOne", reference1.getRuleName());

        RuleReference reference2 = new RuleReference(
                "grammarReferenceTwo", "ruleNameTwo");
        assertEquals("ruleNameTwo", reference2.getRuleName());

        RuleReference reference3 = new RuleReference(
                "grammarReferenceThree", "ruleNameThree", "mediaTypeThree");
        assertEquals("ruleNameThree", reference3.getRuleName());
    }

}
