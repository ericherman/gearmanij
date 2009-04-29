/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ByteArrayBufferTest {
	byte[] expectBytes;

	@Before
	public void setUp() {
		expectBytes = new byte[10 * 1000];
		for (int i = 0; i < expectBytes.length; i++) {
			expectBytes[i] = (byte) i;
		}
	}

	@After
	public void tearDown() {
		expectBytes = null;
	}

	public static void assertArraysEqual(final byte[] left, final byte[] right) {
		if (left == null || right == null) {
			assertEquals(left, right);
			return;
		}

		assertEquals("lengths differ", left.length,
				right.length);
		for (int i = 0; i < left.length; i++) {
			assertEquals("element " + i, left[i], right[i]);
		}
	}

	@Test
	public void testSimpleAppend() {
		final ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(expectBytes);
		assertArraysEqual(expectBytes, buf.getBytes());
	}

	@Test
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

	@Test
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

	@Test
	public void testAppendFromStream() {
		final ByteArrayInputStream bais = new ByteArrayInputStream(expectBytes);
		final ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(bais);
		assertArraysEqual(expectBytes, buf.getBytes());
	}

}
