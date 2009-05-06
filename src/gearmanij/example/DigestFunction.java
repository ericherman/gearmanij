/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import gearmanij.JobFunction;

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
