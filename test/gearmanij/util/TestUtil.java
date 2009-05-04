/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static org.junit.Assert.assertEquals;

public class TestUtil {

	public static void assertArraysEqual(final byte[] left, final byte[] right) {
		if (left == null || right == null) {
			assertEquals(left, right);
			return;
		}

		assertEquals("lengths differ", left.length, right.length);
		for (int i = 0; i < left.length; i++) {
			assertEquals("element " + i, left[i], right[i]);
		}
	}

	public static void assertEqualsIgnoreCase(String left, String right) {
		if (left != null && left.equalsIgnoreCase(right)) {
			return;
		}
		assertEquals(left, right);
	}

}
