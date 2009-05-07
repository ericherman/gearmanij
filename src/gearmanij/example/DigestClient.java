package gearmanij.example;

import gearmanij.Client;
import gearmanij.ConnectionClient;
import gearmanij.Constants;
import gearmanij.SocketConnection;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

public class DigestClient {

  private Client client;

  public DigestClient(Client client) {
    this.client = client;
  }

  public byte[] digest(byte[] input) {
    String function = "digest";
    String uniqueId = null;
    return client.execute(function, uniqueId, input);
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
    Client client = new ConnectionClient(new SocketConnection(host, port));
    byte[] md5 = new DigestClient(client).digest(payload);
    System.out.println(ByteUtils.toHex(md5));
  }

  public static void usage(PrintStream out) {
    String[] usage = {
        "usage: gearmanij.example.DigestClient [-h<host>] [-p<port>] <string>",
        "\t-h<host> - job server host",
        "\t-p<port> - job server port",
        "\n\tExample: java gearmanij.example.DigestClient Foo",
        "\tExample: java gearmanij.example.DigestClient -h127.0.0.1 -p4730 Bar", //
    };

    for (String line : usage) {
      out.println(line);
    }
  }

}