/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman;

public class NotImplementedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NotImplementedException() {
    super();
  }

  public NotImplementedException(String msg) {
    super(msg);
  }

}
