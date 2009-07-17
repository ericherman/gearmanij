/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import org.gearman.util.ClassUtil;
import org.gearman.util.StandardClassUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

final class ReferencedClassFinder {
    private ClassUtil classes;
    boolean ignorePackageClasses;

    ReferencedClassFinder() {
        this(true, new StandardClassUtil());
    }

    ReferencedClassFinder(boolean ignorePackageClasses, ClassUtil classes) {
        this.classes = classes;
        this.ignorePackageClasses = ignorePackageClasses;
    }

    public Collection<Class<?>> find(Object obj) {
        Set<Class<?>> references = new HashSet<Class<?>>();
        List<Object> list = discoverObjectsForInspection(obj);
        try {
            for (int i = 0; i < list.size(); i++) {
                findReferences(references, list.get(i));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return references;
    }

    List<Object> discoverObjectsForInspection(Object obj) {
        Set<Object> set = new HashSet<Object>();
        discoverFields(obj, set);
        return new ArrayList<Object>(set);
    }

    private void discoverFields(Object obj, Set<Object> set) {
        if (obj != null && !set.contains(obj)) {
            set.add(obj);
            Field[] fields = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].setAccessible(true);
                    Object inner = fields[i].get(obj);
                    if (inner != null && !isJavaObject(inner))
                        discoverFields(inner, set);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean isJavaObject(Object inner) {
        return classes.toResourceName(inner.getClass()).startsWith("java/");
    }

    void findReferences(Set<Class<?>> done, Object obj)
            throws ClassNotFoundException, IOException {

        Set<Class<?>> toDo = new HashSet<Class<?>>();
        Class<?> startingClass = obj.getClass();
        if (!shouldIgnore(classes.toResourceName(startingClass))) {
            toDo.add(startingClass);
        }
        while (toDo.size() > 0) {
            Class<?> aClass = getOne(toDo);
            JavaClass jClass = toJavaClass(aClass);
            ConstantPool pool = jClass.getConstantPool();
            for (int i = 0; i < pool.getLength(); i++) {
                Constant con = pool.getConstant(i);
                if (con instanceof ConstantClass) {
                    String refClass = pool.getConstantString(i,
                            Constants.CONSTANT_Class);
                    if (!shouldIgnore(refClass)) {
                        ClassLoader cl = classes.classLoaderFor(aClass);
                        Class<?> nextClass = cl.loadClass(classes
                                .toClassName(refClass));
                        if (!done.contains(nextClass)) {
                            toDo.add(nextClass);
                        }
                    }
                }
            }
            done.add(aClass);
            toDo.remove(aClass);
        }
    }

    private boolean shouldIgnore(String className) {
        return className.startsWith("java/") || isArrayClass(className)
                || (ignorePackageClasses && isPackageClass(className));
    }

    private boolean isPackageClass(String name) {
        return name.startsWith("org/gearman/");
    }

    private boolean isArrayClass(String name) {
        return name.startsWith("[");
    }

    private Class<?> getOne(Set<Class<?>> set) {
        return set.iterator().next();
    }

    private JavaClass toJavaClass(Class<?> aClass) throws IOException {
        String classResourceName = classes.toResourceName(aClass);
        ClassLoader cl = classes.classLoaderFor(aClass);
        InputStream is = cl.getResourceAsStream(classResourceName);
        ClassParser parser = new ClassParser(is, classResourceName);
        return parser.parse();
    }

}
