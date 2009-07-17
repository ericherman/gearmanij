/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.concurrent.Callable;

import org.gearman.PacketConnection;
import org.gearman.client.ClientRequest;
import org.gearman.client.JobResponse;
import org.gearman.common.SocketConnection;
import org.gearman.util.ByteUtils;

public class ReverseObjectRunner {

    private String host;
    private int port;
    private int maxWorkTimeSeconds;
    private PrintStream out;
    private Serializable completedWork;

    public ReverseObjectRunner(String host, int port) {
        this.host = host;
        this.port = port;
        out = System.out;
        maxWorkTimeSeconds = Integer.MAX_VALUE;
    }

    public void setMaxWorkTimeSeconds(int maxWorkTimeSeconds) {
        this.maxWorkTimeSeconds = maxWorkTimeSeconds;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public void reverse(String message) throws Exception {

        Callable<Serializable> order = new ReverseOrder(message);
        PacketConnection conn = new SocketConnection(host, port);
        final ClientRequest request = new ClientRequest(conn, "foo", order);

        Thread t = new Thread(new Runnable() {
            public void run() {
                JobResponse resp = request.call();
                completedWork = ByteUtils.toObject(resp.responseData(), false);
            }
        });
        t.start();

        long start = System.currentTimeMillis();
        long end = start + (maxWorkTimeSeconds * 1000L);
        while (completedWork == null && System.currentTimeMillis() < end) {
            Thread.sleep(250);
        }

        out.println(completedWork);
        out.flush();
    }

    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        ReverseObjectRunner runner = new ReverseObjectRunner(host, port);
        runner.setOut(System.out);

        if (args.length > 3) {
            runner.setMaxWorkTimeSeconds(Integer.parseInt(args[3]));
        }

        String message = args[2];
        runner.reverse(message);
    }

}
