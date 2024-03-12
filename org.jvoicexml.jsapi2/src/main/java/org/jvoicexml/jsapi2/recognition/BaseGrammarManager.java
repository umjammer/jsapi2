/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.speech.EngineException;
import javax.speech.EngineMode;
import javax.speech.EngineStateException;
import javax.speech.SpeechLocale;
import javax.speech.recognition.Grammar;
import javax.speech.recognition.GrammarEvent;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.GrammarListener;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;


/**
 * A base implementation of a {@link GrammarManager}.
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 */
public class BaseGrammarManager implements GrammarManager {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(BaseGrammarManager.class.getName());

    /** The listeners of grammar events. */
    protected final List<GrammarListener> grammarListeners;

    /** Storage of created grammars. */
    protected Map<String, Grammar> grammars;

    /** Mask that filter events. */
    private int grammarMask;

    /** Recognizer which the GrammarManager belongs. */
    private final BaseRecognizer recognizer;

    /**
     * Constructor that associates a Recognizer. with a GrammarManager
     *
     * @param reco BaseRecognizer
     */
    public BaseGrammarManager(BaseRecognizer reco) {
        grammarListeners = new ArrayList<>();
        grammars = new HashMap<>();
        grammarMask = GrammarEvent.DEFAULT_MASK;
        recognizer = reco;
    }

    /**
     * Constructor that allows to use a GrammarManager in standalone mode.
     */
    public BaseGrammarManager() {
        this(null);
    }

    @Override
    public final void addGrammarListener(GrammarListener listener) {
        grammarListeners.add(listener);
    }

    @Override
    public final void removeGrammarListener(GrammarListener listener) {
        grammarListeners.remove(listener);
    }

    @Override
    public RuleGrammar createRuleGrammar(String grammarReference, String rootName)
            throws IllegalArgumentException, EngineStateException, EngineException {
        SpeechLocale locale = SpeechLocale.getDefault();
        return createRuleGrammar(grammarReference, rootName, locale);
    }

    @Override
    public RuleGrammar createRuleGrammar(String grammarReference, String rootName, SpeechLocale locale)
            throws IllegalArgumentException, EngineStateException, EngineException {

        // Validate current state
        ensureValidEngineState();

        if (grammars.containsKey(grammarReference)) {
            throw new IllegalArgumentException("Duplicate grammar name: " + grammarReference);
        }

        // Create grammar
        BaseRuleGrammar grammar = new BaseRuleGrammar(recognizer, grammarReference);
        grammar.setAttribute("xml:lang", locale.toString());
        grammar.setRoot(rootName);

        // Register it
        grammars.put(grammarReference, grammar);

        return grammar;
    }

    /**
     * Deletes a Grammar.
     *
     * @param grammar Grammar
     * @throws IllegalArgumentException if the grammar is unknown
     * @throws EngineStateException     if the current engine state does not allow deleting the
     *                                  grammar
     */
    @Override
    public void deleteGrammar(Grammar grammar) throws IllegalArgumentException, EngineStateException {

        // Validate current state
        ensureValidEngineState();

        if (!grammars.containsKey(grammar.getReference())) {
            throw new IllegalArgumentException("The grammar is unknown");
        }

        // Remove the grammar
        Grammar key = grammars.remove(grammar.getReference());

        if (logger.isLoggable(Level.DEBUG)) {
            logger.log(Level.DEBUG, "Removed grammar :{0}", key.getReference());

            for (String s : grammars.keySet()) {
                logger.log(Level.DEBUG, "Grammar :{0}", s);
            }
        }
    }

    /**
     * Lists the Grammars known to this Recognizer.
     *
     * @return known grammars
     * @throws EngineStateException if the engine state does not allow listing the grammars
     */
    @Override
    public Grammar[] listGrammars() throws EngineStateException {

        // Validate current state
        ensureValidEngineState();

        // List of all grammars
        Collection<Grammar> allGrammars = new java.util.ArrayList<>();

        // Get engine built-in grammars
        if (recognizer != null) {
            Collection<Grammar> builtInGrammars = recognizer.getBuiltInGrammars();
            if (builtInGrammars != null) {
                allGrammars.addAll(builtInGrammars);
            }
        }

        // Add local managed grammars
        if (grammars != null) {
            allGrammars.addAll(grammars.values());
        }

        // Return an array with all know grammars
        return allGrammars.toArray(new Grammar[0]);
    }

    /**
     * Gets the RuleGrammar with the specified grammarReference.
     *
     * @param grammarReference String
     * @return referenced grammar
     * @throws EngineStateException if the engine state does not allow obtaining the grammar
     */
    @Override
    public Grammar getGrammar(String grammarReference) throws EngineStateException {

        // Validate current state
        ensureValidEngineState();

        return grammars.get(grammarReference);
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType)
            throws GrammarException, IllegalArgumentException, IOException,
            EngineStateException, EngineException {

        if (logger.isLoggable(Level.DEBUG)) {
            logger.log(Level.DEBUG, "Load Grammar : {0} with media Type:{1}", grammarReference, mediaType);
        }

        return loadGrammar(grammarReference, mediaType, true, false, null);
    }

    @Override
    public Grammar loadGrammar(String grammarReference,
                               String mediaType, boolean loadReferences,
                               boolean reloadGrammars,
                               @SuppressWarnings("rawtypes") List loadedGrammars)
            throws GrammarException, IllegalArgumentException, IOException,
            EngineStateException, EngineException {

        if (logger.isLoggable(Level.DEBUG)) {
            logger.log(Level.DEBUG, "Load Grammar : {0} with media Type:{1}", grammarReference, mediaType);
            logger.log(Level.DEBUG, "loadReferences : {0} reloadGrammars:{1}", loadReferences, reloadGrammars);
            logger.log(Level.DEBUG, "there are {0} loaded grammars:", loadedGrammars.size());
        }

        // Validate current state
        ensureValidEngineState();

        // Make sure that recognizer supports markup
        // TODO: Is this really correct? Maybe we should change that
        if (recognizer != null) {
            EngineMode mode = recognizer.getEngineMode();
            if (!mode.getSupportsMarkup()) {
                throw new EngineException("Engine doesn't support markup");
            }
        }

        // Process grammar
        URL url = new URL(grammarReference);
        InputStream grammarStream = url.openStream();

        SrgsRuleGrammarParser srgsParser = new SrgsRuleGrammarParser();
        Rule[] rules = srgsParser.load(grammarStream);
        if (rules != null) {
            // Initialize rule grammar
            BaseRuleGrammar brg = new BaseRuleGrammar(recognizer, grammarReference);
            brg.addRules(rules);
            Map<String, String> attributes = srgsParser.getAttributes();
            brg.setAttributes(attributes);

            // Register grammar
            grammars.put(grammarReference, brg);

            return brg;
        }

        return null;
    }

    /**
     * Creates a RuleGrammar from grammar text provided by a Reader.
     *
     * @param grammarReference a unique reference to the {@link Grammar} to load
     * @param mediaType        the type of {@link Grammar} to load
     * @param reader           the {@link Reader} from which the {@link Grammar} text is
     *                         loaded
     * @return a {@link Grammar} object
     * @throws GrammarException         if the <code>mediaType</code> is not supported or if any
     *                                  loaded grammar text contains an error
     * @throws IllegalArgumentException if <code>grammarReference</code> is invalid
     * @throws IOException              if an I/O error occurs
     * @throws EngineStateException     when not in the standard Conditions for Operation
     * @throws EngineException          if {@link RuleGrammar}s are not supported
     */
    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType, Reader reader)
            throws GrammarException, IllegalArgumentException, IOException, EngineStateException, EngineException {

        logger.log(Level.DEBUG, "Load Grammar : {0} with media Type:{1} and Reader :{2}",
                grammarReference, mediaType, reader);

        // Validate current state
        ensureValidEngineState();

        // Make sure that recognizer supports markup
        if (recognizer != null) {
            EngineMode mode = recognizer.getEngineMode();
            Boolean supportsMarkup = mode.getSupportsMarkup();
            if (Boolean.FALSE.equals(supportsMarkup)) {
                throw new EngineException("Engine doesn't support markup");
            }
        }

        // Process grammar
        SrgsRuleGrammarParser parser = new SrgsRuleGrammarParser();
        Rule[] rules = parser.load(reader);
        if (rules == null) {
            throw new IOException("Unable to load grammar '" + grammarReference + "'");
        }

        if (logger.isLoggable(Level.DEBUG)) {
            logger.log(Level.DEBUG, "SrgsRuleGrammarParser parsed rules:");
            for (Rule rule : rules) {
                logger.log(Level.DEBUG, rule.getRuleName());
            }
        }

        // Initialize rule grammar
        BaseRuleGrammar grammar = new BaseRuleGrammar(recognizer,
                grammarReference);

        if (logger.isLoggable(Level.DEBUG)) {
            logger.log(Level.DEBUG, "new BaseRuleGrammar:{0}", grammar.getReference());
        }

        grammar.addRules(rules);

        Map<String, String> attributes = parser.getAttributes();
        grammar.setAttributes(attributes);
        String root = attributes.get("root");
        if (root != null) {
            grammar.setActivatable(root, true);
        }
        grammar.commitChanges();

        // Register grammar
        grammars.put(grammarReference, grammar);

        return grammar;
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType, String grammarText)
            throws GrammarException, IllegalArgumentException, IOException, EngineStateException, EngineException {
        return loadGrammar(grammarReference, mediaType, new StringReader(grammarText));
    }

    @Override
    public Grammar loadGrammar(String grammarReference, String mediaType, InputStream byteStream, String encoding)
            throws GrammarException, IllegalArgumentException, IOException, EngineStateException, EngineException {
        if (byteStream == null) {
            throw new IOException("Unable to read from a null stream!");
        }
        InputStreamReader reader = new InputStreamReader(byteStream, encoding);
        return loadGrammar(grammarReference, mediaType, reader);
    }

    @Override
    public void setGrammarMask(int mask) {
        grammarMask = mask;
    }

    @Override
    public int getGrammarMask() {
        return grammarMask;
    }

    /**
     * Checks if the recognizer is in a valid state to perform grammar
     * operations. If the recognizer is currently allocating resources, these
     * methods waits until the resources are allocated.
     *
     * @throws EngineStateException invalid engine state
     */
    private void ensureValidEngineState() throws EngineStateException {
        if (recognizer == null) {
            return;
        }
        // Validate current state
        if (recognizer.testEngineState(Recognizer.DEALLOCATED | Recognizer.DEALLOCATING_RESOURCES)) {
            throw new EngineStateException("Cannot execute GrammarManager operation: invalid engine state: "
                            + recognizer.stateToString(recognizer.getEngineState()));
        }

        // Wait until end of allocating (if it's currently allocating)
        while (recognizer.testEngineState(Recognizer.ALLOCATING_RESOURCES)) {
            try {
                recognizer.waitEngineState(Recognizer.ALLOCATED);
            } catch (InterruptedException ex) {
                throw new EngineStateException(ex.getMessage());
            }
        }
    }
}
