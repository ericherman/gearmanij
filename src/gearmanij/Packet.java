package gearmanij;

import gearmanij.util.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Packet {

	private PacketMagic magic;

	private PacketType type;

	private byte[] data;

	public Packet(PacketMagic magic, PacketType type, byte[] data) {
		this.magic = magic;
		this.type = type;
		this.data = data;
	}

	public int getDataSize() {
		return data == null ? 0 : data.length;
	}

	/*
	 * 4 byte size - A big-endian (network-job) integer containing the size of
	 * the data being sent after the header.
	 */
	public byte[] getDataSizeBytes() {
		return ByteUtils.toBigEndian(getDataSize());
	}

	public byte[] toBytes() {
		int totalSize = getDataSize() + 12;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(totalSize);
		write(baos);
		try {
			baos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	public void write(OutputStream os) {
		try {
			/*
			 * HEADER
			 * 
			 * 4 byte magic code - This is either "\0REQ" for requests or
			 * "\0RES"for responses.
			 * 
			 * 4 byte type - A big-endian (network-job) integer containing an
			 * enumerated packet type. Possible values are:
			 * 
			 * 4 byte size - A big-endian (network-job) integer containing the
			 * size of the data being sent after the header.
			 */
			os.write(magic.toBytes());
			os.write(type.toBytes());
			os.write(getDataSizeBytes());

			/*
			 * DATA
			 * 
			 * Arguments given in the data part are separated by a NULL byte,
			 * and the last argument is determined by the size of data after the
			 * last NULL byte separator. All job handle arguments must not be
			 * longer than 64 bytes, including NULL terminator.
			 */
			os.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
