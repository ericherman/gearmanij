/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

public class PacketHeader {
	private PacketMagic magic;
	private PacketType type;
	private int dataLength;

	public PacketHeader(PacketMagic magic, PacketType type, int dataLength) {
		this.magic = magic;
		this.type = type;
		this.dataLength = dataLength;
	}

	public PacketHeader(byte[] bytes) {
		ByteArrayBuffer baBuff = new ByteArrayBuffer(bytes);
		magic = PacketMagic.fromBytes(baBuff.subArray(0, 4));
		int typeInt = ByteUtils.fromBigEndian(baBuff.subArray(4, 8));
		type = PacketType.get(typeInt);
		dataLength = ByteUtils.fromBigEndian(baBuff.subArray(8, 12));
	}

	public PacketMagic getMagic() {
		return magic;
	}

	public void setMagic(PacketMagic magic) {
		this.magic = magic;
	}

	public PacketType getType() {
		return type;
	}

	public void setType(PacketType type) {
		this.type = type;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

}
