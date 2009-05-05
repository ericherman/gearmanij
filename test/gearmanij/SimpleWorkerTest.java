/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gearmanij.Worker.WorkerOption;
import gearmanij.util.TestUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SimpleWorkerTest {
  @Test
  public void testWorkerOptions() {
    Worker worker = new SimpleWorker();
    assertEquals(worker.getWorkerOptions(), EnumSet.noneOf(WorkerOption.class));
  }
  
  @Test
  public void testRemoveOptions() {
    Worker worker = new SimpleWorker();
    worker.setWorkerOptions(WorkerOption.NON_BLOCKING, WorkerOption.GRAB_UNIQ);
    Collection<WorkerOption> c = EnumSet.of(WorkerOption.NON_BLOCKING, WorkerOption.GRAB_UNIQ);
    assertTrue(worker.getWorkerOptions().containsAll(c));
    worker.removeWorkerOptions(WorkerOption.GRAB_UNIQ);
    assertTrue(worker.getWorkerOptions().contains(WorkerOption.NON_BLOCKING));
    assertFalse(worker.getWorkerOptions().contains(WorkerOption.GRAB_UNIQ));
  }
  
  @Test
  public void testSetWorkerOptions() {
    Collection<WorkerOption> c = null;
    Worker worker = new SimpleWorker();
    
    worker.setWorkerOptions(WorkerOption.NON_BLOCKING);
    c = EnumSet.of(WorkerOption.NON_BLOCKING);
    assertTrue(worker.getWorkerOptions().containsAll(c));
    
    worker.clearWorkerOptions();
    worker.setWorkerOptions(WorkerOption.NON_BLOCKING, WorkerOption.NON_BLOCKING);
    c = EnumSet.of(WorkerOption.NON_BLOCKING);
    assertTrue(worker.getWorkerOptions().containsAll(c));
  }
  
  @Test
  public void testRegisterFunction() {
    Worker w = new SimpleWorker();
    Connection conn = new SocketConnection();
    JobFunction reverse = new ReverseFunction();
    JobFunction digest = new DigestFunction();
    String id = "SimpleWorker";
    
    try {
      w.addServer(conn);
      w.setWorkerID(id);
      assertTrue(TestUtil.isWorkerFoundByID(conn, id));
      w.registerFunction(digest);
      assertTrue(TestUtil.isFunctionRegisteredForWorker(conn, id, digest.getName()));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      for (Exception e : w.close()) {
        e.printStackTrace();
      }
    }
  }
  
  
  private void dumpTextModeTest(Worker w, Connection conn) {
    TestUtil.dump(System.out, w.textModeTest(conn));
  }
}
