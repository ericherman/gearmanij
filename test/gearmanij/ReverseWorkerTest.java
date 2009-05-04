/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.TestUtil;

import java.io.IOException;

import org.junit.Test;

public class ReverseWorkerTest {
  
  @Test
  public void testReverse() {
    // Before running this test, start a client reverse work and submit a task
    Worker rw = new SimpleWorker();
    Connection conn = null;
    JobFunction reverse = new ReverseFunction();
    try {
      conn = rw.addServer();
      rw.registerFunction(reverse);
      dumpTestModeTest(rw, conn);
      rw.grabJob();
      dumpTestModeTest(rw, conn);
      rw.unregisterFunction(reverse);
      dumpTestModeTest(rw, conn);
    } catch (IOException e) {
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
