/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 69 $
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.speech.spi.EngineFactory;
import javax.speech.spi.EngineListFactory;

import static java.lang.System.getLogger;


/**
 * The initial access point to all speech
 * input and output capabilities.
 * <p>
 * The EngineManager provides the ability to
 * locate, select and create speech Engine instances.
 * <p>
 * The createEngine method creates create speech Engines.
 * It accepts a single parameter that
 * defines the required properties for the Engine to create.
 * The parameter is a subclass of EngineMode corresponding to a
 * subinterface of Engine.
 * <p>
 * The createEngine method operates differently
 * depending on the type of the EngineMode subclass:
 * <p>
 * DEFAULT for an EngineMode: the EngineManager class selects a suitable engine for the default Locale.
 * Application-created EngineMode: the EngineManager class attempts
 * to locate an Engine with all application-specified properties.
 * EngineMode selected from availableEngines:
 * the EngineManager creates an instance of the specific Engine
 * identified by the EngineMode.
 * These EngineModes implement the EngineFactory interface.
 * </p>
 * <pre>
 * // Create a synthesizer for the default Locale
 * Synthesizer synth = (Synthesizer)
 * EngineManager.createEngine(SynthesizerMode.DEFAULT);
 *
 * // Create a recognizer for British English
 * // Note: the UK locale is English spoken in Britain
 * // Can use Locale.UK on J2SE
 * RecognizerMode mode = new RecognizerMode(new Locale("en", "UK"));
 * Recognizer rec = (Recognizer) EngineManager.createEngine(mode);
 *
 * // Obtain a list of all German recognizers
 * RecognizerMode mode = new RecognizerMode(new Locale("de"));
 * EngineList list = EngineManager.availableEngines(mode);
 * // filter by other desired engine properties
 * RecognizerMode chosen = ...
 * // create an engine from "chosen" - an engine-provided descriptor
 * Recognizer rec = (Recognizer) EngineManager.createEngine(chosen);
 * </pre>
 * For cases 1 and 2 there is a defined procedure for selecting an
 * Engine to be created.
 * (For case 3, the application can apply it's own selection procedure.)
 * <p>
 * Locale is treated specially in the selection to ensure that language is
 * always considered when selecting an engine.
 * If a locale is not provided, the default locale
 * (java.util.Locale.getDefault) is used.
 * <p>
 * The selection procedure is:
 * <p>
 * If the Locale not specified, use the language of the default locale
 * in the required properties.
 * If a matching underlying engine has been started already and it has
 * the required properties, return a new corresponding
 * Recognizer of Synthesizer instance that shares the previously
 * allocated underlying resources.
 * The running property is true.
 * Depending on the implementation, underlying engines may be shared
 * by multiple JVMs.
 * Obtain a list of all recognizer or synthesizer modes that match
 * the required properties.
 * Amongst the matching engines, give preference to:
 * <p>
 * A running engine (EngineMode.getRunning is true),
 * An engine that matches the default Locale's country.
 * <p>
 * When more than one engine is a legal match in the final step,
 * the engines are ordered as returned by the availableEngines
 * method.
 * <p>
 * Access to speech engines is restricted.
 * This is to ensure that malicious applets don't use the speech engines inappropriately.
 * For example, a recognizer should not be usable without explicit permission
 * because it could be used to monitor ("bug") an office.
 * <p>
 * A number of methods throughout the API throw SecurityException.
 * Individual implementations of Recognizer and Synthesizer may throw SecurityException
 * <p>
 * The EngineManager class locates, selects and creates speech engines from
 * amongst a list of registered engines.
 * Thus, for an engine to be used by Java applications, the engine must register
 * itself with EngineManager. There are two registration mechanisms:
 * <p>
 * adding an EngineListFactory class to the speech.properties resource,
 * temporarily register an engine by calling the
 * registerEngineListFactory method.
 * <p>
 * The speech.properties resource provides persistent registration of speech engines.
 * When EngineManager is first called, it looks for the speech.properties resource
 * using getResourceAsStream.
 * This searches the CLASSPATH and looks in the location of the EngineManager class.
 * Engines identified are made available through the methods of EngineManager.
 * <p>
 * The speech.properties resource must contain data in the format that is read
 * by the load method of the Properties class.
 * <p>
 * <A/>
 * EngineManager looks for properties of the form
 * <pre>
 * com.acme.recognizer.EngineListFactory=com.acme.recognizer.AcmeEngineListFactory
 * </pre>
 * <p>
 * This line is interpreted as "the EngineListFactory object for the
 * com.acme.recognizer engine is the class called
 * com.acme.recognizer.AcmeEngineListFactory.
 * When it is first called, the EngineManager class will attempt to create an
 * instance of each EngineListFactory object and will ensure that it implements
 * the EngineListFactory interface.
 * <p>
 * The speech.properties resource may or may not be implemented as a file.
 * The specific details depend on the platform and the implementation.
 * If changes for these properties are permitted, it may be through a user
 * interface provided by the implementation rather than by direct
 * editing of the values.
 * @see javax.speech.recognition.RecognizerMode
 * @see javax.speech.synthesis.SynthesizerMode
 * @see java.lang.Class#getResourceAsStream(java.lang.String)
 * @since 2.0.6
 * @since 2.2.0 use service loader mechanism
 */
public class EngineManager {

    private static final Logger logger = getLogger(EngineManager.class.getName());

    private static final List<EngineListFactory> ENGINE_LIST_FACTORIES;

    private static SpeechEventExecutor executor;

    static {
        ENGINE_LIST_FACTORIES = new ArrayList<>();

        // since 2.2.0
        ServiceLoader.load(EngineListFactory.class).forEach(ENGINE_LIST_FACTORIES::add);
logger.log(Level.TRACE, "factory: by service loader: " + ENGINE_LIST_FACTORIES.size());

        InputStream input;
        try {
            Class<?> clazz = Class.forName("javax.speech.EngineManager");
            input = clazz.getResourceAsStream("/speech.properties");
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (input != null) {
            Properties props = new Properties();
            try {
                props.load(input);

                // Close input
                input.close();
            } catch (IOException e) {
                // Ignore.
            }

            Enumeration<?> keys = props.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String className = (String) props.get(key);
                try {
                    registerEngineListFactory(className);
                } catch (IllegalArgumentException | SecurityException | EngineException e) {
                    // Ignore.
                }
            }
        }
logger.log(Level.TRACE, "factory: total: " + ENGINE_LIST_FACTORIES.size());
    }

    /**
     * Lists EngineMode objects for available engine modes
     * that match the required properties.
     * <p>
     * If the require parameter is null, then all known Engines are listed.
     * <p>
     * Returns a zero-length list if no Engines are available or if no Engines
     * have the required properties. (The method never returns null).
     * <p>
     * The order of the EngineMode objects in the list is partially defined.
     * For each registered engine (technically, each registered EngineListFactory
     * object) the order of the descriptors is preserved.
     * Thus, each installed speech engine should order its descriptor objects
     * with the most useful modes first, for example, a mode that is already
     * loaded and running.
     * @param require an EngineMode defining the required features or null for all
     * @return list of mode descriptors with the required properties
     * @throws java.lang.SecurityException if the caller does not have permission for this request
     * @see javax.speech.Engine
     */
    public static EngineList availableEngines(EngineMode require) {
        List<EngineMode> modes = new ArrayList<>();

        for (EngineListFactory factory : ENGINE_LIST_FACTORIES) {
            EngineList list = factory.createEngineList(require);
logger.log(Level.TRACE, "FACTORY: " + factory.getClass().getSimpleName() + (list == null ? "" : ": MODES[" + list.size() + "]: " + list));
            if (list != null) {
                Enumeration<EngineMode> currentModes = list.elements();
                while (currentModes.hasMoreElements()) {
                    EngineMode mode = currentModes.nextElement();
                    modes.add(mode);
                }
            }
        }

        EngineMode[] foundModes = modes.toArray(EngineMode[]::new);

        return new EngineList(foundModes);
    }

    /**
     * Creates an Engine with specified required properties.
     * <p>
     * If there is no Engine with the required properties the method returns null.
     * <p>
     * The required properties defined in the input parameter are provided
     * as an EngineMode object.
     * Subinterfaces of Engine provide examples of how to create a particular
     * kind of Engine.
     * Subclasses of EngineMode provide a DEFAULT field
     * to select an Engine that supports the language of the default Locale.
     * <p>
     * A mode descriptor may be either application-created
     * or a mode descriptor returned by the availableEngines method.
     * <p>
     * The mechanisms for creating an Engine are described above in detail.
     * @param require required engine properties
     * @return an engine matching the required properties or null if none is available
     * @throws java.lang.IllegalArgumentException if the EngineMode is null or contains invalid values
     * @throws javax.speech.EngineException if the engine defined by the EngineMode could not be properly created
     * @throws java.lang.SecurityException if the caller does not have permission
     * @see javax.speech.EngineManager#availableEngines(javax.speech.EngineMode)
     * @see javax.speech.EngineMode
     */
    public static Engine createEngine(EngineMode require) throws IllegalArgumentException, EngineException {
        if (require == null) {
            throw new IllegalArgumentException("An engine mode must be specified to create an engine!");
        }
        SpeechLocale defaultLocale = SpeechLocale.getDefault();
        // TODO Evaluate the default Locale
        EngineList list = availableEngines(require);
logger.log(Level.TRACE, "TOTAL MODES[" + list.size() + "]: " + list);

        Enumeration<EngineMode> enumeration = list.elements();
        EngineFactory preferredFactory = null;
        Boolean preferredFactoryRunning = null;
        while (enumeration.hasMoreElements()) {
            EngineMode mode = enumeration.nextElement();
            if (mode instanceof EngineFactory factory) {
                if (preferredFactory == null) {
                    preferredFactory = factory;
                    preferredFactoryRunning = mode.getRunning();
                }

                Boolean currentFactoryRunning = mode.getRunning();
                Boolean trueVal = Boolean.TRUE;
                if (trueVal.equals(currentFactoryRunning)) {
                    if (!trueVal.equals(preferredFactoryRunning)) {
                        preferredFactory = factory;
                    }
                }
            } else {
logger.log(Level.WARNING, "EngineFactory is not implemented for " + mode.getClass().getName());
            }
        }

        if (preferredFactory == null) {
            return null;
        }

        return preferredFactory.createEngine();
    }

    /**
     * Returns the SpeechEventExecutor for this VM.
     * <p>
     * Engine vendors can use this to fire events while allowing applications
     * to synchronize with various event queues.
     * <p>
     * The default is null meaning that the default Executor will be used.
     * @return the current SpeechEventExecutor
     * @see javax.speech.EngineManager#setSpeechEventExecutor(javax.speech.SpeechEventExecutor)
     */
    public static SpeechEventExecutor getSpeechEventExecutor() {
        return executor;
    }

    /**
     * Sets the SpeechEventExecutor used to fire events for this
     * application.
     * <p>
     * Applications can use this to synchronize speech events with other
     * event queues.
     * <p>
     * By default, SpeechEvents are delivered to applications
     * using a Thread internal to the
     * Engine implementation.
     * Developers should be careful to avoid blocking this callback
     * Thread for extended periods of time.
     * <p>
     * By setting a custom SpeechEventExecutor, applications can
     * control which Thread the Engine
     * implementation will use to fire events.
     * This allows applications to synchronize SpeechEvents with,
     * for example, user interface events on the deployment platform.
     * <p>
     *
     * A MIDlet developer might use the following to synchronize event delivery
     * with the UI thread:
     * <pre>
     * // Ensure speech events arrive on a MIDlet's UI thread
     * EngineManager.setSpeechEventExecutor(new SpeechEventExecutor() {
     *   public void execute(Runnable r) {
     *     javax.microedition.lcdui.Display.getDisplay(this).callSerially(r);
     *   }
     * });
     * </pre>
     *
     * Whereas an AWT or Swing developer might want to synchronize with
     * the AWT/Swing Event Thread:
     * <pre>
     * // Ensure speech events arrive on an AWT or Swing UI thread
     * EngineManager.setSpeechEventExecutor(new SpeechEventExecutor() {
     *   public void execute(Runnable a_r) {
     *     java.awt.EventQueue.invokeLater(a_r);
     *   }
     * });
     * </pre>
     * A server-side developer might use this feature to control the
     * Thread pool used to deliver events.
     * <p>
     * A null argument for speechEventDispatcher causes the default executor
     * to be used
     * @param speechEventDispatcher the executor to use to dispatch events
     * @see javax.speech.EngineManager#getSpeechEventExecutor()
     */
    public static void setSpeechEventExecutor(SpeechEventExecutor speechEventDispatcher) {
        executor = speechEventDispatcher;
    }

    /**
     * Returns the version of this API.
     * <p>
     * The components are major.minor.build.revision.  An example is "2.0.0".
     * @return textual version information for this API.
     */
    public static String getVersion() {
        return "2.2.1";
    }

    /**
     * Registers a speech engine with the EngineManager class for use by
     * the current application.
     * <p>
     * This call adds the specified class name to the list of
     * EngineListFactory objects.
     * The registered engine is not stored persistently in speech.properties.
     * If className is already registered, the call has no effect.
     * <p>
     * The class identified by className must have an empty constructor.
     * <p>
     * Note that not all devices support the dynamic class loading required by
     * this method.
     * @param className name of a class that implements the EngineListFactory
     *         interface and provides access to an engine implementation
     * @throws java.lang.IllegalArgumentException if className is not a legal class
     *         or it does not implement the EngineListFactory interface
     * @throws javax.speech.EngineException if this implementation does not support this method
     * @throws java.lang.SecurityException if this method is not allowed by the calling application
     */
    public static void registerEngineListFactory(String className)
            throws IllegalArgumentException, EngineException, SecurityException {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("'" + className + "' cannot be loaded!");
        }

        EngineListFactory engineListFactory;
        try {
            engineListFactory = (EngineListFactory) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException("'" + className + "' cannot be created!");
        } catch (IllegalAccessException e) {
            throw new SecurityException("'" + className + "' cannot be created!");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("'" + className + "' does not implement EngineListFactory!");
        }

        if (!(engineListFactory instanceof EngineListFactory)) {
            throw new IllegalArgumentException("'" + className + "' does not implement EngineListFactory");
        }

        for (Object current : ENGINE_LIST_FACTORIES) {
            Class<?> currentClass = current.getClass();
            String currentName = currentClass.getName();
            if (className.equals(currentName)) {
                return;
            }
        }

        ENGINE_LIST_FACTORIES.add(engineListFactory);
    }
}
