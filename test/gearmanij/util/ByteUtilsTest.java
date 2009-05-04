/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static gearmanij.util.TestUtil.assertArraysEqual;
import static gearmanij.util.TestUtil.assertEqualsIgnoreCase;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteUtilsTest {

	private void assertBigEndian(int val3, int val2, int val1, int val0,
			byte[] bytes) {
		assertEquals((byte) val3, bytes[0]);
		assertEquals((byte) val2, bytes[1]);
		assertEquals((byte) val1, bytes[2]);
		assertEquals((byte) val0, bytes[3]);
	}

	private int combineOctets(int val3, int val2, int val1, int val0) {
		int val = (val3 * (256 * 256 * 256)) //
				+ (val2 * (256 * 256)) //
				+ (val1 * (256)) //
				+ (val0 * 1) //
		;
		return val;
	}

	@Test
	public void testToFrmBigEndian() {
		int x = combineOctets(4, 3, 2, 1);
		byte[] bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(4, 3, 2, 1, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		x = combineOctets(0, 5, 0, 1);
		bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(0, 5, 0, 1, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		x = combineOctets(0, 0x52, 0x45, 0x51);
		assertEquals(5391697, x);
		bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(0, 0x52, 0x45, 0x51, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		x = combineOctets(0, 0x52, 0x45, 0x53);
		assertEquals(5391699, x);
		bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(0, 0x52, 0x45, 0x53, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(0);
		assertEquals(0, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, ByteUtils.fromBigEndian(bytes));
	}

	@Test
	public void testToFromHex() {
		assertHexRoundTrip("00", new byte[] { 0 });
		assertHexRoundTrip("01FF007F80", new byte[] { 1, -1, 0, 127, -128 });
	}

	private void assertHexRoundTrip(String hex, byte[] bytes) {
		String asHex = ByteUtils.toHex(bytes);
		assertEqualsIgnoreCase(hex, asHex);
		byte[] asBytes = ByteUtils.fromHex(hex);
		assertArraysEqual(bytes, asBytes);
	}

	@Test
	public void testToFromAscii() {
		String string = "Hi, Mom!";
		byte[] bytes = { 72, 105, 44, 32, 77, 111, 109, 33 };
		assertEquals(string, ByteUtils.fromAsciiBytes(bytes));
		assertArraysEqual(bytes, ByteUtils.toAsciiBytes(string));
	}

	@Test
	public void testCopy() {
		assertArraysEqual(new byte[0], ByteUtils.copy(null));
		byte[] bytes = new byte[] { 0, 1, -1, 127, -128 };
		byte[] copy = ByteUtils.copy(bytes);
		assertArraysEqual(bytes, ByteUtils.copy(bytes));
		assertEquals(false, bytes == copy);
	}

}
