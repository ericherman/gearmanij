package gearmanij.util;

import junit.framework.TestCase;

public class ByteUtilsTest extends TestCase {

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

	public void testGetBytes() {
		int x = combineOctets(4, 3, 2, 1);
		byte[] bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(4, 3, 2, 1, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		x = combineOctets(0, 5, 0, 1);
		bytes = ByteUtils.toBigEndian(x);
		assertBigEndian(0, 5, 0, 1, bytes);
		assertEquals(x, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(0);
		assertEquals(0, ByteUtils.fromBigEndian(bytes));

		bytes = ByteUtils.toBigEndian(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, ByteUtils.fromBigEndian(bytes));
	}

}
