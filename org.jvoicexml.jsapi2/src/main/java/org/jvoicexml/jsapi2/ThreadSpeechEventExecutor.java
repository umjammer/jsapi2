/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A speech event executor that is based on a thread.
 *
 * <p>
 * There is only one single thread that is responsible to execute the
 * commands asynchronously.
 * </p>
 *
 * @author Renato Cassaca
 */
public final class ThreadSpeechEventExecutor implements TerminatableSpeechEventExecutor {

    private static final Logger logger = System.getLogger(ThreadSpeechEventExecutor.class.getName());

    /** The thread that executes the commands. */
    private final ExecutorService thread = Executors.newSingleThreadExecutor();

    /** Commands to execute. */
    private final BlockingQueue<Runnable> commands;

    /** <code>false</code> if the executor is terminating. */
    private boolean shouldRun;

    /**
     * Constructs a new object.
     */
    public ThreadSpeechEventExecutor() {
        commands = new java.util.concurrent.LinkedBlockingQueue<>();
        shouldRun = true;
        thread.submit(this::loop);
    }

    @Override
    public void terminate() {
        shouldRun = false;
        thread.shutdown();
        logger.log(Level.TRACE, "shutdown services: " + thread.isShutdown());
    }

    /**
     * Executes the given command.
     *
     * @param command the command to execute.
     */
    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException("Command must not be null!");
        }
        if (!shouldRun) {
            throw new IllegalStateException("SpeechEventExecutor is terminated!");
        }
        commands.offer(command);
    }

    private void loop() {
        while (shouldRun) {
            // Use this thread to run the command
            Runnable command;
            try {
                command = commands.take();
            } catch (InterruptedException ex) {
logger.log(Level.TRACE, "interrupted");
                return;
            }
            if (!shouldRun) {
logger.log(Level.TRACE, "stop looping 1");
                return;
            }
            command.run();
        }
logger.log(Level.TRACE, "stop looping 2");
    }
}
