/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.client;

import static org.gearman.util.ByteUtils.NULL;

import org.gearman.Packet;
import org.gearman.PacketMagic;
import org.gearman.PacketType;
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;

public class SubmitJob extends Packet {

  public SubmitJob(String function, String uuid, byte[] data) {
    super(PacketMagic.REQ, //
        PacketType.SUBMIT_JOB, //
        dataBytes(function, uuid, data)//
    );
  }

  private static byte[] dataBytes(String function, String uuid, byte[] data) {
    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(ByteUtils.toUTF8Bytes(function)); // Function
    buf.append(NULL); // Null Terminated
    if (uuid != null) {
      buf.append(ByteUtils.toUTF8Bytes(uuid));
    }
    buf.append(NULL); // Unique ID
    buf.append(data);// Workload
    return buf.getBytes();
  }

}
