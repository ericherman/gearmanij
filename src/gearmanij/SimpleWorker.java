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
  private Map<String, JobFunction> functions = new HashMap<String, JobFunction>();
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

  public List<Exception> shutdown() {
    running = false;
    println(out, "Shutdown");
    return close();
  }

  public List<Exception> close() {
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
   * @param function
   * @param timeout
   *          positive integer in seconds
   * @throws IllegalArgumentException
   *           if timeout not positive
   */
  public void registerFunction(JobFunction function, int timeout) {
    if (timeout <= 0) {
      // Too harsh? Instead, could just call registerFunction(JobFunction).
      throw new IllegalArgumentException("timeout must be a positive integer");
    }
    functions.put(function.getName(), function);
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
   * @param function
   */
  public void registerFunction(JobFunction function) {
    functions.put(function.getName(), function);

    byte[] data = ByteUtils.toUTF8Bytes(function.getName());
    Packet request = new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);
    for (Connection conn : connections) {
      conn.write(request);
    }
  }

  /**
   * Unregisters with all connections a function that a worker can perform on a
   * Job.
   * 
   * @param function
   */
  public void unregisterFunction(JobFunction function) {
    byte[] data = ByteUtils.toUTF8Bytes(function.getName());
    Packet request = new Packet(PacketMagic.REQ, PacketType.CANT_DO, data);
    for (Connection conn : connections) {
      conn.write(request);
    }

    // Potential race condition unless job server acknowledges CANT_DO, though
    // worker could just return JOB_FAIL if it gets a job it just tried to
    // unregister for.
    functions.remove(function.getName());
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
        jobsGrabbed.put(conn, grabJob(conn));
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
      execute(conn, job);
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
   * Executes a job.
   * 
   * TODO: Return an object or enum to indicate success, failure, warning,
   * exception, etc. TODO: To support WORK_STATUS, job needs to be able to
   * periodically return progress.
   * 
   * @param conn
   *          TODO: Is conn going to be needed in this method?
   * @param job
   */
  public void execute(Connection conn, Job job) {
    // Perform the job and send back results
    JobFunction function = functions.get(job.getFunctionName());
    if (function == null) {
      String msg = job.getFunctionName() + " " + functions.keySet();
      throw new RuntimeException(msg);
    }
    byte[] data = job.getData();
    byte[] result = function.execute(data);
    job.setResult(result);
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
