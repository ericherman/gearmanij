/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import org.gearman.util.StandardClassUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Visitor;

public class ReferencedClassFinderTest extends TestCase {
    interface Baz extends Serializable {
        String interfaceMethod();
    }

    class Foo {
        public int i;
    }

    class Wiz implements Baz {
        private static final long serialVersionUID = 1L;

        public String interfaceMethod() {
            return "wiz";
        }
    }

    void assertContains(Collection<?> c, Object o) {
        assertTrue("should contain: " + o.toString(), c.contains(o));
    }

    public void testClassesReferencedByInterface() throws Exception {
        class InFoo {
            @SuppressWarnings("unused")
            public final Baz baz;

            public InFoo(Baz baz) {
                this.baz = baz;
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false,
                new StandardClassUtil());
        Wiz wiz = new Wiz();
        InFoo foo = new InFoo(wiz);

        List<Object> list = finder.discoverObjectsForInspection(foo);
        assertContains(list, (wiz));

        Collection<Class<?>> found = finder.find(foo);
        assertContains(found, Wiz.class);
    }

    public void testPrivate() throws Exception {
        class InFoo {
            private final Baz baz;

            public InFoo(Baz baz) {
                this.baz = baz;
            }

            public String toString() {
                return baz.toString();
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false,
                new StandardClassUtil());
        Wiz wiz = new Wiz();
        InFoo foo = new InFoo(wiz);

        List<Object> list = finder.discoverObjectsForInspection(foo);
        assertContains(list, wiz);

        Collection<Class<?>> found = finder.find(foo);
        assertContains(found, Wiz.class);
    }

    public void testNullSafe() throws Exception {
        Collection<Class<?>> found = new ReferencedClassFinder().find(null);
        assertEquals(0, found.size());
    }

    public void testNoReferences() throws Exception {
        Set<Class<?>> done = new HashSet<Class<?>>();
        new ReferencedClassFinder(false, new StandardClassUtil())
                .findReferences(done, new Foo());
        assertContains(done, Foo.class);
    }

    public void testOneReference() throws Exception {
        class Bar {
            @SuppressWarnings("unused")
            public Foo getFoo() {
                return new Foo();
            }
        }

        Set<Class<?>> done = new HashSet<Class<?>>();
        new ReferencedClassFinder(false, new StandardClassUtil())
                .findReferences(done, new Bar());
        assertContains(done, Bar.class);
        assertContains(done, Foo.class);
    }

    public void testAnonymousAndInterfaceReferences() throws Exception {
        class Bar implements Baz {
            private static final long serialVersionUID = 1L;

            public Foo getFoo() {
                return new Foo() {
                    public String toString() {
                        return "anon";
                    }
                };
            }

            public String interfaceMethod() {
                return getFoo().toString();
            }
        }

        Bar bar = new Bar();
        Set<Class<?>> done = new HashSet<Class<?>>();
        new ReferencedClassFinder(false, new StandardClassUtil())
                .findReferences(done, bar);
        assertContains(done, Bar.class);
        assertContains(done, Foo.class);
        assertContains(done, Baz.class);
        assertContains(done, bar.getFoo().getClass());
    }

    public void testInList() throws Exception {
        class InFoo {
            private final List<Serializable> serializables;

            public InFoo(Serializable wiz) {
                this.serializables = new ArrayList<Serializable>();
                serializables.add(wiz);
            }

            public String toString() {
                return serializables.get(0).toString();
            }
        }
        ReferencedClassFinder finder = new ReferencedClassFinder(false,
                new StandardClassUtil());
        InFoo foo = new InFoo(new Wiz());

        Collection<Class<?>> found = finder.find(foo);
        assertContains(found, Wiz.class);
    }

    public void testNoPackageClasses() throws Exception {
        Object o = new Node() {
            public void accept(Visitor obj) {
                System.out.println(obj);
            }
        };
        Set<Class<?>> done = new HashSet<Class<?>>();
        new ReferencedClassFinder().findReferences(done, o);
        assertEquals(0, done.size());
    }
}
