/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionServer implements ConnectionAcceptor, NamedExecutor {
    public static final int SLEEP_DELAY = 25;
    private ServerSocket serverSocket;
    private Thread listener;
    private volatile boolean isRunning;
    private final String name;
    private AtomicInteger counter;
    private int port;
    private ConnectionAcceptor acceptor;
    private NamedExecutor executor;

    public ConnectionServer(int port, String name) {
        this.isRunning = true;
        this.name = name;
        this.port = port;
        this.listener = new Thread(new SocketListener(), name);
        this.counter = new AtomicInteger(0);
        this.acceptor = this;
        this.executor = this;
    }

    public void start() throws IOException {
        serverSocket = newServerSocket(port);
        listener.start();
        pause();
    }

    /* consider making this an interface method */
    public ServerSocket newServerSocket(int serverPort) throws IOException {
        return new ServerSocket(serverPort);
    }

    public void acceptConnection(Socket s) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setSocketAcceptor(ConnectionAcceptor acceptor) {
        if (acceptor == null) {
            throw new IllegalArgumentException();
        }
        this.acceptor = acceptor;
    }

    public void close(Socket socket, Exception thrown) {
        try {
            socket.close();
        } catch (IOException e1) { //
        }
    }

    public int getPort() {
        if (serverSocket == null) {
            return port;
        }
        return serverSocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    public void shutdown() throws IOException {
        isRunning = false;
        listener.interrupt();
        if (serverSocket != null) {
            serverSocket.close();
        }
        pause();
    }

    private void pause() {
        try {
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void listenerLoop() throws IOException {
        int count = counter.incrementAndGet();
        String nextName = name + "[" + count + "]";
        Socket socket = serverSocket.accept();
        Runnable target = new AcceptorRunnable(nextName, acceptor, socket);
        executor.execute(target, nextName);
    }

    public void setNamedExecutor(NamedExecutor executor) {
        if (executor == null) {
            throw new IllegalArgumentException();
        }
        this.executor = executor;
    }

    /* override with a threadpool if needed */
    public void execute(Runnable target, String nextName) {
        new Thread(target, nextName).start();
    }

    private class SocketListener implements Runnable {
        public void run() {
            while (isRunning()) {
                try {
                    listenerLoop();
                } catch (Exception e) {
                    if (!socketClosed(e)) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected boolean socketClosed(Exception e) {
            String msg = e.getMessage();
            return "Socket closed".equals(msg)
                    || "Socket is closed".equals(msg);
        }

        public String toString() {
            return getClass().getSimpleName() + ":" + name;
        }

    }

    public static class AcceptorRunnable implements Runnable {
        private Socket socket;
        private ConnectionAcceptor acceptor;
        private String name;

        public AcceptorRunnable(String name, ConnectionAcceptor acceptor,
                Socket socket) {
            this.name = name;
            this.socket = socket;
            this.acceptor = acceptor;
        }

        public void run() {
            Exception caught = null;
            try {
                acceptor.acceptConnection(socket);
            } catch (IOException e) {
                caught = e;
                throw new RuntimeException(e);
            } finally {
                acceptor.close(socket, caught);
            }
        }

        public String toString() {
            return getClass().getSimpleName() + ":" + name;
        }
    }
}
