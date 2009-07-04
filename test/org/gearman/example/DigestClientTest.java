/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.example;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import org.gearman.Job;
import org.gearman.client.JobResponse;
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;
import org.gearman.worker.WorkerJob;
import org.junit.Test;

public class DigestClientTest {

    private byte[] nullTermintedByteArray(String str) {
        byte[] hBytes = new byte[1 + str.length()];
        byte[] bytes = ByteUtils.toUTF8Bytes(str);
        System.arraycopy(bytes, 0, hBytes, 0, str.length());
        hBytes[hBytes.length - 1] = 0;
        return hBytes;
    }

    @Test
    public void testFunction() {
        DigestClient client = new DigestClient(null) {
            protected Callable<JobResponse> newClientJob(final byte[] input,
                    String function, String uniqueId) {
                return new Callable<JobResponse>() {
                    public JobResponse call() {
                        String function = "digest";

                        String uniqueId = "id";
                        byte[] id = ByteUtils.toUTF8Bytes(uniqueId);

                        String handle = "handle";
                        byte[] hBytes = nullTermintedByteArray(handle);

                        Job job = new WorkerJob(hBytes, function, id, input);

                        new DigestFunction().execute(job);

                        byte[] resultBytes = job.getResult();
                        ByteArrayBuffer buf = new ByteArrayBuffer(hBytes);
                        buf.append(resultBytes);
                        return new JobResponse(buf.getBytes());
                    }
                };
            }
        };

        String algorithm = "MD5";
        String s = "foo\n";
        byte[] bytes = ByteUtils.toUTF8Bytes(algorithm);
        ByteArrayBuffer bab = new ByteArrayBuffer(bytes);
        bab.append(ByteUtils.NULL);
        bab.append(ByteUtils.toUTF8Bytes(s));
        byte[] input = bab.getBytes();
        byte[] actualBytes = client.digest(input);
        String expected = "d3b07384d113edec49eaa6238ad5ff00";
        String actual = ByteUtils.toHex(actualBytes);
        assertEquals(expected, actual);
    }
}
