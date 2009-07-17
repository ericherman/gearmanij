/**
 * Copyright (C) 2005 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.io.IOException;

/**
 * Bundle of utility methods that do name conversions from classes to resources
 * and back.
 */
public interface ClassUtil {
    public abstract ClassLoader classLoaderFor(Class<?> aClass);

    public abstract String toClassName(String classResourceName);

    public abstract String toResourceName(Class<?> aClass);

    public abstract String toResourceName(String className);

    public abstract byte[] getResourceBytes(Class<?> aClass) throws IOException;
}
