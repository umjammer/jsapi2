/**
 *
 */

package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test case for {@link javax.speech.recognition.RuleSpecial}.
 *
 * @author Dirk Schnelle
 */
public class RuleSpecialTest {
    /**
     * Test method for {@link javax.speech.recognition.RuleSpecial#toString()}.
     */
    @Test
    void testToString() {
        assertEquals("<ruleref special=\"GARBAGE\"/>", RuleSpecial.GARBAGE
                .toString());

        assertEquals("<ruleref special=\"VOID\"/>", RuleSpecial.VOID.toString());

        assertEquals("<ruleref special=\"NULL\"/>", RuleSpecial.NULL.toString());
    }
}
