/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;
import gearmanij.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Packet {

	private final PacketMagic magic;

	private final PacketType type;

	private final byte[] data;

	public Packet(PacketMagic magic, PacketType type, byte[] data) {
		this.magic = magic;
		this.type = type;
		this.data = ByteUtils.copy(data);
	}

	public Packet(InputStream in) {
		byte[] bytes = new byte[12];
		IOUtil.readFully(in, bytes);

		PacketHeader header = new PacketHeader(bytes);
		byte[] data = new byte[header.getDataLength()];
		if (data.length > 0) {
			IOUtil.readFully(in, data);
		}
		this.magic = header.getMagic();
		this.type = header.getType();
		this.data = data;
	}

	/**
	 * @returns a copy of the array;
	 */
	public byte[] getData() {
		return ByteUtils.copy(data);
	}

	public int getDataSize() {
		return data.length;
	}

	public PacketType getPacketType() {
		return type;
	}

	public byte[] toBytes() {
		int totalSize = getDataSize() + 12;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(totalSize);
		write(baos);
		IOUtil.flush(baos);
		return baos.toByteArray();
	}

	public void write(OutputStream os) {
		/*
		 * HEADER
		 */
		new PacketHeader(magic, type, getDataSize()).write(os);

		/*
		 * DATA
		 * 
		 * Arguments given in the data part are separated by a NULL byte, and
		 * the last argument is determined by the size of data after the last
		 * NULL byte separator. All job handle arguments must not be longer than
		 * 64 bytes, including NULL terminator.
		 */
		IOUtil.write(os, data);
	}

	public PacketType getType() {
		return type;
	}

	public String toString() {
		String s = magic + ":" + type + ":" + data.length;
		if (data.length > 0) {
			s += ": [" + ByteUtils.toHex(data) + "]";
		}
		return s;
	}

}
