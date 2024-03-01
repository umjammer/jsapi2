/*
 * Copyright 2003 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package org.jvoicexml.jsapi2.synthesis.freetts;

import java.util.ArrayList;
import java.util.List;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;

import com.sun.speech.freetts.VoiceManager;


/**
 * Supports the EngineCentral JSAPI 2.0 interface for the
 * FreeTTSSynthesizer.  To use a FreeTTSSynthesizer, you should place
 * a line into the speech.properties file as so:
 *
 * <pre>
 * FreeTTS=org.jvoicexml.implementation.jsapi20.jvxml.freetts.FreeTTSEngineListFactory
 * </pre>
 */
public class FreeTTSEngineListFactory implements EngineListFactory {

    /**
     * Creates a FreeTTSEngineCentral.
     */
    public FreeTTSEngineListFactory() {
    }

    /**
     * Returns a list containing references to all matching
     * synthesizers.  The mapping of FreeTTS VoiceDirectories and
     * Voices to JSAPI Synthesizers and Voices is as follows:
     *
     * <ul>
     * <li>Each FreeTTS VoiceDirectory specifies the list of FreeTTS
     * Voices supported by that directory.  Each Voice in that
     * directory specifies its name (e.g., "kevin" "kevin16" "alan"),
     * domain (e.g., "general" or "time") and locale (e.g., Locale.US).
     * <li>For all FreeTTS Voices from all VoiceDirectories discovered
     * by the VoiceManager, this method will group the Voices
     * according to those that have both a common locale and domain
     * (e.g, all "general" domain voices for the US local will be
     * grouped together).
     * <li>For each group of voices that shares a common locale and
     * domain, this method generates a new JSAPI SynthesizerModeDesc
     * with the following attributes:
     *   <ul>
     *   <li>The engine name is of the form: "FreeTTS &lt;locale&gt;
     *   &lt;domain&gt; synthesizer"  For example, "FreeTTS en_us general
     *   synthesizer"
     *   <li>The locale is the locale shared by all the voices (e.g.,
     *   Locale.US)
     *   <li>The mode name is the domain shared by all the voices
     *   (e.g., "general").
     *   </ul>
     * <li>The JSAPI Voices for each resulting Synthesizer will have
     * the name of the FreeTTS Voice (e.g. "kevin" "kevin16").
     * </ul>
     *
     * @param require an engine mode that describes the desired
     *                synthesizer
     * @return an engineList containing matching engines, or null if
     * no matching engines are found
     */
    @Override
    public EngineList createEngineList(EngineMode require) {
        // Must be a synthesizer.
        if (!(require instanceof SynthesizerMode)) {
            return null;
        }

        // Instantiate FreeTTS VoiceManager to get all voices available
        VoiceManager voiceManager = VoiceManager.getInstance();
        com.sun.speech.freetts.Voice[] voices = voiceManager.getVoices();

        // We want to get all combinations of domains and locales
        List<DomainLocale> domainLocaleList = new ArrayList<>();
        for (com.sun.speech.freetts.Voice value : voices) {
            DomainLocale dl =
                    new DomainLocale(value.getDomain(), value.getLocale());
            // If we find the domain locale in the set, add the existing one
            // otherwise add the template
            DomainLocale dlentry = getItem(domainLocaleList, dl);
            if (dlentry == null) {
                domainLocaleList.add(dl);
                dlentry = dl;
            }
            dlentry.addVoice(value);
        }

        // SynthesizerModes that will be created from combining domain/locale
        // with voice names
        List<FreeTTSSynthesizerMode> synthesizerModes =
                new ArrayList<>();

        // build list of SynthesizerModeDesc's for each domain/locale
        // combination
        for (DomainLocale domainLocale : domainLocaleList) {
            List<FreeTTSVoice> modeVoices = new ArrayList<>();

            // iterate through the voices in a different order
            voices = domainLocale.getVoices();
            for (com.sun.speech.freetts.Voice voice : voices) {
                FreeTTSVoice jsapiVoice = new FreeTTSVoice(voice);
                modeVoices.add(jsapiVoice);
            }

            FreeTTSSynthesizerMode mode = new FreeTTSSynthesizerMode("FreeTTS "
                    + domainLocale.getLocale().toString() + " " + domainLocale.getDomain()
                    + " synthesizer", domainLocale.getDomain(), domainLocale.getLocale(),
                    modeVoices.toArray(new FreeTTSVoice[] {}));

            if (require == null || mode.match(require)) {
                synthesizerModes.add(mode);
            }
        }

        EngineList el;
        if (synthesizerModes.isEmpty()) {
            el = null;
        } else {
            el = new EngineList(synthesizerModes.toArray(new EngineMode[] {}));
        }
        return el;
    }

    /**
     * Gets an item out of a vector.
     *
     * @param vector the vector to search
     * @param o      the object to look for using vector.get(i).equals(o)
     * @return the item if it exists in the vector, else null
     */
    private DomainLocale getItem(List<DomainLocale> vector, DomainLocale o) {
        int index = vector.indexOf(o);
        if (index < 0) {
            return null;
        }
        return vector.get(index);
    }
}

