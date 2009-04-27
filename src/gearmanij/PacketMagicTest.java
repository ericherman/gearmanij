package gearmanij;

import junit.framework.TestCase;

public class PacketMagicTest extends TestCase {

	public void testMagicBytes() {
		assertEquals(2, PacketMagic.values().length);
		assertEquals(new byte[] { 0, 'R', 'E', 'Q' }, PacketMagic.REQ.toBytes());
		assertEquals(new byte[] { 0, 'R', 'E', 'S' }, PacketMagic.RES.toBytes());
	}

	private void assertEquals(byte[] expected, byte[] actual) {
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

}
