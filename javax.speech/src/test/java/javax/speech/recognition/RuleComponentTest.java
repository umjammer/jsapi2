/**
 * 
 */
package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test case for {@link javax.speech.recognition.RuleComponent}.
 * 
 * @author Dirk Schnelle
 */
public class RuleComponentTest {

    /**
     * Test method for {@link javax.speech.recognition.RuleComponent#toString()}.
     */
    @Test
    void testToString() {
        final RuleComponent component = new RuleComponent();
        assertNull(component.toString());
    }

}
