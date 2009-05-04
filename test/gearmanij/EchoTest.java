/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class EchoTest {
  @Test
  public void testEcho() {
    
    SimpleWorker rw = new SimpleWorker();
    Connection conn = null;
    try {
      conn = rw.addServer();
      conn.open();
      
      String text = "abc";
      Packet response = conn.echo(text);
      assertTrue(response.getType() == PacketType.ECHO_RES);
      // Response data is null terminated
      assertTrue(text.length() == response.getDataSize() - 1);
      // Assert data was "abc"
      byte[] textBytes = (text + '\0').getBytes();
      byte[] responseBytes = response.getData();
      assertTrue(Arrays.equals(textBytes, responseBytes));
          
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }

  }
  
  class SimpleWorker extends AbstractWorker {
  }
}
