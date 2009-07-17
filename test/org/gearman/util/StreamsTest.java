/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import junit.framework.TestCase;

public class StreamsTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(StreamsTest.class);
    }

    public void testConnect() throws Exception {
        String expected = "sendMe";
        ByteArrayInputStream send = new ByteArrayInputStream(expected
                .getBytes());

        ByteArrayOutputStream receive = new ByteArrayOutputStream();

        new Streams().connect(send, receive);
        Thread.sleep(20);
        send.close();
        assertEquals(expected, new String(receive.toByteArray()));
    }

    /*
     * Our test tries to provide input to the InputStream and recieve output
     * from the OutputStream.
     * 
     * The confusing part is that in order to provide input to the InputStream
     * we need an output stream to write to, and in order to tell what was sent
     * to the OutputStream we need to read it from an input stream.
     * 
     * Less than obvious code, I know. It's easy to get lost in who's coming and
     * who's going.
     */
    public void testConnectMoreRealistically() throws Exception {
        PipedInputStream senderInputStream = new PipedInputStream();
        PrintWriter send = new PrintWriter(new PipedOutputStream(
                senderInputStream));

        PipedInputStream getback = new PipedInputStream();
        PipedOutputStream recieverOutputStream = new PipedOutputStream(getback);
        BufferedReader receive = new BufferedReader(new InputStreamReader(
                getback));

        new Streams().connect(senderInputStream, recieverOutputStream);
        Thread.sleep(20);

        send.println("foo");
        send.flush();
        send.close();
        Thread.sleep(20);

        assertEquals("foo", receive.readLine());

    }
}
