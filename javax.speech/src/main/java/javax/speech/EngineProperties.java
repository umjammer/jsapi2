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

// Comp. 2.0.6

/**
 * Defines the set of run-time properties of an Engine.
 * <p>
 * This interface is extended for each type of speech engine.
 * <p>
 * Each property of an engine has a set and get method.
 * The method signatures follow the JavaBeans design patterns.
 * <p>
 * The run-time properties of an engine affect the behavior of a running engine.
 * Technically, properties affect engines in the ALLOCATED state.
 * Normally, property changes are made on an ALLOCATED engine and
 * take effect immediately or soon after the change call.
 * <p>
 * The EngineProperties object for an engine is, however, available
 * in all states of an Engine.
 * Changes made to the properties of engine in the DEALLOCATED
 * or the ALLOCATING_RESOURCES state take effect when the engine next
 * enters the ALLOCATED state.
 * A typical scenario for setting the properties of a non-allocated engine is
 * to specify the initial state of the engine.
 * For example, setting the initial voice of a Synthesizer,
 * or the initial SpeakerProfile of a Recognizer.
 * (Setting these properties prior to allocation is desirable
 * because allocating the engine and then changing the voice or the
 * speaker can be computationally expensive.)
 * <p>
 * When setting any engine property:
 * <p>
 * The engine may choose to ignore or limit a set value either because
 * it does not support changes in a property or because it is out-of-range.
 * The engine will apply the property change as soon as possible,
 * but the change is not necessarily immediate. Thus, all set methods are
 * asynchronous (call may return before the effect takes place).
 * All properties of an engine are bound properties - JavaBeans
 * terminology for a property for which an event is issued when the
 * property changes.
 * A PropertyChangeListener can be attached to the EngineProperties
 * object to receive a property change event when a change takes effect.
 * <p>
 * For example, a call to the setPitch method of the SynthesizerProperties
 * interface to change pitch from 120Hz to 200Hz might fail because the
 * value is out-of-range. If it does succeed, the pitch change might be
 * deferred until the synthesizer can make the change by waiting to the
 * end of the current word, sentence, paragraph or text object.
 * When the change does take effect, a PropertyChangeEvent is issued
 * to all attached listeners with the name of the changed property ("Pitch"),
 * the old value (120) and the new value (200).
 * <p>
 * Set calls take effect in the order in which they are received.
 * If multiple changes are requested for a single property,
 * a separate event is issued for each call, even if the multiple
 * changes take place simultaneously.
 * <p>
 * The properties of an engine are persistent across sessions where possible.
 * It is the engine's responsibility to store and restore the property settings.
 * In multi-user and client-server environments the store/restore policy is at
 * the discretion of the engine.
 * <p>
 * The priority property is used with all Engine instances.
 * It helps ensure desired interaction between applications.
 * Priority may differ between trusted applications
 * (such as those installed directly on the machine) and untrusted applications
 * (such as those downloaded from the Web).
 * @see javax.speech.Engine
 * @see java.beans.PropertyChangeListener
 * @see java.beans.PropertyChangeEvent
 */
public interface EngineProperties {

    /**
     * The minimum priority that an Engine instance can have.
     */
    int MIN_PRIORITY = 0x01;

    /**
     * The default priority assigned to a trusted Engine instance.
     */
    int NORM_TRUSTED_PRIORITY = 0x02;

    /**
     * The default priority assigned to a untrusted Engine instance.
     */
    int NORM_UNTRUSTED_PRIORITY = 0x02;

    /**
     * The maximum priority that a untrusted Engine instance can have.
     */
    int MAX_UNTRUSTED_PRIORITY = 0x03;

    /**
     * The maximum priority that an Engine instance can have.
     */
    int MAX_PRIORITY = 0x05;

    void addEnginePropertyListener(EnginePropertyListener listener);

    void removeEnginePropertyListener(EnginePropertyListener listener);

    /**
     * Gets the priority for this Engine instance.
     * @return the priority for this Engine instance
     * @see javax.speech.EngineProperties#setPriority(int)
     */
    int getPriority();

    /**
     * Sets the priority of this Engine instance.
     * <p>
     * Priorities are values between MIN_PRIORITY and MAX_PRIORITY.
     * The priority used may be lower than the priority requested.
     * In this case, the return value will differ from the value requested.
     * <p>
     * Trusted applications may use the full range of priorities.
     * Untrusted applications have a maximum of MAX_UNTRUSTED_PRIORITY,
     * which is less than MAX_PRIORITY.
     * <p>
     * The default priorities for trusted and untrusted applications are
     * NORM_TRUSTED_PRIORITY and NORM_UNTRUSTED_PRIORITY, respectively.
     * <p>
     * Priorities may determine the order in which Engine instances interact.
     * For example, focus gain and loss may be affected by priority.
     * A trusted application could use a higher priority to avoid losing focus
     * during a user's answer to an important question.
     * <p>
     * Priority should be used with care to avoid starvation of other
     * applications.
     * <p>
     * Changing the priority of an Engine instance may cause it to
     * gain or lose focus.
     * Changing the priority may be asynchronous, so any resulting
     * focus change may not occur immediately.
     * <p>
     * The requested value may be rejected or limited.
     *
     * @param priority the priority for this engine
     * @see javax.speech.EngineProperties#getPriority()
     * @see javax.speech.EngineManager
     * @see javax.speech.EngineProperties#addEnginePropertyListener(javax.speech.EnginePropertyListener)
     * @see javax.speech.EngineProperties#MIN_PRIORITY
     * @see javax.speech.EngineProperties#NORM_TRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#NORM_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_UNTRUSTED_PRIORITY
     * @see javax.speech.EngineProperties#MAX_PRIORITY
     */
    void setPriority(int priority) throws IllegalArgumentException;

    /**
     * Resets all properties to reasonable defaults for the Engine.
     * <p>
     * A property change event is issued for each Engine property that
     * changes as the reset takes effect.
     */
    void reset();

    void setBase(String uri) throws IllegalArgumentException;

    String getBase();
}
