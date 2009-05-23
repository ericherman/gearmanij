/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.IORuntimeException;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * A Worker grabs a {@link Job} from a job server, performs the
 * {@link JobFunction} specified on the data in the Job, and returns the results
 * of the processing to the server. The server relays the results to the client
 * that submitted the job. The worker may also return status updates or partial
 * results to the job server.
 */
public interface Worker {
  // These enums were copied over from the C library.
  public enum WorkerOption {
    NON_BLOCKING, PACKET_INIT, GRAB_JOB_IN_USE, PRE_SLEEP_IN_USE, WORK_JOB_IN_USE, CHANGE, GRAB_UNIQ
  }

  enum FunctionOption {
    PACKET_IN_USE, CHANGE, REMOVE
  }

  enum WorkerState {
    START, STATE_FUNCTION_SEND, STATE_CONNECT, STATE_GRAB_JOB_SEND, STATE_GRAB_JOB_RECV, STATE_PRE_SLEEP
  }

  enum WorkState {
    GRAB_JOB, FUNCTION, COMPLETE, FAIL
  }

  /**
   * Wait for a job and call the appropriate callback function when it gets one.
   */
  void work();

  /**
   * Clears all {@link WorkerOption}s.
   */
  void clearWorkerOptions();

  /**
   * Returns {@link java.util.EnumSet} of {@link WorkerOption}s.
   * 
   * @return EnumSet of WorkerOptions
   */
  EnumSet<WorkerOption> getWorkerOptions();

  /**
   * Removes each specified WorkerOption from the current set of Worker options.
   * 
   * @param workerOptions
   *          one or more WorkerOptions
   */
  void removeWorkerOptions(WorkerOption... workerOptions);

  /**
   * Adds each specified WorkerOption to the current set of Worker options. For
   * example,
   * <code>worker.setWorkerOptions(WorkerOption.NON_BLOCKING, WorkerOption.GRAB_JOB_IN_USE))</code>
   * 
   * @param workerOptions
   *          one or more WorkerOptions
   */
  void setWorkerOptions(WorkerOption... workerOptions);

  /**
   * Adds a {@link Connection} to a job server.
   * 
   * @param conn
   *          connection to a job server
   */
  void addServer(Connection conn);

  /**
   * Sends <code>text</code> to a job server with expectation of receiving the
   * same data echoed back.
   * 
   * @param text
   *          String to be echoed
   * @param conn
   *          connection to a job server
   * @throws IORuntimeException
   */
  String echo(String text, Connection conn);

  /**
   * Registers a JobFunction that a Worker can perform on a Job. If the worker
   * does not respond with a result within the given timeout period in seconds,
   * the job server will assume the work will not be performed by that worker
   * and will again make the work available to be performed by any worker
   * capable of performing this function.
   * 
   * @param function
   *          JobFunction that a Worker can perform on a Job
   * @param timeout
   *          time in seconds after job server will consider job to be abandoned
   */
  void registerFunction(JobFunction function, int timeout);

  /**
   * Registers a JobFunction that a Worker can perform on a Job.
   * 
   * @param function
   *          JobFunction that a Worker can perform on a Job
   */
  void registerFunction(JobFunction function);

  /**
   * Sets the worker ID in a job server so monitoring and reporting commands can
   * uniquely identify the connected workers.
   * 
   * @param id
   *          ID that job server should use for an instance of a worker
   */
  void setWorkerID(String id);

  /**
   * Sets the worker ID in a job server so monitoring and reporting commands can
   * uniquely identify the connected workers. If a different ID is set with each
   * job server, and connections can more easily be monitored and reported on
   * independently.
   * 
   * @param id
   *          ID that job server should use for an instance of a worker
   * @param conn
   *          connection to the job server
   */
  void setWorkerID(String id, Connection conn);

  /**
   * Unregisters with the Connection a function that a worker can perform on a
   * Job.
   * 
   * @param function
   *          JobFunction that a Worker can no longer perform on a Job
   */
  void unregisterFunction(JobFunction function);

  /**
   * Unregisters all functions on all Connections.
   */
  void unregisterAll();

  /**
   * Attempts to grab and then execute a Job on each connection.
   * 
   * @return a Map indicating for each connection whether a Job was grabbed
   */
  Map<Connection, PacketType> grabJob();

  /**
   * Attempts to grab and then execute a Job on the specified connection.
   * 
   * @param conn
   *          connection to a job server
   * @return a PacketType indicating with a job was grabbed
   */
  PacketType grabJob(Connection conn);


  /**
   * Stops the work loop; requests to shutdown
   */
  void stop();

  /**
   * Stops the work loop and closes all open connections.
   * 
   * @return a List of any Exceptions thrown when closing connections
   */
  List<Exception> shutdown();

}
