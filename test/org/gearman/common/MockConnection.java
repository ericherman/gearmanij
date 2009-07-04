/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.gearman.Packet;
import org.gearman.PacketType;
import org.gearman.util.ByteUtils;

public class MockConnection extends ThrowingConnection {
  private boolean open;
  private boolean opened;
  private boolean closed;
  private List<String> canDo;
  private List<String> cantDo;
  private List<String> clientId;

  protected Queue<Packet> readQueue;
  protected List<Packet> written;

  public MockConnection() {
    open = false;
    opened = false;
    closed = false;
    canDo = new LinkedList<String>();
    cantDo = new LinkedList<String>();
    clientId = new LinkedList<String>();

    readQueue = new LinkedList<Packet>();
    written = new LinkedList<Packet>();
  }

  public void open() {
    open = true;
    opened = true;
  }

  public void close() {
    open = false;
    closed = true;
  }

  public Packet read() {
    if (!readQueue.isEmpty()) {
      return readQueue.poll();
    }
    throw new RuntimeException("unexpected read");
  }

  public void write(Packet request) {
    written.add(request);
    PacketType packetType = request.getPacketType();
    switch (packetType) {
    case SET_CLIENT_ID:
      clientId.add(ByteUtils.fromAsciiBytes(request.getData()));
      break;
    case CAN_DO:
      canDo.add(ByteUtils.fromAsciiBytes(request.getData()));
      break;
    case CANT_DO:
      cantDo.add(ByteUtils.fromAsciiBytes(request.getData()));
      break;
    default:
      break;
    }
  }

  public boolean isOpen() {
    return open;
  }

  public boolean wasOpened() {
    return opened;
  }

  public boolean wasClosed() {
    return closed;
  }

  public List<String> clientId() {
    return clientId;
  }

  public List<String> canDo() {
    return canDo;
  }

  public List<String> cantDo() {
    return cantDo;
  }

}
