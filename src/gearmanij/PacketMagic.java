/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;

enum PacketMagic {
	REQ("REQ"), RES("RES");

	public static class BadMagicException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private BadMagicException(String hex) {
			super(hex);
		}
	}

	private byte[] name;

	private PacketMagic(String kind) {
		this.name = new byte[1 + kind.length()];
		this.name[0] = 0;
		byte[] bytes = ByteUtils.toAsciiBytes(kind);
		System.arraycopy(bytes, 0, this.name, 1, kind.length());
	}

	public byte[] toBytes() {
		return name;
	}

	/**
	 * "\0REQ" == [ 00 52 45 51 ] == 5391697
	 * 
	 * "\0RES" == [ 00 52 45 53 ] == 5391699
	 * 
	 * @param bytes
	 * @return
	 */
	public static PacketMagic fromBytes(byte[] bytes) {
		if (bytes != null & bytes.length == 4) {
			int magic = ByteUtils.fromBigEndian(bytes);
			if (magic == ByteUtils.fromBigEndian(REQ.toBytes())) {
				return REQ;
			}
			if (magic == ByteUtils.fromBigEndian(RES.toBytes())) {
				return RES;
			}
		}
		throw new BadMagicException(ByteUtils.toHex(bytes));
	}

}
