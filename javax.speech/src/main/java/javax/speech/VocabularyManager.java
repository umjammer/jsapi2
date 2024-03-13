/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 73 $
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

/**
 * Interface for management of words used by a speech Engine.
 * <p>
 * The VocabularyManager for an Engine is returned by the getVocabularyManager
 * method of the Engine interface.
 * Engines are not required support the VocabularyManager -
 * the getVocabularyManager method may return null.
 * <p>
 * Words, technically known as tokens, are provided to the VocabularyManager
 * with optional information about their spoken form,
 * grammatical role, pronunciation, and audio pronunciation.
 * <p>
 * The VocabularyManager is typically used to provide a speech engine with
 * information on problematic words - usually words for which the engine
 * is unable to properly derive a pronunciation.
 * For debugging purposes, an Engine may provide a list of words it
 * finds difficult through the listProblemWords method.
 * <p>
 * Example uses include adding pronunciations for medical terms,
 * pronunciations for game terminology (e.g. "Klingon"), or
 * simply compensating for a very small dictionary.
 * <p>
 * Words in the VocabularyManager can improve the accuracy of recognition.
 * They can also affect the quality of synthesis.
 *
 * @see javax.speech.Engine
 * @see javax.speech.Engine#getVocabularyManager()
 * @see javax.speech.Word
 * @since 2.0.6
 */
public interface VocabularyManager {

    /**
     * Adds a word to this VocabularyManager.
     * <p>
     * Words added by the application take precedence over words with the
     * same written-form text already known on the Engine's internal word list.
     * Words added multiple times with the same written-form text are considered
     * in order of addition with the most recently having highest precedence.
     * In this case, the Engine may use one or more of the pronunciations.
     * <p>
     * Added Words remain for the lifetime of the VocabularyManager.
     * However, added Words may be removed by the removeWord method.
     * Subsequent use of the corresponding written-form text will
     * behave as it did before the Word was added to the VocabularyManager.
     * @param word a Word object to add
     * @see javax.speech.VocabularyManager#addWords(javax.speech.Word[])
     * @see javax.speech.VocabularyManager#removeWord(javax.speech.Word)
     */
    void addWord(Word word) throws EngineStateException, SecurityException;

    /**
     * Adds an array of words to this VocabularyManger.
     * <p>
     * The addWord method provides additional detail.
     * @param words an array of Word objects to add
     * @see javax.speech.VocabularyManager#addWord(javax.speech.Word)
     */
    void addWords(Word[] words) throws EngineStateException, SecurityException;

    String[] getPronounciations(String text, SpeechLocale locale) throws EngineStateException;

    Word[] getWords(String text, SpeechLocale locale) throws EngineStateException;

    /**
     * Removes a word from the vocabulary.
     * <p>
     * Only Words added with addWord or addWords may be removed.
     * Subsequent use of the corresponding written-form text will
     * behave as it did before the Word was added to the VocabularyManager.
     * @param word a Word object to remove
     * @throws java.lang.IllegalArgumentException Word is not known to the VocabularyManager.
     * @see javax.speech.VocabularyManager#removeWords(javax.speech.Word[])
     * @see javax.speech.VocabularyManager#addWord(javax.speech.Word)
     * @see javax.speech.VocabularyManager#addWords(javax.speech.Word[])
     */
    void removeWord(Word word) throws EngineStateException, IllegalArgumentException, SecurityException;

    /**
     * Removes an array of words from the vocabulary.
     * <p>
     * <A/>
     * To remove a set of words for a given written-form text it is often
     * useful to also use getWords:
     * <p>
     * removeWords(getWords("matching"));
     *
     * @param words an array of words to remove
     * @throws java.lang.IllegalArgumentException a Word is not known to the VocabularyManager.
     * @see javax.speech.VocabularyManager#removeWord(javax.speech.Word)
     * @see javax.speech.VocabularyManager#getWords(String, SpeechLocale)
     */
    void removeWords(Word[] words) throws EngineStateException, IllegalArgumentException, SecurityException;
}
