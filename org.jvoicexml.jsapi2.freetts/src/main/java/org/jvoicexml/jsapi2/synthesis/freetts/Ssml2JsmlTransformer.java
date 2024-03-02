/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.synthesis.freetts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static java.lang.System.getLogger;


public class Ssml2JsmlTransformer {

    private static final Logger logger = getLogger(Ssml2JsmlTransformer.class.getName());

    private TransformerFactory tfactory;
    private DOMSource xmlDomSource;
    private DocumentBuilder domBuilder;
    private DOMResult xmlDomResult;
    private DocumentBuilderFactory domFactory;
    private Transformer serializer = null;

    private Templates template;

    public Ssml2JsmlTransformer() {
        tfactory = TransformerFactory.newInstance();
        xmlDomSource = null;
        domBuilder = null;
        domFactory = DocumentBuilderFactory.newInstance();
        try {
            domBuilder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            logger.log(Level.ERROR, ex.getMessage(), ex);
        }

        // create a template from a xsl file
        try {
            InputStream in = Ssml2JsmlTransformer.class.getResourceAsStream("/ssml2jsml.xsl");
            template = tfactory.newTemplates(new StreamSource(in));
        } catch (TransformerConfigurationException ex2) {
            logger.log(Level.ERROR, ex2.getMessage(), ex2);
        }

        try {
            serializer = template.newTransformer();
        } catch (TransformerConfigurationException ex3) {
            logger.log(Level.ERROR, ex3.getMessage(), ex3);
        }
    }

    public Document transform(String ssml) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(ssml.getBytes());
            xmlDomSource = new DOMSource(domBuilder.parse(bais));

        } catch (IOException | SAXException ex1) {
            logger.log(Level.ERROR, ex1.getMessage(), ex1);
        }

        Document resDocument = domBuilder.newDocument();
        xmlDomResult = new DOMResult(resDocument);

        // transform to a Document
        try {
            serializer.transform(xmlDomSource, xmlDomResult);
        } catch (TransformerException ex4) {
            logger.log(Level.ERROR, ex4.getMessage(), ex4);
        }

        return resDocument;
    }
}
