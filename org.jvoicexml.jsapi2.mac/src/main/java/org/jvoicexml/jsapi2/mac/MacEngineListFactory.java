package org.jvoicexml.jsapi2.mac;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.SpeechLocale;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.mac.rococoa.NSSpeechSynthesizer;
import org.jvoicexml.jsapi2.mac.rococoa.NSVoice;
import org.jvoicexml.jsapi2.mac.synthesis.MacSynthesizerMode;

import static java.lang.System.getLogger;


/**
 * Factory for the Mac Speech engine.
 *
 * @author Stefan Radomski
 */
public class MacEngineListFactory implements EngineListFactory {

    private static final Logger logger = getLogger(MacEngineListFactory.class.getName());

    @Override
    public EngineList createEngineList(EngineMode require) {
        if (require instanceof SynthesizerMode) {
            SynthesizerMode mode = (SynthesizerMode) require;
            List<Voice> allVoices = getVoices();
logger.log(Level.INFO, "voices: " + allVoices.size());
            List<Voice> voices = new ArrayList<>();
            if (mode.getVoices() == null) {
                voices.addAll(allVoices);
            } else {
                for (Voice availableVoice : allVoices) {
                    for (Voice requiredVoice : mode.getVoices()) {
                        if (availableVoice.match(requiredVoice)) {
                            voices.add(availableVoice);
                        }
                    }
                }
            }
            SynthesizerMode[] features = new SynthesizerMode[] {
                    new MacSynthesizerMode(null, mode.getEngineName(),
                            mode.getRunning(), mode.getSupportsLetterToSound(), mode.getMarkupSupport(),
                    voices.toArray(new Voice[0]))};
            return new EngineList(features);
        }
        // Mac Recognizer unusable as it is
//		if (require instanceof RecognizerMode) {
//			RecognizerMode[] features = new RecognizerMode[] { new MacRecognizerMode() };
//			return new EngineList(features);
//		}

        return null;
    }

    /**
     * Retrieves all voices.
     *
     * @return all voices
     */
    private static List<Voice> getVoices() {
        List<Voice> voiceList = new LinkedList<>();
        for (NSVoice nativeVoice : NSSpeechSynthesizer.availableVoices()) {
            Voice voice = new Voice(new SpeechLocale(nativeVoice.getLocaleIdentifier()),
                    nativeVoice.getName(),
                    toGenger(nativeVoice.getGender()),
                    nativeVoice.getAge(),
                    Voice.VARIANT_DONT_CARE);
            voiceList.add(voice);
        }
        return voiceList;
    }

    /** */
    private static int toGenger(NSVoice.VoiceGender gender) {
        return switch (gender) {
            case Female -> Voice.GENDER_FEMALE;
            case Male -> Voice.GENDER_MALE;
            case Neuter -> Voice.GENDER_NEUTRAL;
            default -> Voice.GENDER_DONT_CARE;
        };
    }
}