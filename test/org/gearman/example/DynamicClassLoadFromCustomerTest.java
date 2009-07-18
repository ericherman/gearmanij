/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.gearman.Constants;
import org.gearman.io.DynamicClassLoadTestFixture;
import org.gearman.io.ObjectReceiver;
import org.gearman.util.Shell;

public class DynamicClassLoadFromCustomerTest extends
        DynamicClassLoadTestFixture {

    private Serializable result;

    protected void tearDown() throws Exception {
        result = null;
    }

    public void testRoundTripAlienOrder() throws Exception {
        String[] source = {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "public class Alien implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        return \"Alien\";", //
                "    }", //
                "}", //
        };
        roundTrip("Alien", source);
    }

    public void testComplexAlienOrder() throws Exception {
        String[] source1 = { "package aliens;", //
                "public class AlienChild {", //
                "    public int foo = 0;", //
                "    public int getRand() {", //
                "        return (int) (10 * Math.random());", //
                "    }", //
                "}", //
        };

        compileAlienClass("AlienChild", source1);

        String[] source2 = {
                "package aliens;", //
                "import java.io.*;", //
                "import java.util.concurrent.Callable;", //
                "public class ComplexAlien implements Callable<Serializable>, Serializable {", //
                "    public Serializable call() {", //
                "        AlienChild child = new AlienChild() {", //
                "            public int getRand() {", //
                "                return super.getRand() + 20;", //
                "            }", //
                "        };", //
                "        if (child.getRand() > child.foo){", //
                "            return \"ComplexAlien\";", //
                "        }", //
                "        return null;", //
                "    }", //
                "}", //
        };
        roundTrip("ComplexAlien", source2);
    }

    private void roundTrip(String shortClassName, String[] source)
            throws Exception {

        compileAlienClass(shortClassName, source);
        String className = "aliens." + shortClassName;

        String maxSeconds = "20";
        String workUnits = "1";

        String[] cookArgs = new String[] { "java", "-cp", CLASSPATH,
                WorkerRunner.class.getName(),
                InetAddress.getLocalHost().getHostName(),
                "" + Constants.GEARMAN_DEFAULT_TCP_PORT, maxSeconds, workUnits, };

        new Shell(cookArgs, ENVP, "cook", out, err).start();

        String javaProgram = CustomerRunner.class.getName();
        assertEquals("org.gearman.example.CustomerRunner", javaProgram);

        final ServerSocket reportingServer = new ServerSocket(0);

        // System.out.println(CLASSPATH);
        // System.out.println(alienClasspath);
        String[] args = new String[] { "java", "-cp", alienClasspath,
                javaProgram, maxSeconds,
                "" + Constants.GEARMAN_DEFAULT_TCP_PORT, className,
                "" + reportingServer.getLocalPort() };

        launched = new Shell(args, ENVP, "send alien", out, err);
        launched.start();

        Thread t = new Thread(new Runnable() {
            public void run() {
                Socket s = null;
                try {
                    s = reportingServer.accept();
                    result = new ObjectReceiver(s).receive();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (s != null) {
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();
        
        for (int i = 0; i < 10 && !shortClassName.equals(result); i++) {
            Thread.sleep(1000);
        }
        reportingServer.close();
        assertEquals(shortClassName, result);
    }
}
