/**
 *
 */

package org.jvoicexml.jsapi2;

import javax.speech.Engine;
import javax.speech.EngineEvent;
import javax.speech.SpeechEventExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvoicexml.jsapi2.mock.EngineEventAccumulator;
import org.jvoicexml.jsapi2.mock.synthesis.MockSynthesizer;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * State transition test cases for {@link BaseEngine}.
 * @author Dirk Schnelle-Walka
 *
 */
class BaseEngineStateTransitionTest {

    /** The engine to test. */
    private Engine engine;

    /** Collector for issued events. */
    private EngineEventAccumulator eventAccu;

    /**
     * Set up the test environment.
     * @exception Exception set up failed
     */
    @BeforeEach
    void setUp() throws Exception {
        MockSynthesizer synthesizer = new MockSynthesizer();
        synthesizer.setEngineMask(EngineEvent.DEFAULT_MASK
                | EngineEvent.ENGINE_ALLOCATING_RESOURCES
                | EngineEvent.ENGINE_DEALLOCATING_RESOURCES);
        eventAccu = new EngineEventAccumulator();
        synthesizer.addSynthesizerListener(eventAccu);
        engine = synthesizer;
        SpeechEventExecutor executor =
                new SynchronousSpeechEventExecutor();
        engine.setSpeechEventExecutor(executor);
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.BaseEngine#allocate(int)}.
     * @throws Exception test failed
     */
    @Test
    void testAllocate() throws Exception {
        engine.allocate();
        engine.waitEngineState(Engine.ALLOCATED);
        EngineEvent[] events = eventAccu.getEvents();
        assertEquals(2, events.length);
        assertEquals(EngineEvent.ENGINE_ALLOCATING_RESOURCES,
                events[0].getId());
        assertEquals(EngineEvent.ENGINE_ALLOCATED, events[1].getId());
    }

    /**
     * Test method for {@link org.jvoicexml.jsapi2.BaseEngine#allocate(int)}.
     * @throws Exception test failed
     */
    @Test
    void testDeallocate() throws Exception {
        engine.allocate();
        engine.waitEngineState(Engine.ALLOCATED);
        engine.deallocate();
        engine.waitEngineState(Engine.DEALLOCATED);
        EngineEvent[] events = eventAccu.getEvents();
        assertEquals(4, events.length);
        assertEquals(EngineEvent.ENGINE_ALLOCATING_RESOURCES,
                events[0].getId());
        assertEquals(EngineEvent.ENGINE_ALLOCATED, events[1].getId());
        assertEquals(EngineEvent.ENGINE_DEALLOCATING_RESOURCES,
                events[2].getId());
        assertEquals(EngineEvent.ENGINE_DEALLOCATED, events[3].getId());
    }
}
