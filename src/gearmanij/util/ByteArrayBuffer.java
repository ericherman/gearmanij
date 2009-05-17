/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import java.io.InputStream;
import java.io.Serializable;

public class ByteArrayBuffer implements Serializable {
  private static final long serialVersionUID = 1L;

  private byte[] buf;
  private int copyBufferSize;

  public ByteArrayBuffer() {
    this(ByteUtils.EMPTY);
  }

  public ByteArrayBuffer(byte[] bytes) {
    this(bytes, 4 * 1024);
  }

  public ByteArrayBuffer(byte[] bytes, int copyBufferSize) {
    if (bytes == null) {
      bytes = ByteUtils.EMPTY;
    }
    this.copyBufferSize = copyBufferSize;
    this.buf = new byte[bytes.length];
    System.arraycopy(bytes, 0, buf, 0, bytes.length);
  }

  public byte[] getBytes() {
    return buf;
  }

  public ByteArrayBuffer append(byte[] bytes) {
    return append(bytes, 0, bytes.length);
  }

  public ByteArrayBuffer append(byte b) {
    return append(new byte[] { b });
  }

  public ByteArrayBuffer append(byte[] bytes, int startPosition, int len) {
    byte[] old = buf;
    buf = new byte[old.length + len];
    System.arraycopy(old, 0, buf, 0, old.length);
    System.arraycopy(bytes, startPosition, buf, old.length, len);
    return this;
  }

  public ByteArrayBuffer append(InputStream is) {
    final int EOF = -1;
    byte[] inputBuf = new byte[copyBufferSize];
    while (true) {
      int len = IOUtil.read(is, inputBuf);
      if (len == EOF) {
        break;
      }
      append(inputBuf, 0, len);
    }
    return this;
  }

  public int length() {
    return buf.length;
  }

  /**
   * The intest of this method is to be similar to
   * <code>String.substring(int, int)</code>
   * 
   * Returns a new byte[] that is a sub-array of this array. The sub-array
   * begins at the specified <code>beginIndex</code> and extends to the byte at
   * index <code>endIndex - 1</code>. Thus the length of the sub-array is
   * <code>endIndex-beginIndex</code>.
   * 
   * @param beginIndex
   *          the beginning index, inclusive.
   * @param endIndex
   *          the ending index, exclusive.
   * @return the specified substring.
   * @exception IndexOutOfBoundsException
   *              if the <code>beginIndex</code> is negative, or
   *              <code>endIndex</code> is larger than the length of this
   *              <code>byte[]</code>, or <code>beginIndex</code> is larger than
   *              <code>endIndex</code>.
   */
  public byte[] subArray(int beginIndex, int endIndex) {
    if (beginIndex < 0 || endIndex > buf.length || beginIndex > endIndex) {
      String msg = "[" + beginIndex + "," + endIndex + "]" //
          + " (" + 0 + ", " + buf.length + ")";
      throw new IndexOutOfBoundsException(msg);
    }
    byte[] subArray = new byte[endIndex - beginIndex];
    System.arraycopy(buf, beginIndex, subArray, 0, subArray.length);
    return subArray;
  }

  public int indexOf(byte b) {
    return indexOf(b, 0);
  }

  public int indexOf(byte b, int fromIndex) {
    for (int i = fromIndex; i < buf.length; i++) {
      if (buf[i] == b) {
        return i;
      }
    }
    return -1;
  }

  public int lastIndexOf(byte b) {
    return lastIndexOf(b, buf.length - 1);
  }

  public int lastIndexOf(byte b, int fromIndex) {
    for (int i = fromIndex; i >= 0; i--) {
      if (buf[i] == b) {
        return i;
      }
    }
    return -1;
  }

  public String toHex() {
    return ByteUtils.toHex(buf);
  }

  public String toString() {
    return ByteUtils.fromAsciiBytes(buf);
  }

}
