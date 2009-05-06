/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */

package gearmanij.example;

import static gearmanij.util.ByteUtils.NULL;
import gearmanij.Connection;
import gearmanij.Constants;
import gearmanij.Packet;
import gearmanij.PacketType;
import gearmanij.SocketConnection;
import gearmanij.SubmitJob;
import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

// TODO : make a DigestClient and extract the common code into a generic "Client"

public class ReverseClient {

  private Connection connection;

  private boolean loop = true;

  public ReverseClient(String host, int port) {
    this.connection = new SocketConnection(host, port);
    connection.open();
  }

  public String reverse(String input) {
    String function = "reverse";
    String uniqueId = null;
    byte[] data = ByteUtils.toAsciiBytes(input);
    Packet reverseRequest = new SubmitJob(function, uniqueId, data);
    connection.write(reverseRequest);
    byte[] respBytes = readResponse();
    String reversed = ByteUtils.fromAsciiBytes(respBytes);
    // println(reversed);
    return reversed;
  }

  private byte[] readResponse() {
    byte[] respBytes = ByteUtils.EMPTY;
    byte[] jobhandle;
    while (loop) {
      Packet fromServer = connection.readPacket();

      PacketType packetType = fromServer.getPacketType();
      if (packetType == PacketType.JOB_CREATED) {
        jobhandle = fromServer.toBytes();
      } else if (packetType == PacketType.WORK_COMPLETE) {
        ByteArrayBuffer dataBuf = new ByteArrayBuffer(fromServer.getData());
        int handleLen = dataBuf.indexOf(NULL);
        // byte[] jobHandle2 = dataBuf.subArray(0, handleLen);
        // println("expected: " + ByteUtils.fromAsciiBytes(jobhandle));
        // println("got:" + ByteUtils.fromAsciiBytes(jobHandle2));
        respBytes = dataBuf.subArray(handleLen, dataBuf.length());
        break;
      } else {
        println("Unexpected PacketType: " + packetType);
        println("Unexpected Packet: " + fromServer);
      }
    }
    return respBytes;
  }

  public void shutdown() {
    loop = false;
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

  public static void println(String msg) {
    System.err.println(Thread.currentThread().getName() + ": " + msg);
  }

}
