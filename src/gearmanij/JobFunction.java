/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public interface JobFunction {
  /**
   * Returns the name that will be registered with a Gearman job server for this
   * function. Clients will use this name when creating tasks to be performed as
   * jobs by workers.
   * 
   * @return name of function that can be performed by a worker
   */
  String getName();

  /**
   * Performs the intended job function on a job assigned by a job server. Before
   * returning, the execute() method should set the job state and any data to be
   * returned as results or as job completion status.
   * 
   * @param job
   */
  void execute(Job job);
}
