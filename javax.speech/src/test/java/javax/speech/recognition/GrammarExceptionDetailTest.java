/**
 * 
 */
package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test case for {@link javax.speech.recognition.GrammarExceptionDetail}.
 * 
 * @author Dirk Schnelle
 */
public class GrammarExceptionDetailTest {

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarExceptionDetail#getCharNumber()}.
     */
    @Test
    void testGetCharNumber() {
        final GrammarExceptionDetail detail1 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1", "reference1",
                "ruleName1", 42, 43, "message1");
        assertEquals(43, detail1.getCharNumber());

        final GrammarExceptionDetail detail2 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, null, null, null, 44, 45,
                null);
        assertEquals(45, detail2.getCharNumber());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarExceptionDetail#getGrammarReference()}.
     */
    @Test
    void testGetGrammarReference() {
        final GrammarExceptionDetail detail1 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1",
                "reference1", "ruleName1", 42, 43, "message1");
        assertEquals("reference1", detail1.getGrammarReference());

        final GrammarExceptionDetail detail2 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1", null,
                null, 44, 45, null);
        assertNull(detail2.getGrammarReference());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarExceptionDetail#getLineNumber()}.
     */
    @Test
    void testGetLineNumber() {
        final GrammarExceptionDetail detail1 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1",
                "reference1", "ruleName1", 42, 43, "message1");
        assertEquals(42, detail1.getLineNumber());

        final GrammarExceptionDetail detail2 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1", null,
                null, 44, 45, null);
        assertEquals(44, detail2.getLineNumber());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarExceptionDetail#getMessage()}.
     */
    @Test
    void testGetMessage() {
        final GrammarExceptionDetail detail1 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1",
                "reference1", "ruleName1", 42, 43, "message1");
        assertEquals("message1", detail1.getMessage());

        final GrammarExceptionDetail detail2 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1", null,
                null, 44, 45, null);
        assertNull(detail2.getMessage());
    }

    /**
     * Test method for
     * {@link javax.speech.recognition.GrammarExceptionDetail#getRuleName()}.
     */
    @Test
    void testGetRuleName() {
        final GrammarExceptionDetail detail1 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1",
                "reference1", "ruleName1", 42, 43, "message1");
        assertEquals("ruleName1", detail1.getRuleName());

        final GrammarExceptionDetail detail2 = new GrammarExceptionDetail(
                GrammarExceptionDetail.UNKNOWN_TYPE, "info1", null,
                null, 44, 45, null);
        assertNull(detail2.getRuleName());
    }

}
