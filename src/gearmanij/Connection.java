/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.IORuntimeException;

import java.util.List;

/**
 * Objects that implement this interface manage a connection to a Gearman job
 * server.
 */
public interface Connection {

  /**
   * Open a new connection to a job server.
   */
  void open();

  /**
   * Close the current connection, if any, to a job server.
   */
  void close();

  /**
   * Writes a {@link Packet} to a job server.
   * 
   * @param request
   *          Packet to send to job server
   */
  void write(Packet request);

  /**
   * Reads a Packet from a job server.
   * 
   * @return the Packet
   * @throws IORuntimeException
   */
  Packet read();

}
