/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */

package gearmanij.example;

import gearmanij.Client;
import gearmanij.ConnectionClient;
import gearmanij.Constants;
import gearmanij.SocketConnection;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

public class ReverseClient {

  private Client client;

  public ReverseClient(Client client) {
    this.client = client;
  }

  public ReverseClient(String host, int port) {
    this(new ConnectionClient(new SocketConnection(host, port)));
  }

  public String reverse(String input) {
    String function = "reverse";
    String uniqueId = null;
    byte[] data = ByteUtils.toAsciiBytes(input);
    byte[] respBytes = client.execute(function, uniqueId, data);
    String reversed = ByteUtils.fromAsciiBytes(respBytes);
    // System.out.println(reversed);
    return reversed;
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
    System.out.println(new ReverseClient(host, port).reverse(payload));
  }

  public static void usage(PrintStream out) {
    String[] usage = {
        "usage: gearmanij.example.ReverseClient [-h<host>] [-p<port>] <string>",
        "\t-h<host> - job server host",
        "\t-p<port> - job server port",
        "\n\tExample: java gearmanij.example.ReverseClient Foo",
        "\tExample: java gearmanij.example.ReverseClient -h127.0.0.1 -p4730 Bar", //
    };

    for (String line : usage) {
      out.println(line);
    }
  }

}
