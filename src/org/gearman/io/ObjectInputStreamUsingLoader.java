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
import java.io.ObjectStreamClass;

class ObjectInputStreamUsingLoader extends ObjectInputStream {
    private ClassLoader loader;

    public ObjectInputStreamUsingLoader(ClassLoader loader, InputStream in)
            throws IOException {

        super(in);
        this.loader = loader;
    }

    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws ClassNotFoundException {

        return loader.loadClass(desc.getName());
    }
}
