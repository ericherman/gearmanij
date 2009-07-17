/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.net.Socket;

import junit.framework.TestCase;

public class ConnectionServerTest extends TestCase {

    private ConnectionServer server;
    private Socket s1;
    private Socket s2;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(ConnectionServerTest.class);
    }

    protected void setUp() throws Exception {
        server = new LoopbackServer();
        server.start();
    }

    protected void tearDown() throws Exception {
        if (s1 != null) {
            s1.close();
        }
        if (s2 != null) {
            s2.close();
        }
        server.shutdown();

        s1 = null;
        s2 = null;
        server = null;
    }

    public void testSocketLoopback() throws Exception {
        s1 = new Socket("localhost", server.getPort());

        new ObjectSender(s1).send("Hello, World!");

        assertEquals("Hello, World!", new ObjectReceiver(s1).receive());
    }

    public void testTwoSockets() throws Exception {
        s1 = new Socket("localhost", server.getPort());
        ObjectSender sender1 = new ObjectSender(s1);
        ObjectReceiver receiver1 = new ObjectReceiver(s1);

        s2 = new Socket("localhost", server.getPort());
        ObjectSender sender2 = new ObjectSender(s2);
        ObjectReceiver receiver2 = new ObjectReceiver(s2);

        sender1.send("Nevada Test Site Potatoe");
        sender2.send("Idaho Potato");

        assertEquals("Idaho Potato", receiver2.receive());
        assertEquals("Nevada Test Site Potatoe", receiver1.receive());
    }

    public void testPort() throws Exception {
        tearDown();
        server = new LoopbackServer(9973);
        server.start();
        assertEquals("getPort", 9973, server.getPort());
    }

    public void testRandomPort() throws Exception {
        tearDown();
        server = new LoopbackServer(0);
        server.start();
        assertTrue("getPort", server.getPort() > 1024);
    }
}
