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
    Packet response = conn.readPacket();
    assertTrue(response.getType() == PacketType.ECHO_RES);
    // Response data is null terminated
    assertTrue(textBytes.length == response.getDataSize());
    // Assert data was "abc"
    byte[] responseBytes = response.getData();
    assertTrue(Arrays.equals(textBytes, responseBytes));
  }

  @Test
  public void testTextMode() {
    // Add assertions to verify commands work as expected
    TextCommand command = TextCommand.WORKERS;
    List<String> response = conn.sendTextModeCommand(command);
    TestUtil.dump(command.toString(), response);
  }

}
