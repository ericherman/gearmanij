/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.gearman.Constants;
import org.gearman.io.ConnectionServer;
import org.gearman.util.Shell;

public class AcceptanceMultiVMTest extends TestCase {

    // netstat -anpt | grep 16
    private int port;
    private int port2;
    private PrintStream out;
    private PrintStream err;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AcceptanceMultiVMTest.class);
    }

    protected void setUp() throws Exception {
        port = Constants.GEARMAN_DEFAULT_TCP_PORT;
        port2 = (false) ? tryForRandomPort()
                : Constants.GEARMAN_DEFAULT_TCP_PORT + 10000;
        // out = new NullPrintStream();
        // err = out;
        out = System.out;
        err = System.err;
    }

    private int tryForRandomPort() {
        return 16000 + (int) (1000 * Math.random())
                + (int) (System.currentTimeMillis() % 1000L);
    }

    protected void tearDown() {
        out = null;
        err = null;
    }

    public void testTriangle() throws Exception {
        String path = System.getProperty("java.library.path");
        String[] envp = new String[] { "PATH=" + path };
        String classpath = System.getProperty("java.class.path");
        String maxSeconds = "15";
        String workUnits = "0";
        String sandbox = Boolean.FALSE.toString();

        String[] gearmandArgs = null;

        String[] workerArgs = new String[] { "java", "-cp", classpath,
                org.gearman.example.WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, sandbox };

        String[] resenderArgs = new String[] { "java", "-cp", classpath,
                org.gearman.example.ResendRunner.class.getName(), "" + port2,
                InetAddress.getLocalHost().getHostName(), "" + port,
                maxSeconds, workUnits, };

        assertNull(gearmandArgs);

        ByteArrayOutputStream workdOs = new ByteArrayOutputStream();
        PrintStream workOut = new PrintStream(workdOs);
        Shell worker = new Shell(workerArgs, envp, "workerd", workOut, workOut);
        worker.start();

        ByteArrayOutputStream sendOs = new ByteArrayOutputStream();
        PrintStream sendOut = new PrintStream(sendOs);
        Shell sender = new Shell(resenderArgs, envp, "resend", sendOut, sendOut);
        sender.start();

        Socket s = null;
        try {
            s = getSocket(25, port2);
            OutputStream os = s.getOutputStream();
            os.write("Hello, World!".getBytes("UTF-8"));
        } catch (Exception e) {
            dump(workdOs, workOut, worker, sendOs, sendOut, sender);
            throw e;
        } finally {
            if (s != null) {
                s.close();
            }
        }

        List<String> expected = new ArrayList<String>();
        expected.add("'H'");
        expected.add("'e'");
        expected.add("'l'");
        expected.add("'l'");
        expected.add("'o'");
        expected.add("','");
        expected.add("' '");
        expected.add("'W'");
        expected.add("'o'");
        expected.add("'r'");
        expected.add("'l'");
        expected.add("'d'");
        expected.add("'!'");

        int found = 0;
        int loopLimit = 500;
        for (int i = 0; end(expected, found, sender); i++) {
            if (i >= loopLimit) {
                dump(workdOs, workOut, worker, sendOs, sendOut, sender);
                assertTrue("infinite loop ", i < loopLimit);
            }
            sendOut.flush();
            String capturedSenderOut = sendOs.toString();
            found = 0;
            for (String single : expected) {
                if (capturedSenderOut.contains(single)) {
                    found++;
                }
            }
            Thread.sleep(ConnectionServer.SLEEP_DELAY);
        }

        if (expected.size() != found) {
            dump(workdOs, workOut, worker, sendOs, sendOut, sender);
        }
        assertEquals(expected.size(), found);
    }

    private void dump(ByteArrayOutputStream workdOs, PrintStream workOut,
            Shell worker, ByteArrayOutputStream sendOs, PrintStream sendOut,
            Shell sender) {

        err.println();
        err.println(sender.getName());
        err.println(sender);
        sendOut.flush();
        err.println(sendOs.toString());
        err.println();
        err.println(worker.getName());
        err.println(worker);
        workOut.flush();
        err.println(workdOs.toString());
        err.println();
        Properties properties = System.getProperties();
        for (Object key : properties.keySet()) {
            err.print(key);
            err.print(" = ");
            err.println(properties.getProperty((String) key));
        }
    }

    private boolean end(List<String> expected, int found, Shell sender) {
        if (Integer.MIN_VALUE != sender.getReturnCode()) {
            return true;
        }
        return found < expected.size();
    }

    private Socket getSocket(int tries, int portNum) throws Exception {
        for (int i = 0; i < tries; i++) {
            try {
                return new Socket(InetAddress.getLocalHost(), portNum);
            } catch (ConnectException e) {
                Thread.sleep(100 * ConnectionServer.SLEEP_DELAY);
            }
        }
        return new Socket(InetAddress.getLocalHost(), portNum);
    }

}
