/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.gearman.PacketConnection;
import org.gearman.client.ClientRequest;
import org.gearman.client.JobResponse;
import org.gearman.common.SocketConnection;
import org.gearman.io.ObjectSender;

public class CustomerRunner {
    private static Serializable completedWork;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        int maxWorkTimeSeconds = Integer.parseInt(args[0]);
        int orderPort = Integer.parseInt(args[1]);
        String className = args[2];
        int reportingPort = Integer.parseInt(args[3]);

        String host = "localhost";
        int maxLoops = maxWorkTimeSeconds * 4;

        Class<?> aClass = Class.forName(className);
        Callable<Serializable> order;
        order = (Callable<Serializable>) aClass.newInstance();
        PacketConnection conn = new SocketConnection(host, orderPort);
        final ClientRequest request = new ClientRequest(conn, "foo", order);

        Thread t = new Thread(new Runnable() {
            public void run() {
                JobResponse resp = request.call();
                completedWork = resp.responseObject();
            }
        });
        t.start();

        for (int i = 0; i < maxLoops && completedWork == null; i++) {
            Thread.sleep(250);
        }

        if (reportingPort != 0) {
            Socket s = new Socket(host, reportingPort);
            new ObjectSender(s).send(completedWork);
            s.close();
        }
    }
}
