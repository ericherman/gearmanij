/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

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
      conn.textModeTest(System.out);
      rw.grabJob();
      conn.textModeTest(System.out);
      rw.unregisterFunction(reverse);
      conn.textModeTest(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      for (Exception e : rw.close()) {
          e.printStackTrace();
      }
    }
  }

}
