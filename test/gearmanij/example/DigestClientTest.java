/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import static org.junit.Assert.assertEquals;
import gearmanij.util.ByteUtils;

import java.util.concurrent.Callable;

import org.junit.Test;

public class DigestClientTest {

  @Test
  public void testFunction() {
    DigestClient client = new DigestClient(null) {
      protected Callable<byte[]> newClientJob(final byte[] input,
          String function, String uniqueId) {
        return new Callable<byte[]>() {
          public byte[] call() {
            return new DigestFunction().execute(input);
          }
        };
      }
    };

    String s = "foo\n";
    byte[] input = ByteUtils.toUTF8Bytes(s);
    byte[] actual = client.digest(input);
    assertEquals("d3b07384d113edec49eaa6238ad5ff00", ByteUtils.toHex(actual));
  }
}
