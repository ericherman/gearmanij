package gearmanij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gearmanij.Worker.WorkerOption;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Test;

public class AbstractWorkerTest {
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
  
  class SimpleWorker extends AbstractWorker {

    
  }
}
