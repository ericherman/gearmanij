/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public class JobImpl implements Job {

  public JobImpl(byte[] handle, String functionName, byte[] id, byte[] data) {
    this.data = data;
    this.handle = handle;
    this.id = id;
    this.functionName = functionName;
  }
  
  // The handle is opaque to the worker, so the null termination byte is retained
  private byte[] handle;
  
  private byte[] id;
  
  private String functionName;
  
  private byte[] data;
  
  private byte[] result;

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

}
