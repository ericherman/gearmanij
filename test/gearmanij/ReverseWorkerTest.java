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
  public void testReverseWorker() {
    
    ReverseWorker rw = new ReverseWorker();
    Connection conn = null;
    try {
      conn = rw.addServer();
      conn.open();
      
      // temp test to visually verify connection
      conn.textModeTest();
      
      conn.registerFunction(function);
      
      // temp test to visually verify function was registered
      conn.textModeTest();
      
      // temp test so I can step through the packet writing and reading code
      conn.echoTest("abc");
      
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
