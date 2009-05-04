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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Connection {

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
    request.write(getOutputStream());
  }

  public void open() throws IOException {
    addr = InetAddress.getByName(host);
    socket = new Socket(addr, port);
  }

  public void close() throws IOException {
    if (socket != null) {
      socket.close();
    }
  }

  /**
   * Reads from socket and constructs a Packet.
   * 
   * @return
   * @throws RuntimeIOException
   */
  public Packet readPacket() {
    return new Packet(getInputStream());
  }

  private String host;
  private int port;
  private InetAddress addr;
  private Socket socket;
}
