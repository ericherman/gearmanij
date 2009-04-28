/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TestGearmanClient {

	public static void main(String[] args) throws Exception {

		String function = "foo";
		ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(ByteUtils.getAsciiBytes(function));
		buf.append((byte) 0);
		byte[] data = buf.getBytes();

		// create a packet
		Packet packet = new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);

/*
		// connect to server
		Socket s = new Socket(Constants.GEARMAN_DEFAULT_TCP_HOST,
				Constants.GEARMAN_DEFAULT_TCP_PORT);

		OutputStream out = s.getOutputStream();
		InputStream in = s.getInputStream();
		InputStreamReader readIn = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(readIn);
		
		// write and read
		out.write(packet.toBytes());
		out.flush();
		while (true) {
			System.err.print(reader.readLine());
		}
*/
	}

}
