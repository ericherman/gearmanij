/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman.worker;

import org.gearman.Job;
import org.gearman.util.ByteArrayBuffer;
import org.gearman.util.ByteUtils;

public class WorkerJob implements Job {
  
  /**
   * Represents the per cent completion of a job.
   */
  private class JobProgressImpl implements JobProgress {
    private int numerator = 0;
    private int denominator = 100;
    
    public int getNumerator() {
      return numerator;
    }
    public void setNumerator(int numerator) {
      this.numerator = numerator;
    }
    public int getDenominator() {
      return denominator;
    }
    public void setDenominator(int denominator) {
      this.denominator = denominator;
    }
  }
  
  /**
   * this is currently geared towards PacketType.JOB_ASSIGN
   * 
   * we may wish to do something different for PacketType.JOB_ASSIGN_UNIQ
   * 
   * @param responseData
   *          a byte[] from a PacketgetData.getData()
   */
  public WorkerJob(byte[] responseData) {
    // Parse null terminated params - job handle, function name, function arg
    ByteArrayBuffer baBuff = new ByteArrayBuffer(responseData);
    int start = 0;
    int end = baBuff.indexOf(ByteUtils.NULL);
    // Treat handle as opaque, so keep null terminator
    byte[] handle = baBuff.subArray(start, end + 1);
    start = end + 1;
    end = baBuff.indexOf(ByteUtils.NULL, start);
    byte[] name = baBuff.subArray(start, end);
    start = end + 1;
    byte[] data = baBuff.subArray(start, responseData.length);

    this.data = data;
    this.handle = handle;
    this.id = null;
    this.functionName = new String(name);
    this.state = JobState.NEW;
  }

  public WorkerJob(byte[] handle, String functionName, byte[] id, byte[] data) {
    this.data = data;
    this.handle = handle;
    this.id = id;
    this.functionName = functionName;
    this.state = JobState.NEW;
  }

  // The handle is opaque to the worker, so the null termination byte is
  // retained
  private byte[] handle;

  private byte[] id;

  private String functionName;

  private byte[] data;

  private byte[] result;
  
  private Job.JobState state;
  
  private Job.JobProgress progress = new JobProgressImpl();

  public byte[] getData() {
    return data;
  }

  public byte[] getHandle() {
    return handle;
  }

  public byte[] getID() {
    return id;
  }

  public String getFunctionName() {
    return functionName;
  }

  public byte[] getResult() {
    return result;
  }

  public void setResult(byte[] result) {
    this.result = result;
  }

  /**
   * @return the current state of a Job
   */
  public Job.JobState getState() {
    return state;
  }

  /**
   * Sets the current state of a job.
   * @param state
   *          the new JobState
   */
  public void setState(Job.JobState state) {
    this.state = state;
  }

  public Job.JobProgress getProgress() {
    return progress;
  }
}
