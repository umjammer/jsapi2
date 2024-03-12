/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 54 $
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

package javax.speech;

import java.util.List;

/**
 * Provides information to applications about changes in the state
 * of a speech Engine.
 * <p>
 * EngineEvents are issued to the appropriate listener attached to an Engine.
 * The subclasses of EngineEvent
 * provide specific events for the associated types of Engines.
 * @see javax.speech.Engine
 * @see javax.speech.EngineListener
 * @since 2.0.6
 */
public class EngineEvent extends SpeechEvent {

    // Events.

    /**
     * Event issued when Engine allocation is complete.
     * <p>
     * The ALLOCATED flag of the newEngineState is set.
     */
    public static final int ENGINE_ALLOCATED = 0x1000001;

    /**
     * Event issued when Engine deallocation is complete.
     * <p>
     * The DEALLOCATED flag of the newEngineState is set.
     */
    public static final int ENGINE_DEALLOCATED = 0x1000002;

    /**
     * Event issued when Engine allocation has commenced.
     * <p>
     * The ALLOCATING_RESOURCES flag of the newEngineState is set.
     */
    public static final int ENGINE_ALLOCATING_RESOURCES = 0x1000004;

    /**
     * Event issued when Engine deallocation has commenced.
     * <p>
     * The DEALLOCATING_RESOURCES flag of the newEngineState is set.
     */
    public static final int ENGINE_DEALLOCATING_RESOURCES = 0x1000008;

    /**
     * Event issued when the Engine is paused.
     * <p>
     * The PAUSED flag of the newEngineState is set.
     */
    public static final int ENGINE_PAUSED = 0x1000010;

    /**
     * Event issued when the Engine is resumed.
     * <p>
     * The RESUMED flag of the newEngineState is set.
     */
    public static final int ENGINE_RESUMED = 0x1000020;

    /**
     * Event issued when an Engine loses focus.
     * The Engine changes from the
     * FOCUSED state to the DEFOCUSED state.
     * <p>
     * The DEFOCUSED flag of the newEngineState is set.
     */
    public static final int ENGINE_DEFOCUSED = 0x1000040;

    /**
     * Event issued when an Engine gains focus.
     * The Engine changes from the
     * DEFOCUSED state to the FOCUSED state.
     * <p>
     * The FOCUSED flag of the newEngineState is set.
     */
    public static final int ENGINE_FOCUSED = 0x1000080;

    /**
     * Event issued when an Engine error occurs.
     * <p>
     * This event provides asynchronous notification of an internal error in
     * the Engine which prevents normal behavior of that Engine.
     * The application should deallocate the Engine in this case.
     * <p>
     * The event indicates that the ERROR_OCCURRED bit of the Engine state
     * is set.
     */
    public static final int ENGINE_ERROR = 0x1000100;

    /**
     * The default mask for events in this class.
     * <p>
     * The following events are delivered by default:
     * ENGINE_ALLOCATED, ENGINE_DEALLOCATED,
     * ENGINE_PAUSED, ENGINE_RESUMED,
     * ENGINE_FOCUSED, ENGINE_DEFOCUSED and ENGINE_ERROR.
     * <p>
     * The events ENGINE_ALLOCATING_RESOURCES and ENGINE_DEALLOCATING_RESOURCES
     * represent transient states and may be enabled if desired.
     */
    public static final int DEFAULT_MASK = ENGINE_ALLOCATED
            | ENGINE_DEALLOCATED | ENGINE_PAUSED | ENGINE_RESUMED
            | ENGINE_FOCUSED | ENGINE_DEFOCUSED | ENGINE_ERROR;

    private long oldEngineState;

    private long newEngineState;

    private Throwable problem;

    /**
     * Constructs a generic EngineEvent.
     * <p>
     * The old and new states are zero if the Engine states are
     * unknown or undefined.
     * For an ENGINE_ERROR, problem should be non-null.
     * @param source the object that issued the event
     * @param id the identifier for the event type
     * @param oldEngineState the Engine state prior to this event
     * @param newEngineState the Engine state following this event
     * @param problem description of a detected problem
     * @see java.util.EventObject#getSource()
     * @see javax.speech.SpeechEvent#getId()
     * @see javax.speech.EngineEvent#getOldEngineState()
     * @see javax.speech.EngineEvent#getNewEngineState()
     * @see javax.speech.EngineEvent#getEngineError()
     * @see javax.speech.Engine#getEngineState()
     * @see javax.speech.EngineEvent#ENGINE_ERROR
     */
    public EngineEvent(Engine source, int id, long oldEngineState, long newEngineState, Throwable problem)
            throws IllegalArgumentException {
        super(source, id);

        if ((problem != null) && (id != ENGINE_ERROR)) {
            throw new IllegalArgumentException("A problem can only be provided for ENGINE_ERROR");
        }
        if ((problem == null) && (id == ENGINE_ERROR)) {
            throw new IllegalArgumentException("A problem must be provided for ENGINE_ERROR");
        }
        this.oldEngineState = oldEngineState;
        this.newEngineState = newEngineState;
        this.problem = problem;
    }

    /**
     * Returns the state following this EngineEvent.
     * <p>
     * The value matches the getEngineState method.
     * @return the new Engine state
     * @see javax.speech.Engine#getEngineState()
     */
    public long getNewEngineState() {
        return newEngineState;
    }

    /**
     * Returns the state preceding this EngineEvent.
     * @return the old Engine state
     * @see javax.speech.Engine#getEngineState()
     */
    public long getOldEngineState() {
        return oldEngineState;
    }

    /**
     * Returns the Throwable object that describes the problem when there is
     * an Engine error.
     * <p>
     * This will return non-null when the event ID is ENGINE_ERROR and null
     * otherwise.
     * @return the exception or error describing the problem
     * @see javax.speech.EngineEvent#ENGINE_ERROR
     */
    public Throwable getEngineError() {
        int id = getId();
        if (id == ENGINE_ERROR) {
            return problem;
        }

        return null;
    }

    @Override
    protected void id2String(StringBuffer str) {
        maybeAddId(str, ENGINE_ALLOCATED, "ENGINE_ALLOCATED");
        maybeAddId(str, ENGINE_DEALLOCATED, "ENGINE_DEALLOCATED");
        maybeAddId(str, ENGINE_ALLOCATING_RESOURCES, "ENGINE_ALLOCATING_RESOURCES");
        maybeAddId(str, ENGINE_DEALLOCATING_RESOURCES, "ENGINE_DEALLOCATING_RESOURCES");
        maybeAddId(str, ENGINE_PAUSED, "ENGINE_PAUSED");
        maybeAddId(str, ENGINE_RESUMED, "ENGINE_RESUMED");
        maybeAddId(str, ENGINE_DEFOCUSED, "ENGINE_DEFOCUSED");
        maybeAddId(str, ENGINE_FOCUSED, "ENGINE_FOCUSED");
        maybeAddId(str, ENGINE_ERROR, "ENGINE_ERROR");
        super.id2String(str);
    }

    @Override
    protected List<Object> getParameters() {
        List<Object> parameters = super.getParameters();

        Long oldEngineStateObject = oldEngineState;
        parameters.add(oldEngineStateObject);
        Long newEngineStateObject = newEngineState;
        parameters.add(newEngineStateObject);
        parameters.add(problem);

        return parameters;
    }
}
