/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 58 $
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

import java.util.List;
import javax.speech.SpeechEvent;

// Comp 2.0.6

/**
 * Event issued to each GrammarListener attached to a Grammar
 * when major events associated with that Grammar occur.
 * <p>
 * The source for a GrammarEvent is always a Grammar object.
 * @see javax.speech.recognition.Grammar
 * @see javax.speech.recognition.GrammarListener
 */
public class GrammarEvent extends SpeechEvent {

    /**
     * Event issued when a Recognizer completes
     * committing changes to a Grammar.
     * <p>
     * The event is issued immediately following the CHANGES_COMMITTED
     * event that is issued to RecognizerListeners.
     * That event indicates that changes have been applied to all grammars of a Recognizer.
     * The GRAMMAR_CHANGES_COMMITTED event is specific to each individual grammar.
     * <p>
     * The event is issued when the definition of the grammar is changed,
     * when its enabled property is changed, or both.
     * The enabledChanged and definitionChanged flags are set accordingly.
     * <p>
     * If any syntactic or logical errors are detected for a Grammar during
     * the commit, the generated GrammarException is included with this event.
     * If no problem is found the value is null.
     */
    public static int GRAMMAR_CHANGES_COMMITTED = 0x4000001;

    /**
     * Event issued when a grammar changes
     * state from deactivated to activated.
     * <p>
     * The isActive method of the Grammar will now return true.
     * <p>
     * Grammar activation changes follow one of two RecognizerEvents:
     * <p>
     * a CHANGES_COMMITTED event in which a grammar's enabled flag
     * is set true or
     * an ENGINE_FOCUSED event.
     * <p>
     * The full details of the activation conditions under which a Grammar
     * is activated are described in the Grammar interface.
     */
    public static int GRAMMAR_ACTIVATED = 0x4000002;

    /**
     * Event issued when a grammar changes
     * state from activated to deactivated.
     * <p>
     * The isActive method of the Grammar will now return false.
     * <p>
     * Grammar deactivation changes follow one of two RecognizerEvents:
     * <p>
     * a CHANGES_COMMITTED event in which a grammar's enabled flag
     * is set false or
     * an ENGINE_DEFOCUSED event.
     * <p>
     * The full details of the activation conditions under which a Grammar
     * is deactivated are described in the documentation for the Grammar interface.
     */
    public static int GRAMMAR_DEACTIVATED = 0x4000004;

    public static int GRAMMAR_CHANGES_REJECTED = 0x4000008;

    public static int DEFAULT_MASK = GRAMMAR_ACTIVATED
            | GRAMMAR_CHANGES_COMMITTED | GRAMMAR_CHANGES_REJECTED
            | GRAMMAR_DEACTIVATED;
    /**
     * The default mask for events in this class.
     * <p>
     * The following events are delivered by default: all.
     */

    private boolean activableChanged;

    private boolean definitionChanged;

    private GrammarException grammarException;

    public GrammarEvent(Object source, int id) throws IllegalArgumentException {
        super(source, id);
    }

    /**
     * Constructs a GrammarEvent event with a specified event identifier
     * plus state change and exception values.
     * <p>
     * For a GRAMMAR_CHANGES_COMMITTED event, the enabledChanged and
     * definitionChanged parameters should indicate what properties of
     * the Grammar has changed, otherwise they should be false.
     * For a GRAMMAR_CHANGES_COMMITTED event, the grammarException
     * parameter should be non-null only if an error is encountered
     * in the grammar definition.
     * @param source the object that issued the event
     * @param id the identifier for the event type
     * @param activableChanged true if the grammar's enabled property changed
     * @param definitionChanged true if the grammar's definition has changed
     * @param grammarException non-null if an error is detected in a grammar's definition
     */
    public GrammarEvent(Grammar source, int id, boolean activableChanged,
                        boolean definitionChanged, GrammarException grammarException)
            throws IllegalArgumentException {
        super(source, id);
        if ((id != GRAMMAR_CHANGES_REJECTED) && (grammarException != null)) {
            throw new IllegalArgumentException(
                    "A grammar exception can only supplied for " + "GRAMMAR_CHANGES_REJECTED!");
        }
        this.activableChanged = activableChanged;
        this.definitionChanged = definitionChanged;
        this.grammarException = grammarException;
    }

    /**
     * Returns non-null for a GRAMMAR_CHANGES_COMMITTED event if an error
     * is found in the grammar definition.
     * @return a GrammarException with detail about the grammar error
     */
    public GrammarException getGrammarException() {
        int id = getId();
        if (id == GRAMMAR_CHANGES_REJECTED) {
            return grammarException;
        }

        return null;
    }

    /**
     * Returns true for a GRAMMAR_CHANGES_COMMITTED event if the definition
     * of the source Grammar has changed.
     * @return flag that source Grammar has changed.
     */
    public boolean isDefinitionChanged() {
        return definitionChanged;
    }

    public boolean isActivableChanged() {
        return activableChanged;
    }

    @Override
    protected void id2String(StringBuffer str) {
        maybeAddId(str, GRAMMAR_ACTIVATED, "GRAMMAR_ACTIVATED");
        maybeAddId(str, GRAMMAR_CHANGES_COMMITTED, "GRAMMAR_CHANGES_COMMITTED");
        maybeAddId(str, GRAMMAR_CHANGES_REJECTED, "GRAMMAR_CHANGES_REJECTED");
        maybeAddId(str, GRAMMAR_DEACTIVATED, "GRAMMAR_DEACTIVATED");
        super.id2String(str);
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        Boolean definitionChangedObject = definitionChanged;
        parameters.add(definitionChangedObject);
        Boolean enabledChangedObject = activableChanged;
        parameters.add(enabledChangedObject);
        parameters.add(grammarException);

        return parameters;
    }
}
