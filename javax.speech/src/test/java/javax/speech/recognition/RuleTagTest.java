/**
 * 
 */
package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test case for {@link javax.speech.recognition.RuleTag}.
 * 
 * @author Dirk Schnelle
 */
public class RuleTagTest {
    /**
     * Test method for
     * {@link javax.speech.recognition.RuleToken#RuleToken(String)}.
     */
    @Test
    void testNewRuleToken() {
        RuleTag tag1 = new RuleTag("test");
        assertEquals("test", tag1.getTag());

        RuleTag tag2 = new RuleTag(42);
        assertEquals(42, tag2.getTag());
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleTag#toString()}.
     */
    @Test
    void testToString() {
        RuleTag tag = new RuleTag("CL");
        assertEquals("<tag>CL</tag>", tag.toString());

        Exception failure = null;
        RuleTag tag2 = new RuleTag(null);
        try {
            tag2.toString();
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleTag#getTag()}.
     */
    @Test
    void testGetTag() {
        RuleTag tag = new RuleTag("CL");
        assertEquals("CL", tag.getTag());

        RuleTag tag2 = new RuleTag(null);
        assertNull(tag2.getTag());
    }
}
