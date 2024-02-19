package javax.speech.recognition;

import javax.speech.SpeechLocale;

//Comp. 2.0.6

public class RuleLocale extends RuleComponent {

    private final RuleComponent ruleComponent;

    private final SpeechLocale locale;

    public RuleLocale(RuleComponent ruleComponent, SpeechLocale locale) {
        this.ruleComponent = ruleComponent;
        this.locale = locale;
    }

    public RuleComponent getRuleComponent() {
        return ruleComponent;
    }

    public SpeechLocale getSpeechLocale() {
        return locale;
    }

    public String toString() {

        String str = "xml:lang=\"" + locale.toString() + "\"";

        return str;
    }
}