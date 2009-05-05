/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.RuntimeIOException;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Gearman worker classes implement this interface, generally by extending AbstractWorker.
 */
public interface Worker {
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
   * Returns an error string for the last error encountered.
   * 
   * @return error string for the last error encountered
   */
  String getError();

  /**
   * Clears all {@link WorkerOption}s.
   * 
   * @return EnumSet of WorkerOptions
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
   * @param options
   *          one or more WorkerOptions
   */
  void removeWorkerOptions(WorkerOption... workerOptions) ;
  
  /**
   * Adds each specified WorkerOption to the current set of Worker options. For example,
   * <code>worker.setWorkerOptions(WorkerOption.NON_BLOCKING, WorkerOption.GRAB_JOB_IN_USE))</code>
   * 
   * @param options
   *          one or more WorkerOptions
   */
  void setWorkerOptions(WorkerOption... workerOptions);
    
  /**
   * Adds a connection to the server and calls <code>connection.open()</code>.
   */
  void addServer(Connection conn);

  /**
   * Sends <code>text</code> to job server with expectation of receiving the
   * same data echoed back.
   * 
   * @param text
   * @throws RuntimeIOException
   */
  String echo(String text, Connection conn);

  /**
   * Blocking I/O test code written to step through socket reading and writing
   * in text mode.
   * 
   * @throws RuntimeIOException
   */
  Map<String, List<String>> textModeTest(Connection conn);

  /**
   * Registers a JobFunction that a Worker can perform on a Job. If the worker
   * does not respond with a result within the given timeout period in seconds,
   * the job server will assume the work will not be performed by that worker
   * and will again make the work available to be performed by any worker
   * capable of performing this function.
   * 
   * @param function
   */
  void registerFunction(JobFunction function, int timeout);

  /**
   * Registers a JobFunction that a Worker can perform on a Job.
   * 
   * @param function
   */
  void registerFunction(JobFunction function);
  
  /**
   * Sets the worker ID in a job server so monitoring and reporting
   * commands can uniquely identify the various workers, and different
   * connections to job servers from the same worker.
   * TODO: Add method to set ID with a single connection.
   *  
   * @param id
   */
  void setWorkerID(String id);

  /**
   * Unregisters with the Connection a function that a worker can perform on a
   * Job.
   * 
   * @param function
   */
  void unregisterFunction(JobFunction function);

  /**
   * Unregisters all functions with the Connection.
   * 
   * @param function
   */
  void unregisterAll();

  Map<Connection, PacketType> grabJob();

  List<Exception> close();

}
