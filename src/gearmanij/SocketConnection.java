/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.IORuntimeException;
import gearmanij.util.IOUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A class which implements the {@link Connection} interface by wrapping a
 * {@link java.net.Socket} for sending and receiving data to a Gearman job
 * server.
 */
public class SocketConnection implements Connection, AdminConnection {

  private String host;
  private int port;
  private Socket socket;
  private PrintStream log;

  /**
   * Creates a SocketConnection for localhost and the default Gearman port.
   */
  public SocketConnection() {
    this(Constants.GEARMAN_DEFAULT_TCP_HOST);
  }

  /**
   * Creates a SocketConnection for the specified host and the default Gearman
   * port.
   * 
   * @param host
   *          hostname where job server is running
   */
  public SocketConnection(String host) {
    this(host, Constants.GEARMAN_DEFAULT_TCP_PORT);
  }

  /**
   * Creates a {@link SocketConnection} for the specified host and port. Use
   * {@link #open()} and {@link #close()} to open and close the connection.
   * 
   * @param host
   * @param port
   */
  public SocketConnection(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void write(Packet request) {
    log("write: ", request);
    request.write(getOutputStream());
  }

  public void open() {
    log("open: ", host + ":" + port);
    socket = IOUtil.newSocket(host, port);
  }

  public void close() {
    log("close: " + socket);
    if (socket != null) {
      IOUtil.close(socket);
    }
  }

  /**
   * Reads from socket and constructs a Packet.
   * 
   * @return the Packet
   * @throws IORuntimeException
   *           if a complete packet cannot be read or if any other I/O exception
   *           occurs
   */
  public Packet readPacket() {
    Packet response = new Packet(getInputStream());
    log("readPacket: ", response);
    return response;
  }

  private PrintWriter bufferedWriter() {
    OutputStreamWriter osw = new OutputStreamWriter(getOutputStream());
    return new PrintWriter(new BufferedWriter(osw), true);
  }

  private BufferedReader bufferedReader() {
    InputStream is = getInputStream();
    return new BufferedReader(new InputStreamReader(is));
  }

  private InputStream getInputStream() {
    return IOUtil.getInputStream(socket);
  }

  private OutputStream getOutputStream() {
    return IOUtil.getOutputStream(socket);
  }

  public void setLog(PrintStream log) {
    this.log = log;
  }

  private void log(Object... args) {
    if (log == null) {
      return;
    }
    StringBuffer buf = new StringBuffer();
    buf.append(Thread.currentThread().getName()).append(": ");
    for (Object arg : args) {
      buf.append(arg);
    }
    log.println(buf.toString());
  }

  public String toString() {
    return host + ":" + port;
  }
  
  //
  // AdminConnection Interface
  //

  public List<String> getFunctionStatus() {
    return getTextModeListResult(AdminConnection.COMMAND_STATUS);
  }

  public String getVersion() {
    BufferedReader in = bufferedReader();
    PrintWriter out = bufferedWriter();
    out.println(AdminConnection.COMMAND_VERSION);
    return IOUtil.readLine(in);
  }

  public List<String> getWorkerInfo() {
    return getTextModeListResult(AdminConnection.COMMAND_WORKERS);
  }

  public boolean setDefaultMaxQueueSize(String functionName) {
    BufferedReader in = bufferedReader();
    PrintWriter out = bufferedWriter();
    StringBuilder sb = new StringBuilder(AdminConnection.COMMAND_MAXQUEUE);
    sb.append(' ').append(functionName);
    out.println(sb);
    String response = IOUtil.readLine(in);
    return "OK".equals(response);
  }

  public boolean setMaxQueueSize(String functionName, int size) {
    BufferedReader in = bufferedReader();
    PrintWriter out = bufferedWriter();
    StringBuilder sb = new StringBuilder(AdminConnection.COMMAND_MAXQUEUE);
    sb.append(' ').append(functionName).append(' ').append(size);
    out.println(sb);
    String response = IOUtil.readLine(in);
    return "OK".equals(response);
  }
  
  /**
   * Sends an admin command to a Gearman job server and returns the results
   * as a List of Strings. This works only for the workers and status text commands.
   * 
   * 
   * The maxqueue and shutdown commands can take arguments and do not return a final line with a '.'.
   * <p>
   * TODO:Rather than potentially blocking forever, there should be a timeout.
   * 
   * @param command
   *          The text command
   * @return results as a List of Strings for the command
   */
  private List<String> getTextModeListResult(String command) {
    BufferedReader in = bufferedReader();
    PrintWriter out = bufferedWriter();
    List<String> response = new ArrayList<String>();
    out.println(command);
    while (true) {
      String line = IOUtil.readLine(in);
      if (line.equals(".")) {
        break;
      }
      response.add(line);
    }
    return response;
  }
  
  
}
