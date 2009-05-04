/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.io.IOException;

import org.junit.Test;

/**
 * Real ReverseWorker would extend AbstractWorker.
 */
public class ReverseWorkerTest {

  @Test
  public void testTextMode() {
    
    ReverseWorker rw = new ReverseWorker();
    Connection conn = null;
    try {
      conn = rw.addServer();
      conn.open();
      
      // Verify connection
      conn.textModeTest();
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }

  }
  
  @Test
  public void testReverse() {
    // Before running this test, start a client reverse work and submit a task
    ReverseWorker rw = new ReverseWorker();
    Connection conn = null;
    try {
      conn = rw.addServer();
      conn.open();
      conn.registerFunction(function);
      conn.textModeTest();
      conn.grabJob();
      conn.textModeTest();
      conn.unregisterFunction(function.getName());
      conn.textModeTest();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }
  }
  
  JobFunction function = new JobFunction() {
    public String execute(String data) {
      StringBuffer sb = new StringBuffer(data);
      sb = sb.reverse();
      return sb.toString();
    }
    public String getName() {
      return "reverse";
    }
  };
  
  class ReverseWorker extends AbstractWorker {
  }

}
