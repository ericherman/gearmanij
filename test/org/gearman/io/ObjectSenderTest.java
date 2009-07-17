/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.TestCase;

public class ObjectSenderTest extends TestCase {
    private ConnectionServer loopback;
    private InetAddress localhost;
    private Socket s;

    protected void setUp() throws Exception {
        localhost = InetAddress.getLocalHost();
    }

    protected void tearDown() throws Exception {
        if (loopback != null) {
            loopback.shutdown();
        }
        if (s != null) {
            s.close();
        }
        localhost = null;
        loopback = null;
        s = null;
    }

    private ObjectInputStream objectInputStream(Socket sock) throws IOException {
        InputStream in = sock.getInputStream();
        return new ObjectInputStream(in);
    }

    public void testWriteObject() throws Exception {
        class Server extends ConnectionServer {
            Object obj;

            public Server() {
                super(0, "testWriteObject");
            }

            public void acceptConnection(Socket sock) throws IOException {
                try {
                    ObjectInputStream ois = objectInputStream(sock);
                    do {
                        obj = ois.readObject();
                    } while (isRunning() && obj instanceof ClassDefinition);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Server server = new Server();
        server.start();

        loopback = server;
        s = new Socket(localhost, server.getPort());
        ObjectSender sender = new ObjectSender(s);
        sender.send("FOO");
        Thread.sleep(ConnectionServer.SLEEP_DELAY);
        assertEquals("FOO", server.obj);
    }

    public void testWriteAndRead() throws Exception {
        loopback = new LoopbackServer();
        loopback.start();
        Object obj1 = "";
        Object obj2 = "";

        s = new Socket(localhost, loopback.getPort());
        ObjectSender sender = new ObjectSender(s);
        sender.send(null);
        sender.send("bar");

        ObjectInputStream ois = objectInputStream(s);
        do {
            obj1 = ois.readObject();
        } while (obj1 instanceof ClassDefinition);
        do {
            obj2 = ois.readObject();
        } while (obj2 instanceof ClassDefinition);

        assertEquals(null, obj1);
        assertEquals("bar", obj2);
    }

    public void testAssumptions() throws Exception {
        loopback = new LoopbackServer();
        loopback.start();

        Object obj1 = "Foo";
        Object[] arr = new Object[] { obj1, obj1 };

        assertSame(arr[0], arr[1]);

        s = new Socket(localhost, loopback.getPort());
        new ObjectSender(s).send(arr);
        arr = (Object[]) new ObjectReceiver(s).receive();

        assertSame(arr[0], arr[1]);
        assertNotSame(obj1, arr[0]);
    }
}
