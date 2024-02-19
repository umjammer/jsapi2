/**
 *
 */

package javax.speech.recognition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test case for {@link javax.speech.recognition.RuleCount}.
 *
 * @author Dirk Schnelle
 */
public class RuleCountTest {
    private RuleComponent component;

    @BeforeEach
    protected void setUp() throws Exception {
        component = new RuleToken("token");
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleCount#toString()}.
     */
    @Test
    void testToString() {
        RuleCount count1 = new RuleCount(component, 42);
        String str1 = count1.toString();
        assertEquals("<item repeat=\"42-\">token</item>", str1);

        RuleCount count2 = new RuleCount(component, 43, 44);
        String str2 = count2.toString();
        assertEquals("<item repeat=\"43-44\">token</item>", str2);

        RuleCount count3 = new RuleCount(component, 45, 45);
        String str3 = count3.toString();
        assertEquals("<item repeat=\"45\">token</item>", str3);

        RuleCount count4 = new RuleCount(component, 45, 46,
                RuleCount.MAX_PROBABILITY);
        String str4 = count4.toString();
        assertEquals("<item repeat=\"45-46\" repeat-prop=\""
                + RuleCount.MAX_PROBABILITY + "\">token</item>", str4);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleCount#RuleCount(javax.speech.recognition.RuleComponent, int)}.
     */
    @Test
    void testRuleCountRuleComponentInt() {
        RuleCount count1 = new RuleCount(component, 42);

        Exception failure = null;
        try {
            RuleCount count2 = new RuleCount(component, -34);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleCount#RuleCount(javax.speech.recognition.RuleComponent, int, int)}.
     */
    @Test
    void testRuleCountRuleComponentIntInt() {
        RuleCount count1 = new RuleCount(component, 42, 43);

        RuleCount count2 = new RuleCount(component, 42, 42);

        Exception failure = null;
        try {
            RuleCount count3 = new RuleCount(component, 44, 42);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            RuleCount count4 = new RuleCount(component, -44, 42);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleCount#RuleCount(javax.speech.recognition.RuleComponent, int, int, int)}.
     */
    @Test
    void testRuleCountRuleComponentIntIntInt() {
        RuleCount count1 = new RuleCount(component, 42, 43, 0);

        RuleCount count2 = new RuleCount(component, 42, 42,
                RuleCount.MAX_PROBABILITY);

        Exception failure = null;
        try {
            RuleCount count3 = new RuleCount(component, 44, 42,
                    RuleCount.MAX_PROBABILITY);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            RuleCount count4 = new RuleCount(component, -44, 42,
                    RuleCount.MAX_PROBABILITY);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            RuleCount count4 = new RuleCount(component, -44, 42, -2);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleCount#getRepeatMax()}.
     */
    @Test
    void testGetRepeatMax() {
        RuleCount count1 = new RuleCount(component, 42);
        assertEquals(RuleCount.REPEAT_INDEFINITELY, count1.getRepeatMax());

        RuleCount count2 = new RuleCount(component, 43, 44);
        assertEquals(44, count2.getRepeatMax());

        RuleCount count3 = new RuleCount(component, 45, 46,
                RuleCount.MAX_PROBABILITY);
        assertEquals(46, count3.getRepeatMax());
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleCount#getRepeatMin()}.
     */
    @Test
    void testGetRepeatMin() {
        RuleCount count1 = new RuleCount(component, 42);
        assertEquals(42, count1.getRepeatMin());

        RuleCount count2 = new RuleCount(component, 43, 44);
        assertEquals(43, count2.getRepeatMin());

        RuleCount count3 = new RuleCount(component, 45, 46,
                RuleCount.MAX_PROBABILITY);
        assertEquals(45, count3.getRepeatMin());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleCount#getRepeatProbability()}.
     */
    @Test
    void testGetRepeatProbability() {
        RuleCount count1 = new RuleCount(component, 42);
        assertEquals(RuleCount.REPEAT_INDEFINITELY, count1
                .getRepeatProbability());

        RuleCount count2 = new RuleCount(component, 43, 44);
        assertEquals(RuleCount.REPEAT_INDEFINITELY, count2
                .getRepeatProbability());

        RuleCount count3 = new RuleCount(component, 45, 46, 57843);
        assertEquals(57843, count3.getRepeatProbability());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleCount#getRuleComponent()}.
     */
    @Test
    void testGetRuleComponent() {
        RuleCount count1 = new RuleCount(component, 42);
        assertEquals(component, count1.getRuleComponent());

        RuleCount count2 = new RuleCount(component, 43, 44);
        assertEquals(component, count2.getRuleComponent());

        RuleCount count3 = new RuleCount(component, 45, 46,
                RuleCount.MAX_PROBABILITY);
        assertEquals(component, count3.getRuleComponent());
    }
}
