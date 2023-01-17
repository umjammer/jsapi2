/**
 * 
 */
package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test case for {@link javax.speech.recognition.RuleAlternatives}.
 * 
 * @author Dirk Schnelle
 */
public class RuleAlternativesTest {

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleAlternatives#toString()}.
     */
    @Test
    void testToString() {
        RuleComponent[] components1 =
                new RuleComponent[] {};
        RuleAlternatives alternatives1 = new RuleAlternatives(components1);
        String str1 = alternatives1.toString();
        assertEquals(RuleSpecial.VOID.toString(), str1);

        RuleComponent[] components2 = new RuleComponent[0];
        RuleAlternatives alternatives2 = new RuleAlternatives(components2);
        String str2 = alternatives2.toString();
        assertEquals(RuleSpecial.VOID.toString(), str2);

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag") };
        RuleAlternatives alternatives3 = new RuleAlternatives(components3);
        String str3 = alternatives3.toString();
        assertEquals("<one-of><item>token</item><item><tag>tag</tag></item>"
                + "</one-of>", str3);

        String[] tokens4 = null;
        RuleAlternatives alternatives4 = new RuleAlternatives(tokens4);
        String str4 = alternatives4.toString();
        assertEquals(RuleSpecial.VOID.toString(), str4);

        String[] tokens5 = new String[0];
        RuleAlternatives alternatives5 = new RuleAlternatives(tokens5);
        String str5 = alternatives5.toString();
        assertEquals(RuleSpecial.VOID.toString(), str5);

        String[] tokens6 = new String[] { "tokenOne", "tokenTwo",
                "tokenThree" };
        RuleAlternatives alternatives6 = new RuleAlternatives(tokens6);
        String str6 = alternatives6.toString();
        assertEquals("<one-of><item>tokenOne</item><item>tokenTwo</item>"
                + "<item>tokenThree</item></one-of>", str6);

        RuleComponent[] components7 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights7 = new int[] { RuleAlternatives.MIN_WEIGHT,
                RuleAlternatives.NORM_WEIGHT, RuleAlternatives.MAX_WEIGHT };
        RuleAlternatives alternatives7 = new RuleAlternatives(
                components7, weights7);
        String str7 = alternatives7.toString();
        assertEquals("<one-of><item weight=\"" + RuleAlternatives.MIN_WEIGHT
                + "\">token</item>"
                + "<item><tag>tag</tag></item>"
                + "<item weight=\""
                +  RuleAlternatives.MAX_WEIGHT 
                + "\">otherToken</item></one-of>", str7);

        RuleComponent[] components8 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights8 = null;
        RuleAlternatives alternatives8 = new RuleAlternatives(
                components8, weights8);
        String str8 = alternatives8.toString();
        assertEquals("<one-of><item>token</item>"
                + "<item><tag>tag</tag></item>"
                + "<item>otherToken</item></one-of>", str8);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleAlternatives#RuleAlternatives(javax.speech.recognition.RuleComponent[], int[])}.
     */
    @Test
    void testRuleAlternativesRuleComponentArrayIntArray() {
        RuleComponent[] components1 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights1 = new int[] { RuleAlternatives.MIN_WEIGHT,
                RuleAlternatives.NORM_WEIGHT, RuleAlternatives.MAX_WEIGHT };
        RuleAlternatives alternatives1 = new RuleAlternatives(
                components1, weights1);
        assertNotNull(alternatives1);

        Exception failure = null;

        RuleComponent[] components2 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights2 = new int[] {};
        try {
            RuleAlternatives alternatives2 = new RuleAlternatives(
                    components2, weights2);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights3 = null;
        RuleAlternatives alternatives3 = new RuleAlternatives(
                components3, weights3);
        assertNotNull(alternatives3);

        RuleComponent[] components4 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights4 = new int[] { RuleAlternatives.NORM_WEIGHT };

        try {
            RuleAlternatives alternatives4 = new RuleAlternatives(
                    components4, weights4);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleAlternatives#getRuleComponents()}.
     */
    @Test
    void testGetRuleComponents() {
        RuleComponent[] components1 = new RuleComponent[] {};
        RuleAlternatives alternatives1 = new RuleAlternatives(components1);
        assertEquals(components1,
                alternatives1.getRuleComponents());

        RuleComponent[] components2 = new RuleComponent[0];
        RuleAlternatives alternatives2 = new RuleAlternatives(components2);
        RuleComponent[] actcomponents2 = alternatives2
                .getRuleComponents();
        assertEquals(0, actcomponents2.length);

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag") };
        RuleAlternatives alternatives3 = new RuleAlternatives(components3);
        assertEquals(components3, alternatives3.getRuleComponents());

        String[] tokens4 = null;
        RuleAlternatives alternatives4 = new RuleAlternatives(tokens4);
        assertNull(alternatives4.getRuleComponents());

        String[] tokens5 = new String[0];
        RuleAlternatives alternatives5 = new RuleAlternatives(tokens5);
        RuleComponent[] components5 = alternatives5.getRuleComponents();
        assertEquals(0, components5.length);

        String[] tokens6 = new String[] { "tokenOne", "tokenTwo",
                "tokenThree" };
        RuleAlternatives alternatives6 = new RuleAlternatives(tokens6);
        RuleComponent[] components6 = alternatives6.getRuleComponents();
        assertEquals(tokens6.length, components6.length);
        for (int i = 0; i < tokens6.length; i++) {
            String token = tokens6[i];
            RuleComponent component = components6[i];
            assertEquals(new RuleToken(token), component);
        }

        RuleComponent[] components7 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights7 = new int[] { RuleAlternatives.MIN_WEIGHT,
                RuleAlternatives.NORM_WEIGHT, RuleAlternatives.MAX_WEIGHT };
        RuleAlternatives alternatives7 = new RuleAlternatives(
                components7, weights7);
        assertEquals(components7, alternatives7.getRuleComponents());

        RuleComponent[] components8 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights8 = null;
        RuleAlternatives alternatives8 = new RuleAlternatives(
                components8, weights8);
        assertEquals(components8, alternatives8.getRuleComponents());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.RuleAlternatives#getWeights()}.
     */
    @Test
    void testGetWeights() {
        RuleComponent[] components1 = new RuleComponent[] {};
        RuleAlternatives alternatives1 = new RuleAlternatives(components1);
        assertNull(alternatives1.getWeights());

        RuleComponent[] components2 = new RuleComponent[0];
        RuleAlternatives alternatives2 = new RuleAlternatives(components2);
        assertNull(alternatives2.getWeights());

        RuleComponent[] components3 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag") };
        RuleAlternatives alternatives3 = new RuleAlternatives(components3);
        assertNull(alternatives3.getWeights());

        String[] tokens4 = null;
        RuleAlternatives alternatives4 = new RuleAlternatives(tokens4);
        assertNull(alternatives4.getWeights());

        String[] tokens5 = new String[0];
        RuleAlternatives alternatives5 = new RuleAlternatives(tokens5);
        assertNull(alternatives5.getWeights());

        String[] tokens6 = new String[] { "tokenOne", "tokenTwo",
                "tokenThree" };
        RuleAlternatives alternatives6 = new RuleAlternatives(tokens6);
        assertNull(alternatives6.getWeights());

        RuleComponent[] components7 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights7 = new int[] { RuleAlternatives.MIN_WEIGHT,
                RuleAlternatives.NORM_WEIGHT, RuleAlternatives.MAX_WEIGHT };
        RuleAlternatives alternatives7 = new RuleAlternatives(
                components7, weights7);
        assertEquals(weights7, alternatives7.getWeights());

        RuleComponent[] components8 = new RuleComponent[] {
                new RuleToken("token"), new RuleTag("tag"),
                new RuleToken("otherToken") };
        int[] weights8 = null;
        RuleAlternatives alternatives8 = new RuleAlternatives(
                components8, weights8);
        int[] actweights8 = alternatives8.getWeights();
        assertEquals(components8.length, actweights8.length);
        int weight8 = actweights8[0];
        for (int weight : actweights8) {
            assertEquals(weight8, weight);
        }
    }
}
