/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.IOException;
import java.net.InetAddress;

import org.gearman.PacketConnection;
import org.gearman.client.ClientRequest;
import org.gearman.common.SocketConnection;

public class ResendRunner {

    public static class BisectorClient implements ResendServer.Sender {
        private final String host;
        private final int port;
        private final InetAddress resendHost;
        private final int resendPort;
        private String lastLine;

        public BisectorClient(InetAddress host, int port,
                InetAddress resendHost, int resendPort) {
            this.host = host.getHostName();
            this.port = port;
            this.resendHost = resendHost;
            this.resendPort = resendPort;
        }

        public boolean send(String line) {
            lastLine = line;
            if (line == null || line.length() < 2) {
                return false;
            }
            PacketConnection con = new SocketConnection(host, port);
            BisectJob runnable = new BisectJob(line, resendHost, resendPort);
            ClientRequest request = new ClientRequest(con, runnable);
            request.call();
            return true;
        }

        public String toString() {
            return BisectorClient.class.getSimpleName() + " " + lastLine;
        }
    }

    public static void main(String[] args) throws Exception {
        final InetAddress resendHost = InetAddress.getLocalHost();
        final int resendPort = parseIntArg(args, 0);
        final InetAddress hpHost = parseInetAddressArg(args, 1);
        final int hpPort = parseIntArg(args, 2);
        int maxRunTimeSeconds = parseIntArg(args, 3);
        int quota = parseIntArg(args, 4);

        ResendServer.Sender sender = new BisectorClient(hpHost, hpPort,
                resendHost, resendPort);

        ResendServer server = new ResendServer(resendPort, sender);

        server.start();

        long start = System.currentTimeMillis();
        while (!done(server, start, maxRunTimeSeconds, quota)) {
            Thread.sleep(250);
        }

        server.shutdown();
    }

    private static InetAddress parseInetAddressArg(String[] args, int i)
            throws IOException {
        if (args.length <= i) {
            return InetAddress.getLocalHost();
        }
        return InetAddress.getByName(args[i]);
    }

    private static int parseIntArg(String[] args, int i) {
        return (args.length <= i) ? 0 : Integer.parseInt(args[i]);
    }

    private static boolean done(ResendServer server, long start,
            int maxRunTimeSeconds, int quota) {

        if (maxRunTimeSeconds > 0) {
            long end = start + (maxRunTimeSeconds * 1000L);
            if (System.currentTimeMillis() > end) {
                return true;
            }
        }

        if (quota > 0) {
            if (server.stringsRecieved() >= quota) {
                return true;
            }
        }
        return false;
    }

}
