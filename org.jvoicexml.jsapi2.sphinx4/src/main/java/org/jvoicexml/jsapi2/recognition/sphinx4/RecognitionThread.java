/*
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jsapi2.recognition.sphinx4;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import edu.cmu.sphinx.api.SpeechResult;


/**
 * Recognition thread to run the recognizer in parallel.
 *
 * @author Dirk Schnelle-Walka
 * @author Stefan Radomski
 */
final class RecognitionThread extends Thread {

    /** Logger for this class. */
    private static final Logger logger = System.getLogger(RecognitionThread.class.getName());

    /** The wrapper for the sphinx4 recognizer. */
    private Sphinx4Recognizer recognizer;
    private boolean started;

    /**
     * Creates a new object.
     *
     * @param rec The wrapper for the sphinx4 recognizer.
     */
    public RecognitionThread(Sphinx4Recognizer rec) {
        super("RecognitionThread");
        recognizer = rec;
        setDaemon(true);
    }

    /**
     * Runs this thread.
     */
    @Override
    public void run() {
        logger.log(Level.DEBUG, "recognition thread started");

        // Start the Sphinx recognizer
        Jsapi2Recognizer rec = recognizer.getRecognizer();
        started = true;

        // send start of speech and processing event
        // @todo change this;
        recognizer.postStartOfSpeechEvent();
        recognizer.postProcessingEvent();
        while (started) {
            logger.log(Level.DEBUG, "obtaining a result ..");
            SpeechResult speechResult = rec.getResult();
            String hypothesis = speechResult.getHypothesis();
            logger.log(Level.DEBUG, "received result " + hypothesis);
            if (!hypothesis.equalsIgnoreCase("<sil>")) {
                recognizer.postEndOfSpeechEvent();
                recognizer.postListeningEvent();
                started = false;
            }
        }
        // send end of speech and listening event
        // @todo change this;

        logger.log(Level.DEBUG, "recognition thread terminated");
    }

    /**
     * Stop this recognition thread.
     */
    public void stopRecognition() {
        started = false;
    }
}
