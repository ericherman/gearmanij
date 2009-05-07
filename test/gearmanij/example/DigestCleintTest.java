package gearmanij.example;

import static org.junit.Assert.assertEquals;

import gearmanij.Client;
import gearmanij.util.ByteUtils;

import java.io.PrintStream;

import org.junit.Test;

public class DigestCleintTest {

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
