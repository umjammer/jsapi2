/**
 *
 */

package javax.speech;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author Dirk Schnelle
 *
 */
public class WordTest {
    @Test
    void testConstructor() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word1 = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertNotNull(word1);

        Exception failure = null;
        try {
            Word word2 = new Word(null, pronounciations, "spoken form",
                    segment, Word.NOUN | Word.CARDINAL);
            assertNotNull(word2);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for {@link javax.speech.Word#getAudioSegment()}.
     */
    @Test
    void testGetAudioSegment() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertEquals(segment, word.getAudioSegment());
    }

    /**
     * Test method for {@link javax.speech.Word#getCategories()}.
     */
    @Test
    void testGetCategories() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertEquals(Word.NOUN | Word.CARDINAL, word.getCategories());
    }

    /**
     * Test method for {@link javax.speech.Word#getPronunciations()}.
     */
    @Test
    void testGetPronunciations() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertEquals(pronounciations, word.getPronunciations());
    }

    /**
     * Test method for {@link javax.speech.Word#getSpokenForm()}.
     */
    @Test
    void testGetSpokenForm() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertEquals("spoken form", word.getSpokenForm());
    }

    /**
     * Test method for {@link javax.speech.Word#getText()}.
     */
    @Test
    void testGetText() {
        String[] pronounciations = new String[] {"one", "two", "three"};
        final String locator = "file:///user/smith/hello.wav";
        final String markup = "<speak xml:lang='en-US' version='1.0'>Hello</speak>";
        AudioSegment segment = new AudioSegment(locator, markup);
        Word word = new Word("text", pronounciations, "spoken form",
                segment, Word.NOUN | Word.CARDINAL);
        assertEquals("text", word.getText());
    }
}
