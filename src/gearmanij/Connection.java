/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.IORuntimeException;

import java.util.List;
import java.util.Map;

public interface Connection {

	void open();

	void close();

	void write(Packet request);

	/**
	 * Reads from socket and constructs a Packet.
	 * 
	 * @return
	 * @throws IORuntimeException
	 */
	Packet readPacket();

	Map<String, List<String>> textMode(List<String> commands);

}