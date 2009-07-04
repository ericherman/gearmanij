/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.example;

import java.io.PrintStream;
import java.util.concurrent.Callable;

import org.gearman.PacketConnection;
import org.gearman.Constants;
import org.gearman.client.ClientRequest;
import org.gearman.client.JobResponse;
import org.gearman.common.SocketConnection;
import org.gearman.util.ByteUtils;
import org.gearman.util.Exceptions;

public class DigestClient {

    private PacketConnection conn;

    public DigestClient(PacketConnection conn) {
        this.conn = conn;
    }

    public DigestClient(String host, int port) {
        this(new SocketConnection(host, port));
    }

    public byte[] digest(byte[] input) {
        String function = "digest";
        String uniqueId = null;
        Callable<JobResponse> client = newClientJob(input, function, uniqueId);
        return Exceptions.call(client).responseData();
    }

    protected Callable<JobResponse> newClientJob(byte[] input, String function,
            String uniqueId) {
        return new ClientRequest(conn, function, uniqueId, input);
    }

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 3) {
            usage(System.out);
            return;
        }
        String host = Constants.GEARMAN_DEFAULT_TCP_HOST;
        int port = Constants.GEARMAN_DEFAULT_TCP_PORT;
        byte[] payload = ByteUtils.toUTF8Bytes(args[args.length - 1]);
        for (String arg : args) {
            if (arg.startsWith("-h")) {
                host = arg.substring(2);
            } else if (arg.startsWith("-p")) {
                port = Integer.parseInt(arg.substring(2));
            }
        }
        byte[] md5 = new DigestClient(host, port).digest(payload);
        System.out.println(ByteUtils.toHex(md5));
    }

    public static void usage(PrintStream out) {
        String[] usage = {
                "usage: org.gearman.example.DigestClient [-h<host>] [-p<port>] <string>",
                "\t-h<host> - job server host",
                "\t-p<port> - job server port",
                "\n\tExample: java org.gearman.example.DigestClient Foo",
                "\tExample: java org.gearman.example.DigestClient -h127.0.0.1 -p4730 Bar", //
        };

        for (String line : usage) {
            out.println(line);
        }
    }

}
