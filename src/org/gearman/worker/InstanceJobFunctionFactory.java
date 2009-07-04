/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.worker;

import org.gearman.JobFunction;
import org.gearman.JobFunctionFactory;

public class InstanceJobFunctionFactory implements JobFunctionFactory {
  private final JobFunction jobFunction;

  public InstanceJobFunctionFactory(JobFunction jobFunction) {
    if (jobFunction == null) {
      throw new IllegalArgumentException();
    }
    this.jobFunction = jobFunction;
  }

  public String getFunctionName() {
    return jobFunction.getName();
  }

  public JobFunction getJobFunction() {
    return jobFunction;
  }

}
