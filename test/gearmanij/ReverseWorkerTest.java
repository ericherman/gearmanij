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

    MockConnection conn = new MockConnection() {
      private boolean assigned = false;

      public void write(Packet request) {
        super.write(request);
        if (request.getPacketType().equals(PacketType.GRAB_JOB)) {
          if (!assigned) {
            assigned = true;
            readQueue.offer(reverseFooJob());
          } else {
            readQueue.offer(noJob());
          }
        }
      }
    };

    final List<String> executed = new ArrayList<String>();
    JobFunction reverse = new ReverseFunction() {
      public String execute(String data) {
        String result = super.execute(data);
        executed.add(data + " -> " + result);
        return result;
      }
    };

    final Worker reverseWorker = new SimpleWorker();

    assertFalse(conn.isOpen());

    reverseWorker.addServer(conn);

    assertTrue(conn.wasOpened());
    assertTrue(conn.isOpen());

    String id = "testReverse";

    reverseWorker.setWorkerID(id);
    reverseWorker.registerFunction(reverse);

    TestUtil.startThread(id + "_thread", new Runnable() {
      public void run() {
        reverseWorker.work();
      }
    });

    for (int i = 0; executed.isEmpty() && i < 100; i++) {
      TestUtil.sleep(25);
    }

    reverseWorker.unregisterFunction(reverse);
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
