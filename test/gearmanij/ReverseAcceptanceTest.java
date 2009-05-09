/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gearmanij.example.ReverseClient;
import gearmanij.example.ReverseWorker;
import gearmanij.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReverseAcceptanceTest {

  private boolean jobFinished;
  private String reversed;
  private ReverseWorker worker;
  private ReverseClient client;

  @Before
  public void setUp() {
    jobFinished = false;
    reversed = null;
    client = new ReverseClient(host(), port());
    worker = new ReverseWorker(host(), port());
  }

  @After
  public void tearDown() {
    reversed = null;
    client = null;
    worker.shutdown();
    worker = null;
  }

  // TODO not really @Deprecated, just make parameterizable
  @Deprecated
  private int port() {
    return Constants.GEARMAN_DEFAULT_TCP_PORT;
  }

  // TODO not really @Deprecated, just make parameterizable
  @Deprecated
  private String host() {
    return Constants.GEARMAN_DEFAULT_TCP_HOST;
  }

  @Test
  public void testRoundTrip() throws Exception {

    final String hello = "Hello";
    final String olleh = "olleH";

    Thread workerThread = TestUtil.startThread("Worker", new Runnable() {
      public void run() {
        worker.work();
      }
    });

    Thread customerThread = TestUtil.startThread("Customer", new Runnable() {
      public void run() {
        reversed = client.reverse(hello);
        jobFinished = true;
      }
    });

    customerThread.join(2000);
    worker.shutdown();
    workerThread.join(1000);

    assertTrue(jobFinished);
    assertNotNull(reversed);
    assertEquals(olleh.trim(), reversed.trim());
    assertEquals(olleh, reversed);
  }

}
