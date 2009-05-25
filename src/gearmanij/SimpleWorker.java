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

public class SimpleWorker implements Worker {

  private EnumSet<WorkerOption> options = EnumSet.noneOf(WorkerOption.class);
  private List<Connection> connections = new LinkedList<Connection>();
  private Map<String, Class<? extends JobFunction>> functions = new HashMap<String, Class<? extends JobFunction>>();
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
    if (timeout <= 0) {
      // Too harsh? Instead, could just call registerFunction(JobFunction).
      throw new IllegalArgumentException("timeout must be a positive integer");
    }
    JobFunction function = getFunctionInstance(functionClass);
    functions.put(function.getName(), functionClass);
    
    byte[] fName = ByteUtils.toUTF8Bytes(function.getName());
    ByteArrayBuffer baBuff = new ByteArrayBuffer(fName);
    baBuff.append(ByteUtils.NULL);
    baBuff.append(ByteUtils.toUTF8Bytes(String.valueOf(timeout)));
    byte[] in = baBuff.getBytes();
    Packet req = new Packet(PacketMagic.REQ, PacketType.CAN_DO_TIMEOUT, in);
    for (Connection conn : connections) {
      conn.write(req);
    }

  }

  /**
   * Registers with all connections a JobFunction that a Worker can perform on a
   * Job.
   * 
   * @param functionClass
   *          Class that implements the {@link JobFunction} interface
   */
  public void registerFunction(Class<? extends JobFunction> functionClass) {
    JobFunction function = getFunctionInstance(functionClass);
    functions.put(function.getName(), functionClass);

    byte[] data = ByteUtils.toUTF8Bytes(function.getName());
    Packet request = new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);
    for (Connection conn : connections) {
      conn.write(request);
    }
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
      Job job = new JobImpl(response.getData());
      execute(job);
      // If successful, call WORK_COMPLETE.
      // Need to add support for WORK_* cases.
      workComplete(conn, job);
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
   * TODO: Consider whether this really needs to be public
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
    Class<? extends JobFunction> functionClass = functions.get(job
        .getFunctionName());
    JobFunction function = null;
    
    if (functionClass != null) {
      function = getFunctionInstance(functionClass);
      if (function == null) {
        String msg = "Worker could not instantiate JobFunction class for "
          + job.getFunctionName();
        throw new RuntimeException(msg);
      }
    } else {
      String msg = "Worker no longer registered to execute function "
        + job.getFunctionName();
      throw new IllegalArgumentException(msg);
    }

    byte[] results = function.execute(job.getData());
    job.setResult(results);
  }
  
  private JobFunction getFunctionInstance(Class<? extends JobFunction> functionClass) {
    JobFunction function = null;

    if (functionClass != null) {
      try {
        function = functionClass.newInstance();
      } catch (InstantiationException e) {
        throw new RuntimeException(e.getMessage());
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    
    return function;
  }

  /**
   * Returns results for a job to the appropriate job server.
   * 
   * TODO: Consider whether this really needs to be public
   * 
   * @param conn
   * @param job
   */
  public void workComplete(Connection conn, Job job) {
    ByteArrayBuffer baBuff = new ByteArrayBuffer(job.getHandle());
    baBuff.append(job.getResult());
    byte[] data = baBuff.getBytes();
    conn.write(new Packet(PacketMagic.REQ, PacketType.WORK_COMPLETE, data));
  }

  public void setErr(PrintStream err) {
    this.err = err;
  }

  public void setOut(PrintStream out) {
    this.out = out;
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
