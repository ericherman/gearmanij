/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.Serializable;
import java.net.Socket;

public class DynamicClassSender {
    public static void main(String[] args) throws Exception {
        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        String className = args[2];
        Class<?> aClass = Class.forName(className);
        Serializable payload = (Serializable) aClass.newInstance();
        Socket socket = new Socket(hostName, port);
        ObjectSender sender = new ObjectSender(socket);
        sender.send(payload);
        socket.close();
    }
}
