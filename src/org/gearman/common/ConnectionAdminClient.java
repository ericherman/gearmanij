/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.common;

import java.util.List;

import org.gearman.AdminClient;
import org.gearman.Connection;

public class ConnectionAdminClient implements AdminClient {

    private Connection conn;

    public ConnectionAdminClient(Connection conn) {
        this.conn = conn;
    }

    public List<String> getWorkerInfo() {
        return conn.getTextModeListResult(AdminClient.COMMAND_WORKERS);
    }

    public List<String> getFunctionStatus() {
        return conn.getTextModeListResult(AdminClient.COMMAND_STATUS);
    }

    public boolean setDefaultMaxQueueSize(String functionName) {
        Object[] params = new Object[] { functionName };
        String command = AdminClient.COMMAND_MAXQUEUE;
        String resp = conn.getTextModeResult(command, params);
        return "OK".equals(resp);
    }

    public boolean setMaxQueueSize(String functionName, int size) {
        Object[] params = new Object[] { functionName, size };
        String command = AdminClient.COMMAND_MAXQUEUE;
        String resp = conn.getTextModeResult(command, params);
        return "OK".equals(resp);
    }

    public String getVersion() {
        String command = AdminClient.COMMAND_VERSION;
        return conn.getTextModeResult(command, new Object[0]);
    }

}
