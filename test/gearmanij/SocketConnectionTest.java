/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertTrue;
import gearmanij.util.ByteUtils;
import gearmanij.util.TestUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SocketConnectionTest {

  private Connection conn = null;

  @Before
  public void setUp() {
    conn = new SocketConnection();
    conn.open();
  }

  @After
  public void tearDown() {
    try {
      if (conn != null) {
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    conn = null;
  }

  @Test
  public void testEcho() {
    byte[] textBytes = ByteUtils.toAsciiBytes("abc");
    Packet request = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ, textBytes);
    conn.write(request);
    Packet response = conn.read();
    assertTrue(response.getType() == PacketType.ECHO_RES);
    // Response data is null terminated
    assertTrue(textBytes.length == response.getDataSize());
    // Assert data was "abc"
    byte[] responseBytes = response.getData();
    assertTrue(Arrays.equals(textBytes, responseBytes));
  }

  @Test
  public void testGetWorkerInfo() {
    // Add assertions to verify commands work as expected
    List<String> workerInfo = ((AdminConnection) conn).getWorkerInfo();
    TestUtil.dump(AdminConnection.COMMAND_WORKERS, workerInfo);
  }
  
  @Test
  public void testGetFunctionStatus() {
    // Add assertions to verify commands work as expected
    List<String> functionStatus = ((AdminConnection) conn).getFunctionStatus();
    TestUtil.dump(AdminConnection.COMMAND_STATUS, functionStatus);
  }

  @Test
  public void testGetVersion() {
    // Add assertions to verify version matches the version of gearmand
    String version = ((AdminConnection) conn).getVersion();
    TestUtil.dump(AdminConnection.COMMAND_VERSION, version);
  }
  
  @Test
  @Ignore
  // TODO Need to have a worker that has registered a function
  public void testSetDefaultMaxQueueSize() {
    String functionName = "maxqueuetest";
    boolean success = ((AdminConnection) conn).setDefaultMaxQueueSize(functionName);
    assertTrue(success);
  }
  
  @Test
  @Ignore
  // TODO Need to have a worker that has registered a function. Ideally, then
  // have a client submit tasks for each of the scenarios and confirm they behave
  // as expected.
  public void testSetMaxQueueSize() {
    String functionName = "maxqueuetest";
    boolean success;
    
    // Unlimited
    success = ((AdminConnection) conn).setMaxQueueSize(functionName, -1);
    assertTrue(success);
    
    // Need to confirm setting to 0 prevents queueing
    success = ((AdminConnection) conn).setMaxQueueSize(functionName, 0);
    assertTrue(success);
    
    // Queue depth of 2
    success = ((AdminConnection) conn).setMaxQueueSize(functionName, 2);
    assertTrue(success);
  }

}
