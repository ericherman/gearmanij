/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
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
		return new byte[] { selectByte(3, i), //
				selectByte(2, i), // 
				selectByte(1, i), //
				selectByte(0, i) };
	}

	public static final int fromBigEndian(byte[] b) {
		return toInt(3, b[0]) //
				+ toInt(2, b[1]) //
				+ toInt(1, b[2]) //
				+ toInt(0, b[3]);
	}

	public static byte selectByte(int byteSignificance, int from) {
		byte b = (byte) (from >>> (8 * byteSignificance));
		// System.err.println("selectByte(byteSignificance: " + byteSignificance + ", int: " + from + "): " + b);
		return b;
	}

	public static int toInt(int byteSignificance, byte b) {
		int i = ((b & 0xFF) << (8 * byteSignificance));
		// System.err.println("toInt(byte: " + b + ", byteSignificance: " + byteSignificance + "): " + i);
		return i;
	}

}
