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

import javax.speech.Engine;
import javax.speech.EngineException;

// Comp. 2.0.6

/**
 * Implemented by EngineMode objects obtained
 * through calls to the EngineListFactory objects of each speech
 * technology implementation registered with the EngineManager class.
 * <p>
 * This interface is part of the Service Provider Interface (SPI).
 * Application developers do not need to use this interface.
 * EngineFactory is used internally by EngineManager and speech engines.
 * <p>
 * Each Engine implementation must sub-class either RecognizerMode
 * or SynthesizerMode to implement the EngineFactory interface. For example:
 * <p>
 * public MyRecognizerMode extends RecognizerMode implements EngineFactory
 * {
 * ...
 * public Engine createEngine() {
 * // Use mode properties to create an appropriate Engine
 * }
 * }
 * <p>
 * This implementation mechanism allows the Engine to embed additional mode
 * information (engine-specific mode identifiers, GUIDs etc) that
 * simplify creation of the Engine if requested by the EngineManager class.
 * The Engine-specific mode descriptor may need to override equals
 * and other methods if Engine-specific features are defined.
 * <p>
 * The Engine must perform the same security checks on access to speech
 * Engines as the EngineManager class.
 * @see javax.speech.EngineManager
 * @see javax.speech.spi.EngineListFactory
 * @see javax.speech.EngineMode
 * @see javax.speech.recognition.RecognizerMode
 * @see javax.speech.synthesis.SynthesizerMode
 */
public interface EngineFactory {

    /**
     * Creates an Engine with the properties specified by this object.
     * <p>
     * A new Engine should be created in the DEALLOCATED state.
     * @return an Engine object
     * @throws java.lang.IllegalArgumentException the properties of the EngineMode
     *             do not refer to a known engine or engine mode
     * @throws javax.speech.EngineException The engine defined by this EngineMode
     *             could not be properly created.
     * @throws java.lang.SecurityException if the caller does not have
     *             createEngine permission but is attempting to create an engine
     * @see javax.speech.Engine#DEALLOCATED
     */
    Engine createEngine() throws IllegalArgumentException, EngineException;
}
