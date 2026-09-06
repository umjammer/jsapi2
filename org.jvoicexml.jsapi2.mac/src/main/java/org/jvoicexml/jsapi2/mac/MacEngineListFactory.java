package org.jvoicexml.jsapi2.mac;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.SpeechLocale;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.mac.synthesis.MacSynthesizerMode;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSObject;
import vavix.rococoa.avfoundation.AVSpeechSynthesisVoice;

import static java.lang.System.getLogger;
import static vavix.rococoa.avfoundation.AVSpeechSynthesisVoice.AVSpeechSynthesisVoiceGenderFemale;
import static vavix.rococoa.avfoundation.AVSpeechSynthesisVoice.AVSpeechSynthesisVoiceGenderMale;
import static vavix.rococoa.avfoundation.AVSpeechSynthesisVoice.AVSpeechSynthesisVoiceGenderUnspecified;


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
logger.log(Level.INFO, "voices: " + allVoices.size());
            SynthesizerMode[] features = new SynthesizerMode[] {
                    new MacSynthesizerMode(null, mode.getEngineName(),
                            mode.getRunning(), mode.getSupportsLetterToSound(), mode.getMarkupSupport(),
                            voices.toArray(Voice[]::new))};
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
        for (NSObject object : AVSpeechSynthesisVoice.speechVoices()) {
            AVSpeechSynthesisVoice nativeVoice = Rococoa.cast(object, AVSpeechSynthesisVoice.class);
            Voice voice = new Voice(getSpeechLocale(nativeVoice),
                    nativeVoice.name(),
                    getGender(nativeVoice),
                    getAge(nativeVoice),
                    Voice.VARIANT_DONT_CARE);
            voiceList.add(voice);
        }
        return voiceList;
    }

    private static final Pattern localPattern = Pattern.compile("\\p{Alpha}{2}-\\p{Alpha}{2}");

    /** */
    private static SpeechLocale getSpeechLocale(AVSpeechSynthesisVoice nativeVoice) {
        try {
            Matcher m = localPattern.matcher(nativeVoice.identifier());
            if (m.find()) {
                String found = m.group();
//logger.log(Level.DEBUG, "found: " + found);
                String[] pair = found.split("-");
//logger.log(Level.DEBUG, "locale: " + nativeVoice.identifier() + ", " + pair[0] + ", " +  pair[1]);
                return new SpeechLocale(pair[0], pair[1], "");
            }
        } catch (Exception e) {
logger.log(Level.DEBUG, "getSpeechLocale: " + e);
        }
//logger.log(Level.TRACE, "getSpeechLocale: " + nativeVoice.identifier());
        return SpeechLocale.getDefault();
    }

    /** */
    private static int getAge(AVSpeechSynthesisVoice nativeVoice) {
        return Voice.AGE_DONT_CARE;
    }

    /** */
    private static int getGender(AVSpeechSynthesisVoice nativeVoice) {
        int gender;

        try {
            gender = nativeVoice.gender();
        } catch (IllegalArgumentException e) {
logger.log(Level.DEBUG, "getGender: " + nativeVoice.name());
            return Voice.GENDER_DONT_CARE;
        }

        return switch (gender) {
            case AVSpeechSynthesisVoiceGenderFemale -> Voice.GENDER_FEMALE;
            case AVSpeechSynthesisVoiceGenderMale -> Voice.GENDER_MALE;
            case AVSpeechSynthesisVoiceGenderUnspecified -> Voice.GENDER_NEUTRAL;
            default -> Voice.GENDER_DONT_CARE;
        };
    }
}