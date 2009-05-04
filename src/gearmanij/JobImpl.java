/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

public class JobImpl implements Job {

  /**
   * this is currently geared towards PacketType.JOB_ASSIGN
   * 
   * we may wish to do something different for PacketType.JOB_ASSIGN_UNIQ
   * 
   * @param response.getData a byte[] from a PacketgetData() 
   */
  public JobImpl(byte[] responseData) {
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
  }

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
