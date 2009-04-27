package gearmanij.util;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

public class ByteArrayBufferTest extends TestCase {
	byte[] expectBytes;

	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		expectBytes = new byte[10 * 1000];
		for (int i = 0; i < expectBytes.length; i++) {
			expectBytes[i] = (byte) i;
		}
	}

	protected void tearDown() {
		expectBytes = null;
		try {
			super.tearDown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void assertArraysEqual(final byte[] left, final byte[] right) {
		if (left == null || right == null) {
			assertEquals(left, right);
			return;
		}

		assertEquals("lengths differ", left.length, right.length);
		for (int i = 0; i < left.length; i++) {
			assertEquals("element " + i, left[i], right[i]);
		}
	}

	public void testSimpleAppend() {
		final ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(expectBytes);
		assertArraysEqual(expectBytes, buf.getBytes());
	}

	public void testEachSoloByte() {
		for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
			checkSoloByte(b);
		}
		checkSoloByte(Byte.MAX_VALUE);
	}

	private void checkSoloByte(final byte b) {
		final byte[] ba = new byte[] { b };
		assertArraysEqual(ba, new ByteArrayBuffer().append(ba).getBytes());
	}

	public void testVariableAppendSizes() {
		final ByteArrayBuffer buf = new ByteArrayBuffer();
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

	public void testAppendFromStream() {
		final ByteArrayInputStream bais = new ByteArrayInputStream(expectBytes);
		final ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(bais);
		assertArraysEqual(expectBytes, buf.getBytes());
	}

}
