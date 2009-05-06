/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import gearmanij.Connection;
import gearmanij.Constants;
import gearmanij.PacketType;
import gearmanij.SimpleWorker;
import gearmanij.SocketConnection;
import gearmanij.Worker;

import java.io.PrintStream;
import java.util.Map;

public class ReverseWorker {

  private Connection connection;

  private boolean loop = true;

  public ReverseWorker(String host, int port) {
    this.connection = new SocketConnection(host, port);
  }

  public void work() {
    Worker reverse = new SimpleWorker();
    reverse.addServer(connection);
    reverse.registerFunction(new ReverseFunction());

    for (int i = 0; loop; i++) {
      // println("Loop Number: " + i);
      Map<Connection, PacketType> jobs = reverse.grabJob();
      PacketType packetType = jobs.get(connection);

      if (packetType == PacketType.NO_JOB) {
        sleep(1000);
      } else if (packetType == PacketType.JOB_ASSIGN) {
        // println("YAY!");
      } else if (packetType == PacketType.NOOP) {
        // println("noop");
      } else {
        println("Unexpected PacketType: " + packetType);
      }
    }
    // println("FINISHED");
  }

  public void shutdown() {
    loop = false;
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      if (loop) {
        throw new RuntimeException(e);
      }
    }
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

    new ReverseWorker(host, port).work();
  }

  public static void usage(PrintStream out) {
    String[] usage = {
        "usage: gearmanij.example.ReverseWorker [-h<host>] [-p<port>]",
        "\t-h<host> - job server host", "\t-p<port> - job server port",
        "\n\tExample: java gearmanij.example.ReverseWorker",
        "\tExample: java gearmanij.example.ReverseWorker -h127.0.0.1 -p4730", //
    };

    for (String line : usage) {
      out.println(line);
    }
  }

  public static void println(String msg) {
    System.err.println(Thread.currentThread().getName() + ": " + msg);
  }

}
