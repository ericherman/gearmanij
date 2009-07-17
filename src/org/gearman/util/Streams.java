/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stream operation utility methods. This class is final simply as a hint to the
 * compiler, feel free to un-finalize it.
 */
public final class Streams {
    private static final int END_OF_STREAM = -1;

    public Thread connect(final InputStream from, final OutputStream to) {
        Thread t = new Thread("StreamConnector") {
            public void run() {
                try {
                    copy(from, to, false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        t.start();
        return t;
    }

    public void copy(InputStream from, OutputStream to) throws IOException {
        copy(from, to, true);
    }

    public void copy(InputStream from, OutputStream to, boolean buffer)
            throws IOException {
        if (buffer) {
            from = new BufferedInputStream(from);
            to = new BufferedOutputStream(to);
        }
        while (true) {
            int i = from.read();
            if (i == END_OF_STREAM) {
                break;
            }
            to.write(i);
        }
        to.flush();
    }

    public String readString(InputStream from) throws IOException {
        return readAll(from).toString();
    }

    public byte[] readBytes(InputStream from) throws IOException {
        return readAll(from).toByteArray();
    }

    private ByteArrayOutputStream readAll(InputStream from) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        copy(from, buf);
        return buf;
    }
}
