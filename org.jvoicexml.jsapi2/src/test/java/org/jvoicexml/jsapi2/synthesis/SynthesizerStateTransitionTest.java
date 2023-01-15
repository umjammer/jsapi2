package org.jvoicexml.jsapi2.synthesis;

import javax.speech.Engine;
import javax.speech.synthesis.Synthesizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test cases for state transitions of the
 * {@link javax.speech.synthesis.Synthesizer}.
 *
 * @author Stefan Radomski
 * @author Dirk Schnelle-Walka
 */
class SynthesizerStateTransitionTest {

    /** The synthesizer to test. */
    private BaseSynthesizer synthesizer;

    /**
     * Set up the test environment.
     *
     * @throws Exception set up failed
     */
    @BeforeEach
    void setUp() throws Exception {
        synthesizer = new MockSynthesizer();
    }

    /**
     * Checks for the given state.
     *
     * @param expected the expected state
     */
    private void checkState(final long expected) {
        assertTrue(synthesizer.testEngineState(expected), "Expected " + synthesizer.stateToString(expected) + " but was " + synthesizer.stateToString(synthesizer.getEngineState()));
    }

    /**
     * Test cases for the PAUSED-RESUMED transitions.
     *
     * @throws Exception test failed
     */
    @Test
    void testSynthesizerPauseResume() throws Exception {
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        checkState(Synthesizer.RESUMED);
        synthesizer.pause();
        synthesizer.waitEngineState(Synthesizer.PAUSED);
        checkState(Synthesizer.PAUSED);
        synthesizer.resume();
        synthesizer.waitEngineState(Synthesizer.RESUMED);
        checkState(Synthesizer.RESUMED);
        synthesizer.deallocate();
        synthesizer.waitEngineState(Synthesizer.DEALLOCATED);
        checkState(Synthesizer.DEALLOCATED);
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        checkState(Synthesizer.RESUMED);
        synthesizer.deallocate();
        synthesizer.waitEngineState(Synthesizer.DEALLOCATED);
        checkState(Synthesizer.DEALLOCATED);
    }

    /**
     * Test cases for nested PAUSED-RESUMED transitions.
     *
     * @throws Exception test failed
     */
    @Test
    void testSynthesizerNestedPauseResume() throws Exception {
        synthesizer.allocate();
        synthesizer.waitEngineState(Engine.ALLOCATED);
        checkState(Synthesizer.RESUMED);
        synthesizer.pause();
        synthesizer.waitEngineState(Synthesizer.PAUSED);
        checkState(Synthesizer.PAUSED);
        synthesizer.pause();
        synthesizer.waitEngineState(Synthesizer.PAUSED);
        checkState(Synthesizer.PAUSED);
        synthesizer.resume();
        checkState(Synthesizer.PAUSED);
        synthesizer.pause();
        checkState(Synthesizer.PAUSED);
        synthesizer.resume();
        checkState(Synthesizer.PAUSED);
        synthesizer.resume();
        checkState(Synthesizer.RESUMED);
        synthesizer.deallocate();
        synthesizer.waitEngineState(Synthesizer.DEALLOCATED);
        checkState(Synthesizer.DEALLOCATED);
    }
}
