/**
 * Copyright 1998-2003 Sun Microsystems, Inc.
 * <p>
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package org.jvoicexml.jsapi2.recognition;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.SpeechLocale;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.GrammarManager;
import javax.speech.recognition.Recognizer;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleAlternatives;
import javax.speech.recognition.RuleComponent;
import javax.speech.recognition.RuleCount;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.RuleReference;
import javax.speech.recognition.RuleSequence;
import javax.speech.recognition.RuleTag;
import javax.speech.recognition.RuleToken;


/**
 * Implementation of javax.speech.recognition.RuleGrammar.
 */
public class BaseRuleGrammar extends BaseGrammar implements RuleGrammar {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(BaseRuleGrammar.class.getName());

    protected Map<String, InternalRule> rules;
    protected List<RuleGrammarOperation> uncommitedChanges = new java.util.ArrayList<>();

    // Attributes of the rule grammar
    protected String root;
    protected String version;
    protected String xmlns;
    protected String xmlBase;
    protected String mode;
    protected String tagFormat;
    protected String xmlnsXsi;
    protected String xsiSchemaLocation;
    protected String doctype;

    private int ruleId;

    protected List<?> imports;
    protected List<?> importedRules;

    /**
     * Creates a new object.
     *
     * @param recognizer the BaseRecognizer for this grammar.
     * @param reference  the unique reference of this grammar.
     */
    public BaseRuleGrammar(BaseRecognizer recognizer, String reference) {
        super(recognizer, reference);
        rules = new java.util.HashMap<>();

        // Initialize rule grammar attributes
        root = null;
        version = "1.0";
        xmlns = "";
        xmlBase = "";
        mode = "voice";
        tagFormat = "";
        xmlnsXsi = "";
        xsiSchemaLocation = "";
        doctype = null;

        ruleId = 0;
    }

    /**
     * Internal representation of a Rule that holds additionally the
     * <code>activable</code> property.
     */
    private static class InternalRule {
        private Rule rule;
        private boolean activable;
        private int id;

        /**
         * Constructs a new object.
         *
         * @param r  the rule
         * @param id id of the rule
         */
        public InternalRule(Rule r, int id) {
            rule = r;
            this.id = id;
            activable = true;
        }

        public boolean isActivable() {
            return activable;
        }

        public void setActivable(boolean status) {
            activable = status;
        }

        public boolean isPublic() {
            return rule.getScope() == Rule.PUBLIC;
        }

        public String getRulename() {
            return rule.getRuleName();
        }

        public Rule getRule() {
            return rule;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return rule.toString();
        }
    }

    private static class InternalRuleIdComparator implements Comparator<InternalRule> {
        @Override
        public int compare(InternalRule rule1, InternalRule rule2) {
            return rule1.getId() - rule2.getId();
        }
    }

    /**
     * Abstract class used to make uncommited operations uniform.
     */
    private abstract static class RuleGrammarOperation {
        public abstract void execute() throws GrammarException;
    }

    /**
     * Class that describes an uncommitted add rule
     */
    private class AddRuleOperation extends RuleGrammarOperation {
        private InternalRule rule;

        public AddRuleOperation(InternalRule rule) {
            this.rule = rule;
        }

        @Override
        public void execute() throws GrammarException {
            if (rule != null) {
                rules.put(rule.getRulename(), rule);
            } else {
                throw new GrammarException("AddRule: cannot add null rules");
            }
        }
    }

    /**
     * Class that describes an uncomitted delete off a rule
     */
    private class RemoveRuleOperation extends RuleGrammarOperation {
        private String name;

        public RemoveRuleOperation(String name) {
            this.name = name;
        }

        @Override
        public void execute() throws GrammarException {
            InternalRule rule = rules.remove(name);
            if (rule == null) {
                throw new GrammarException("Rule " + name + " was not found");
            } else {
                if (root.equals(name)) {
                    updateRootRule();
                }
            }
        }
    }

    /**
     * Class that describes an uncommited enable of RuleGrammar.
     */
    private class GrammarEnablerOperation extends RuleGrammarOperation {
        private boolean status;

        public GrammarEnablerOperation(boolean status) {
            this.status = status;
        }

        @Override
        public void execute() throws GrammarException {
            boolean activatable = isActivatable();
            if (status != activatable) {
                setActivatable(status);
            }
        }
    }

    private class RuleEnablerOperation extends RuleGrammarOperation {
        private String[] ruleNames;
        private boolean status;

        private RuleEnablerOperation(String ruleName, boolean status) {
            ruleNames = new String[1];
            ruleNames[0] = ruleName;
            this.status = status;
        }

        private RuleEnablerOperation(String[] ruleNames, boolean status) {
            this.ruleNames = ruleNames;
            this.status = status;
        }

        @Override
        public void execute() throws GrammarException {
            for (String ruleName : ruleNames) {
                InternalRule iRule = rules.get(ruleName);
                if (iRule != null) {
                    if (iRule.isPublic()) {
                        iRule.setActivable(status);
                    } else {
                        throw new GrammarException("Rule: " + ruleName + " doesn't have PUBLIC scope");
                    }
                } else {
                    throw new GrammarException("Rule: " + ruleName + " was not found in rules");
                }
            }
        }
    }

    private class RootSetterOperation extends RuleGrammarOperation {
        private String rootRuleName;

        public RootSetterOperation(String rulename) {
            rootRuleName = rulename;

//            try {
//                throw new GrammarException("Cannot set a PRIVATE_SCOPE rule as root");
//            } catch (GrammarException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Set RootRuleName : {0}", rootRuleName);
            }
        }

        @Override
        public void execute() throws GrammarException {
            if (rootRuleName == null) {
                updateRootRule();
            } else {
                Rule rule = getRule(rootRuleName);
                if (rule == null) {
                    throw new GrammarException("Root rule '" + rootRuleName + "' not found");
                }
                if (rule.getScope() == Rule.PRIVATE) {
                    throw new GrammarException("Cannot set a PRIVATE_SCOPE rule as root");
                }

                root = rootRuleName;
            }
        }
    }

    //
    // Begin overridden Grammar Methods
    //

//    /**
//     * Set the enabled property of the Grammar. From
//     * javax.speech.recognition.Grammar.
//     *
//     * @param enabled the new desired state of the enabled property.
//     */
//    public void setEnabled(boolean enabled) {
//        GrammarEnablerOperation geo = new GrammarEnablerOperation(enabled);
//        uncommitedChanges.add(geo);
//    }

    //
    // End overridden Grammar Methods
    //

    //
    // Begin RuleGrammar Methods
    //

    /**
     * Set a rule in the grammar either by creating a new rule or updating an
     * existing rule.
     *
     * @param rule the definition of the rule.
     */
    @Override
    public void addRule(Rule rule) {
        InternalRule iRule = new InternalRule(rule, ruleId);
        AddRuleOperation aro = new AddRuleOperation(iRule);
        uncommitedChanges.add(aro);
        ruleId++;
    }

    /**
     * Update the root rulename It defaults to the first PUBLIC_SCOPE rule that
     * is enabled.
     */
    private void updateRootRule() {
        InternalRule nextRootCandidate = null;
        for (InternalRule iRule : rules.values()) {
            if (iRule.isPublic() && iRule.isActivable()) {
                if (nextRootCandidate == null) {
                    nextRootCandidate = iRule;
                } else {
                    if (iRule.getId() < nextRootCandidate.getId()) {
                        nextRootCandidate = iRule;
                    }
                }
            }
        }
        if (nextRootCandidate != null) {
            root = nextRootCandidate.getRulename();
        }
    }

    @Override
    public void addRule(String ruleText) throws GrammarException {
        SrgsRuleGrammarParser srgsParser = new SrgsRuleGrammarParser();
        Rule[] loadedRules = srgsParser.loadRule(new StringReader(ruleText));
        addRules(loadedRules);
    }

    @Override
    public void addRules(Rule[] rulesToAdd) {
        for (Rule rule : rulesToAdd) {
            addRule(rule);
        }
    }

    @Override
    public String getRoot() {
        if (root == null) {
            updateRootRule();
        }
        return root;
    }

    @Override
    public void setRoot(String rulename) {
        RootSetterOperation rsgo = new RootSetterOperation(rulename);
        uncommitedChanges.add(rsgo);

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "added RootRule : {0}", rulename);
        }
    }

    protected void setAttributes(Map<String, String> attributes) {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Try to set Attributes for Grammar rule :{0}", attributes.get("root"));
        }

        for (String name : attributes.keySet()) {
            String value = attributes.get(name);
            setAttribute(name, value);
        }
    }

    @Override
    public void setAttribute(String attribute, String value) throws IllegalArgumentException {
        switch (attribute) {
        case "root":
            setRoot(value);
            break;
        case "version":
            version = value;
            break;
        case "xmlns":
            xmlns = value;
            break;
        case "xml:lang":
            setSpeechLocale(new SpeechLocale(value));
            break;
        case "xml:base":
            xmlBase = value;
            break;
        case "mode":
            mode = value;
            break;
        case "tag-format":
            tagFormat = value;
            break;
        case "xmlns:xsi":
            xmlnsXsi = value;
            break;
        case "xsi:schemaLocation":
            xsiSchemaLocation = value;
            break;
        case "type":
        case "scope":
        case "src":
        case "weight":
        case "fetchtimeout":
        case "maxage":
        case "maxstale":
            // Ignored for VoiceXml 2.0 compatibility
            // Visit: http://www.w3.org/TR/voicexml20/vxml.dtd
            break;
        default:
            throw new IllegalArgumentException("Unknown attribute name: " + attribute);
        }
    }

    @Override
    public String getAttribute(String attribute) throws IllegalArgumentException {
        return switch (attribute) {
            case "root" -> getRoot();
            case "version" -> version;
            case "xmlns" -> xmlns;
            case "xml:lang" -> getSpeechLocale().toString();
            case "xml:base" -> xmlBase;
            case "mode" -> mode;
            case "tag-format" -> tagFormat;
            case "xmlns:xsi" -> xmlnsXsi;
            case "xsi:schemaLocation" -> xsiSchemaLocation;
            default -> throw new IllegalArgumentException("Unknown attribute: " + attribute);
        };
    }

    @Override
    public void addElement(String element) throws IllegalArgumentException {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public void removeElement(String element) throws IllegalArgumentException {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public String[] getElements() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    /**
     * Retrieves the document type.
     *
     * @return document type
     */
    public String getDoctype() {
        return doctype;
    }

    /**
     * Sets the document type.
     * TODO Implement it (have to parse the param)
     *
     * @param type String
     * @throws IllegalArgumentException if the doctype is invalid
     */
    public void setDoctype(String type) throws IllegalArgumentException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    /**
     * Return a copy of the data structure for the named rule. From
     * javax.speech.recognition.RuleGrammar.
     *
     * @param name the name of the rule.
     * @return named rule or {@code null} if a rule with the given name does
     * not exist
     */
    @Override
    public Rule getRule(String name) {
        InternalRule rule = rules.get(name);
        return rule != null ? rule.getRule() : null;
    }

    @Override
    public String[] listRuleNames() {
        return rules.keySet().toArray(new String[] {});
    }

    @Override
    public void removeRule(String ruleName) {
        RemoveRuleOperation rro = new RemoveRuleOperation(ruleName);
        uncommitedChanges.add(rro);
    }

    @Override
    public void setActivatable(String ruleName, boolean enabled) {
        RuleEnablerOperation operation = new RuleEnablerOperation(ruleName, enabled);
        uncommitedChanges.add(operation);
    }

    @Override
    public boolean isActivatable(String ruleName) {
        InternalRule rule = rules.get(ruleName);
        return rule != null && rule.isActivable();
    }

    @Override
    public RuleReference resolve(RuleReference name) throws GrammarException {
        RuleReference rn = new RuleReference(name.getRuleName());

//        String simpleName = rn.getRuleName();
//        String grammarName = rn.getGrammarReference();
//        //String packageName = rn.getPackageName();
//        //String fullGrammarName = rn.getFullGrammarName();
//        String fullGrammarName = rn.getGrammarReference();
//
//        // Check for badly formed RuleName
//        if (packageName != null && grammarName == null) {
//            throw new GrammarException("Error: badly formed rulename " + rn, null);
//        }
//
//        if (name.getRuleName().equals("NULL")) {
//            return RuleSpecial.NULL;
//        }
//
//        if (name.getRuleName().equals("VOID")) {
//            return RuleSpecial.VOID;
//        }
//
//        // Check simple case: a local rule reference
//        if (fullGrammarName == null && this.getRule(simpleName) != null) {
//            return new RuleReference(myName + "." + simpleName);
//        }
//
//        // Check for fully-qualified reference
//        if (fullGrammarName != null) {
//            RuleGrammar g = myRec.getRuleGrammar(fullGrammarName);
//            if (g != null) {
//                if (g.getRule(simpleName) != null) { // we have a successful
//                    resolution return new RuleReference(fullGrammarName + "." + simpleName);
//                }
//            }
//        }

        // Collect all matching imports into a vector. After trying to
        // match rn to each import statement the vec will have
        // size()=0 if rn is unresolvable
        // size()=1 if rn is properly resolvable
        // size()>1 if rn is an ambiguous reference
        List<?> matches = new ArrayList<>();

//        // Get list of imports
//        // Add local grammar to simply the case of checking for
//        // a qualified or fully-qualified local reference.
//        RuleReference imports[] = listImports();
//
//        if (imports == null) {
//            imports = new RuleReference[1];
//            imports[0] = new RuleReference(getReference() + ".*");
//        } else {
//            RuleReference[] tmp = new RuleReference[imports.length + 1];
//            System.arraycopy(imports, 0, tmp, 0, imports.length);
//            tmp[imports.length] = new RuleReference(getReference() + ".*");
//            imports = tmp;
//        }
//
//        // Check each import statement for a possible match
//        //
//        for (int i = 0; i < imports.length; i++) {
//            // TO-DO: update for JSAPI 1.0 String
//            iSimpleName = imports[i].getRuleName();
//            String iGrammarName = imports[i].getGrammarReference();
//            //String iPackageName = imports[i].getPackageName();
//            String iFullGrammarName = imports[i].getGrammarReference();
//
//            // Check for badly formed import name
//            if (iFullGrammarName == null)
//                throw new GrammarException("Error: badly formed import " + imports[i], null);
//
//            // Get the imported grammar
//            RuleGrammar gref = myRec.getRuleGrammar(iFullGrammarName);
//            if (gref == null) {
//                System.err.println("Warning: import of unknown grammar " + imports[i] + " in " + getReference());
//                continue;
//            }
//
//            // If import includes simpleName, test that it really exists
//            if (!iSimpleName.equals("*") && gref.getRule(iSimpleName) == null) {
//                System.err.println("Warning: import of undefined rule " + imports[i] + " in " + getReference());
//                continue;
//            }
//
//            // Check for fully-qualified or qualified reference
//            if (iFullGrammarName.equals(fullGrammarName) || iGrammarName.equals(fullGrammarName)) {
//                // Know that either
//                // import <ipkg.igram.???> matches <pkg.gram.???>
//                // OR
//                // import <ipkg.igram.???> matches <gram.???>
//                // (ipkg may be null)
//
//                if (iSimpleName.equals("*")) {
//                    if (gref.getRule(simpleName) != null) {
//                        // import <pkg.gram.*> matches <pkg.gram.rulename>
//                        matches.addElement(new RuleReference(iFullGrammarName + "." + simpleName));
//                    }
//                    continue;
//                } else {
//                    // Now testing
//                    // import <ipkg.igram.iRuleName > against <??.gram.ruleName>
//                    //
//                    if (iSimpleName.equals(simpleName)) {
//                        // import <pkg.gram.rulename> exact match for <???.gram.rulename >
//                        matches.addElement(new RuleReference(iFullGrammarName + "." + simpleName));
//                    }
//                    continue;
//                }
//            }
//
//            // If we get here and rn is qualified or fully-qualified
//            // then the match failed - try the next import statement
//            if (fullGrammarName != null) {
//                continue;
//            }
//
//            // Now test
//            // import <ipkg.igram.*> against <simpleName>
//
//            if (iSimpleName.equals("*")) {
//                if (gref.getRule(simpleName) != null) {
//                    // import <pkg.gram.*> matches <simpleName>
//                    matches.addElement(new RuleReference(iFullGrammarName + "." + simpleName));
//                }
//                continue;
//            }
//
//            // Finally test
//            // import <ipkg.igram.iSimpleName> against <simpleName>
//
//            if (iSimpleName.equals(simpleName)) {
//                matches.addElement(new RuleReference(iFullGrammarName + "." + simpleName));
//                continue;
//            }
//        }
//
//        //
//        // The return behavior depends upon number of matches
//        //
//        if (matches.size() == 0) {
//            // Return null if rulename is unresolvable
//            return null;
//        } else if (matches.size() > 1) {
//            // Throw exception if ambiguous reference
//            StringBuffer b = new StringBuffer();
//            b.append("Warning: ambiguous reference " + rn + " in " + getReference() + " to ");
//            for (int i = 0; i < matches.size(); i++) {
//                RuleReference tmp = (RuleReference) (matches.elementAt(i));
//                if (i > 0) {
//                    b.append(" and ");
//                }
//                b.append(tmp);
//            }
//            throw new GrammarException(b.toString(), null);
//        } else {

        // Return successfully
        return (RuleReference) (matches.get(0));
        // }
    }

    @Override
    public RuleParse parse(String text, String ruleName) throws GrammarException {
        Recognizer recognizer = getRecognizer();
        GrammarManager manager = recognizer.getGrammarManager();
        String reference = getReference();
        return RuleParser.parse(text, manager, reference, ruleName);
    }

    @Override
    public RuleParse parse(String[] tokens, String ruleName) throws GrammarException {
        Recognizer recognizer = getRecognizer();
        GrammarManager manager = recognizer.getGrammarManager();
        String reference = getReference();
        return RuleParser.parse(tokens, manager, reference, ruleName);
    }

    /**
     * Return a String containing the specification for this grammar, sorted by
     * id rule.
     *
     * @param displayDisabledRules {@code true} if disabled rules should be
     *                             considered
     * @return string representation of this grammar.
     */
    public String toString(boolean displayDisabledRules) {
        StringBuilder str = new StringBuilder();
//        str.append("<?xml version=\"1.0\" encoding=\"");
//        str.append("UTF-8");
//        str.append("\"?> \n"
//                + "<!DOCTYPE grammar PUBLIC \"-//W3C//DTD GRAMMAR 1.0//EN\""
//                + " \"http://www.w3.org/TR/speech-grammar/grammar.dtd\"> \n");
        str.append("<grammar version=\"");
        str.append(version);
        str.append("\" mode=\"");
        str.append(mode);
        str.append("\" xml:lang=\"");
        str.append(getSpeechLocale());
        str.append("\" root=\"");
        str.append(getRoot());
        str.append("\" tag-format=\"");
        str.append(tagFormat);
        str.append("\" xmlns=\"http://www.w3.org/2001/06/grammar\">\n");

        Iterator<String> it = rules.keySet().iterator();
        List<InternalRule> list = new java.util.ArrayList<>();
        while (it.hasNext()) {
            InternalRule r = rules.get(it.next());
            list.add(r);
        }

        list.sort(new InternalRuleIdComparator());
        for (InternalRule rule : list) {
            InternalRule internalRule = rule;
            if (displayDisabledRules || internalRule.isActivable()) {
                str.append(internalRule);
                str.append('\n');
            }
        }

        str.append("</grammar>");

        return str.toString();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Resolve and linkup all rule references contained in all rules.
     *
     * @throws GrammarException error parsing the gramamr
     */
    protected void resolveAllRules() throws GrammarException {
        // StringBuffer b = new StringBuffer();

        // First make sure that all imports are resolvable

//        RuleReference imports[] = listImports();
//
//        if (imports != null) {
//            for (int i = 0; i < imports.length; i++) {
//                String gname = imports[i].getGrammarReference();
//                RuleGrammar GI = myRec.getRuleGrammar(gname);
//                if (GI == null) {
//                    b.append("Undefined grammar " + gname + " imported in " + getReference() + "\n");
//                }
//            }
//        }
//        if (b.length() > 0) {
//            throw new GrammarException(b.toString(), null);
//        }


        for (String rulename : listRuleNames()) {
            InternalRule rule = rules.get(rulename);
            if (rule != null) {
                resolveRule(rule.getRule().getRuleComponent());
            } else {
                throw new GrammarException("null rule in Rules map!");
            }
        }
    }

    /**
     * Resolve the given rule.
     *
     * @param component the rule component to resolve
     * @throws GrammarException error parsing the grammar
     */
    protected void resolveRule(RuleComponent component) throws GrammarException {

        if (component instanceof RuleToken) {
            return;
        }

        if (component instanceof RuleAlternatives ra) {
            RuleComponent[] array = ra.getRuleComponents();
            for (RuleComponent ruleComponent : array) {
                resolveRule(ruleComponent);
            }
            return;
        }

        if (component instanceof RuleSequence rs) {
            RuleComponent[] array = rs.getRuleComponents();
            for (RuleComponent ruleComponent : array) {
                resolveRule(ruleComponent);
            }
            return;
        }

        if (component instanceof RuleCount rc) {
            resolveRule(rc.getRuleComponent());
            return;
        }

        if (component instanceof RuleTag) {
//            RuleTag rt = (RuleTag) r;
//            resolveRule(rt.getTag());
            return;
        }

//        if (r instanceof BaseRuleName) {
//            BaseRuleName rn = (BaseRuleName) r;
//            RuleReference resolved = resolve(rn);
//
//            if (resolved == null) {
//                throw new GrammarException(
//                        "Unresolvable rulename in grammar " + getReference() + ": " + rn.toString(), null);
//            } else {
//                // WDW - This forces all rule names to be fully resolved. This should be changed.
//                rn.resolvedRuleName = resolved.getRuleName();
//                rn.setRuleName(resolved.getRuleName());
//                return;
//            }
//        }


        throw new GrammarException("Unknown rule type", null);
    }

    /**
     * Commits the grammar changes.
     *
     * @return {@code true} if changes were committed
     * @throws GrammarException if there is an error in the grammar
     */
    public boolean commitChanges() throws GrammarException {
        boolean existChanges = !uncommitedChanges.isEmpty();
        RootSetterOperation rootSetter = null;
        while (!uncommitedChanges.isEmpty()) {
            RuleGrammarOperation operation = uncommitedChanges.remove(0);
            if (operation instanceof RootSetterOperation) {
                rootSetter = (RootSetterOperation) operation;
            } else {
                operation.execute();
            }
        }

        // Perform the root setter as the last operation
        if (rootSetter != null) {
            rootSetter.execute();
        }
        return existChanges;
    }
}
