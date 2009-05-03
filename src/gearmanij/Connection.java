/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Connection {
  enum Option {
    READY, PACKET_IN_USE, EXTERNAL_FD
  }

  enum State {
    ADDRINFO, CONNECT, CONNECTING, CONNECTED
  }

  enum SendState {
    NONE, PRE_FLUSH, FORCE_FLUSH, FLUSH, FLUSH_DATA
  }

  enum ReceiveState {
    NONE, READ, READ_DATA
  }

  /**
   * Creates a Connection object for the specified host and port. Use open() and
   * close() to open and close the socket connection.
   * 
   * @param host
   * @param port
   * @throws IOException
   */
  public Connection(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Blocking I/O test code written to step through socket reading and writing
   * in text mode.
   * 
   * @throws IOException
   */
  public void textModeTest() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(socket
        .getInputStream()));
    PrintWriter out = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream())), true);

    // Send all supported text mode commands

    String buffer = "WORKERS";
    out.println(buffer);

    System.out.println("WORKERS reponse:");
    String response = in.readLine();
    while (!response.equals(".")) {
      System.out.println(response);
      response = in.readLine();
    }

    buffer = "STATUS";
    out.println(buffer);

    System.out.println("STATUS reponse:");
    response = in.readLine();
    while (!response.equals(".")) {
      System.out.println(response);
      response = in.readLine();
    }
  }

  /**
   * Sends <code>text</code> to job server with expectation of receiving the
   * same data echoed back.
   * <p>
   * Create unit test to verify ECHO_RES with same data is returned.
   * 
   * @param text
   * @throws IOException
   */
  public Packet echo(String text) throws IOException {
    byte[] data = (text + '\0').getBytes("ASCII");
    Packet request = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ, data);
    request.write(socket.getOutputStream());

    return readPacket();
  }

  /**
   * Registers a JobFunction that a Worker can perform on a Job.
   * 
   * @param function
   */
  public void registerFunction(JobFunction function) throws IOException {
    functions.put(function.getName(), function);

    byte[] data = (function.getName() + '\0').getBytes("ASCII");
    Packet request = new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);
    request.write(socket.getOutputStream());
  }

  /**
   * Registers a JobFunction that a Worker can perform on a Job. If the worker
   * does not respond with a result within the given timeout period in seconds,
   * the job server will assume the work will not be performed by that worker
   * and will again make the work available to be performed by any worker
   * capable of performing this function.
   * 
   * @param function
   */
  public void registerFunction(JobFunction function, int timeout)
      throws IOException {
    functions.put(function.getName(), function);

    // Send CAN_DO_TIMEOUT command to job server

  }

  /**
   * Unregisters with the Connection a function that a worker can perform on a
   * Job.
   * 
   * @param function
   */
  public void unregisterFunction(String name) throws IOException {
    byte[] data = (name + '\0').getBytes("ASCII");
    Packet request = new Packet(PacketMagic.REQ, PacketType.CANT_DO, data);
    request.write(socket.getOutputStream());

    // Potential race condition unless job server acknowledges CANT_DO, though
    // worker could just return JOB_FAIL if it gets a job it just tried to
    // unregister for.
    functions.remove(name);
  }

  /**
   * Unregisters all functions with the Connection.
   * 
   * @param function
   */
  public void unregisterAll() {
    functions.clear();

    // Send RESET_ABILITIES command to job server

  }

  public void grabJob() throws IOException {
    Packet request = new Packet(PacketMagic.REQ, PacketType.GRAB_JOB, null);
    request.write(socket.getOutputStream());

    Packet response = readPacket();

    if (response.getType() == PacketType.NO_JOB) {
      preSleep();
    } else if (response.getType() == PacketType.JOB_ASSIGN) {
      // Parse null terminated params - job handle, function name, function arg
      // See ByteArrayBuffer indexOf(NULL) and  
      byte[] data = response.getData();

      // Perform the job and send back results

    } else {
      // Need to handle other cases here, if any
    }
  }

  /**
   * If non-blocking I/O implemented, worker/connection would go to sleep.
   * 
   * @throws IOException
   */
  public void preSleep() throws IOException {
    Packet request = new Packet(PacketMagic.REQ, PacketType.PRE_SLEEP, null);
    request.write(socket.getOutputStream());
  }

  public void open() throws IOException {
    addr = InetAddress.getByName(host);
    socket = new Socket(addr, port);
  }

  public void close() {
    try {
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      // TODO decide whether to let client handle, log and swallow, etc.
      e.printStackTrace();
    }
  }

  /**
   * Reads from socket and constructs a Packet.
   * 
   * @return
   * @throws IOException
   */
  private Packet readPacket() throws IOException {
    InputStream is = socket.getInputStream();
    byte[] headerBytes = new byte[12];
    readFully(is, headerBytes);
    PacketHeader header = new PacketHeader(headerBytes);
    int dataLength = header.getDataLength();
    byte[] dataBytes = new byte[dataLength];
    if (dataBytes.length > 0) {
        readFully(is, dataBytes);
    }

    return new Packet(header.getMagic(), header.getType(), dataBytes);
  }

  /**
   * Similar to <code>DataInputStream.readFully()</code> with more informative
   * error message.
   */
  private void readFully(InputStream is, byte[] buffer) throws IOException {
    int bytesRead = is.read(buffer);
    if (bytesRead != buffer.length) {
      String msg = "Bad, bad packet: " + ByteUtils.toHex(buffer); 
      throw new RuntimeException(msg);
    }
  }

  private Map<String, JobFunction> functions = new HashMap<String, JobFunction>();
  private String host;
  private int port;
  private InetAddress addr;
  private Socket socket;
}
