/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static gearmanij.util.TestUtil.assertArraysEqual;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class ByteArrayBufferTest {

  private byte[] expectBytes() {
    byte[] expectBytes;
    expectBytes = new byte[10 * 1000];
    for (int i = 0; i < expectBytes.length; i++) {
      expectBytes[i] = (byte) i;
    }
    return expectBytes;
  }

  @Test
  public void testSimpleAppend() {
    byte[] expectBytes = expectBytes();
    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(expectBytes);
    assertArraysEqual(expectBytes, buf.getBytes());
  }

  @Test
  public void testNullSafety() {
    ByteArrayBuffer buf = new ByteArrayBuffer(null);
    assertArraysEqual(ByteUtils.EMPTY, buf.getBytes());
  }

  @Test
  public void testEachSoloByte() {
    for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
      checkSoloByte(b);
    }
    checkSoloByte(Byte.MAX_VALUE);
  }

  private void checkSoloByte(final byte b) {
    byte[] ba = new byte[] { b };
    assertArraysEqual(ba, new ByteArrayBuffer().append(ba).getBytes());
  }

  @Test
  public void testVariableAppendSizes() {
    byte[] expectBytes = expectBytes();
    ByteArrayBuffer buf = new ByteArrayBuffer();
    int numberOfAppends = 0;
    int position = 0;
    while (position < expectBytes.length) {
      for (int i = 0; i < 100; i++) {
        int length = i;
        if (position + length >= expectBytes.length) {
          length = expectBytes.length - position;
        }
        numberOfAppends++;
        buf.append(expectBytes, position, length);
        position += length;
      }
    }
    assertArraysEqual(expectBytes, buf.getBytes());
    assertEquals(300, numberOfAppends);
  }

  @Test
  public void testAppendFromStream() {
    byte[] expectBytes = expectBytes();
    ByteArrayInputStream bais = new ByteArrayInputStream(expectBytes);
    ByteArrayBuffer buf = new ByteArrayBuffer();
    buf.append(bais);
    assertArraysEqual(expectBytes, buf.getBytes());
  }

  @Test
  public void testSubArray() {
    byte[] hamburger = ByteUtils.toAsciiBytes("hamburger");
    byte[] urge = ByteUtils.toAsciiBytes("urge");
    ByteArrayBuffer buf = new ByteArrayBuffer(hamburger);
    assertArraysEqual(urge, buf.subArray(4, 8));

    byte[] smiles = ByteUtils.toAsciiBytes("smiles");
    byte[] mile = ByteUtils.toAsciiBytes("mile");
    buf = new ByteArrayBuffer(smiles);
    assertArraysEqual(mile, buf.subArray(1, 5));
    assertArraysEqual(smiles, buf.subArray(0, buf.length()));
  }

  @Test
  public void testIndexOf() {
    byte[] foo = new byte[] { 1, 0, 5, 1, 2, 0, 3 };
    ByteArrayBuffer buf = new ByteArrayBuffer(foo);
    assertEquals(0, buf.indexOf((byte) 1));
    assertEquals(3, buf.lastIndexOf((byte) 1));

    assertEquals(1, buf.indexOf((byte) 0));
    assertEquals(5, buf.lastIndexOf((byte) 0));

    assertEquals(2, buf.indexOf((byte) 5));
    assertEquals(2, buf.lastIndexOf((byte) 5));

    assertEquals(-1, buf.indexOf((byte) 7));
    assertEquals(-1, buf.lastIndexOf((byte) 7));

    assertEquals(3, buf.indexOf((byte) 1, 1));
    assertEquals(0, buf.lastIndexOf((byte) 1, 1));
  }

}
