/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Test cases for {@link SmlInterpretationExtractor}.
 *
 * @author Dirk Schnelle-Walka
 */
@EnabledOnOs(OS.WINDOWS)
public final class SmlInterpretationExtractorTest {

    /** Maximal diff for parsing the confidence value. */
    private static final float MAX_CONFIDENCE_DIFF = 0.0001f;

    /**
     * Test case for a simple recognition process.
     *
     * @throws Exception test failed
     */
    @Test
    void testSimple() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        InputStream in =
                SmlInterpretationExtractorTest.class.getResourceAsStream(
                        "sml-simple.xml");
        Source source = new StreamSource(in);
        SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        assertEquals("Hello Dirk",
                extractor.getUtterance());
        assertEquals(0.5100203, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        assertEquals(0, interpretations.size());
        assertEquals("Hello Dirk", extractor.getUtteranceTag());
    }

    /**
     * Test case for a simple recognition process with a single tag.
     *
     * @throws Exception test failed
     */
    @Test
    void testTag() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        InputStream in =
                SmlInterpretationExtractorTest.class.getResourceAsStream(
                        "sml-tag.xml");
        Source source = new StreamSource(in);
        SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        assertEquals("Good morning Dirk",
                extractor.getUtterance());
        assertEquals(0.7378733, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        assertEquals(0, interpretations.size());
        assertEquals("Projectmanager", extractor.getUtteranceTag());
    }

    /**
     * Test case for multiple tags.
     *
     * @throws Exception test failed
     */
    @Test
    void testMultipleTags() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        InputStream in =
                SmlInterpretationExtractorTest.class.getResourceAsStream(
                        "sml-multiple-tags.xml");
        Source source = new StreamSource(in);
        SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        assertEquals("Hello Dirk",
                extractor.getUtterance());
        assertEquals(0.6734907, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        assertEquals(2, interpretations.size());
        SmlInterpretation greet = interpretations.get(0);
        assertEquals("greet", greet.getTag());
        assertEquals("\"general\"", greet.getValue());
        assertEquals(2.069468E-02f, greet.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        SmlInterpretation who = interpretations.get(1);
        assertEquals("who", who.getTag());
        assertEquals("\"Projectmanager\"", who.getValue());
        assertEquals(2.069468E-02f, who.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        assertEquals("", extractor.getUtteranceTag());
    }

    /**
     * Test case for a compound object.
     *
     * @throws Exception test failed
     */
    @Test
    void testCompundObject() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        InputStream in =
                SmlInterpretationExtractorTest.class.getResourceAsStream(
                        "sml-compound.xml");
        Source source = new StreamSource(in);
        SmlInterpretationExtractor extractor =
                new SmlInterpretationExtractor();
        Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        assertEquals("a small pizza with salami",
                extractor.getUtterance());
        assertEquals(0.8081474f, extractor.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        List<SmlInterpretation> interpretations =
                extractor.getInterpretations();
        assertEquals(3, interpretations.size());
        SmlInterpretation order = interpretations.get(0);
        assertEquals("order", order.getTag());
        assertNull(order.getValue());
        assertEquals(0.8131593f, order.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        SmlInterpretation size = interpretations.get(1);
        assertEquals("order.size", size.getTag());
        assertEquals("\"small\"", size.getValue());
        assertEquals(0.8131593f, size.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        SmlInterpretation topping = interpretations.get(2);
        assertEquals("order.topping", topping.getTag());
        assertEquals("\"salami\"", topping.getValue());
        assertEquals(0.8131593f, topping.getConfidence(),
                MAX_CONFIDENCE_DIFF);
        assertEquals("", extractor.getUtteranceTag());
    }
}
