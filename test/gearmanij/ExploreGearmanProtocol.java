/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static gearmanij.util.ByteUtils.NULL;
import static gearmanij.util.IOUtil.flush;
import static gearmanij.util.IOUtil.getInputStream;
import static gearmanij.util.IOUtil.getOutputStream;
import static gearmanij.util.IOUtil.newSocket;
import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class ExploreGearmanProtocol {

	static int customerLoopLimit = 3;
	static int workerLoopLimit = customerLoopLimit + 3;

	public static void main(String[] args) throws Exception {

		Thread workerThread = startThread("Worker", new Runnable() {
			public void run() {
				workerStuff();
			}
		});

		Thread customerThread = startThread("Customer", new Runnable() {
			public void run() {
				customerStuff();
			}
		});

		customerThread.join(1000);
		workerThread.join(1000);
	}

	public static void println(String msg) {
		System.err.println(Thread.currentThread().getName() + ": " + msg);
	}

	private static Thread startThread(String threadName, Runnable target) {
		Thread t = new Thread(target, threadName);
		println("Starting " + threadName);
		t.start();
		sleep(100);
		return t;
	}

	private static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void workerStuff() {
		// worker connect to server
		SocketConnection conn = new SocketConnection();
		conn.setLog(System.err);

		Worker reverse = new SimpleWorker();
		reverse.addServer(conn);
		reverse.registerFunction(new ReverseFunction());

		for (int i = 0; i < workerLoopLimit; i++) {
			println("Loop Number: " + i);
			Map<Connection, PacketType> jobs = reverse.grabJob();
			PacketType packetType = jobs.get(conn);

			if (packetType == PacketType.NO_JOB) {
				sleep(1000);
			} else if (packetType == PacketType.JOB_ASSIGN) {
				println("YAY!");
			} else if (packetType == PacketType.NOOP) {
				println("noop");
			} else {
				println("YIKES!");
			}
		}
		println("FINISHED");
	}

	private static void writePacket(String name, OutputStream out, Packet packet) {
		println("Writing " + name + " packet ...");
		// write and read
		packet.write(out);
		flush(out);
		println(name + " written.");
	}

	private static void customerStuff() {
		// customer connect to server
		Socket socket = newSocket(Constants.GEARMAN_DEFAULT_TCP_HOST,
				Constants.GEARMAN_DEFAULT_TCP_PORT);

		println("Socket: " + socket);
		final OutputStream out = getOutputStream(socket);
		final InputStream in = getInputStream(socket);

		writePacket("reverse 'Hello'", out, submitReverseJob("Hello"));

		byte[] jobhandle = new byte[0];

		for (int i = 0; i < customerLoopLimit; i++) {
			println("Loop Number: " + i);
			Packet fromServer = new Packet(in);
			println("recived: " + fromServer);

			PacketType packetType = fromServer.getPacketType();
			if (packetType == PacketType.JOB_CREATED) {
				jobhandle = fromServer.toBytes();
				println(ByteUtils.fromAsciiBytes(jobhandle));
			} else if (packetType == PacketType.WORK_COMPLETE) {
				ByteArrayBuffer data = new ByteArrayBuffer(fromServer.getData());
				int handleLen = data.indexOf(NULL);
				byte[] jobHandle2 = data.subArray(0, handleLen);
				println("expected: " + ByteUtils.fromAsciiBytes(jobhandle));
				println("got:" + ByteUtils.fromAsciiBytes(jobHandle2));
				byte[] respBytes = data.subArray(handleLen, data.length());
				String response = ByteUtils.fromAsciiBytes(respBytes);
				println("RESULT:" + response);
				break;
			} else {
				println("EEK!");
				break;
			}
		}
		println("FINISHED");
	}

	private static Packet submitReverseJob(String str) {
		ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(ByteUtils.toAsciiBytes("reverse")); // Function
		buf.append(NULL); // Null Terminated
		buf.append(NULL); // Unique ID
		buf.append(ByteUtils.toAsciiBytes(str));// Workload
		byte[] data = buf.getBytes();
		return new Packet(PacketMagic.REQ, PacketType.SUBMIT_JOB, data);
	}

}
