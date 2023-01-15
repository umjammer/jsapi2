package org.jvoicexml.jsapi2.recognition.sphinx4;

import javax.speech.SpeechLocale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;


@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
public class Sphinx4RecognizerTest {

    @Test
    void testAllocate() throws Exception {
        SphinxRecognizerMode mode = new SphinxRecognizerMode(SpeechLocale.US);
        Sphinx4Recognizer recognizer = new Sphinx4Recognizer(mode);
        recognizer.allocate();
        recognizer.resume();
        Thread.sleep(10000);
    }
}
