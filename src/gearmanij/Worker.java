/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.RuntimeIOException;

import java.io.IOException;
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
   * Adds a connection to the server using localhost and the default Gearman port.
   * 
   * @return Connection
   */
  SocketConnection addServer() throws IOException;
  
  /**
   * Adds a connection to the server using the specified host and the default Gearman port.
   * 
   * @param host
   *          hostname where job server is running
   * @return Connection
   */
  SocketConnection addServer(String host) throws IOException;
  
  /**
   * Adds a connection to the server using the specified host and port.
   * 
   * @param host
   *          hostname where job server is running
   * @param port
   *          port on which job server is listening
   * @return Connection
   */
  SocketConnection addServer(String host, int port) throws IOException;

  /**
   * Sends <code>text</code> to job server with expectation of receiving the
   * same data echoed back.
   * 
   * @param text
   * @throws RuntimeIOException
   */
  String echo(String text, SocketConnection conn);

  /**
   * Blocking I/O test code written to step through socket reading and writing
   * in text mode.
   * 
   * @throws IOException
   */
  Map<String, List<String>> textModeTest(SocketConnection conn);

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

  void grabJob();

  List<Exception> close();

}
