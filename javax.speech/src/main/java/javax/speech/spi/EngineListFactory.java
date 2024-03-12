/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 56 $
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

package javax.speech.spi;

import javax.speech.EngineList;
import javax.speech.EngineMode;

/**
 * Provides a list of EngineMode objects that define the available
 * operating modes of a speech Engine.
 * <p>
 * This interface is part of the Service Provider Interface (SPI).
 * Application developers do not need to use this interface.
 * EngineListFactory is used internally by the EngineManager and
 * speech engine implementations.
 * <p>
 * Each speech engine implementation registers an EngineListFactory object
 * with the EngineManager class using the registerEngineListFactory method.
 * When requested by the EngineManager class, each
 * registered EngineListFactory object provides a list with an EngineMode
 * object that describes each available operating mode of the
 * speech engine implementation.
 * <p>
 * The EngineMode objects returned by EngineListFactory in its list must
 * implement the EngineFactory interface and be a sub-class of EngineMode
 * (not EngineMode directly).
 * The EngineManager class calls the createEngine method of the
 * EngineListFactory interface when it is requested to create an Engine.
 * (See EngineFactory for more detail.)
 * <p>
 * The speech engine implementation must perform the same security checks on
 * access to speech Engines as the EngineManager class.
 *
 * @see javax.speech.Engine
 * @see javax.speech.EngineManager
 * @see javax.speech.EngineManager#registerEngineListFactory(java.lang.String)
 * @see javax.speech.spi.EngineFactory
 * @see javax.speech.EngineMode
 * @since 2.0.6
 */
public interface EngineListFactory {

    /**
     * Creates an EngineList containing an EngineMode for each mode of
     * operation of a speech Engine that matches a set of required features.
     * <p>
     * Each object in the list must be a sub-class of EngineMode
     * and must implement the EngineFactory interface.
     * <p>
     * Returns null if no Engines are available or if none meet the
     * specified requirements.
     * <p>
     * The returned list should indicate the list of modes available at the
     * time of the call (the list may change over time).
     * An implementation can create the list at the time of the call or it
     * may be pre-stored.
     *
     * @param require a required set of features
     * @return the EngineList with matching EngineMode descriptions
     * @throws java.lang.SecurityException if the caller does not have permission
     * @see javax.speech.spi.EngineFactory
     * @see javax.speech.EngineMode
     * @see javax.speech.recognition.RecognizerMode
     * @see javax.speech.synthesis.SynthesizerMode
     */
    EngineList createEngineList(EngineMode require);
}
