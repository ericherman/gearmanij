/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MockConnection extends ThrowingConnection {
  private boolean open;
  private boolean opened;
  private boolean closed;

  protected Queue<Packet> readQueue;
  protected List<Packet> written;

  public MockConnection() {
    open = false;
    opened = false;
    closed = false;
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

}
