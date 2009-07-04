/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Eric Lambert
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.client;

import java.util.List;

import org.gearman.Connection;
import org.gearman.Packet;
import org.gearman.PacketMagic;
import org.gearman.PacketType;
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;

public class Status {
  private Connection conn;
  private byte[] handle;
  private long lastUpdate;

  private boolean isRunning;
  private boolean isKnown;
  private long numerator;
  private long denominator;

  public Status(Connection conn) {
    this.conn = conn;
    this.handle = ByteUtils.EMPTY;
  }

  public void setHandle(byte[] handle) {
    this.handle = handle;
  }

  public void update() {
    conn.open();
    try {
      Packet req = new Packet(PacketMagic.REQ, PacketType.GET_STATUS, handle);
      conn.write(req);
      Packet resp = conn.read();
      update(resp);
    } finally {
      conn.close();
    }
  }

  public void update(Packet fromServer) {
    ByteArrayBuffer buf = new ByteArrayBuffer(fromServer.getData());
    byte[] pattern = new byte[] { ByteUtils.NULL };
    int numberOfparts = 5;
    List<byte[]> parts = buf.split(pattern, numberOfparts);

    isKnown = parseBoolean(parts.get(1));
    isRunning = parseBoolean(parts.get(2));
    numerator = parseLong(parts.get(3));
    denominator = parseLong(parts.get(4));

    lastUpdate = System.currentTimeMillis();
  }

  private long parseLong(byte[] bytes) {
    return Long.parseLong(ByteUtils.fromUTF8Bytes(bytes));
  }

  private boolean parseBoolean(byte[] bytes) {
    return bytes[0] == '0' ? false : true;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public boolean isKnown() {
    return isKnown;
  }

  public long getNumerator() {
    return numerator;
  }

  public long getDenominator() {
    return denominator;
  }

  public long getLastUpdate() {
    return lastUpdate;
  }

}
