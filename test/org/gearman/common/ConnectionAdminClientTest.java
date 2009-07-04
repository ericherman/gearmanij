/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.common;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.gearman.AdminClient;
import org.gearman.Connection;
import org.gearman.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ConnectionAdminClientTest {

    private AdminClient admin;
    private Connection conn = null;

    @Before
    public void setUp() {
        conn = new SocketConnection();
        conn.open();
        admin = new ConnectionAdminClient(conn);
    }

    @After
    public void tearDown() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn = null;
    }

    @Test
    public void testGetWorkerInfo() {
        // Add assertions to verify commands work as expected
        List<String> workerInfo = admin.getWorkerInfo();
        TestUtil.dump(AdminClient.COMMAND_WORKERS, workerInfo);
    }

    @Test
    public void testGetFunctionStatus() {
        // Add assertions to verify commands work as expected
        List<String> functionStatus = admin.getFunctionStatus();
        TestUtil.dump(AdminClient.COMMAND_STATUS, functionStatus);
    }

    @Test
    public void testGetVersion() {
        // Add assertions to verify version matches the version of gearmand
        String version = admin.getVersion();
        TestUtil.dump(AdminClient.COMMAND_VERSION, version);
    }

    @Test
    @Ignore
    // TODO Need to have a worker that has registered a function
    public void testSetDefaultMaxQueueSize() {
        String functionName = "maxqueuetest";
        boolean success = admin.setDefaultMaxQueueSize(functionName);
        assertTrue(success);
    }

    @Test
    @Ignore
    // TODO Need to have a worker that has registered a function. Ideally, then
    // have a client submit tasks for each of the scenarios and confirm they
    // behave as expected.
    public void testSetMaxQueueSize() {
        String functionName = "maxqueuetest";
        boolean success;

        // Unlimited
        success = admin.setMaxQueueSize(functionName, -1);
        assertTrue(success);

        // Need to confirm setting to 0 prevents queueing
        success = admin.setMaxQueueSize(functionName, 0);
        assertTrue(success);

        // Queue depth of 2
        success = admin.setMaxQueueSize(functionName, 2);
        assertTrue(success);
    }

}
