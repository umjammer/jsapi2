/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 55 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.speech;

// Comp. 2.0.6

import java.util.Arrays;


/**
 * Provides a standard representation of speakable words for
 * speech engines.
 * <p>
 * A Word object provides the following information:
 * <p>
 * "Written form" text: String that can be used to present the Word visually.
 * "Spoken form" text: String that indicates how the Word is spoken.
 * Pronunciation: a string of phonetic characters indicating how the Word is spoken.
 * Audio: the actual audio signal for the Word.
 * Grammatical categories: flags indicating grammatical "part-of-speech" information.
 * <p>
 * The written-form text must be specified.
 * The other properties are optional.
 * <p>
 * Typically, at most one of the optional properties are specified.
 * All the optional properties of a word are hints to the speech engine.
 * If more than one is specified, they are considered in the following precedence
 * order from most to least specific:
 * <p>
 * Audio
 * Pronunciation
 * Spoken form
 * Written form
 * <p>
 * The Word class allows the specification of multiple pronunciations and
 * multiple grammatical categories.
 * If supplied, pronunciations must be appropriate to each category.
 * If not, separate Word objects should be created.
 * For example, consider the different verb and noun pronunciations in
 * "they will record a record".
 * <p>
 * Words are added through the VocabularyManager.
 * In some applications, the pronunciation may be derived through acoustics
 * and the written-form text may be less useful than the pronunciation.
 * In this case, the Word may be created with the ACOUSTIC category.
 * @see javax.speech.VocabularyManager
 * @see javax.speech.Engine#getVocabularyManager()
 * @see javax.speech.recognition.RuleToken
 */
public class Word {

    /**
     * Grammatical category of word is unknown.
     * <p>
     * This value implies that no other category flag is set.
     */
    public static final long UNKNOWN = 0x00000;

    /**
     * Grammatical category of word doesn't matter.
     */
    public static final long DONT_CARE = 0x00001;

    /**
     * Grammatical category of word not otherwise specified.
     */
    public static final long OTHER = 0x00002;

    /**
     * Grammatical category of word is noun.
     * <p>
     * English examples: "car", "house", "elephant".
     */
    public static final long NOUN = 0x00004;

    /**
     * Grammatical category of word is proper noun.
     * <p>
     * English examples: "Yellowstone", "Singapore".
     */
    public static final long PROPER_NOUN = 0x00008;

    /**
     * Grammatical category of word is pronoun.
     * <p>
     * English examples: "me", "I", "they".
     */
    public static final long PRONOUN = 0x00010;

    /**
     * Grammatical category of word is verb.
     * <p>
     * English examples: "run", "debug", "integrate".
     */
    public static final long VERB = 0x00020;

    /**
     * Grammatical category of word is adverb.
     * <p>
     * English examples: "slowly", "loudly", "barely", "very", "never".
     */
    public static final long ADVERB = 0x00040;

    /**
     * Grammatical category of word is adjective.
     * <p>
     * English examples: "red", "mighty", "very", "first", "eighteenth".
     */
    public static final long ADJECTIVE = 0x00080;

    /**
     * Grammatical category of word is proper adjective.
     * <p>
     * English examples: "British", "Brazilian".
     */
    public static final long PROPER_ADJECTIVE = 0x00100;

    /**
     * Grammatical category of word is auxiliary.
     * <p>
     * English examples: "have", "do", "is", "shall", "must", "cannot".
     */
    public static final long AUXILIARY = 0x00200;

    /**
     * Grammatical category of word is determiner.
     * <p>
     * English examples: "the", "a", "some", "many", "his", "her".
     */
    public static final long DETERMINER = 0x00400;

    /**
     * Grammatical category of word is cardinal.
     * <p>
     * English examples: "one", "two", "million".
     */
    public static final long CARDINAL = 0x00800;

    /**
     * Grammatical category of word is conjunction.
     * <p>
     * English examples: "and", "or", "since", "if".
     */
    public static final long CONJUNCTION = 0x01000;

    /**
     * Grammatical category of word is preposition.
     * <p>
     * English examples: "of", "for".
     */
    public static final long PREPOSITION = 0x02000;

    /**
     * Grammatical category of word is contraction.
     * <p>
     * English examples: "don't", "can't".
     */
    public static final long CONTRACTION = 0x04000;

    /**
     * Grammatical category of word is an abbreviation or acronym.
     * <p>
     * English examples: "Mr.", "USA".
     */
    public static final long ABBREVIATION = 0x08000;

    /**
     * Grammartical category of word is an acoutically-derived pronunciation.
     * <p>
     * This is not a true grammatical category, but provides
     * information useful for deciding how to use this Word.
     * <p>
     * In this case, the pronunciation is more useful than the written-form text.
     * For example, the written-form text might be "NameDialerEntry3", but the
     * pronunciation might represent "Bill Smith".
     */
    public static final long ACOUSTIC = 0x10000;

    private String text;

    private String[] pronunciations;

    private String spokenForm;

    private AudioSegment audioSegment;

    private long categories;

    private SpeechLocale locale;

    /**
     * Constructs a Word object.
     * <p>
     * The written-form text should be a string that could be used to
     * present the Word visually.
     * The pronunciation may be derived from audio spoken by the user.
     * In this case, the text may not be known.
     * <p>
     * The written-form text must be specified.
     * All other values may be null (or UNKNOWN for categories).
     * <p>
     * To use pronunciations in recognition without the writtenForm, use a Word in the constructor for RuleToken.
     * Synthesizers should use the pronunciation rather than the written form, if available.
     * @param text the written-form text of the Word
     * @param pronunciations a set of pronunciations for the Word
     * @param spokenForm the spoken form of the Word
     * @param audioSegment a recorded AudioSegment for the Word
     * @param categories grammatical categories for this Word
     * @throws java.lang.IllegalArgumentException if the written-form text is null
     * @see javax.speech.Word#getText()
     * @see javax.speech.Word#getPronunciations()
     * @see javax.speech.Word#getSpokenForm()
     * @see javax.speech.Word#getAudioSegment()
     * @see javax.speech.Word#getCategories()
     * @see javax.speech.Word#UNKNOWN
     * @see javax.speech.recognition.RuleToken
     */
    public Word(String text, String[] pronunciations, String spokenForm,
                AudioSegment audioSegment, long categories) throws IllegalArgumentException {
        this(text, pronunciations, spokenForm, audioSegment, categories, null);
    }

    public Word(String text, String[] pronunciations, String spokenForm,
                AudioSegment audioSegment, long categories, SpeechLocale locale)
            throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("Written form text must be specified");
        }
        this.text = text;
        this.pronunciations = pronunciations;
        this.spokenForm = spokenForm;
        this.audioSegment = audioSegment;
        this.categories = categories;
        this.locale = locale;
    }

    /**
     * Gets the audio segment for this Word.
     * <p>
     * The value may be null if it is not available.
     * <p>
     * Audio may be available for the word in speaker dependent systems or
     * when the pronunciation is derived from audio.
     * <p>
     * The audio may be used as feedback for the user.
     * @return the audio segment for this Word
     */
    public AudioSegment getAudioSegment() {
        return audioSegment;
    }

    /**
     * Gets the categories of this Word.
     * <p>
     * Value may be UNKNOWN or an OR'ed set of the categories defined by this class.
     * <p>
     * <A/>
     * For example, a word may be classified as both a noun and a verb:
     * <p>
     * Word w = new Word("running", null, null, null, Word.NOUN | Word.VERB);
     * <p>
     * The category information is a guide to the word's grammatical role.
     * Speech synthesizers can use this information to improve phrasing and accenting.
     * @return the set of categories for this word
     */
    public long getCategories() {
        return categories;
    }

    /**
     * Gets the pronunciations of this Word.
     * <p>
     * The pronunciation string uses the Unicode IPA subset.
     * Returns null if no pronunciations are specified.
     * <p>
     * Speech engines should be expected to handle most words of the language they support.
     * <p>
     * Recognizers can use pronunciation information to improve recognition
     * accuracy. Synthesizers use the information to accurately speak unusual words
     * (e.g., foreign words).
     * @return the pronunciations of this word
     */
    public String[] getPronunciations() {
        return pronunciations;
    }

    /**
     * Gets the "spoken form" of this Word.
     * <p>
     * Returns null if the spoken form is not defined.
     * <p>
     * The spoken form of a word is useful for mapping the written-form text
     * to words that are likely to be handled by a speech recognizer or synthesizer.
     * For example, "JavaSoft" to "java soft", "toString" -> "to string", "IEEE" -> "I triple E".
     * @return the spoken form of this word
     * @see javax.speech.Word#getText()
     */
    public String getSpokenForm() {
        return spokenForm;
    }

    /**
     * Gets the "written form" text of this Word.
     * <p>
     * The written-form text should be a string that could be used to present
     * the Word visually.
     * @return the written-form text of this word
     * @see javax.speech.Word#getSpokenForm()
     */
    public String getText() {
        return text;
    }

    public SpeechLocale getSpeechLocale() {
        return locale;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(getClass());
        str.append("[");

        str.append(text);

        if (pronunciations == null) {
            str.append(Arrays.toString(pronunciations));
        } else {
            str.append("[");
            int max = pronunciations.length;
            for (int i = 0; i < max; i++) {
                str.append(pronunciations[i]);
                if (i != max - 1) {
                    str.append(",");
                }
            }
            str.append("]");
        }

        str.append(spokenForm);
        str.append(audioSegment);
        str.append(categories);

        str.append("]");

        return str.toString();
    }
}
