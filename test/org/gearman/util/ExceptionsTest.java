/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.junit.Test;

public class ExceptionsTest {

  @Test
  public void testToRuntime() {
    RuntimeException e1 = new RuntimeException("x");
    assertEquals(e1, Exceptions.toRuntime(e1));

    Exception e2 = new Exception("y");
    assertEquals(e2, Exceptions.toRuntime(e2).getCause());

    IOException e3 = new IOException("z");
    Exception x = Exceptions.toRuntime(e3);
    assertEquals(IORuntimeException.class, x.getClass());

  }

  @Test
  public void testCall() {
    String foo = Exceptions.call(new Callable<String>() {
      public String call() throws Exception {
        return "foo";
      }
    });
    assertEquals("foo", foo);

    RuntimeException expected = null;
    try {
      Exceptions.call(new Callable<String>() {
        public String call() throws Exception {
          throw new Exception("a");
        }
      });
    } catch (RuntimeException e) {
      expected = e;
    }
    assertEquals("a", expected.getCause().getMessage());
  }

}
