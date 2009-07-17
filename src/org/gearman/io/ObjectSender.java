/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Collection;

/**
 * ObjectSender sends objects over a network connection. It also sends all class
 * files that will be needed in order to deserialize the object on the remote
 * side.
 */
public class ObjectSender {
    private ObjectOutputStream oos;
    private ReferencedClassFinder finder;

    public ObjectSender(Socket s) throws IOException {
        this(s.getOutputStream());
    }

    public ObjectSender(OutputStream out) throws IOException {
        oos = new ObjectOutputStream(out);
        finder = new ReferencedClassFinder();
    }

    public void send(Serializable obj) throws IOException {
        Collection<Class<?>> classes = finder.find(obj);
        for (Class<?> cls : classes) {
            oos.writeObject(new ClassDefinition(cls));
        }

        oos.writeObject(obj);
        oos.flush();
    }
}
