/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JSAPI - An base implementation for JSR 113.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 */

package org.jvoicexml.jsapi2.protocols.playback;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import javax.sound.sampled.SourceDataLine;


/**
 * An {@link OutputStream} that writes to a {@link SourceDataLine}.
 *
 * @author Dirk Schnelle-Walka
 */
public final class LineOutputStream extends OutputStream implements Closeable {

    private static final Logger logger = System.getLogger(LineOutputStream.class.getName());

    /** The source data line. */
    private SourceDataLine line;

    /**
     * Constructs a new object.
     *
     * @param source the line to write to.
     */
    public LineOutputStream(SourceDataLine source) {
        line = source;
    }

    @Override
    public void write(int b) throws IOException {
        byte[] bytes = new byte[1];
        write(bytes, 0, bytes.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        line.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void close() throws IOException {
        line.drain();
        line.stop();
        line.close();
        logger.log(Level.DEBUG, "line close: " + line.hashCode());
        super.close();
    }

    @Override
    public void flush() throws IOException {
        line.drain();
        super.flush();
    }
}
