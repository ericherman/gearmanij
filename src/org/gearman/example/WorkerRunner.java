/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import org.gearman.Constants;
import org.gearman.Worker;
import org.gearman.common.SocketConnection;
import org.gearman.worker.JavaFunction;
import org.gearman.worker.StandardWorker;

public class WorkerRunner {
    public static void main(String[] args) throws Exception {
        String host = Constants.GEARMAN_DEFAULT_TCP_HOST;
        if (args.length > 0) {
            host = args[0];
        }
        int port = Constants.GEARMAN_DEFAULT_TCP_PORT;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        int maxWorkTimeSeconds = 0;
        if (args.length > 2) {
            maxWorkTimeSeconds = Integer.parseInt(args[2]);
        }
        int quota = 0;
        if (args.length > 3) {
            quota = Integer.parseInt(args[3]);
        }
        boolean sandbox = false;
        if (args.length > 4) {
            sandbox = !args[4].equalsIgnoreCase(Boolean.FALSE.toString());
        }

        final Worker worker = new StandardWorker();
        worker.addServer(new SocketConnection(host, port));
        worker.registerFunction(ReverseFunction.class);
        worker.registerFunction(new JavaFunction(sandbox));
        new Thread(new Runnable() {
            public void run() {
                worker.work();
            }
        }).start();

        long start = System.currentTimeMillis();
        while (!done(worker, start, maxWorkTimeSeconds, quota)) {
            Thread.sleep(250);
        }

        worker.shutdown();
        
    }

    private static boolean done(Worker worker, long start,
            int maxWorkTimeSeconds, int quota) {

        if (maxWorkTimeSeconds > 0) {
            long end = start + (maxWorkTimeSeconds * 1000L);
            if (System.currentTimeMillis() > end) {
                return true;
            }
        }

        if (quota > 0) {
            if (worker.jobsCompleted() >= quota) {
                return true;
            }
        }

        return false;
    }
}
