/**
 *
 */

package org.jvoicexml.jsapi2.mock;

import java.util.ArrayList;
import java.util.List;
import javax.speech.EngineEvent;
import javax.speech.recognition.RecognizerEvent;
import javax.speech.recognition.RecognizerListener;
import javax.speech.synthesis.SynthesizerEvent;
import javax.speech.synthesis.SynthesizerListener;


/**
 * A collector of engine events.
 * @author Dirk Schnelle-Walka
 */
public class EngineEventAccumulator
        implements RecognizerListener, SynthesizerListener {

    /** Collected events. */
    private final List<EngineEvent> events;

    /**
     * Constructs a new object.
     */
    public EngineEventAccumulator() {
        events = new ArrayList<>();
    }

    @Override
    public void synthesizerUpdate(SynthesizerEvent e) {
        events.add(e);
    }

    @Override
    public void recognizerUpdate(RecognizerEvent e) {
        events.add(e);
    }

    /**
     * Retrieves the collected events.
     * @return the collected events
     */
    public EngineEvent[] getEvents() {
        return events.toArray(new EngineEvent[0]);
    }
}
