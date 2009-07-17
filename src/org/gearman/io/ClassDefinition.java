/**
 * Copyright (C) 2003, 2005 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import org.gearman.util.ClassUtil;
import org.gearman.util.StandardClassUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

class ClassDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String className;

    private final byte[] classBytes;

    public ClassDefinition(Class<?> aClass) throws IOException {
        this(new StandardClassUtil(), aClass);
    }

    public ClassDefinition(ClassUtil classUtil, Class<?> aClass)
            throws IOException {
        this(aClass.getName(), classUtil.getResourceBytes(aClass));
    }

    public ClassDefinition(String className, byte[] classBytes) {
        if (className == null || classBytes == null) {
            throw new IllegalArgumentException();
        }
        this.className = className;
        this.classBytes = classBytes;
    }

    public String className() {
        return className;
    }

    public byte[] classBytes() {
        return classBytes;
    }

    public String toString() {
        return className() + ", size: " + classBytes().length;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.hashCode() != other.hashCode()) {
            return false;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        ClassDefinition that = (ClassDefinition) other;
        if (!className().equals(that.className())) {
            return false;
        }
        return Arrays.equals(classBytes(), that.classBytes());

    }

    public int hashCode() {
        return className().hashCode();
    }
}
