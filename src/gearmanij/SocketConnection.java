/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.IOUtil;
import gearmanij.util.RuntimeIOException;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SocketConnection implements Connection {

  /**
   * Creates a connection to the server using localhost and the default Gearman port.
   */
  public SocketConnection() {
    this(Constants.GEARMAN_DEFAULT_TCP_HOST);
  }

  /**
   * Creates a connection to the server using the specified host and the default Gearman port.
   * 
   * @param host
   *          hostname where job server is running
   */
  public SocketConnection(String host) {
    this(host, Constants.GEARMAN_DEFAULT_TCP_PORT);
  }

  /**
   * Creates a Connection object for the specified host and port. Use open() and
   * close() to open and close the socket connection.
   * 
   * @param host
   * @param port
   */
  public SocketConnection(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public Map<String, List<String>> textMode(List<String> commands) {
    BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(getOutputStream())), true);
    Map<String, List<String>> responses = new LinkedHashMap<String, List<String>>();
    for (String command : commands) {
        List<String> cresp = new ArrayList<String>();
        responses.put(command, cresp);
        out.println(command);
        while (true) {
          String response = IOUtil.readLine(in);
          if (response.equals(".")) {
            break;
          }
          cresp.add(response);
        }
    }
    return responses;
  }

  private InputStream getInputStream() {
    return IOUtil.getInputStream(socket);
  }

  private OutputStream getOutputStream() {
    return IOUtil.getOutputStream(socket);
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
   * @return
   * @throws RuntimeIOException
   */
  public Packet readPacket() {
    Packet response = new Packet(getInputStream());
    log("readPacket: ", response);
    return response;
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
  
  private String host;
  private int port;
  private Socket socket;
  private PrintStream log;
}
