/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: $
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

package javax.speech.mock.recognition;

import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarListener;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.ResultListener;


/**
 * Grammar for test purposes.
 *
 * @author Dirk Schnelle-Walka
 */
public class MockGrammar implements Grammar {

    @Override
    public void addGrammarListener(GrammarListener listener) {
    }

    @Override
    public void removeGrammarListener(GrammarListener listener) {
    }

    @Override
    public void addResultListener(ResultListener listener) {
    }

    @Override
    public void removeResultListener(ResultListener listener) {
    }

    @Override
    public int getActivationMode() {
        return 0;
    }

    @Override
    public GrammarManager getGrammarManager() {
        return null;
    }

    @Override
    public String getReference() {
        return null;
    }

    @Override
    public void setActivationMode(int mode) throws IllegalArgumentException {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isActivatable() {
        return false;
    }

    @Override
    public void setActivatable(boolean activatable) {
    }
}
