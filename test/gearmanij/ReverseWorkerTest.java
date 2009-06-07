/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gearmanij.example.ReverseFunction;
import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;
import gearmanij.util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ReverseWorkerTest {

  private Packet reverseFooJob() {
    String hexFoo = "483a68616c6c652e6c6f63616c3a31007265766572736500466f6f";
    byte[] data = ByteUtils.fromHex(hexFoo);
    return new Packet(PacketMagic.RES, PacketType.JOB_ASSIGN, data);
  }

  private Packet noJob() {
    return new Packet(PacketMagic.RES, PacketType.NO_JOB, ByteUtils.EMPTY);
  }

  @Test
  public void testReverse() throws Exception {
    final List<String> workComplete = new ArrayList<String>();

    MockConnection conn = new MockConnection() {
      private boolean assigned = false;

      public void write(Packet request) {
        super.write(request);
        PacketType packetType = request.getPacketType();
        switch (packetType) {
        case GRAB_JOB:
          if (!assigned) {
            assigned = true;
            readQueue.offer(reverseFooJob());
          } else {
            readQueue.offer(noJob());
          }
          break;
        case WORK_COMPLETE:
          ByteArrayBuffer buf = new ByteArrayBuffer(request.getData());
          int lastNull = buf.lastIndexOf(ByteUtils.NULL);
          byte[] oofBytes = buf.subArray(lastNull + 1, buf.length());
          String oof = ByteUtils.fromAsciiBytes(oofBytes);
          workComplete.add(oof);
          break;
        case SET_CLIENT_ID:
        case CAN_DO:
        case CANT_DO:
        case PRE_SLEEP:
          break;
        default:
          throw new RuntimeException(packetType.toString());
        }
      }
    };

    final List<String> executed = new ArrayList<String>();
    
    JobFunction reverse = new ReverseFunction() {
      public void execute(Job job) {
        String from = ByteUtils.fromUTF8Bytes(job.getData());
        super.execute(job);
        String result = ByteUtils.fromUTF8Bytes(job.getResult());
        executed.add(from + " -> " + result);
      }
    };

    final Worker reverseWorker = new StandardWorker();

    assertFalse(conn.isOpen());

    reverseWorker.addServer(conn);

    assertTrue(conn.wasOpened());
    assertTrue(conn.isOpen());

    String id = "testReverse";

    assertEquals(0, conn.clientId().size());
    reverseWorker.setWorkerID(id);
    assertEquals(1, conn.clientId().size());
    assertEquals(id, conn.clientId().get(0));

    assertEquals(0, conn.canDo().size());
    reverseWorker.registerFunction(reverse);
    assertEquals(1, conn.canDo().size());
    assertEquals(reverse.getName(), conn.canDo().get(0));

    TestUtil.startThread(id + "_thread", new Runnable() {
      public void run() {
        reverseWorker.work();
      }
    });

    for (int i = 0; executed.isEmpty() && i < 100; i++) {
      TestUtil.sleep(25);
    }
    assertEquals(1, workComplete.size());
    assertEquals("ooF", workComplete.get(0));

    reverseWorker.unregisterFunction(reverse.getName());
    assertEquals(1, conn.cantDo().size());
    assertEquals(reverse.getName(), conn.cantDo().get(0));

    List<Exception> exceptions = reverseWorker.shutdown();

    if (!exceptions.isEmpty()) {
      for (Exception e : exceptions) {
        e.printStackTrace();
      }
    }
    assertTrue(conn.wasClosed());
    assertFalse(conn.isOpen());
    assertTrue(exceptions.toString(), exceptions.isEmpty());

    assertEquals(executed.toString(), 1, executed.size());
    assertEquals("Foo -> ooF", executed.get(0));
  }

}
