/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.gearman.AdminClient;
import org.gearman.PacketConnection;
import org.gearman.Job;
import org.gearman.JobFunction;
import org.gearman.PacketType;
import org.gearman.TextConnection;
import org.gearman.Worker;
import org.gearman.Worker.WorkerOption;
import org.gearman.common.ConnectionAdminClient;
import org.gearman.common.SocketConnection;
import org.gearman.example.DigestFunction;
import org.gearman.example.ReverseClient;
import org.gearman.example.ReverseFunction;
import org.gearman.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StandardWorkerTest {

    private Worker worker;
    private PacketConnection conn;
    private AdminClient connAdmin;
    private PacketConnection clientConn;

    @Before
    public void setUp() {
        worker = new StandardWorker();
    }

    @After
    public void tearDown() {
        conn = null;
        connAdmin = null;
        List<Exception> close = Collections.emptyList();
        try {
            close = worker.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        worker = null;
        for (Exception e : close) {
            e.printStackTrace();
        }
        try {
            if (clientConn != null) {
                clientConn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientConn = null;
        }
    }

    @Test
    public void testWorkerOptions() {
        assertEquals(worker.getWorkerOptions(), EnumSet
                .noneOf(WorkerOption.class));
    }

    @Test
    public void testRemoveOptions() {
        worker.setWorkerOptions(WorkerOption.NON_BLOCKING,
                WorkerOption.GRAB_UNIQ);
        Collection<WorkerOption> c;
        c = EnumSet.of(WorkerOption.NON_BLOCKING, WorkerOption.GRAB_UNIQ);
        assertTrue(worker.getWorkerOptions().containsAll(c));
        worker.removeWorkerOptions(WorkerOption.GRAB_UNIQ);
        assertTrue(worker.getWorkerOptions()
                .contains(WorkerOption.NON_BLOCKING));
        assertFalse(worker.getWorkerOptions().contains(WorkerOption.GRAB_UNIQ));
    }

    @Test
    public void testSetWorkerOptions() {
        Collection<WorkerOption> c = null;

        worker.setWorkerOptions(WorkerOption.NON_BLOCKING);
        c = EnumSet.of(WorkerOption.NON_BLOCKING);
        assertTrue(worker.getWorkerOptions().containsAll(c));

        worker.clearWorkerOptions();
        worker.setWorkerOptions(WorkerOption.NON_BLOCKING,
                WorkerOption.NON_BLOCKING);
        c = EnumSet.of(WorkerOption.NON_BLOCKING);
        assertTrue(worker.getWorkerOptions().containsAll(c));
    }

    @Test
    public void testSetWorkerID() {
        newSocketConnection();
        String id = "SimpleWorker";

        worker.setWorkerID(id);
        assertTrue(TestUtil.isWorkerFoundByID(connAdmin, id));
    }

    private void newSocketConnection() {
        conn = new SocketConnection();
        connAdmin = new ConnectionAdminClient((TextConnection) conn);
        worker.addServer(conn);
    }

    @Test
    public void testRegisterFunction() {
        newSocketConnection();
        JobFunction digest = new DigestFunction();
        String id = "testRegisterFunction";

        worker.setWorkerID(id);
        worker.registerFunction(DigestFunction.class);
        String name = digest.getName();
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
    }

    /**
     * Registers a function that sleeps for 3 seconds with a timeout of only 1
     * second.
     */
    @Test
    public void testRegisterFunctionWithTimeout() throws Exception {
        clientConn = new SocketConnection();
        final ReverseClient client = new ReverseClient(clientConn);
        Thread t = TestUtil.startThread("test_reverse_client", new Runnable() {
            public void run() {
                client.reverse("foo");
            }
        });

        newSocketConnection();

        class DelayReverseFunction extends ReverseFunction {
            private long delay;

            public DelayReverseFunction(int delaySeconds) {
                this.delay = delaySeconds * 1000;
            }

            public void execute(Job job) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.execute(job);
            }
        }

        int delaySeconds = 3;
        JobFunction reverse = new DelayReverseFunction(delaySeconds);
        int timeout = 1;
        String id = "testRegisterFunctionWithTimeout";
        PacketType type;

        worker.setWorkerID(id);
        worker.registerFunction(reverse, timeout);
        String name = reverse.getName();
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
        type = worker.grabJob(conn);
        assertTrue(PacketType.JOB_ASSIGN == type);
        worker.unregisterFunction(reverse.getName());
        assertFalse(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
        t.join(100);
    }

    @Test
    public void testUnregisterFunction() {
        newSocketConnection();
        JobFunction digest = new DigestFunction();
        String id = "testUnregisterFunction";

        worker.setWorkerID(id);
        worker.registerFunction(DigestFunction.class);
        String name = digest.getName();
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
        worker.unregisterFunction(digest.getName());
        assertFalse(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
    }

    /**
     * Note: This crashes gearmand 0.5 if run twice. The crash occurs when
     * digest is registered. So, ignoring this test for now.
     */
    @Test
    @Ignore
    public void testUnregisterAll() {
        newSocketConnection();
        JobFunction reverse = new ReverseFunction();
        String revName = reverse.getName();
        JobFunction digest = new DigestFunction();
        String digName = digest.getName();
        String id = "testUnregisterAll";

        worker.setWorkerID(id);
        worker.registerFunction(ReverseFunction.class);
        worker.registerFunction(DigestFunction.class);
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id,
                revName));
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id,
                digName));
        worker.unregisterAll();
        assertFalse(TestUtil.isFunctionRegisteredForWorker(connAdmin, id,
                revName));
        assertFalse(TestUtil.isFunctionRegisteredForWorker(connAdmin, id,
                digName));
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterIllegalFunction() {
        newSocketConnection();
        JobFunction digest = new IllegalJobFunction();
        String id = "testRegisterIllegalFunction";

        worker.setWorkerID(id);
        worker.registerFunction(IllegalJobFunction.class);
        String name = digest.getName();
        assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));
    }

    private static class IllegalJobFunction extends DigestFunction {
    }

}
