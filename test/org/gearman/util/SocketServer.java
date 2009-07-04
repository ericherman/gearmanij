/*
 * Copyright (C) 2003-2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class SocketServer {
  public static final int SLEEP_DELAY = 25;
  private ServerSocket server;
  private Thread listener;
  private volatile boolean isRunning;
  private final String name;
  private int count = 0;
  int port;

  public SocketServer(String name) {
    isRunning = true;
    this.name = name;
    listener = new Thread(new SocketListener(), name);
  }

  public void start() throws IOException {
    server = new ServerSocket(0);
    port = server.getLocalPort();
    listener.start();
    TestUtil.sleep(SLEEP_DELAY);
  }

  protected abstract void acceptConnection(Socket s);

  public int getPort() {
    return port;
  }

  public InetAddress getInetAddress() {
    return server.getInetAddress();
  }

  public void shutdown() throws IOException {
    isRunning = false;
    listener.interrupt();
    if (server != null) {
      server.close();
    }
    TestUtil.sleep(25);
  }

  public boolean isRunning() {
    return isRunning;
  }

  private class SocketListener implements Runnable {
    public IOException caught = null;

    public void run() {
      try {
        runIO();
      } catch (IOException e) {
        caught = e;
        if (toRuntime(e)) {
          throw new IORuntimeException(e);
        }
      }
    }

    protected void runIO() throws IOException {
      while (true) {
        String nextName = name + "[" + count++ + "]";
        Socket socket = server.accept();
        ConnectionAcceptor acceptor = new ConnectionAcceptor(socket);
        execute(acceptor, nextName);
      }
    }

    /* override with a threadpool if needed */
    protected void execute(Runnable target, String name) {
      new Thread(target, name).start();
    }

    protected boolean toRuntime(IOException e) {
      if (!isRunning) {
        return false;
      }
      String msg = e.getMessage();
      if ("Socket closed".equals(msg)) {
        return false;
      }
      if ("Socket is closed".equals(msg)) {
        return false;
      }
      return true;
    }
  }

  private class ConnectionAcceptor implements Runnable {
    private Socket socket;

    public ConnectionAcceptor(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      acceptConnection(socket);
      try {
        socket.close();
      } catch (IOException e1) { //
      }
    }
  }
}
