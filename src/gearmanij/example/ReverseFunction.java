/*
 * Copyright (C) 2009 by Robert Stewart
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij.example;

import gearmanij.Job;
import gearmanij.Job.JobState;
import gearmanij.JobFunction;
import gearmanij.util.ByteUtils;

public class ReverseFunction implements JobFunction {

  // TODO: Find out if the C reverse client specifies what encoding is used
  private String encoding = ByteUtils.CHARSET_ASCII;


  public void execute(Job job) {
    // Get the input as a String
    String data = ByteUtils.fromBytes(job.getData(), encoding);
    // Perform the reversal
    StringBuffer sb = new StringBuffer(data);
    sb = sb.reverse();
    // Store result as bytes
    job.setResult(ByteUtils.toBytes(sb.toString(), encoding));
    // Set the job state
    job.setState(JobState.COMPLETE);
  }

  public String getName() {
    return "reverse";
  }

}
