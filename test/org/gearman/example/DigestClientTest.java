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
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;
import org.gearman.worker.WorkerJob;
import org.junit.Test;

public class DigestClientTest {

  @Test
  public void testFunction() {
    DigestClient client = new DigestClient(null) {
      protected Callable<byte[]> newClientJob(final byte[] input,
          String function, String uniqueId) {
        return new Callable<byte[]>() {
          public byte[] call() {
            String function = "digest";
            String uniqueId = "id";
            String handle = "handle";
            byte[] handleBytes = new byte[1 + handle.length()];
            byte[] bytes = ByteUtils.toUTF8Bytes(handle);
            System.arraycopy(bytes, 0, handleBytes, 0, handle.length());
            handleBytes[handleBytes.length - 1] = 0;
            byte[] id = ByteUtils.toUTF8Bytes(uniqueId);
            Job job = new WorkerJob(handleBytes, function, id, input);
            new DigestFunction().execute(job);
            return job.getResult();
          }
        };
      }
    };

    String algorithm = "MD5";
    String s = "foo\n";
    ByteArrayBuffer bab = new ByteArrayBuffer(ByteUtils.toUTF8Bytes(algorithm));
    bab.append(ByteUtils.NULL);
    bab.append(ByteUtils.toUTF8Bytes(s));
    byte[] input = bab.getBytes();
    byte[] actual = client.digest(input);
    assertEquals("d3b07384d113edec49eaa6238ad5ff00", ByteUtils.toHex(actual));
  }
}
