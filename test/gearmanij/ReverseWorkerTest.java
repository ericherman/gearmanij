/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.TestUtil;

import org.junit.Test;

public class ReverseWorkerTest {
  
  @Test
  public void testReverse() {
    // Before running this test, start a client reverse work and submit a task
    Worker rw = new SimpleWorker();
    Connection conn = new SocketConnection();
    JobFunction reverse = new ReverseFunction();
    try {
      rw.addServer(conn);
      rw.registerFunction(reverse);
      dumpTestModeTest(rw, conn);
      rw.grabJob();
      dumpTestModeTest(rw, conn);
      rw.unregisterFunction(reverse);
      dumpTestModeTest(rw, conn);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      for (Exception e : rw.close()) {
          e.printStackTrace();
      }
    }
  }

  private void dumpTestModeTest(Worker rw, Connection conn) {
    TestUtil.dump(System.out, rw.textModeTest(conn));
  }

}
