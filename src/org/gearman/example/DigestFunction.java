/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.gearman.Job;
import org.gearman.JobFunction;
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;

/**
 * The data passed to DigestFunction should contain two parameters separated by
 * a null byte.
 * <ol>
 * <li>name of a digest algorithm implemented by
 * {@link java.security.MessageDigest}
 * <li>data to digest
 * </ol>
 */
public class DigestFunction implements JobFunction {

  public void execute(Job job) {
    job.setState(Job.JobState.FAIL);

    // First param is algorithm. Second is the data to digest.
    ByteArrayBuffer bab = new ByteArrayBuffer(job.getData());
    List<byte[]> params = bab.split(new byte[] { '\0' });
    if (params.size() != 2) {
      job.setState(Job.JobState.EXCEPTION);
      String msg = "Data to digest should be preceded by name of an algorithm";
      job.setResult(ByteUtils.toUTF8Bytes(msg));
      return;
    }
    String algorithm = ByteUtils.fromUTF8Bytes(params.get(0));
    byte[] data = params.get(1);

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      job.setState(Job.JobState.EXCEPTION);
      String msg = "Unsupported digest algorithm " + algorithm;
      job.setResult(ByteUtils.toUTF8Bytes(msg));
      return;
    }

    // Compute the digest using the specified algorithm
    byte[] digest = md.digest(data);
    // Store the digest on the job
    job.setResult(digest);
    // Set the job state to complete
    job.setState(Job.JobState.COMPLETE);
  }

  public String getName() {
    return "digest";
  }

}
