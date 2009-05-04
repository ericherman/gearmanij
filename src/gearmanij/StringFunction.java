/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
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
		String textIn = ByteUtils.fromBytes(data, encoding);
		String textOut = execute(textIn);
		return ByteUtils.toBytes(textOut, encoding);
	}

	public abstract String execute(String textIn);

}
