/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertTrue;
import gearmanij.util.RuntimeIOException;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConnectionTest {

  private Connection conn = null;

  @Before
  public void setUp() {
    conn = new Connection(Constants.GEARMAN_DEFAULT_TCP_HOST,
        Constants.GEARMAN_DEFAULT_TCP_PORT);
    try {
	  conn.open();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown() {
    try {
      if (conn != null) {
        conn.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    conn = null;
  }
  
  @Test
  public void testEcho() {
    try {
      String text = "abc";
      Packet response = conn.echo(text);
      assertTrue(response.getType() == PacketType.ECHO_RES);
      // Response data is null terminated
      assertTrue(text.length() == response.getDataSize());
      // Assert data was "abc"
      byte[] textBytes = text.getBytes();
      byte[] responseBytes = response.getData();
      assertTrue(Arrays.equals(textBytes, responseBytes));
    } catch (RuntimeIOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testTextMode() {
    try {
      // Verify connection
      conn.textModeTest(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
