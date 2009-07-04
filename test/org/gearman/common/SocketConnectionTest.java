/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.common;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.gearman.NotImplementedException;
import org.gearman.Packet;
import org.gearman.PacketMagic;
import org.gearman.PacketType;
import org.gearman.util.ByteUtils;
import org.gearman.util.SocketServer;
import org.gearman.util.TestUtil;
import org.junit.After;
import org.junit.Test;

public class SocketConnectionTest {

  private SocketConnection conn = null;
  private SocketServer server = null;

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

    try {
      if (server != null) {
        server.shutdown();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    server = null;

  }

  @Test
  public void testEcho() throws Exception {
    server = new SocketServer("Fake gearmand echo server") {
      protected void acceptConnection(Socket s) {
        try {
          InputStream is = s.getInputStream();
          OutputStream os = s.getOutputStream();
          while (true) {
            Packet p = new Packet(is);
            if (p.getPacketType().equals(PacketType.ECHO_REQ)) {
              byte[] data = p.getData();
              Packet o = new Packet(PacketMagic.RES, PacketType.ECHO_RES, data);
              o.write(os);
            } else {
              throw new NotImplementedException("" + p.getPacketType());
            }
          }
        } catch (NotImplementedException e) {
          throw e;
        } catch (Exception quit) {
          quit.printStackTrace();
          // just die on exception
          // we're probably done
        }
      }
    };
    server.start();

    conn = new SocketConnection(server.getPort());
    conn.open();

    byte[] textBytes = ByteUtils.toAsciiBytes("abc");
    Packet request = new Packet(PacketMagic.REQ, PacketType.ECHO_REQ, textBytes);
    conn.write(request);
    Packet response = conn.read();
    assertTrue(response.getType() == PacketType.ECHO_RES);
    // Response data is null terminated
    assertTrue(textBytes.length == response.getDataSize());
    // Assert data was "abc"
    byte[] responseBytes = response.getData();
    TestUtil.assertArraysEqual(textBytes, responseBytes);
  }

}
