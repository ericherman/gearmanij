package gearmanij.util;

import java.io.UnsupportedEncodingException;

public class ByteUtils {

	public static byte[] getAsciiBytes(String string) {
		try {
			return string.getBytes("ASCII");
		} catch (UnsupportedEncodingException noAscii) {
			throw new RuntimeException(noAscii);
		}
	}

	public static final byte[] toBigEndian(int i) {
		return new byte[] { toByte(3, i), //
				toByte(2, i), // 
				toByte(1, i), //
				toByte(0, i) };
	}

	public static final int fromBigEndian(byte[] b) {
		return toInt(b[0], 3) //
				+ toInt(b[1], 2) //
				+ toInt(b[2], 1) //
				+ toInt(b[3], 0);
	}

	public static byte toByte(int whichByte, int i) {
		byte b = (byte) (i >>> (8 * whichByte));
		// System.err.println("toByte(whichByte: " + whichByte + ", int: " + i + "): " + b);
		return b;
	}

	private static int toInt(byte b, int whichByte) {
		int i = ((b & 0xFF) << (8 * whichByte));
		// System.err.println("toInt(byte: " + b + ", whichByte: " + whichByte + "): " + i);
		return i;
	}

}
