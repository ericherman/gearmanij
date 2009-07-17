/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import org.gearman.util.ClassUtil;
import org.gearman.util.StandardClassUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.Policy;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

class ClassDefinitionClassLoader extends SecureClassLoader {
    private final Map<String, byte[]> defs;
    private final ClassUtil classes;
    private CodeSource codeSource;

    public ClassDefinitionClassLoader(ClassLoader parent) {
        this(new StandardClassUtil(), parent);
    }

    ClassDefinitionClassLoader(ClassUtil classes, ClassLoader parent) {
        super(parent);
        this.defs = new HashMap<String, byte[]>();
        this.classes = classes;
        SecurityManager manager = System.getSecurityManager();

        if (manager == null) {
            manager = new SecurityManager();
            Policy myPolicy = new AlienCodeSourcePolicy();
            Policy.setPolicy(myPolicy);
            System.setSecurityManager(manager);
        }
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = getBytes(name);
        if (bytes == null)
            throw new ClassNotFoundException(name);
        int offset = 0;
        return defineClass(name, bytes, offset, bytes.length, codeSource());
    }

    private byte[] getBytes(String className) {
        return defs.get(className);
    }

    public void register(ClassDefinition classDef) {
        defs.put(classDef.className(), classDef.classBytes());
    }

    public InputStream getResourceAsStream(String resource) {
        byte[] bytes = getBytes(classes.toClassName(resource));
        return (bytes == null) ? null : new ByteArrayInputStream(bytes);
    }

    public CodeSource codeSource() {
        return (codeSource != null) ? codeSource : new AlienCodeSource();
    }

    public void useSandBox(boolean sandbox) {
        if (sandbox) {
            codeSource = new AlienCodeSource();
        } else {
            URL url = null;
            java.security.cert.Certificate[] certs = null;
            codeSource = new CodeSource(url, certs);
        }
    }

}
