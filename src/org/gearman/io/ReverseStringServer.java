/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class ReverseStringServer extends ConnectionServer {

    public ReverseStringServer(int port) {
        super(port, "ReverseStringServer_" + port);
    }

    public void acceptConnection(Socket s) throws IOException {
        ObjectReceiver receiver = new ObjectReceiver(s);
        Serializable request = receiver.receive();
        StringBuffer buf = new StringBuffer();
        buf.append(request);
        Serializable reply = buf.reverse();
        new ObjectSender(s).send(reply);
    }
}
