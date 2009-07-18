/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */

package org.gearman.example;

import java.io.PrintStream;

import org.gearman.Constants;
import org.gearman.PacketConnection;
import org.gearman.client.ClientRequest;
import org.gearman.client.JobResponse;
import org.gearman.common.SocketConnection;
import org.gearman.util.ByteUtils;

public class ReverseClient {

    private PacketConnection conn;

    private ClientRequest client;

    public ReverseClient(PacketConnection conn) {
        this.conn = conn;
    }

    public ReverseClient(String host, int port) {
        this(new SocketConnection(host, port));
    }

    public String reverseGearmanFunciton(String input) {
        String function = "reverse";
        String uniqueId = null;
        byte[] data = ByteUtils.toUTF8Bytes(input);

        client = new ClientRequest(conn, function, uniqueId, data);
        JobResponse resp = client.call();
        return ByteUtils.fromUTF8Bytes(resp.responseData());
    }

    public String reverseJavaFunction(String input) {
        String uniqueId = null;
        ReverseOrder order = new ReverseOrder(input);

        client = new ClientRequest(conn, uniqueId, order);
        JobResponse resp = client.call();
        Object obj = resp.responseObject();
        return "" + obj;
    }

    public byte[] getHandle() {
        return client.getHandle();
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 3) {
            usage(System.out);
            return;
        }
        String host = Constants.GEARMAN_DEFAULT_TCP_HOST;
        int port = Constants.GEARMAN_DEFAULT_TCP_PORT;
        String payload = args[args.length - 1];
        for (String arg : args) {
            if (arg.startsWith("-h")) {
                host = arg.substring(2);
            } else if (arg.startsWith("-p")) {
                port = Integer.parseInt(arg.substring(2));
            }
        }
        System.out.println(new ReverseClient(host, port)
                .reverseGearmanFunciton(payload));
    }

    public static void usage(PrintStream out) {
        String[] usage = {
                "usage: org.gearman.example.ReverseClient [-h<host>] [-p<port>] <string>",
                "\t-h<host> - job server host",
                "\t-p<port> - job server port",
                "\n\tExample: java org.gearman.example.ReverseClient Foo",
                "\tExample: java org.gearman.example.ReverseClient -h127.0.0.1 -p4730 Bar", //
        };

        for (String line : usage) {
            out.println(line);
        }
    }

}
