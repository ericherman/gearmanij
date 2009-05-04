/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;
import gearmanij.util.IOUtil;
import gearmanij.util.RuntimeIOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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

  /**
   * Blocking I/O test code written to step through socket reading and writing
   * in text mode.
   * 
   * @throws IOException
   */
  public void textModeTest(PrintStream report) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(socket
        .getInputStream()));
    PrintWriter out = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(getOutputStream())), true);

    // Send all supported text mode commands

    reportCommand(in, out, report, "WORKERS");
    reportCommand(in, out, report, "STATUS");
  }

  public void reportCommand(BufferedReader in, PrintWriter out,
			PrintStream report, String command) throws IOException {
    out.println(command);
    report.println(command + " response:");
    String response = in.readLine();
    while (!response.equals(".")) {
      report.println(response);
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
   * @throws RuntimeIOException
   */
  public Packet echo(String text) {
    Packet request = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ,
        ByteUtils.toAsciiBytes(text));
    request.write(getOutputStream());

    return readPacket();
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
    return new Packet(IOUtil.getInputStream(socket));
  }

  private String host;
  private int port;
  private InetAddress addr;
  private Socket socket;
}
