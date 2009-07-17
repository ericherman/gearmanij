/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import org.gearman.io.ConnectionServer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ResendServer extends ConnectionServer {
    public interface Sender {
        boolean shouldSend(String line);

        void send(String line);
    }

    private AtomicInteger counter;
    private PrintStream out;
    private Sender sender;

    public ResendServer(int port, Sender sender) {
        this(port, System.out, sender);
    }

    public ResendServer(int port, PrintStream out, Sender sender) {
        super(port, ResendServer.class.getSimpleName());
        this.out = out;
        this.sender = sender;
        this.counter = new AtomicInteger(0);
    }

    public void acceptConnection(Socket s) throws IOException {
        while (isRunning()) {
            InputStream is = new BufferedInputStream(s.getInputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            counter.incrementAndGet();
            out.println("received: '" + line + "'");
            if (sender.shouldSend(line)) {
                out.println("sending: '" + line + "'");
                sender.send(line);
            }
        }
    }

    public int stringsRecieved() {
        return counter.intValue();
    }

    public void start() throws IOException {
        super.start();
        out.println("Accepting connections on port " + getPort());
    }
}
