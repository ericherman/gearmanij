/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * This is final simply as a hint to the compiler, it can be un-finalized
 * safely.
 */
public final class StandardClassUtil implements Serializable, ClassUtil {
    private static final long serialVersionUID = 1L;

    public ClassLoader classLoaderFor(Class<?> aClass) {
        ClassLoader cl = aClass.getClassLoader();
        return (cl != null) ? cl : ClassLoader.getSystemClassLoader();
    }

    public byte[] getResourceBytes(Class<?> aClass) throws IOException {
        String resourceName = toResourceName(aClass.getName());
        ClassLoader classLoader = classLoaderFor(aClass);
        return loadResourceBytes(resourceName, classLoader);
    }

    byte[] loadResourceBytes(String resourceName, ClassLoader classLoader)
            throws IOException {

        InputStream in = classLoader.getResourceAsStream(resourceName);
        return new Streams().readBytes(in);
    }

    public String toClassName(String classResourceName) {
        if (classResourceName.endsWith(".class")) {
            int end = classResourceName.length() - ".class".length();
            classResourceName = classResourceName.substring(0, end);
        }
        return classResourceName.replace('/', '.');
    }

    public String toResourceName(Class<?> aClass) {
        return toResourceName(aClass.getName());
    }

    public String toResourceName(String className) {
        return className.replace('.', '/') + ".class";
    }
}
