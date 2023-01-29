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

package javax.speech.recognition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test case for {@link javax.speech.recognition.RuleToken}.
 *
 * @author Dirk Schnelle-Walka
 */
public class RuleTokenTest {
    /**
     * Test method for
     * {@link javax.speech.recognition.RuleToken#RuleToken(String)}.
     */
    @Test
    void testNewRuleToken() {
        RuleToken token1 = new RuleToken("test");
        assertEquals("test", token1.getText());

        RuleToken token2 = new RuleToken("  New    York   ");
        assertEquals("New York", token2.getText());

        RuleToken token3 = new RuleToken("möaüAÖÜß?!");
        assertEquals("möaüAÖÜß?!", token3.getText());

        Exception failure = null;
        try {
            new RuleToken(null);
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);

        failure = null;
        try {
            new RuleToken("");
        } catch (IllegalArgumentException e) {
            failure = e;
        }
        assertNotNull(failure);
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleToken#getText()}.
     */
    @Test
    void testGetText() {
        RuleToken token = new RuleToken("test");
        assertEquals("test", token.getText());
    }

    /**
     * Test method for {@link javax.speech.recognition.RuleToken#toString()}.
     */
    @Test
    void testToString() {
        RuleToken token = new RuleToken("test");
        assertEquals("test", token.getText());
    }

}
