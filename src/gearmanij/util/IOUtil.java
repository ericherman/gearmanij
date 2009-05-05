/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IOUtil {

  /**
   * Similar to <code>DataInputStream.readFully()</code> with more informative
   * error message.
   */
  public static void readFully(InputStream in, byte[] buffer) {
    int c = read(in, buffer);
    if (c != buffer.length) {
      String msg = c + " != " + buffer.length + ": " + ByteUtils.toHex(buffer);
      throw new IORuntimeException(new EOFException(msg));
    }
  }

  public static void flush(final Flushable baos) {
    try {
      baos.flush();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static int read(InputStream in, byte[] buffer) {
    try {
      return in.read(buffer);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static void write(OutputStream os, byte[] bytes) {
    try {
      os.write(bytes);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static InputStream getInputStream(Socket s) {
    try {
      return s.getInputStream();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static OutputStream getOutputStream(Socket s) {
    try {
      return s.getOutputStream();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static Socket newSocket(String host, int port) {
    try {
      return new Socket(host, port);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static void close(Socket socket) {
    try {
      socket.close();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public static String readLine(BufferedReader in) {
    try {
      return in.readLine();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

}
