package gearmanij;

import static gearmanij.util.ByteUtils.NULL;
import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

public class SubmitJob extends Packet {

  public SubmitJob(String function, String uuid, byte[] data) {
    super(PacketMagic.REQ, //
        PacketType.SUBMIT_JOB, //
        dataBytes(function, uuid, data)//
    );
  }

  private static byte[] dataBytes(String function, String uuid, byte[] data) {
    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(ByteUtils.toAsciiBytes(function)); // Function
    buf.append(NULL); // Null Terminated
    if (uuid != null) {
      buf.append(ByteUtils.toAsciiBytes(uuid));
    }
    buf.append(NULL); // Unique ID
    buf.append(data);// Workload
    return buf.getBytes();
  }

}
