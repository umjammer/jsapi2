/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package org.jvoicexml.jsapi2.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


/**
 * MacNativeLibrary.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-02-18 nsano initial version <br>
 */
public interface MacNativeLibrary extends Library {

    MacNativeLibrary INSTANCE = Native.load("foundation", MacNativeLibrary.class);

    int noErr = 0;

    int /* OSErr */ GetIndVoice(short index, Pointer /* VoiceSpec */ voice);
}
