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
import java.util.List;

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

  @Test
  public void testSplit() {
    byte[] bytes = { 1, 0, 2, 2, 0, 3, 3, 3, 0, 4, 4, 4, 0, 5, 5 };

    ByteArrayBuffer buf = new ByteArrayBuffer(bytes);

    byte[] pattern = { 0 };
    int limit = 3;
    List<byte[]> result = buf.split(pattern, limit);
    assertEquals(4, result.size());

    assertArraysEqual(new byte[] { 1 }, result.get(0));
    assertArraysEqual(new byte[] { 2, 2 }, result.get(1));
    assertArraysEqual(new byte[] { 3, 3, 3 }, result.get(2));
    assertArraysEqual(new byte[] { 4, 4, 4, 0, 5, 5 }, result.get(3));
  }

  @Test
  public void testSplitNullNull() {
    byte[] bytes = { 1, 0, 0, 2, 2, 0, 0, 3, 3, 3, 0, 0, 4, 4, 4, 0, 0, 5, 5 };

    ByteArrayBuffer buf = new ByteArrayBuffer(bytes);

    byte[] pattern = { 0, 0 };
    int limit = 3;
    List<byte[]> result = buf.split(pattern, limit);
    assertEquals(4, result.size());

    assertArraysEqual(new byte[] { 1 }, result.get(0));
    assertArraysEqual(new byte[] { 2, 2 }, result.get(1));
    assertArraysEqual(new byte[] { 3, 3, 3 }, result.get(2));
    assertArraysEqual(new byte[] { 4, 4, 4, 0, 0, 5, 5 }, result.get(3));
  }

  @Test
  public void testSplitNoTail() {
    byte[] bytes = { 0, 2, 2, 0 };

    ByteArrayBuffer buf = new ByteArrayBuffer(bytes);

    byte[] pattern = { 0 };
    List<byte[]> result = buf.split(pattern);
    assertEquals(2, result.size());
    assertArraysEqual(new byte[0], result.get(0));
    assertArraysEqual(new byte[] { 2, 2 }, result.get(1));
  }

  @Test
  public void testSplitNotFound() {
    byte[] bytes = { 0, 2, 2, 0 };

    ByteArrayBuffer buf = new ByteArrayBuffer(bytes);

    byte[] pattern = { 0, 1, 2, 3, 4, 5, 6 };
    List<byte[]> result = buf.split(pattern);
    assertEquals(1, result.size());
    assertArraysEqual(bytes, result.get(0));
  }

}
