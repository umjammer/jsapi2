/**
 *
 */

package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test case for {@link javax.speech.recognition.RuleSequence}.
 *
 * @author Dirk Schnelle-Walka
 */
public class RuleSequenceTest {

    /**
     * Test method for {@link javax.speech.recognition.RuleSequence#toString()}.
     */
    @Test
    void testToString() {
        RuleComponent[] components1 = new RuleComponent[0];
        RuleSequence sequence1 = new RuleSequence(components1);
        assertEquals("", sequence1.toString());

        RuleComponent[] components2 = new RuleComponent[0];
        RuleSequence sequence2 = new RuleSequence(components2);
        assertEquals("", sequence2.toString());

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag")};
        RuleSequence sequence3 = new RuleSequence(components3);
        StringBuilder str3 = new StringBuilder();
        for (RuleComponent component : components3) {
            str3.append(component.toString());
        }
        assertEquals(str3.toString(), sequence3.toString());

        String[] tokens4 = new String[0];
        RuleSequence sequence4 = new RuleSequence(tokens4);
        assertEquals("", sequence4.toString());

        String[] tokens5 = new String[0];
        RuleSequence sequence5 = new RuleSequence(tokens5);
        assertEquals("", sequence5.toString());

        String[] tokens6 = new String[] {"tokenOne", "tokenTwo",
                "tokenThree"};
        RuleSequence sequence6 = new RuleSequence(tokens6);
        StringBuilder str6 = new StringBuilder();
        for (String token : tokens6) {
            RuleComponent component = new RuleToken(token);
            str6.append(component);
        }
        assertEquals(str6.toString(), sequence6.toString());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleSequence#getRuleComponents()}.
     */
    @Test
    void testGetRuleComponents() {
        RuleComponent[] components1 = new RuleComponent[0];
        RuleSequence sequence1 = new RuleSequence(components1);
        assertEquals(components1, sequence1.getRuleComponents());

        RuleComponent[] components2 = new RuleComponent[0];
        RuleSequence sequence2 = new RuleSequence(components2);
        RuleComponent[] actcomponents2 = sequence2.getRuleComponents();
        assertEquals(0, actcomponents2.length);

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag")};
        RuleSequence sequence3 = new RuleSequence(components3);
        assertEquals(components3, sequence3.getRuleComponents());

        String[] tokens4 = new String[0];
        RuleSequence sequence4 = new RuleSequence(tokens4);
        assertEquals(0, sequence4.getRuleComponents().length);

        String[] tokens5 = new String[0];
        RuleSequence sequence5 = new RuleSequence(tokens5);
        RuleComponent[] components5 = sequence5.getRuleComponents();
        assertEquals(0, components5.length);

        String[] tokens6 = new String[] {"tokenOne", "tokenTwo",
                "tokenThree"};
        RuleSequence sequence6 = new RuleSequence(tokens6);
        RuleComponent[] components6 = sequence6.getRuleComponents();
        assertEquals(tokens6.length, components6.length);
        for (int i = 0; i < tokens6.length; i++) {
            String token = tokens6[i];
            RuleComponent component = components6[i];
            assertEquals(new RuleToken(token), component);
        }
    }
}
