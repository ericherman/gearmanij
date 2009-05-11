/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;

/**
 * Abstract convenience class for job functions that take a single String as
 * input and return a single String as output.
 * <p>
 * The encoding that will be used for the input and output can be specified in a
 * constructor. The default encoding is ASCII.
 */
public abstract class StringFunction implements JobFunction {

  private final String encoding;

  /**
   * StringFunction that uses the default encoding of ASCII for the input and
   * output.
   */
  public StringFunction() {
    this(ByteUtils.CHARSET_ASCII);
  }

  /**
   * StringFunction that uses the specified encoding for the input and output.
   * 
   * @param encoding
   *          The name of a java.io character encoding, such as ASCII, ISO8859_1
   *          or UTF8
   */
  public StringFunction(String encoding) {
    this.encoding = encoding;
  }

  /**
   * Performs the job function by calling execute(String), which must be
   * implemented by all subclasses. The data is a blob of data from a client
   * task that execute(byte[]) must know how to interpret.
   * <p>
   * The return value is also a byte[] of data that the client must know how to
   * interpret.
   * 
   * @param data
   *          The input to the job function
   * @return the result that the job server will then return to the client
   */
  public byte[] execute(byte[] data) {
    String textIn = fromBytes(data);
    String textOut = execute(textIn);
    return ByteUtils.toBytes(textOut, encoding);
  }

  private String fromBytes(byte[] data) {
    return ByteUtils.fromBytes(data, encoding);
  }

  /**
   * Method implemented by subclasses 
   * @param textIn
   *          The input to the job function, converted to a String in the desired encoding 
   * @return the result as a String that the job server will then return to the client
   */
  public abstract String execute(String textIn);

}
