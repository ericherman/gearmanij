/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.util.Collections;
import java.util.List;

import gearmanij.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReverseWorkerTest {

  private Worker rw;
  private Connection conn;

  @Before
  public void setUp() {
    rw = new SimpleWorker();
    conn = newSocketConnection();
    rw.addServer(conn);
  }

  @After
  public void tearDown() {
    conn = null;
    List<Exception> close = Collections.emptyList();
    try {
      /* rw.close() calls conn.close() */
      close = rw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    rw = null;
    for (Exception e : close) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: replace the use of a socket connection with that of a fake
   * connection so we may eliminate the prerequisites and external
   * dependencies in this
   */
  @Deprecated
  private Connection newSocketConnection() {
    return new SocketConnection();
  }

  /**
   * Prereqs:
   * job server running on localhost on default port
   * reverse client running
   * reverse client has submitted a task that has not yet been assigned
   *
   * TODO: replace the DUMP commands with assertions about what the Worker has
   * sent and received to the Connection
   */
  @Test
  public void testReverse() {
    JobFunction reverse = new ReverseFunction();
    rw.registerFunction(reverse);
    TestUtil.dump(rw.textModeTest(conn));
    rw.grabJob();
    TestUtil.dump(rw.textModeTest(conn));
    rw.unregisterFunction(reverse);
    TestUtil.dump(rw.textModeTest(conn));
  }

}
