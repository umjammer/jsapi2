/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package org.jvoicexml.jsapi2.protocols.capture;

import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;


/**
 * ClasspathURLStreamHandlerProvider.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-13 nsano initial version <br>
 */
public class CaptureURLStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public URLStreamHandler createURLStreamHandler(String s) {
        if (s.equals("capture")) {
//System.err.println("scheme: " + s);
            return new Handler();
        } else {
            return null;
        }
    }
}
