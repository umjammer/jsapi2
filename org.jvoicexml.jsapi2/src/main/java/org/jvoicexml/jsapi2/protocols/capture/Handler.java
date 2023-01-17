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

package org.jvoicexml.jsapi2.protocols.capture;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.UnknownHostException;

/**
 * Protocol handler for the capture protocol.
 *
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version 1.0
 */
public final class Handler extends URLStreamHandler {
    /**
     * Constructs a new object.
     */
    public Handler() {
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new CaptureURLConnection(url);
    }

    @Override
    protected int getDefaultPort() {
        return 0;
    }

    @Override
    protected synchronized InetAddress getHostAddress(URL url) {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

}
