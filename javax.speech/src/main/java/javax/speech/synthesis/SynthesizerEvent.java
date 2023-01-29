/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 65 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.speech.synthesis;

import java.util.List;
import javax.speech.EngineEvent;

// Comp. 2.0.6

/**
 * Event issued by a Synthesizer to indicate a change
 * in state or other activity.
 * <p>
 * A SynthesizerEvent is issued to each SynthesizerListener attached to a
 * Synthesizer using the addSynthesizerListener method in the Synthesizer
 * interface.
 * @see javax.speech.synthesis.Synthesizer
 * @see javax.speech.synthesis.Synthesizer#addSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
 * @see javax.speech.synthesis.Synthesizer#removeSynthesizerListener(javax.speech.synthesis.SynthesizerListener)
 */
public class SynthesizerEvent extends EngineEvent {

    /**
     * Event issued when the speaking queue of the Synthesizer has emptied and
     * the Synthesizer has changed to the QUEUE_EMPTY state.
     * <p>
     * The queue may become empty because speech output of all items in
     * the queue is completed, or because the items have been cancelled.
     * <p>
     * This event follows the SPEAKABLE_ENDED or
     * SPEAKABLE_CANCELLED event that removed the last item from the
     * speaking queue.
     */
    public static final int QUEUE_EMPTIED = 0x3000800;

    /**
     * Event issued when the speech output queue has changed.
     * <p>
     * This event may indicate a change in state of the Synthesizer from
     * QUEUE_EMPTY to QUEUE_NOT_EMPTY. The event may also occur in the
     * QUEUE_NOT_EMPTY state without changing state.
     * <p>
     * The speech output queue changes when
     * <p>
     * a new item is placed on
     * the queue with a call to one of the speak methods,
     * an item
     * is removed from the queue with one of the cancel methods
     * (without emptying the queue), or
     * output of the top item
     * of the queue is completed (again, without leaving an empty queue).
     * <p>
     * The isTopOfQueueChanged method returns true if the
     * top item on the queue has changed.
     * <p>
     * This event follows the SPEAKABLE_ENDED or
     * SPEAKABLE_CANCELLED event that updated the
     * speaking queue.
     */
    public static final int QUEUE_UPDATED = 0x3001000;

    public static final int SYNTHESIZER_BUFFER_UNFILLED = 0x3002000;

    public static final int SYNTHESIZER_BUFFER_READY = 0x50348032;

    /**
     * The default mask for events in this class.
     * <p>
     * The following events, in addition to events in
     * EngineEvent.DEFAULT_MASK, are delivered by default:
     * QUEUE_EMPTIED.
     * <p>
     * The QUEUE_UPDATED event may be enabled if desired.
     */
    public static final int DEFAULT_MASK = EngineEvent.DEFAULT_MASK | QUEUE_EMPTIED
            | SYNTHESIZER_BUFFER_UNFILLED | SYNTHESIZER_BUFFER_READY;

    private boolean topOfQueueChanged;

    /**
     * Constructs a SynthesizerEvent to indicate a change in
     * state or other activity.
     * <p>
     * SynthesizerEvent includes an event source,
     * event identifier, old and new states,
     * problem if there is an ENGINE_ERROR event,
     * and a topOfQueueChanged flag
     * <p>
     * The old and new states are zero if
     * the engine states are unknown or undefined.
     * For an ENGINE_ERROR, problem should be non-null.
     * The topOfQueueChanged flag is true if the top item on the
     * speech output queue changed.
     * @param source the Synthesizer that issued the event
     * @param id the identifier for the event type
     * @param oldEngineState the Engine state prior to this event
     * @param newEngineState the Engine state following this event
     * @param problem description of any detected error
     * @param topOfQueueChanged true if the top item on
     *         the speech output queue changed
     * @see javax.speech.EngineEvent#ENGINE_ERROR
     * @see javax.speech.synthesis.SynthesizerEvent#QUEUE_EMPTIED
     * @see javax.speech.synthesis.SynthesizerEvent#QUEUE_UPDATED
     */
    public SynthesizerEvent(Synthesizer source, int id, long oldEngineState,
                            long newEngineState, Throwable problem, boolean topOfQueueChanged)
            throws IllegalArgumentException {
        super(source, id, oldEngineState, newEngineState, problem);
        if ((id != ENGINE_ERROR) && (problem != null)) {
            throw new IllegalArgumentException("A problem can only be specified for ENGINE_ERROR");
        }
        if (topOfQueueChanged && (id != QUEUE_UPDATED)
                && (id != QUEUE_EMPTIED)) {
            throw new IllegalArgumentException("topOfQueueChanged can only be set for the events" +
                            " QUEUE_UPDATED and QUEUE_EMPTIED");
        }
        this.topOfQueueChanged = topOfQueueChanged;
    }

    /**
     * Returns the change status of the top of the Synthesizer queue.
     * <p>
     * The value is true for a QUEUE_UPDATED event when the top item
     * in the speech output queue has changed.
     * @return true when the top of the speech output queue has changed
     * @see javax.speech.synthesis.SynthesizerEvent#QUEUE_UPDATED
     */
    public boolean isTopOfQueueChanged() {
        int id = getId();
        if (id == QUEUE_UPDATED) {
            return topOfQueueChanged;
        }

        return false;
    }

    @Override
    protected void id2String(StringBuffer str) {
        maybeAddId(str, QUEUE_EMPTIED, "QUEUE_EMPTIED");
        maybeAddId(str, QUEUE_UPDATED, "QUEUE_UPDATED");
        maybeAddId(str, SYNTHESIZER_BUFFER_READY, "SYNTHESIZER_BUFFER_READY");
        maybeAddId(str, SYNTHESIZER_BUFFER_UNFILLED, "SYNTHESIZER_BUFFER_UNFILLED");
        super.id2String(str);
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        Boolean topOfQueueChangedObject = topOfQueueChanged;
        parameters.add(topOfQueueChangedObject);

        return parameters;
    }
}
