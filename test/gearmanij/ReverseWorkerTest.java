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
  
  /**
   * Prereqs:
   * job server running on localhost on default port
   * reverse client running
   * reverse client has submitted a task that has not yet been assigned
   */
  @Test
  public void testReverse() {
    Worker rw = new SimpleWorker();
    Connection conn = new SocketConnection();
    JobFunction reverse = new ReverseFunction();
    try {
      rw.addServer(conn);
      rw.registerFunction(reverse);
      dumpTextModeTest(rw, conn);
      rw.grabJob();
      dumpTextModeTest(rw, conn);
      rw.unregisterFunction(reverse);
      dumpTextModeTest(rw, conn);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      for (Exception e : rw.close()) {
          e.printStackTrace();
      }
    }
  }

  private void dumpTextModeTest(Worker w, Connection conn) {
    TestUtil.dump(System.out, w.textModeTest(conn));
  }

}
