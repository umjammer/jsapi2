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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Comp. 2.0.6

/**
 * The root event class for all speech events.
 * <p>
 * Events from a speech Engine are
 * <I>not</I>
 * synchronized with any other event queues. Synchronization with other event
 * queues must be implemented by the application.  This root event class helps
 * with integration of the Java Speech API and other event-driven components.
 * <p>
 * The
 * {@link javax.speech.EngineManager#setSpeechEventExecutor(javax.speech.SpeechEventExecutor)}
 * method provides examples of event queue integration.
 * @see javax.speech.Engine
 * @see javax.speech.EngineManager#setSpeechEventExecutor(javax.speech.SpeechEventExecutor)
 */
public abstract class SpeechEvent {

    private final Object source;

    private final int id;

    /**
     * A mask helpful in disabling all events in a subclass of SpeechEvent.
     * <p>
     * See the list of known subclasses above.
     */
    public static final int DISABLE_ALL = 0;

    /**
     * A mask helpful in enabling all events in a subclass of SpeechEvent.
     * <p>
     * See the list of known subclasses above.
     */
    public static final int ENABLE_ALL = -1;

    /**
     * Constructs a SpeechEvent.
     * <p>
     * The source must be non-null.
     * @param source the object that issued the event.
     * @param id the identifier for the event type.
     * @see java.util.EventObject#getSource()
     * @see javax.speech.SpeechEvent#getId()
     */
    public SpeechEvent(Object source, int id) {
        this.source = source;
        this.id = id;
    }

    public Object getSource() {
        return source;
    }

    /**
     * Returns the event identifier. Id values are defined for each sub-class
     * of SpeechEvent.
     * @return the event identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Appends a human readable representation of the id to the given
     * representation.
     *
     * @param str the current buffer.
     */
    protected void id2String(StringBuffer str) {
        if (str.length() == 0) {
            str.append(id);
        }
    }

    /**
     * Checks if the given flag is set in the id and adds a human readable
     * description to the existing description if the flag is set.
     *
     * @param str         the existing description
     * @param flag        the flag to check for
     * @param description the description to add
     */
    protected void maybeAddId(StringBuffer str, int flag, String description) {
        if ((id & flag) == flag) {
            if (str.length() > 0) {
                str.append('|');
            }
            str.append(description);
        }
    }

    /**
     * Creates a collection of all parameters.
     *
     * @return collection of all parameters.
     */
    protected List<Object> getParameters() {
        List<Object> parameters = new ArrayList<>();

        Object source = getSource();
        parameters.add(source);
        StringBuffer str = new StringBuffer();
        id2String(str);
        String identifier = str.toString();
        parameters.add(identifier);

        return parameters;
    }

    /**
     * Returns a parameter string that contains the event ID in text form.
     * <p>
     * The method toString may provide more detail.
     * This method is useful for event-logging and for debugging.
     * @return A string that contains the event ID in text form.
     * @see javax.speech.SpeechEvent#toString()
     */
    public String paramString() {
        // TODO this method should be abstract
        return getParameters().stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /**
     * Returns a printable String. Useful for event-logging and debugging.
     * <p>
     * The method paramString also provides printable information.
     * @return A printable string.
     * @see javax.speech.SpeechEvent#paramString()
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + paramString() + "]";
    }
}
