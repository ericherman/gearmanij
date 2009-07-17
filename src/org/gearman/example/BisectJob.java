/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.example;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BisectJob implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;
    private final String line;
    private final int port;
    private final InetAddress host;

    public BisectJob(String line, InetAddress host, int port) {
        this.line = line;
        this.host = host;
        this.port = port;
    }

    public void run() {
        for (String half : bisect(line)) {
            Socket socket = null;
            try {
                socket = new Socket(host, port);
                OutputStream out = socket.getOutputStream();
                out.write(half.getBytes("UTF-8"));
                out.write("\n".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    List<String> bisect(String s) {
        List<String> list = new ArrayList<String>(2);
        int middle = s.length() / 2;
        list.add(s.substring(0, middle));
        list.add(s.substring(middle, s.length()));
        return list;
    }

    public String toString() {
        return getClass().getSimpleName() + " " + line;
    }
}
