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
import java.io.Serializable;
import java.net.Socket;

/**
 * ObjectReceiver receives objects over a network connection. It loads all of
 * the classes using a custom class loader, into a security domain that has no
 * privileges.
 */
public class ObjectReceiver {
    private ClassDefinitionClassLoader loader;
    private ObjectInputStream ois;

    public ObjectReceiver(Socket s) throws IOException {
        this(s, true);
    }

    public ObjectReceiver(Socket s, boolean sandbox) throws IOException {
        this(s.getInputStream(), sandbox);
    }

    public ObjectReceiver(InputStream in, boolean sandbox) throws IOException {
        loader = new ClassDefinitionClassLoader(getClass().getClassLoader());
        loader.useSandBox(sandbox);

        ois = new ObjectInputStreamUsingLoader(loader, in);
    }

    public Serializable receive() throws IOException {
        try {
            Object obj = ois.readObject();
            while (obj instanceof ClassDefinition) {
                loader.register((ClassDefinition) obj);
                obj = ois.readObject();
            }
            return (Serializable) obj;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
