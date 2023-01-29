/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2010-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.sapi.recognition;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.ResultToken;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.jvoicexml.jsapi2.recognition.BaseResult;
import org.jvoicexml.jsapi2.recognition.BaseResultToken;


/**
 * A recognition result from the SAPI engine.
 *
 * @author Dirk Schnelle-Walka
 */
public final class SapiResult extends BaseResult {

    /** The extractor for the SML values. */
    private SmlInterpretationExtractor extractor;

    /** The received utterance. */
    private String sml;

    /**
     * Constructs a new object.
     */
    public SapiResult() {
    }

    /**
     * Constructs a new object.
     *
     * @param grammar the grammar
     */
    public SapiResult(Grammar grammar) {
        super(grammar);
    }

    /**
     * Retrieves the confidence of the result.
     *
     * @return confidence
     */
    public float getConfidence() {
        if (extractor != null) {
            return extractor.getConfidence();
        }
        return 0.0f;
    }

    /**
     * Retrieves the utterance.
     *
     * @return the utterance
     */
    public String getUtterance() {
        if (extractor != null) {
            return extractor.getUtterance();
        }
        return null;
    }

    /**
     * Sets the retrieved SML string.
     *
     * @param value the SML string
     * @throws TransformerException error transforming the obtained result
     */
    public void setSml(String value) throws TransformerException {
        sml = value;
        extractor = parseSml(sml);

        // iterate through tags and set resultTags
        List<SmlInterpretation> interpretations = extractor
                .getInterpretations();
        List<String> smltags = new java.util.ArrayList<>();
        if (interpretations.isEmpty()) {
            String utterance = extractor.getUtterance();
            String tag = extractor.getUtteranceTag();
            // Hmpf this way we will not be able to process things like
            // <item>yes<tag>yes</tag></item>
            if (!utterance.equals(tag)) {
                smltags.add(tag);
            }
        } else {
            for (int k = 0; k < interpretations.size(); k++) {
                SmlInterpretation interpretation = interpretations.get(k);
                String tag = interpretation.getTag();
                String val = interpretation.getValue();
                boolean addedObject = false;
                if (k < interpretations.size() - 1) {
                    int currentLevel = interpretation
                            .getObjectHierachyLevel();
                    SmlInterpretation next = interpretations.get(k + 1);
                    int nextLevel = next.getObjectHierachyLevel();
                    if (currentLevel < nextLevel) {
                        if (currentLevel == 0) {
                            final String out = "out = new Object();";
                            smltags.add(out);
                        }
                        String str = "out." + tag + " = new Object();";
                        smltags.add(str);
                        addedObject = true;
                    }
                }

                // for the time being, a help tag is of the form
                // "*.help = 'help'",
                // e.g. "out.help = 'help'"
                boolean specialTag = (tag.equalsIgnoreCase("help")
                        && val.equalsIgnoreCase("help"))
                        || (tag.equalsIgnoreCase("cancel")
                        && val.equalsIgnoreCase("cancel"));
                // SRGS-tags like <tag>FOO="bar"</tag>
                if (!specialTag && (val != null) && !val.isEmpty()) {
                    String str = "out." + tag + "=" + val + ";";
                    smltags.add(str);
                } else {
                    if (!addedObject) {
                        // SRGS-tags like <tag>FOO</tag>
                        smltags.add(tag);
                    }
                }
            }
        }
        // Copy everything to tags.
        tags = new Object[smltags.size()];
        tags = smltags.toArray(tags);
        String utt = getUtterance();
        ResultToken[] tokens = resultToResultToken(utt);
        setTokens(tokens);
    }

    /**
     * Parses the given SML string.
     *
     * @param sml the SML to parse
     * @return the parsed information
     * @throws TransformerException error parsing
     */
    private SmlInterpretationExtractor parseSml(String sml)
            throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        Reader reader = new StringReader(sml);
        Source source = new StreamSource(reader);
        SmlInterpretationExtractor extractor = new SmlInterpretationExtractor();
        javax.xml.transform.Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        return extractor;
    }

    /**
     * Creates a vector of ResultToken (jsapi) from a sphinx result.
     *
     * @param utterance The Sphinx4 result
     * @return The current BaseResult (jsapi)
     */
    private ResultToken[] resultToResultToken(String utterance) {
        StringTokenizer st = new StringTokenizer(utterance);
        int nTokens = st.countTokens();
        ResultToken[] res = new ResultToken[nTokens];
        for (int i = 0; i < nTokens; ++i) {
            String text = st.nextToken();
            BaseResultToken brt = new BaseResultToken(this, text);
            if (getResultState() == BaseResult.ACCEPTED) {
                // TODO set confidenceLevel, startTime and end time, of each token
            }
            res[i] = brt;
        }
        return res;
    }

    /**
     * Retrieves the SML string.
     *
     * @return the SML string
     */
    public String getSml() {
        return sml;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(SapiResult.class.getCanonicalName());
        str.append('[');
        str.append(getUtterance());
        str.append(',');
        str.append(getConfidence());
        str.append(',');
        str.append(sml);
        if (tags != null) {
            str.append(',');
            str.append('[');
            for (int i = 0; i < tags.length; i++) {
                String tag = tags[i].toString();
                str.append(tag);
                if (i < tags.length - 1) {
                    str.append(',');
                }
            }
            str.append(']');
        }
        str.append(']');
        return str.toString();
    }
}
