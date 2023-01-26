/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */
package org.jvoicexml.jsapi2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


/**
 * A speech event executor that is based on a thread.
 *
 * <p>
 * There is only one single thread that is responsible to execute the
 * commands asynchronously.
 * </p>
 * @author Renato Cassaca
 */
public final class ThreadSpeechEventExecutor
    implements TerminatableSpeechEventExecutor, Runnable {

    private static final Logger logger = Logger.getLogger(ThreadSpeechEventExecutor.class.getName());

    /** Number of msec to wait before inspecting the command queue. */
    private static final int COMMAND_POLL_INTERVALL = 1000;

    /** The thread that executes the commands. */
    private final ExecutorService thread = Executors.newSingleThreadExecutor();

    /** Commands to execute. */
    private final List<Runnable> commands;

    /** <code>false</code> if the executor is terminating. */
    private boolean shouldRun;

    /**
     * Constructs a new object.
     */
    public ThreadSpeechEventExecutor() {
        commands = new java.util.ArrayList<>();
        shouldRun = true;
        thread.submit(this);
    }

    /**
     * {@inheritDoc}
     *
     * Terminates the execution thread.
     */
    @Override
    protected void finalize() throws Throwable {
        terminate();
        super.finalize();
    }

    @Override
    public void terminate() {
        shouldRun = false;
        synchronized (commands) {
            commands.notifyAll();
        }
        thread.shutdown();
logger.finer("shutdown services: " + thread.isShutdown());
    }

    /**
     * Executes the given command.
     *
     * @param command the command to execute.
     */
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException("Command must not be null!");
        }
        if (!shouldRun) {
            throw new IllegalStateException(
                    "SpeechEventExecutor is terminated!");
        }
        commands.add(command);
        synchronized (commands) {
            commands.notify();
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            while ((commands.isEmpty()) && (shouldRun)) {
                synchronized (commands) {
                    try {
                        commands.wait(COMMAND_POLL_INTERVALL);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
            if (!shouldRun) {
                return;
            }

            //Use this thread to run the command
            Runnable command = commands.get(0);
            commands.remove(0);
            command.run();
        }
    }
}
