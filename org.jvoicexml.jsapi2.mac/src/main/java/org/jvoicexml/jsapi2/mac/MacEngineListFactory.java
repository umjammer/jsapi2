package org.jvoicexml.jsapi2.mac;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.EngineMode;
import javax.speech.spi.EngineListFactory;
import javax.speech.synthesis.SynthesizerMode;
import javax.speech.synthesis.Voice;

import org.jvoicexml.jsapi2.mac.synthesis.MacSynthesizerMode;

import static java.lang.System.getLogger;
import static org.jvoicexml.jsapi2.mac.MacNativeLibrary.noErr;


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
            Voice[] allVoices = new Voice[0];
            try {
                allVoices = macGetVoices();
            } catch (EngineException e) {
                throw new RuntimeException(e);
            }
            List<Voice> voices = new ArrayList<>();
            if (mode.getVoices() == null) {
                voices.addAll(Arrays.asList(allVoices));
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
//			final RecognizerMode[] features = new RecognizerMode[] { new MacRecognizerMode() };
//			return new EngineList(features);
//		}

        return null;
    }

    /**
     * Retrieves all voices.
     *
     * @return all voices
     */
    private Voice[] macGetVoices() throws EngineException {

//  logger.log(Level.DEBUG, "macGetVoices");

        int /* OSErr */ theErr = noErr;
        short numOfVoices;

        theErr = CountVoices(numOfVoices);

        if (theErr != noErr) {
            throw new EngineException("No voices found");
        }

//  logger.log(Level.DEBUG, "Nr. of Voices " << numOfVoices);

        Voice[] jvoices = new Voice[numOfVoices];

        if (jvoices == null) {;
            throw new NullPointerException("Error creating the voices array!");
        }

        // iterate all voices - they really do start at index 1
        int voiceIndex;
        for (voiceIndex = 1; voiceIndex <= numOfVoices; voiceIndex++) {
            VoiceDescription voiceDesc;
            VoiceSpec	theVoiceSpec;

            theErr = GetIndVoice(voiceIndex, theVoiceSpec);
            if (theErr != noErr)
                throw new EngineException("Cannot get voice");

            theErr = GetVoiceDescription(theVoiceSpec, voiceDesc, sizeof(voiceDesc));
            if (theErr != noErr)
                throw new EngineException("Cannot get voice description");

            byte[] voiceName = voiceDesc.name[1];

            String name = new String(voiceName);
            Voice voice = new Voice( null, name, -1, -1, -1);
            jvoices[voiceIndex - 1] = voice;
        }

        return jvoices;
    }
}
