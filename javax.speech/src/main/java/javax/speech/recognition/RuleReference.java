/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
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

// Comp. 2.0.6

/**
 * Specifies a reference to a named Rule in a RuleGrammar.
 * <p>
 * The following table summarizes the various forms of rule reference in SRGS:
 *
 * <TABLE>
 *
 * <TR>Local rule reference
 * <TD><ruleref uri="#rulename"/></TD>
 * </TR>
 * <TR>
 * <TD>Reference to a named rule of a grammar identified by a URI
 * with optional media type</TD>
 * <TD><ruleref uri="grammarURI#rulename" type="media-type"/></TD>
 * </TR>
 * <TR>
 * <TD>Reference to the root rule of a grammar identified by a URI
 * with optional media type</TD>
 * <TD><ruleref uri="grammarURI" type="media-type"/></TD>
 * </TR>
 * </TABLE>
 *
 * Rule names must match the
 * <p>
 * <A href="http://www.w3.org/TR/2000/REC-xml-20001006#sec-common-syn">
 *  "Name" Production</A>
 * <p>
 * of XML 1.0
 * <p>
 * <A href="http://www.w3.org/TR/2000/REC-xml-20001006">[XML �2.3]</A>
 * and be a legal XML ID.
 * <p>
 * The RuleSpecial class defines the special definitions
 * NULL, VOID, and GARBAGE.
 * @see javax.speech.recognition.RuleGrammar
 * @see javax.speech.recognition.RuleSpecial
 * @see javax.speech.recognition.RuleSpecial#NULL
 * @see javax.speech.recognition.RuleSpecial#VOID
 * @see javax.speech.recognition.RuleSpecial#GARBAGE
 */
public class RuleReference extends RuleComponent {

    private static final String DEFAULT_MEDIA_TYPE = "application/srgs+xml";

    private String grammarReference;

    private String ruleName;

    private String mediaType;

    /**
     * Constructs a RuleReference given a rule name.
     * <p>
     * The rule name is local to the grammar context used.
     * @param ruleName the name of a rule
     * @throws java.lang.IllegalArgumentException if the ruleName is not
     *          valid grammar text
     * @see javax.speech.recognition.RuleReference#getRuleName()
     */
    public RuleReference(String ruleName) throws IllegalArgumentException {
        checkValidGrammarText(ruleName);

        this.ruleName = ruleName;
    }

    /**
     * Constructs a RuleReference given a grammar reference and rule name.
     * <p>
     * The rule name is considered in the context of the grammar reference.
     * @param grammarReference the grammar reference for the desired grammar
     * @param ruleName the name of a rule
     * @throws java.lang.IllegalArgumentException if the arguments are not
     *          valid grammar text
     * @see javax.speech.recognition.RuleReference#getGrammarReference()
     * @see javax.speech.recognition.RuleReference#getRuleName()
     */
    public RuleReference(String grammarReference, String ruleName) throws IllegalArgumentException {
        checkValidGrammarText(grammarReference);
        checkValidGrammarText(ruleName);

        this.grammarReference = grammarReference;
        this.ruleName = ruleName;
    }

    /**
     * Constructs a RuleReference from a
     * grammar reference, rule name and media type.
     * <p>
     * The rule name is considered in the context of the grammar reference.
     * The media type should match the media type of
     * the grammar being referenced.
     * <p>
     * If the media type is null, the default media type is used.
     * The default media type is "application/srgs+xml".
     *
     * @param grammarReference the grammar reference for the desired grammar
     * @param ruleName the name of a rule
     * @param mediaType the media type of the containing grammar
     * @throws java.lang.IllegalArgumentException if the arguments are not
     *          valid grammar text
     * @see javax.speech.recognition.RuleReference#getGrammarReference()
     * @see javax.speech.recognition.RuleReference#getMediaType()
     * @see javax.speech.recognition.RuleReference#getRuleName()
     */
    public RuleReference(String grammarReference, String ruleName, String mediaType)
            throws IllegalArgumentException {
        checkValidGrammarText(ruleName);

        // TODO According to the specification, we must check the media type
        // to be valid grammar text. This forbids strings like
        // 'application/x-jsgf'.
        if (mediaType != null) {
            this.mediaType = mediaType;
        }

        this.grammarReference = grammarReference;
        this.ruleName = ruleName;
    }

    /**
     * Gets the grammar reference.
     * <p>
     * This can return null if this is a local RuleReference.
     * @return the full grammar name
     */
    public String getGrammarReference() {
        return grammarReference;
    }

    /**
     * Gets the media type.
     * <p>
     * This returns the default media type if not specified in the constructor.
     * @return the media type
     */
    public String getMediaType() {
        if (mediaType == null) {
            return DEFAULT_MEDIA_TYPE;
        }

        return mediaType;
    }

    /**
     * Gets the rule name part of this RuleReference.
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    void appendStartTag(StringBuffer str) {
        str.append("<ruleref uri=\"");

        if (grammarReference != null) {
            str.append(grammarReference);
        }
        str.append("#");
        str.append(ruleName);
        str.append("\"");

        if (mediaType != null) {
            str.append(" type=\"");
            str.append(mediaType);
            str.append("\"");
        }
    }

    /**
     * Returns a String representing this RuleComponent as grammar text.
     * <p>
     * The String represents a portion of a grammar that could appear
     * on the right hand side of a Rule definition.
     * @return printable String representing grammar text.
     * @see javax.speech.recognition.RuleComponent
     * @see javax.speech.recognition.Rule
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        appendStartTag(str);
        str.append("/>");

        return str.toString();
    }
}
