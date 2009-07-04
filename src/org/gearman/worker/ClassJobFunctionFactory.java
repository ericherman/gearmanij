/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.worker;

import org.gearman.JobFunction;
import org.gearman.JobFunctionFactory;
import org.gearman.util.Exceptions;

public class ClassJobFunctionFactory implements JobFunctionFactory {
  private Class<? extends JobFunction> functionClass;

  public ClassJobFunctionFactory(Class<? extends JobFunction> functionClass) {
    if (functionClass == null) {
      throw new IllegalArgumentException();
    }
    this.functionClass = functionClass;
  }

  public String getFunctionName() {
    return getJobFunction().getName();
  }

  public JobFunction getJobFunction() {
    try {
      return functionClass.newInstance();
    } catch (Exception e) {
      throw Exceptions.toRuntime(e);
    }
  }

}
