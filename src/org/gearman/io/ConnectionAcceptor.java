/**
 * Copyright (C) 2009 by Eric Herman. 
 * For licensing information see GnuLesserGeneralPublicLicense-2.1.txt 
 *  or http://www.gnu.org/licenses/lgpl-2.1.txt
 *  or for alternative licensing, email Eric Herman: eric AT freesa DOT org
 */
package org.gearman.io;

import java.io.IOException;
import java.net.Socket;

public interface ConnectionAcceptor {
    void acceptConnection(Socket s) throws IOException;

    void close(Socket socket, Exception thrown);
}
