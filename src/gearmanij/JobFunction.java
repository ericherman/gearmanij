/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public interface JobFunction {
  /**
   * Returns the name that will be registered with a Gearman job server for
   * this function. Clients will use this name when creating tasks to be performed
   * as jobs by workers.
   * 
   * @return name of function that can be performed by a worker
   */
  String getName();
  
  /**
   * Performs the job function. The data is a null character-delimited (maybe change this to
   * a ByteArray or try to do some kind of parsing of the bytes) blob of data from a client task
   * that execute(String) must know how to parse.
   * <p>
   * The return value is also a String (or maybe ByteArray) blob that the client must know how
   * to parse.
   * 
   * @param data
   */
  String execute(String data);
}
