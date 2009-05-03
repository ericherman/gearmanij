/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gearmanij.PacketMagic.BadMagicException;
import gearmanij.util.TestUtil;

import org.junit.Test;

public class PacketMagicTest {
	byte[] reqBytes = new byte[] { 0, 'R', 'E', 'Q' };
	byte[] resBytes = new byte[] { 0, 'R', 'E', 'S' };

	@Test
	public void testMagicBytes() {
		assertEquals(2, PacketMagic.values().length);
		assertBytesEq(reqBytes, PacketMagic.REQ.toBytes());
		assertBytesEq(resBytes, PacketMagic.RES.toBytes());
	}

	private void assertBytesEq(byte[] expected, byte[] actual) {
		TestUtil.assertArraysEqual(expected, actual);
	}

	@Test
	public void testFromBytes() {
		/*
		 * "\0REQ" == [ 00 52 45 51 ] == 5391697
		 * 
		 * "\0RES" == [ 00 52 45 53 ] == 5391699
		 */
		assertEquals(PacketMagic.REQ, PacketMagic.fromBytes(reqBytes));
		assertEquals(PacketMagic.RES, PacketMagic.fromBytes(resBytes));
		BadMagicException expected = null;
		try {
			PacketMagic.fromBytes(new byte[] { 2, 3, 4, 5 });
		} catch(BadMagicException e) {
			expected = e;
		}
		assertNotNull(expected);
	}
}
