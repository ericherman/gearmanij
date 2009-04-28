/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class ByteArrayBuffer implements Serializable {
	private static final long serialVersionUID = 1L;

	private byte[] buf;
	private int copyBufferSize;

	public ByteArrayBuffer() {
		this(new byte[0]);
	}

	public ByteArrayBuffer(byte[] bytes) {
		this(bytes, 4 * 1024);
	}

	public ByteArrayBuffer(byte[] bytes, int copyBufferSize) {
		this.copyBufferSize = copyBufferSize;
		this.buf = new byte[bytes.length];
		System.arraycopy(bytes, 0, buf, 0, bytes.length);
	}

	public byte[] getBytes() {
		return buf;
	}

	public ByteArrayBuffer append(byte[] bytes) {
		return append(bytes, 0, bytes.length);
	}

	public ByteArrayBuffer append(byte b) {
		return append(new byte[] { b });
	}

	public ByteArrayBuffer append(byte[] bytes, int startPosition, int len) {
		byte[] old = buf;
		buf = new byte[old.length + len];
		System.arraycopy(old, 0, buf, 0, old.length);
		System.arraycopy(bytes, startPosition, buf, old.length, len);
		return this;
	}

	public ByteArrayBuffer append(InputStream is) {
		final int EOF = -1;
		byte[] inputBuf = new byte[copyBufferSize];
		while (true) {
			int len = read(is, inputBuf);
			if (len == EOF) {
				break;
			}
			append(inputBuf, 0, len);
		}
		return this;
	}

	private int read(InputStream is, byte[] inputBuf) {
		try {
			return is.read(inputBuf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toString() {
		try {
			return new String(buf, "ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
