/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PacketMagicTest {
	@Test
	public void testMagicBytes() {
		assertEquals(2, PacketMagic.values().length);
		assertBytesEq(new byte[] { 0, 'R', 'E', 'Q' }, PacketMagic.REQ.toBytes());
		assertBytesEq(new byte[] { 0, 'R', 'E', 'S' }, PacketMagic.RES.toBytes());
	}

	private void assertBytesEq(byte[] expected, byte[] actual) {
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

}
