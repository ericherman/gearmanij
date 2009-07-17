/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.util.Arrays;

import junit.framework.TestCase;

public class StandardClassUtilTest extends TestCase {

    private StandardClassUtil classUtil;

    protected void setUp() {
        classUtil = new StandardClassUtil();
    }

    protected void tearDown() {
        classUtil = null;
    }

    public void testResourceName() {
        String className = getClass().getName();
        assertEquals("org.gearman.util.StandardClassUtilTest", className);

        String expected = "org/gearman/util/StandardClassUtilTest.class";
        assertEquals(expected, classUtil.toResourceName(className));
        assertEquals(expected, classUtil.toResourceName(getClass()));
    }

    public void testToClassName() {
        String className = classUtil.toClassName("aliens/Alien.class");
        assertEquals("aliens.Alien", className);
    }

    public void testLoadResourceBytes() throws Exception {
        byte[] simpleFileBytes = new byte[] { 68, 111, 110, 39, 116, 32, 99,
                104, 97, 110, 103, 101, 32, 109, 101, 33, 10 };

        String fileName = "org/gearman/util/simplefile.txt";
        ClassLoader system = ClassLoader.getSystemClassLoader();
        byte[] result = classUtil.loadResourceBytes(fileName, system);
        assertTrue(Arrays.equals(result, simpleFileBytes));
    }
}
