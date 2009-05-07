/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static gearmanij.util.ByteUtils.NULL;
import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

public class ConnectionClient implements Client {

  private Connection connection;

  private boolean loop = true;

  private byte[] currentJobHandle;

  private PrintStream err;

  public ConnectionClient(Connection connection) {
    this.connection = connection;
    this.currentJobHandle = ByteUtils.EMPTY;
    this.err = System.err;
  }

  public byte[] execute(String function, String uniqueId, byte[] data) {
    Packet request = new SubmitJob(function, uniqueId, data);
    connection.open();
    try {
      connection.write(request);
      return readResponse();
    } finally {
      connection.close();
    }
  }

  private byte[] readResponse() {
    byte[] respBytes = ByteUtils.EMPTY;

    while (loop) {
      Packet fromServer = connection.readPacket();

      PacketType packetType = fromServer.getPacketType();
      if (packetType == PacketType.JOB_CREATED) {
        currentJobHandle = fromServer.toBytes();
      } else if (packetType == PacketType.WORK_COMPLETE) {
        ByteArrayBuffer dataBuf = new ByteArrayBuffer(fromServer.getData());
        int handleLen = dataBuf.indexOf(NULL);
        // byte[] jobHandle2 = dataBuf.subArray(0, handleLen);
        // println("expected: " + ByteUtils.fromAsciiBytes(jobhandle));
        // println("got:" + ByteUtils.fromAsciiBytes(jobHandle2));
        currentJobHandle = ByteUtils.EMPTY;
        respBytes = dataBuf.subArray(handleLen, dataBuf.length());
        break;
      } else {
        printErr("Unexpected PacketType: " + packetType);
        printErr("Unexpected Packet: " + fromServer);
      }
    }
    return respBytes;
  }

  public void shutdown() {
    loop = false;
  }

  public void setErr(PrintStream err) {
    this.err = err;
  }

  public void printErr(String msg) {
    err.println(Thread.currentThread().getName() + ": " + msg);
  }

  public String toString() {
    return "connection: " + connection.toString() //
        + " currentJobHandle:" + ByteUtils.toHex(currentJobHandle);
  }

}
