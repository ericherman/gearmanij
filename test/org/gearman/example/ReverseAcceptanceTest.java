/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.gearman.Constants;
import org.gearman.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReverseAcceptanceTest {

    private boolean jobFinished1;
    private boolean jobFinished2;
    private String reversed1;
    private String reversed2;
    private ReverseWorker worker;
    private ReverseClient client;

    @Before
    public void setUp() {
        jobFinished1 = false;
        jobFinished2 = false;
        reversed1 = null;
        reversed2 = null;
        client = new ReverseClient(host(), port());
        worker = new ReverseWorker(host(), port());
    }

    @After
    public void tearDown() {
        reversed1 = null;
        reversed2 = null;
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
                worker.start();
            }
        });

        Thread customerThread = TestUtil.startThread("Customer",
                new Runnable() {
                    public void run() {
                        reversed1 = client.reverseGearmanFunciton(hello);
                        jobFinished1 = true;
                        reversed2 = client.reverseJavaFunction(hello);
                        jobFinished2 = true;
                    }
                });

        customerThread.join(2000);
        worker.shutdown();
        workerThread.join(1000);

        assertTrue(jobFinished1);
        assertNotNull(reversed1);
        assertEquals(olleh.trim(), reversed1.trim());
        assertEquals(olleh, reversed1);

        assertTrue(jobFinished2);
        assertNotNull(reversed2);
        assertEquals(olleh.trim(), reversed2.trim());
        assertEquals(olleh, reversed2);
    }

}
