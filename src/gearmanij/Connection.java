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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
   * Blocking I/O test code written to step through socket reading and writing
   * in binary mode.
   * 
   * @param text
   * @throws IOException
   */
  public void echoTest(String text) throws IOException {
    // Send ECHO_REQ command to job server

    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(ByteUtils.getAsciiBytes(text));
    buf.append((byte) 0);
    byte[] data = buf.getBytes();

    Packet packet = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ, data);
    packet.write(socket.getOutputStream());

    DataInputStream is = new DataInputStream(socket.getInputStream());
    int c = is.read();

    // Read magic bytes and confirm it was RES
    // Seems the null byte is skipped, so I made the buffer only 3 bytes.
    byte[] magicBytes = new byte[3];
    is.readFully(magicBytes);

    // Read command/type and confirm it was ECHO_RES = 0x00000011 = 17
    byte[] typeBytes = new byte[4];
    is.readFully(typeBytes);

    // Would we want to construct a Packet object from the response? If so, we need
    // to be able to create a PacketType from its code.
    int type = ByteUtils.fromBigEndian(typeBytes);

    // Read length and confirm it was length of data sent
    int length = is.readInt();

    // Read data and confirm it matches what was sent
    byte[] echoedDataBytes = new byte[data.length];
    int i = 0;
    for (; i < length; i++) {
      c = is.read();
      if (c != -1) {
        echoedDataBytes[i] = (byte) c;
      } else {
        throw new RuntimeException("Bad, bad packet");
      }
    }

  }

  /**
   * Registers a JobFunction that a Worker can perform on a Job.
   * 
   * @param function
   */
  public void registerFunction(JobFunction function) throws IOException {
    // Send CAN_DO command to job server

    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(ByteUtils.getAsciiBytes(function.getName()));
    buf.append((byte) 0);
    byte[] data = buf.getBytes();

    Packet packet = new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);
    packet.write(socket.getOutputStream());
  }

  /**
   * Registers a JobFunction that a Worker can perform on a Job. If the worker does
   * not respond with a result within the given timeout period in seconds, the job server
   * will assume the work will not be performed by that worker and will again make
   * the work available to be performed by any worker capable of performing this
   * function.
   * 
   * @param function
   */
  public void registerFunction(JobFunction function, int timeout)
      throws IOException {
    // Send CAN_DO_TIMEOUT command to job server

  }

  /**
   * Unregisters with the Connection a function that a worker can perform on a Job.
   * 
   * @param function
   */
  public void unregisterFunction(String name) throws IOException {
    // Send CANT_DO command to job server

    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(ByteUtils.getAsciiBytes(name));
    buf.append((byte) 0);
    byte[] data = buf.getBytes();

    Packet packet = new Packet(PacketMagic.REQ, PacketType.CANT_DO, data);
    packet.write(socket.getOutputStream());
  }

  /**
   * Unregisters all functions with the Connection.
   * 
   * @param function
   */
  public void unregisterAll() {
    // Send RESET_ABILITIES command to job server

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

  private String host;
  private int port;
  private InetAddress addr;
  private Socket socket;
}
