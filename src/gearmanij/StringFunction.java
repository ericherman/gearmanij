/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;

public abstract class StringFunction implements JobFunction {

  private final String encoding;

  public StringFunction() {
    this(ByteUtils.CHARSET_ASCII);
  }

  public StringFunction(String encoding) {
    this.encoding = encoding;
  }

  public byte[] execute(byte[] data) {
    String textIn = fromBytes(data);
    // TestUtil.dump("textIn", textIn);
    String textOut = execute(textIn);
    // TestUtil.dump("textout", textIn);
    return ByteUtils.toBytes(textOut, encoding);
  }

  private String fromBytes(byte[] data) {
    // if (data != null && data.length > 0 && data[data.length - 1] == 0) {
    // TestUtil.dump("null terminated was: ", data);
    // System.err.println("null terminated");
    // data = new ByteArrayBuffer(data).subArray(0, data.length - 2);
    // TestUtil.dump("null terminated now: ", data);
    // }
    return ByteUtils.fromBytes(data, encoding);
  }

  public abstract String execute(String textIn);

}
