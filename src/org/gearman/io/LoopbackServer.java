/**
 * Copyright (C) 2003 - 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.net.Socket;

public class LoopbackServer extends ConnectionServer {
    public LoopbackServer() {
        this(0);
    }

    public LoopbackServer(int port) {
        super(port, "LoopBack");
    }

    public void acceptConnection(Socket s) {
        try {
            ObjectSender sender = new ObjectSender(s);
            ObjectReceiver reciever = new ObjectReceiver(s);
            while (isRunning()) {
                sender.send(reciever.receive());
            }
        } catch (Exception quit) {
            // just die on exception
            // we're probably done
        }
    }
}
