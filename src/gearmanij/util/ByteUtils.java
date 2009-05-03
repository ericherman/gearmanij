/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class ByteUtils {
	private static final String CHARSET_ASCII = "ASCII";

	public static byte[] toAsciiBytes(String string) {
		try {
			return string.getBytes(CHARSET_ASCII);
		} catch (UnsupportedEncodingException noAscii) {
			throw new RuntimeException(noAscii);
		}
	}

	public static String fromAsciiBytes(byte[] bytes) {
		try {
			return new String(bytes, CHARSET_ASCII);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toHex(final byte bytes[]) {
		String hexFormat = "%0" + (bytes.length * 2) + "x";
		return String.format(hexFormat, new BigInteger(1, bytes));
	}

	public static byte[] fromHex(final String hex) {
		return new BigInteger(hex, 16).toByteArray();
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
		return (byte) (from >>> (8 * byteSignificance));
	}

	public static int toInt(int byteSignificance, byte b) {
		return ((b & 0xFF) << (8 * byteSignificance));
	}

}
