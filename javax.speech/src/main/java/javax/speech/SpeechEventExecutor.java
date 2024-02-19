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
 * Interface for objects that execute Runnables.
 * <p>
 * The SpeechEventExecutor provides flexibility to merge SpeechEvents with
 * other event queues.
 * <p>
 * The general intent is that execution can be asynchronous,
 * or at least independent of the caller. For example, one of the
 * simplest implementations of execute is
 * <pre>
 * new Thread(command).start();
 * </pre>
 * For example uses of this class to integrate speech events with
 * lcdui or Swing,
 * see
 * {@link javax.speech.EngineManager#setSpeechEventExecutor(javax.speech.SpeechEventExecutor)}
 * .
 * <p>
 * This interface is compatible with more complete implementations
 * that employ queuing or pooling, or perform additional bookkeeping.
 * For example, see JSR-166 intended for J2SE.
 * Java 2 Platform SE 5.0 defines a compatible interface.
 */
public interface SpeechEventExecutor {

    /**
     * Executes the given command.
     * <p>
     * This method is guaranteed only to arrange for execution,
     * that may actually occur sometime later; for example in a new thread.
     * However, in fully generic use, callers should be prepared for
     * execution to occur in any fashion at all, including immediate
     * direct execution.
     * <p>
     * The method is defined not to throw any checked exceptions during
     * execution of the command. Generally, any problems encountered
     * will be asynchronous and so must be dealt with via callbacks
     * or error handler objects.
     * If necessary, any context-dependent catastrophic errors
     * encountered during actions that arrange for execution could be
     * accompanied by throwing context-dependent unchecked exceptions.
     * <p>
     * However, the method does throw InterruptedException:
     * It will fail to arrange for execution if the current thread
     * is currently interrupted. Further, the general contract
     * of the method is to avoid, suppress, or abort execution if
     * interruption is detected in any controllable context
     * surrounding execution.
     * @param command the command to execute on a thread
     * @throws java.lang.InterruptedException the current thread is currently interrupted
     * @throws java.lang.NullPointerException - if command is null
     */
    void execute(Runnable command) throws IllegalStateException, NullPointerException;
}
