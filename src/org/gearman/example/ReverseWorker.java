/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.example;

import java.io.PrintStream;

import org.gearman.PacketConnection;
import org.gearman.Constants;
import org.gearman.Worker;
import org.gearman.common.SocketConnection;
import org.gearman.worker.StandardWorker;

public class ReverseWorker {

    private PacketConnection connection;
    private Worker reverse;

    public ReverseWorker(String host, int port) {
        this(new SocketConnection(host, port));
    }

    public ReverseWorker(PacketConnection connection) {
        this.connection = connection;
    }

    public void start() {
        reverse = new StandardWorker();
        reverse.addServer(connection);
        reverse.registerFunction(ReverseFunction.class);
        reverse.work();
    }

    public void shutdown() {
        reverse.stop();
    }

    public static void main(String[] args) {
        if (args.length > 2) {
            usage(System.out);
            return;
        }
        String host = Constants.GEARMAN_DEFAULT_TCP_HOST;
        int port = Constants.GEARMAN_DEFAULT_TCP_PORT;
        for (String arg : args) {
            if (arg.startsWith("-h")) {
                host = arg.substring(2);
            } else if (arg.startsWith("-p")) {
                port = Integer.parseInt(arg.substring(2));
            }
        }

        new ReverseWorker(host, port).start();
    }

    public static void usage(PrintStream out) {
        String[] usage = {
                "usage: org.gearman.example.ReverseWorker [-h<host>] [-p<port>]",
                "\t-h<host> - job server host",
                "\t-p<port> - job server port",
                "\n\tExample: java org.gearman.example.ReverseWorker",
                "\tExample: java org.gearman.example.ReverseWorker -h127.0.0.1 -p4730", //
        };

        for (String line : usage) {
            out.println(line);
        }
    }

}
