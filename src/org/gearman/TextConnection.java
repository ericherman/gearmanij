/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman;

import java.util.List;

/**
 * Objects that implement this interface manage a connection to a Gearman job
 * server.
 * 
 * It may be worth splitting out the text commands into a separate interface
 * when we have multiple real implementations.
 */
public interface TextConnection {

    /**
     * Open a new connection to a job server.
     */
    void open();

    /**
     * Close the current connection, if any, to a job server.
     */
    void close();

    String getTextModeResult(String command, Object[] params);

    /**
     * Sends an admin command to a Gearman job server and returns the results as
     * a List of Strings. This works only for the workers and status text
     * commands.
     * 
     * 
     * The maxqueue and shutdown commands can take arguments and do not return a
     * final line with a '.'.
     * <p>
     * TODO:Add a params argument?
     * <p>
     * TODO:Rather than potentially blocking forever, there should be a timeout.
     * 
     * @param command
     *            The text command
     * @return results as a List of Strings for the command
     */
    List<String> getTextModeListResult(String command);

}
