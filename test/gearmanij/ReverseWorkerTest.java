/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import gearmanij.example.ReverseFunction;
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
   * TODO: replace the use of a socket connection with that of a fake connection
   * so we may eliminate the prerequisites and external dependencies in this
   */
  @Deprecated
  private Connection newSocketConnection() {
    return new SocketConnection();
  }

  /**
   * Prerequisites:
   * <ul>
   * <li>job server running on localhost on default port 
   * <li>reverse client running
   * <li>reverse client has submitted a task that has not yet been assigned
   * </ul>
   * 
   * Manual verification:
   * <ul>
   * <li>confirm client received reversed text
   * </ul>
   */
  @Test
  public void testReverse() {
    String id = "testReverse";
    JobFunction reverse = new ReverseFunction();
    String name = reverse.getName();
    rw.setWorkerID(id);
    rw.registerFunction(reverse);
    assertTrue(TestUtil.isFunctionRegisteredForWorker(conn, id, name));
    rw.grabJob();
    rw.unregisterFunction(reverse);
    assertFalse(TestUtil.isFunctionRegisteredForWorker(conn, id, name));
  }

}
