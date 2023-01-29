/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 59 $
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

package javax.speech.recognition;

// Comp 2.0.6

/**
 * Describes a problem found in a Grammar.
 * <p>
 * This is usually bundled with a GrammarException.
 * <p>
 * The Grammar may have been created programmatically or by loading
 * grammar text.
 * Multiple GrammarExceptionDetail objects may be encapsulated by a single
 * GrammarException.
 * <p>
 * Depending on the type of error and the context in which the error is
 * identified, some or all of the following information may be provided:
 * <p>
 * Grammar reference (URI, named resource, or application name),
 * Rule name in which error is found,
 * Line number of error in the grammar text,
 * Character number (within line) in the grammar text,
 * A printable description string.
 * <p>
 * The following problems may be encountered when loading grammar text:
 * <p>
 * Missing or illegal grammar reference declaration.
 * URI String does not reference a grammar text file.
 * Illegal rule name or token.
 * Grammar text syntax error.
 * Redefinition of a rule.
 * Definition of a reserved rule name (NULL, VOID, GARBAGE).
 * Empty rule definition or empty alternative.
 * Missing or illegal weight on alternatives.
 * Some other error.
 * <p>
 * When the commitChanges method of a Recognizer is called, it performs
 * additional checks to ensure all loaded grammars are legal.
 * The following problems may be encountered:
 * <p>
 * Reference to an undefined rule name.
 * Unsupported recursion: the recognizer does not support
 * embedded recursion.
 *
 * @see javax.speech.recognition.GrammarException
 * @see javax.speech.recognition.Recognizer#loadRuleGrammar(java.lang.String, java.io.Reader)
 * @see javax.speech.recognition.Recognizer#processGrammars()
 */
public class GrammarExceptionDetail {

    public static final int UNKNOWN_VALUE = -1;

    public static final int UNKNOWN_TYPE = -1;

    public static final int UNSUPPORTED_ALPHABET = 1;

    public static final int UNSUPPORTED_LANGUAGE = 4;

    public static final int UNSUPPORTED_LEXEME = 5;

    public static final int UNSUPPORTED_LEXICON = 6;

    public static final int UNSUPPORTED_PHONEME = 7;

    public static final int SYNTAX_ERROR = 9;

    private final int type;

    private final String textInfo;

    private final String grammarReference;

    private final String ruleName;

    private final int lineNumber;

    private final int charNumber;

    private final String message;

    public GrammarExceptionDetail(int type, String textInfo,
                                  String grammarReference, String ruleName, int lineNumber,
                                  int charNumber, String message) throws IllegalArgumentException {
        if ((lineNumber <= 0) && (lineNumber != UNKNOWN_VALUE)) {
            throw new IllegalArgumentException("Line number must be a positive integer or UNKNOWN_VALUE");
        }
        if ((charNumber <= 0) && (charNumber != UNKNOWN_VALUE)) {
            throw new IllegalArgumentException("Char number must be a positive integer or UNKNOWN_VALUE");
        }
        this.type = type;
        this.textInfo = textInfo;
        this.grammarReference = grammarReference;
        this.ruleName = ruleName;
        this.lineNumber = lineNumber;
        this.charNumber = charNumber;
        this.message = message;
    }

    /**
     * Gets the character number in a line in a grammar text file
     * containing this problem.
     * <p>
     * Negative values indicate that the character number is unknown.
     * @return the character number in a line in a grammar text file
     */
    public int getCharNumber() {
        if (getLineNumber() == UNKNOWN_VALUE) {
            return UNKNOWN_VALUE;
        }
        return charNumber;
    }

    /**
     * Gets the grammar reference in which this problem is encountered.
     * <p>
     * May be null.
     * @return the grammar reference in which this problem is encountered
     */
    public String getGrammarReference() {
        return grammarReference;
    }

    /**
     * Gets the line number in a grammar text file containing this problem.
     * <p>
     * Negative values indicate that the line number is unknown.
     * @return the line number in a grammar text file
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets a printable string describing this problem.
     * <p>
     * May be null.
     * @return a string description of this problem
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the rule name within a Grammar containing this problem.
     * <p>
     * May be null.
     * @return the rule name containing this problem
     */
    public String getRuleName() {
        return ruleName;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public int getType() {
        return type;
    }
}
