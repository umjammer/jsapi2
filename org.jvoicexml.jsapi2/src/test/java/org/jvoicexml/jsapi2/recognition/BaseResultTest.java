/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 63 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.recognition;

import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;

import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.recognition.MockRecognizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test cases for {@link BaseResult}.
 * @author Dirk Schnelle-Walka
 *
 */
public final class BaseResultTest {

    /**
     * Test method for {@link org.jvoicexml.jsapi2.recognition.BaseResult#getTags(int)}.
     * @exception Exception test failed
     */
    @Test
    void testGetTags() throws Exception {
        BaseRecognizer recognizer = new MockRecognizer();
        GrammarManager manager = recognizer.getGrammarManager();
        RuleGrammar grammar =
            manager.createRuleGrammar("grammar:test", "test");
        RuleComponent[] components = new RuleComponent[]  {
                new RuleToken("test"),
                new RuleTag("T")
        };
        RuleSequence sequence = new RuleSequence(components);
        Rule root = new Rule("test", sequence, Rule.PUBLIC);
        grammar.addRule(root);
        recognizer.processGrammars();
        BaseResult result = new BaseResult(grammar, "test");
        Object[] tags = result.getTags(0);
        assertNotNull(tags);
        assertEquals(1, tags.length);
        assertEquals("T", tags[0]);
    }
}
