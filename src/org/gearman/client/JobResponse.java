package org.gearman.client;

import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;

public class JobResponse {

    byte[] uniqueId;
    byte[] respData;

    public JobResponse(byte[] respBytes) {
        ByteArrayBuffer baBuff = new ByteArrayBuffer(respBytes);
        int end = baBuff.indexOf(ByteUtils.NULL);
        this.uniqueId = baBuff.subArray(0, end + 1);
        this.respData = baBuff.subArray(uniqueId.length, respBytes.length);
    }

    public byte[] responseData() {
        return respData;
    }

}
