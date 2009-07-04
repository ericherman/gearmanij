/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.worker;

import static org.junit.Assert.assertTrue;

import org.gearman.Connection;
import org.gearman.JobFunctionFactory;
import org.gearman.Worker;
import org.gearman.common.ConnectionAdminClient;
import org.gearman.common.SocketConnection;
import org.gearman.example.ReverseFunction;
import org.gearman.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class JobFunctionFactoryTest {
    private Worker worker;
    private Connection conn;
    private ConnectionAdminClient connAdmin;

    @Before
    public void setUp() {
        worker = new StandardWorker();
    }

    @Test
    public void testRegisterFactory() {

        newSocketConnection();
        String id = "RegisterFactory";
        ReverseFunction function = new ReverseFunction();
        JobFunctionFactory factory = new InstanceJobFunctionFactory(function);
        String name = factory.getFunctionName();

        worker.setWorkerID(id);
        worker.registerFunctionFactory(factory);
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));

    }

    private void newSocketConnection() {
        conn = new SocketConnection();
        connAdmin = new ConnectionAdminClient(conn);
        worker.addServer(conn);
    }
}
