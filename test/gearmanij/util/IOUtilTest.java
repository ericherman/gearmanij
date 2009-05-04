/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static gearmanij.util.TestUtil.assertArraysEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IOUtilTest {

	@Test
	public void testReadFully() throws Exception {
		byte[] source = new byte[] { 0, 1, 2 };
		InputStream is = new ByteArrayInputStream(source);
		byte[] buf = new byte[source.length];
		IOUtil.readFully(is, buf);
		is.close();
		assertArraysEqual(source, buf);

		is = new ByteArrayInputStream(source);
		EOFException expected = null;
		try {
			buf = new byte[source.length + 1];
			IOUtil.readFully(is, buf);
			is.close();
		} catch (RuntimeIOException e) {
			expected = (EOFException) e.getCause();
		}
		assertNotNull(expected);
		String find = ByteUtils.toHex(source);
		String msg = expected.getMessage();
		assertTrue(msg, msg.indexOf(find) >= 0);
	}

	@Test
	public void testFlush() {
		class MockFlushable implements Flushable {
			public int flushCalls = 0;
			public boolean boom = false;

			public void flush() throws IOException {
				flushCalls++;
				if (boom) {
					throw new IOException("boom");
				}
			}
		}
		MockFlushable mf = new MockFlushable();
		IOUtil.flush(mf);
		assertEquals(1, mf.flushCalls);

		IOException expected = null;
		try {
			mf.boom = true;
			IOUtil.flush(mf);
		} catch (RuntimeIOException e) {
			expected = e.getCause();
		}
		assertNotNull(expected);
		String msg = expected.getMessage();
		assertTrue(msg, msg.indexOf("boom") >= 0);
	}

	@Test
	public void testRead() {
		class MockInputStream extends InputStream {
			int chars = 0;
			int reads = 0;
			boolean boom = false;

			public int read() throws IOException {
				if (boom) {
					throw new IOException("bang");
				}
				if (chars > reads) {
					reads++;
					return reads;
				} else {
					return -1;
				}
			}
		}
		MockInputStream mis = new MockInputStream();
		mis.chars = 3;
		byte[] buf = new byte[6];
		int c = IOUtil.read(mis, buf);
		assertEquals(3, c);
		assertArraysEqual(new byte[] { 1, 2, 3, 0, 0, 0 }, buf);

		IOException expected = null;
		try {
			mis.boom = true;
			IOUtil.read(mis, buf);
		} catch (RuntimeIOException e) {
			expected = e.getCause();
		}
		assertNotNull(expected);
		String msg = expected.getMessage();
		assertTrue(msg, msg.indexOf("bang") >= 0);
	}

	@Test
	public void testWrite() {
		class MockOutputStream extends OutputStream {
			boolean boom = false;
			List<Integer> written = new ArrayList<Integer>();

			public void write(int b) throws IOException {
				if (boom) {
					throw new IOException("blam");
				}
				written.add(b);
			}
		}

		MockOutputStream mos = new MockOutputStream();
		IOUtil.write(mos, new byte[] { 5, 7 });
		assertEquals(2, mos.written.size());
		assertEquals(5, mos.written.get(0));
		assertEquals(7, mos.written.get(1));

		IOException expected = null;
		try {
			mos.boom = true;
			IOUtil.write(mos, new byte[] { 13 });
		} catch (RuntimeIOException e) {
			expected = e.getCause();
		}
		assertNotNull(expected);
		String msg = expected.getMessage();
		assertTrue(msg, msg.indexOf("blam") >= 0);
	}

}
