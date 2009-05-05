/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.util;

import static org.junit.Assert.assertEquals;
import gearmanij.Connection;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

	public static void dump(PrintStream out, Map<String, List<String>> responses) {
		for (Map.Entry<String, List<String>> entry : responses.entrySet()) {
			out.println(entry.getKey() + " response:");
			for (String response : entry.getValue()) {
				out.println(response);
			}
		}
	}
	
	/**
	 * Returns true if a worker with the specified ID is found when querying a job server
	 * for information on all connected workers.
	 * 
   * @param conn
   *          Connection to a job server
	 * @param id
	 *          ID of the worker that is being searched for
	 * @return
	 */
	public static boolean isWorkerFoundByID(Connection conn, String id) {
    String[] TEXT_MODE_TEST_COMMANDS = {"WORKERS"};
    Map<String, List<String>> workerEntries;
	  workerEntries = conn.textMode(Arrays.asList(TEXT_MODE_TEST_COMMANDS));
    boolean foundWorker = false;
    for (String worker : workerEntries.keySet()) {
      List<String> value = workerEntries.get(worker);
      if (value.get(0).contains(id)) {
        foundWorker = true;
      }
    }
    return foundWorker;
	}
	
	/**
   * Returns true if a worker with the specified ID is found when querying a job server
   * for information on all connected workers.
   * 
   * @param conn
   *          Connection to a job server
   * @param id
   *          ID of the worker that is being searched for
   * @return
   */
  public static boolean isFunctionRegisteredForWorker(Connection conn, String id, String name) {
    String[] TEXT_MODE_TEST_COMMANDS = {"WORKERS"};
    Map<String, List<String>> workerEntries;
    workerEntries = conn.textMode(Arrays.asList(TEXT_MODE_TEST_COMMANDS));
    boolean foundFunction = false;
    for (String worker : workerEntries.keySet()) {
      List<String> value = workerEntries.get(worker);
      if (value.get(0).contains(id) && value.get(0).endsWith(name)) {
        foundFunction = true;
      }
    }
    return foundFunction;
  }

}
