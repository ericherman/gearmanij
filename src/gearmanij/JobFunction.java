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
   * Performs the job function. The data is a blob of data from a client task
   * that execute(byte[]) must know how to parse.
   * <p>
   * The return value is also a byte[] of data that the client must know how to
   * parse.
   * 
   * @param data
   */
  byte[] execute(byte[] data);
}
