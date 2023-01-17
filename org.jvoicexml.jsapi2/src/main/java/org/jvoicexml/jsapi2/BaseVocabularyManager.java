/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
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

package org.jvoicexml.jsapi2;

import java.security.Permission;

import javax.speech.EngineStateException;
import javax.speech.SpeechLocale;
import javax.speech.SpeechPermission;
import javax.speech.VocabularyManager;
import javax.speech.Word;

/**
 * Base implementation of a {@link VocabularyManager}. This implementation is
 * currently empty.
 * @author Dirk Schnelle-Walka
 *
 */
public class BaseVocabularyManager implements VocabularyManager {
    @Override
    public void addWord(Word word) throws EngineStateException,
            SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            Permission permission = new SpeechPermission(
                    "javax.speech.VocabularyManager.update");
            security.checkPermission(permission);
        }
    }

    @Override
    public void addWords(Word[] words) throws EngineStateException,
            SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            Permission permission = new SpeechPermission(
                    "javax.speech.VocabularyManager.update");
            security.checkPermission(permission);
        }
    }

    @Override
    public String[] getPronounciations(String text,
                                       SpeechLocale locale)
            throws EngineStateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Word[] getWords(String text, SpeechLocale locale)
            throws EngineStateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeWord(Word word) throws EngineStateException,
            IllegalArgumentException, SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            Permission permission = new SpeechPermission(
                    "javax.speech.VocabularyManager.update");
            security.checkPermission(permission);
        }
    }

    @Override
    public void removeWords(Word[] words) throws EngineStateException,
            IllegalArgumentException, SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            Permission permission = new SpeechPermission(
                    "javax.speech.VocabularyManager.update");
            security.checkPermission(permission);
        }
    }
}
