/**
 * Copyright (C) 2003 - 2009 by Eric Herman.
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt 
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org 
 */
package org.gearman.util;

import java.io.IOException;
import java.io.PrintStream;

public class Shell extends Thread {
    private String[] args;
    private String[] envp;
    private PrintStream out;
    private PrintStream err;
    private Streams streams;
    private int returnCode;

    public Shell(String[] args, String[] envp, String name, PrintStream out,
            PrintStream err) {
        super(name);
        this.args = args;
        this.envp = envp;
        this.returnCode = Integer.MIN_VALUE;
        this.streams = new Streams();
        this.out = (out != null) ? out : new NullPrintStream();
        this.err = (err != null) ? err : new NullPrintStream();

    }

    public Shell(String[] args, String[] envp, String name) {
        this(args, envp, name, System.out, System.err);
    }

    public void run() {
        try {
            returnCode = runtimeExec();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int runtimeExec() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, envp);
        streams.connect(p.getInputStream(), out);
        streams.connect(p.getErrorStream(), err);
        return p.waitFor();
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName());
        buf.append(" envp: ");
        for (int i = 0; i < envp.length; i++) {
            buf.append(envp[i]);
            buf.append(", ");
        }
        if (envp.length > 0) {
            buf.delete(buf.length() - 2, buf.length());
        }
        buf.append("\n");
        buf.append(" args: ");
        for (int i = 0; i < args.length; i++) {
            buf.append(args[i]);
            buf.append(" ");
        }
        if (args.length > 0) {
            buf.delete(buf.length() - 1, buf.length());
        }
        return buf.toString();
    }

}
