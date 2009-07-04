/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.util;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Exceptions {

  public static RuntimeException toRuntime(Exception e) {
    if (e instanceof RuntimeException) {
      return (RuntimeException) e;
    }
    if (e instanceof IOException) {
      return new IORuntimeException((IOException) e);
    }
    return new RuntimeException(e);
  }

  public static <T> T call(Callable<T> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      throw toRuntime(e);
    }
  }

}
