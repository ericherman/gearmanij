/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

/**
 * Factory for producing {@link JobFunction} objects. A factory can be
 * registered with a {@link Worker} to allow control over the JobFunction
 * instance that the Worker will call to perform {@link Job}s.
 */
public interface JobFunctionFactory {

  /**
   * Returns the name of the function for which this factory creates JobFunction
   * objects.
   * 
   * @return name of the function for which this factory creates JobFunction
   *         objects
   */
  String getFunctionName();

  /**
   * Factory method for generating a JobFunction object. Returns a JobFunction
   * object that a Worker will call with a Job.
   * 
   * @return JobFunction instance
   */
  JobFunction getJobFunction();

}
