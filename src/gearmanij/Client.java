/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.io.PrintStream;

/**
 * A Client submits a job to a job server and listens for responses.
 */
public interface Client {

  /**
   * Submits a job to a job server.
   * 
   * @param function
   *          Name of the function to be performed
   * @param uniqueId
   *          Unique ID associated with the job
   * @param data
   *          Data to be used by a {@link Worker} to perform the job
   * @return result returned by a Worker
   */
  byte[] execute(String function, String uniqueId, byte[] data);

  /**
   * Stops listening for responses from a job server and closes any open
   * connections.
   */
  void shutdown();

  /**
   * Sets the {@link PrintStream} object to which error messages will be
   * written.
   * 
   * @param err
   *          destination for error messages
   */
  void setErr(PrintStream err);

  /**
   * Writes an error message to the PrintStream specified via
   * {@link #setErr(PrintStream)}
   * 
   * @param msg
   *          An error message
   */
  void printErr(String msg);

}
