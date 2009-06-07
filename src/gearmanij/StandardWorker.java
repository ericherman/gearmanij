/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;
import gearmanij.util.IORuntimeException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Standard implementation of the Worker interface that should meet most needs.
 * <p>
 * After a StandardWorker has been connected to at least one job server with
 * {@link #addServer(Connection)}, the worker must be registered to perform a
 * function in order to grab jobs. A function can be registered by specifying
 * the either a JobFunction class or a JobFunctionFactory that will be used to
 * produce a JobFunction instance. The JobFunction instance is used to execute
 * the function on a Job.
 */
public class StandardWorker implements Worker {

  private EnumSet<WorkerOption> options = EnumSet.noneOf(WorkerOption.class);
  private List<Connection> connections = new LinkedList<Connection>();
  private Map<String, JobFunctionFactory> functions = new HashMap<String, JobFunctionFactory>();
  private volatile boolean running = true;
  private PrintStream err = System.err;
  private PrintStream out = null;

  public void work() {
    while (running) {
      Map<Connection, PacketType> jobs = grabJob();
      int nojob = 0;
      for (Map.Entry<Connection, PacketType> entry : jobs.entrySet()) {
        Connection conn = entry.getKey();
        PacketType packetType = entry.getValue();
        switch (packetType) {
        case NO_JOB:
          nojob++;
          break;
        case JOB_ASSIGN:
        case NOOP:
          break;
        default:
          println(err, conn, " returned unexpected PacketType: ", packetType);
          break;
        }
      }
      if (running && jobs.size() == nojob) {
        sleep(250);
      }
    }
    close();
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      if (running) {
        throw new RuntimeException(e);
      }
    }
  }

  public void clearWorkerOptions() {
    options = EnumSet.noneOf(WorkerOption.class);
  }

  public EnumSet<WorkerOption> getWorkerOptions() {
    return options;
  }

  public void removeWorkerOptions(WorkerOption... workerOptions) {
    for (WorkerOption option : workerOptions) {
      options.remove(option);
    }
  }

  public void setWorkerOptions(WorkerOption... workerOptions) {
    for (WorkerOption option : workerOptions) {
      options.add(option);
    }
  }

  public void addServer(Connection conn) {
    conn.open();
    connections.add(conn);
  }

  public void stop() {
    running = false;
  }

  public List<Exception> shutdown() {
    stop();
    return close();
  }

  public List<Exception> close() {
    println(out, "close");
    List<Exception> exceptions = new ArrayList<Exception>();
    for (Connection conn : connections) {
      try {
        conn.close();
      } catch (Exception e) {
        exceptions.add(e);
      }
    }
    return exceptions;
  }

  public String echo(String text, Connection conn) {
    byte[] in = ByteUtils.toUTF8Bytes(text);
    Packet request = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ, in);
    conn.write(request);
    byte[] out = conn.read().getData();
    return ByteUtils.fromAsciiBytes(out);
  }

  public void registerFunction(JobFunction function, int timeout) {
    registerFunctionFactory(new InstanceJobFunctionFactory(function), timeout);
  }

  public void registerFunction(JobFunction function) {
    registerFunctionFactory(new InstanceJobFunctionFactory(function));
  }

  /**
   * Registers a JobFunction that a Worker can perform on a Job. If the worker
   * does not respond with a result within the given timeout period in seconds,
   * the job server will assume the work will not be performed by that worker
   * and will again make the work available to be performed by any worker
   * capable of performing this function.
   * 
   * @param functionClass
   *          Class that implements the {@link JobFunction} interface
   * @param timeout
   *          positive integer in seconds
   * @throws IllegalArgumentException
   *           if timeout not positive
   */
  public void registerFunction(Class<? extends JobFunction> functionClass,
      int timeout) {
    registerFunctionFactory(new ClassJobFunctionFactory(functionClass), timeout);
  }

  /**
   * Registers with all connections a JobFunction that a Worker can perform on a
   * Job.
   * 
   * @param functionClass
   *          Class that implements the {@link JobFunction} interface
   */
  public void registerFunction(Class<? extends JobFunction> functionClass) {
    registerFunctionFactory(new ClassJobFunctionFactory(functionClass));
  }

  public void registerFunctionFactory(JobFunctionFactory factory, int timeout) {
    functions.put(factory.getFunctionName(), factory);
    registerFunctionAllConnections(factory.getFunctionName(), timeout);
  }

  public void registerFunctionFactory(JobFunctionFactory factory) {
    registerFunctionFactory(factory, 0);
  }

  /**
   * Unregisters with all connections a function that a worker can no longer
   * perform on a Job.
   * 
   * @param functionName
   */
  public void unregisterFunction(String functionName) {
    byte[] data = ByteUtils.toUTF8Bytes(functionName);
    Packet request = new Packet(PacketMagic.REQ, PacketType.CANT_DO, data);
    for (Connection conn : connections) {
      conn.write(request);
    }

    // Potential race condition unless job server acknowledges CANT_DO, though
    // worker could just return JOB_FAIL if it gets a job it just tried to
    // unregister for.
    functions.remove(functionName);
  }

  /**
   * Unregisters all functions on all connections.
   */
  public void unregisterAll() {
    functions.clear();

    Packet req = new Packet(PacketMagic.REQ, PacketType.RESET_ABILITIES, null);
    for (Connection conn : connections) {
      conn.write(req);
    }
  }

  public void setWorkerID(String id) {
    byte[] data = ByteUtils.toUTF8Bytes(id);
    Packet request = new Packet(PacketMagic.REQ, PacketType.SET_CLIENT_ID, data);
    for (Connection conn : connections) {
      conn.write(request);
    }
  }

  public void setWorkerID(String id, Connection conn) {
    byte[] data = ByteUtils.toUTF8Bytes(id);
    Packet request = new Packet(PacketMagic.REQ, PacketType.SET_CLIENT_ID, data);
    conn.write(request);
  }

  public Map<Connection, PacketType> grabJob() {
    Map<Connection, PacketType> jobsGrabbed;
    jobsGrabbed = new LinkedHashMap<Connection, PacketType>();
    for (Connection conn : connections) {
      if (running) {
        try {
          PacketType grabJob = grabJob(conn);
          jobsGrabbed.put(conn, grabJob);
        } catch (IORuntimeException e) {
          if (running) {
            // we're done
          } else {
            e.printStackTrace(err);
          }
        }
      }
    }
    return jobsGrabbed;
  }

  public PacketType grabJob(Connection conn) {
    Packet request = new Packet(PacketMagic.REQ, PacketType.GRAB_JOB, null);
    conn.write(request);

    Packet response = conn.read();
    if (response.getType() == PacketType.NO_JOB) {
      preSleep(conn);
    } else if (response.getType() == PacketType.JOB_ASSIGN) {
      Job job = new WorkerJob(response.getData());
      boolean jobInProgress = true;
      while (jobInProgress) {
        execute(job);
        switch (job.getState()) {
        case COMPLETE:
          workComplete(conn, job);
          jobInProgress = false;
          break;
        case EXCEPTION:
          workException(conn, job);
          jobInProgress = false;
          break;
        case PARTIAL_DATA:
          workPartialData(conn, job);
          break;
        case STATUS:
          returnStatus(conn, job);
          break;
        case WARNING:
          workWarning(conn, job);
          break;
        case FAIL:
          workFail(conn, job);
          jobInProgress = false;
        default:
          String msg = "Function returned invalid job state " + job.getState();
          System.err.println(msg);
          workFail(conn, job);
          jobInProgress = false;
          break;
        }
      }
    } else if (response.getType() == PacketType.NOOP) {
      // do nothing
    } else {
      // Need to handle other cases here, if any
      String msg = "unhandled type: " + response.getType() + " - " + response;
      System.err.println(msg);
    }
    return response.getType();
  }

  /**
   * If non-blocking I/O implemented, worker/connection would go to sleep until
   * woken up with a NOOP command.
   * 
   * @throws IORuntimeException
   */
  public void preSleep(Connection conn) {
    Packet request = new Packet(PacketMagic.REQ, PacketType.PRE_SLEEP, null);
    conn.write(request);
  }

  /**
   * Executes a job by calling the execute() method on the JobFunction for the
   * job. TODO: These RuntimeExceptions should likely cause Worker to send a
   * WORK_EXCEPTION to the job server.
   * 
   * @param job
   * @throws IllegalArgumentException
   *           if Worker not registered to execute the function
   * @throws RuntimeException
   *           any other error occurs while trying to execute the function
   */
  public void execute(Job job) {
    JobFunction function = null;

    String name = job.getFunctionName();
    JobFunctionFactory factory = functions.get(name);
    if (factory == null) {
      String msg = "Worker no longer registered to execute function " + name;
      throw new IllegalArgumentException(msg);
    }

    function = factory.getJobFunction();

    if (function == null) {
      // Do we need this? It indicates a seriously broken JobFunctionFactory
      String msg = "Worker could not instantiate JobFunction for " + name;
      throw new NullPointerException(msg);
    }

    function.execute(job);
  }

  public void workComplete(Connection conn, Job job) {
    returnResults(conn, job, PacketType.WORK_COMPLETE, true);
  }

  public void workException(Connection conn, Job job) {
    returnResults(conn, job, PacketType.WORK_EXCEPTION, true);
  }

  public void workFail(Connection conn, Job job) {
    returnResults(conn, job, PacketType.WORK_FAIL, false);
  }

  public void workWarning(Connection conn, Job job) {
    returnResults(conn, job, PacketType.WORK_WARNING, true);
  }

  public void workPartialData(Connection conn, Job job) {
    returnResults(conn, job, PacketType.WORK_DATA, true);
  }

  public void setErr(PrintStream err) {
    this.err = err;
  }

  public void setOut(PrintStream out) {
    this.out = out;
  }

  private void returnResults(Connection conn, Job job, PacketType command,
      boolean includeData) {
    ByteArrayBuffer baBuff = new ByteArrayBuffer(job.getHandle());
    byte[] data = null;
    if (includeData) {
      baBuff.append(job.getResult());
      data = baBuff.getBytes();
    }
    conn.write(new Packet(PacketMagic.REQ, command, data));
  }

  private void returnStatus(Connection conn, Job job) {
    ByteArrayBuffer baBuff = new ByteArrayBuffer(job.getHandle());
    byte[] data = null;
    baBuff.append(job.getResult());
    data = baBuff.getBytes();
    conn.write(new Packet(PacketMagic.REQ, PacketType.WORK_STATUS, data));
  }

  private void registerFunctionAllConnections(String name, int timeout) {
    byte[] fName = ByteUtils.toUTF8Bytes(name);
    ByteArrayBuffer baBuff = new ByteArrayBuffer(fName);
    PacketType type;
    if (timeout > 0) {
      type = PacketType.CAN_DO_TIMEOUT;
      baBuff.append(ByteUtils.NULL);
      baBuff.append(ByteUtils.toUTF8Bytes(String.valueOf(timeout)));
    } else {
      type = PacketType.CAN_DO;
    }
    Packet req = new Packet(PacketMagic.REQ, type, baBuff.getBytes());
    for (Connection conn : connections) {
      conn.write(req);
    }
  }

  private void println(PrintStream out, Object... msgs) {
    if (out == null) {
      return;
    }
    synchronized (out) {
      out.print(Thread.currentThread().getName());
      out.print(" ");
      out.print(getClass().getSimpleName());
      out.print(": ");
      for (Object msg : msgs) {
        out.print(msg);
      }
      out.println();
    }
  }

}
