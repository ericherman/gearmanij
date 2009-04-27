package gearmanij;

import gearmanij.util.ByteUtils;

enum PacketMagic {
	REQ("REQ"), RES("RES");

	private byte[] name;

	private PacketMagic(String kind) {
		name = new byte[1 + kind.length()];
		name[0] = 0;
		byte[] bytes = ByteUtils.getAsciiBytes(kind);
		System.arraycopy(bytes, 0, name, 1, kind.length());
	}

	public byte[] toBytes() {
		return name;
	}
}
