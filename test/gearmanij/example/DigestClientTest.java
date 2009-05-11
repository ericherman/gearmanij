/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import static org.junit.Assert.assertEquals;
import gearmanij.Client;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

import org.junit.Test;

public class DigestClientTest {

  @Test
  public void testFunction() {
    Client faux = new Client() {

      public byte[] execute(String function, String uniqueId, byte[] data) {
        return new DigestFunction().execute(data);
      }

      public void printErr(String msg) {
        throw new RuntimeException(msg);
      }

      public void setErr(PrintStream err) {
        throw new RuntimeException("" + err);
      }

      public void shutdown() {
        throw new RuntimeException();
      }
    };
    DigestClient client = new DigestClient(faux);

    String s = "foo\n";
    byte[] input = ByteUtils.toUTF8Bytes(s);
    byte[] actual = client.digest(input);
    assertEquals("d3b07384d113edec49eaa6238ad5ff00", ByteUtils.toHex(actual));
  }
}
