/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.gearman.Constants;
import org.gearman.common.SocketConnection;
import org.gearman.example.ReverseClient;
import org.gearman.example.ReverseWorker;
import org.gearman.util.ByteUtils;
import org.gearman.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StatusCheckAcceptanceTest {

    private boolean jobFinished;
    private String reversed;
    private ReverseWorker worker;
    private ReverseClient client;
    private Status status;

    @Before
    public void setUp() {
        jobFinished = false;
        reversed = null;

        SocketConnection c1 = new SocketConnection(host(), port());
        client = new ReverseClient(c1);

        SocketConnection c2 = new SocketConnection(host(), port());
        worker = new ReverseWorker(c2);

        SocketConnection c3 = new SocketConnection(host(), port());
        status = new Status(c3);
    }

    @After
    public void tearDown() {
        reversed = null;
        client = null;
        worker.shutdown();
        worker = null;
        status = null;
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

        Thread customerThread = TestUtil.startThread("Customer",
                new Runnable() {
                    public void run() {
                        reversed = client.reverse(hello);
                        jobFinished = true;
                    }
                });

        Thread workerThread = TestUtil.startThread("Worker", new Runnable() {
            public void run() {
                for (int i = 0; i < 10 && status.getLastUpdate() == 0; i++) {
                    TestUtil.sleep(200);
                }
                worker.start();
            }
        });

        assertEquals(false, jobFinished);
        assertEquals(0L, status.getLastUpdate());

        status.setHandle(getHandle(1000));
        status.update();
        assertTrue(status.getLastUpdate() > 0);

        customerThread.join(2000);
        worker.shutdown();
        workerThread.join(1000);

        assertTrue(jobFinished);
        assertNotNull(reversed);
        assertEquals(olleh.trim(), reversed.trim());
        assertEquals(olleh, reversed);
    }

    private byte[] getHandle(long timeout) throws InterruptedException {
        long start = System.currentTimeMillis();
        byte[] handle = ByteUtils.EMPTY;
        while (Arrays.equals(ByteUtils.EMPTY, handle)) {
            handle = client.getHandle();
            if (start + timeout > System.currentTimeMillis()) {
                Thread.sleep(100);
            }
        }
        assertTrue(!Arrays.equals(ByteUtils.EMPTY, handle));
        return handle;
    }

}
