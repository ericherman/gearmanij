/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.util.List;

/**
 * Interface implemented by Connection classes that support the administrative
 * text mode commands.
 */
public interface AdminConnection {

  /**
   * Returns information about all workers connected to a job server. The
   * strings in the list are blank space delimited. Each string begins with the
   * file descriptor, IP address, the worker ID, followed by a colon. If a
   * worker has not set an ID a '-' appears in place of the ID. The colon is
   * followed by a list of registered functions a worker can perform. The format
   * is:
   * <p>
   * FD IP-ADDRESS CLIENT-ID : FUNCTION ...
   * 
   * @return information about all workers connected to a job server
   */
  List<String> getWorkerInfo();

  /**
   * Returns status information about each function registered with a job server
   * by at least one worker. The strings in the list are tab delimited. The
   * format is:
   * <p>
   * FUNCTION\tTOTAL\tRUNNING\tAVAILABLE_WORKERS
   * 
   * @return status information about each function registered with a job server
   */
  List<String> getFunctionStatus();

  /**
   * Sets the maximum queue size for a function to the default size for the job
   * server.
   * 
   * @param functionName
   * @return true if successful
   */
  boolean setDefaultMaxQueueSize(String functionName);

  /**
   * Sets the maximum queue size to the specified value for a function to the
   * default size for the job server. If the value is negative, the queue size
   * is unlimited.
   * 
   * @param functionName
   * @param size
   *          The queue size. Unlimited if value is negative.
   * @return true if successful
   */
  boolean setMaxQueueSize(String functionName, int size);

  /**
   * Returns a version string for the job server.
   * 
   * @return version string for the job server
   */
  String getVersion();

  public static final String COMMAND_WORKERS = "workers";
  public static final String COMMAND_STATUS = "status";
  public static final String COMMAND_MAXQUEUE = "maxqueue";
  public static final String COMMAND_VERSION = "version";
}
