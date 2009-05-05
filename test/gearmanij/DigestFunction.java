package gearmanij;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// More interesting if first element in data contained algorithm name followed
// by the data to digest.
public class DigestFunction implements JobFunction {
  private String algorithm = "MD5";

  public byte[] execute(byte[] data) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {

      String msg = "Unsupported digest algorithm " + algorithm;
      throw new RuntimeException(msg);
    }
    byte[] digest = md.digest(data);
    return digest;
  }

  public String getName() {
    return "digest";
  }

}
