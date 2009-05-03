/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteArrayBuffer;
import gearmanij.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestGearmanClient {

	public static void main(String[] args) throws Exception {

		// worker connect to server
		Socket workerSocket = new Socket(Constants.GEARMAN_DEFAULT_TCP_HOST,
				Constants.GEARMAN_DEFAULT_TCP_PORT);
		println("Socket: " + workerSocket);
		final OutputStream workerOut = workerSocket.getOutputStream();
		final InputStream workerIn = workerSocket.getInputStream();

		Thread workerThread = startThread("Worker", new Runnable() {
			public void run() {
				try {
					workerStuff(workerOut, workerIn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// customer connect to server
		Socket customerSocket = new Socket(Constants.GEARMAN_DEFAULT_TCP_HOST,
				Constants.GEARMAN_DEFAULT_TCP_PORT);
		println("Socket: " + customerSocket);
		final OutputStream customerOut = customerSocket.getOutputStream();
		final InputStream customerIn = customerSocket.getInputStream();

		Thread customerThread = startThread("Customer", new Runnable() {
			public void run() {
				try {
					customerStuff(customerIn, customerOut);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		customerThread.join(1000);
		workerThread.join(1000);
	}

	public static void println(String msg) {
		System.err.println(Thread.currentThread().getName() + ": " + msg);
	}

	private static Thread startThread(String threadName, Runnable target)
			throws InterruptedException {
		Thread t = new Thread(target, threadName);
		println("Starting " + threadName);
		t.start();
		Thread.sleep(100);
		return t;
	}

	private static void workerStuff(OutputStream out, InputStream in)
			throws Exception {
		writePacket("canDo reverse", out, canDo("reverse"));

		int loopLimit = 20;
		for (int i = 0; i < loopLimit; i++) {
			writePacket("grabJob", out, grabJob());

			Packet fromServer = readPacket(in);
			println("recived: " + fromServer);

			PacketType packetType = fromServer.getPacketType();
			if (packetType == PacketType.NO_JOB) {
				Packet preSleep = preSleep();
				// writePacket("preSleep", out, preSleep);
				Thread.sleep(1000);
			} else if (packetType == PacketType.JOB_ASSIGN) {
				println("YIKES!");
			} else {
				println("EEK!");
			}
		}
	}

	private static void writePacket(String name, OutputStream out,
			Packet preSleep) throws IOException, InterruptedException {
		println("Writing " + name + " packet ...");
		// write and read
		out.write(preSleep.toBytes());
		out.flush();
		println(name + " written.");
		Thread.sleep(100);
	}

	private static Packet preSleep() {
		return new Packet(PacketMagic.REQ, PacketType.PRE_SLEEP, new byte[0]);
	}

	private static void customerStuff(InputStream in, OutputStream out)
			throws Exception {
		writePacket("reverse 'Hello'", out, submitReverseJob("Hello"));

		byte[] jobhandle = new byte[0];

		int loopLimit = 20;
		for (int i = 0; i < loopLimit; i++) {
			Packet fromServer = readPacket(in);
			println("recived: " + fromServer);

			PacketType packetType = fromServer.getPacketType();
			if (packetType == PacketType.JOB_CREATED) {
				jobhandle = fromServer.toBytes();
				println(ByteUtils.fromAsciiBytes(jobhandle));
			} else if (packetType == PacketType.WORK_COMPLETE) {
				ByteArrayBuffer data = new ByteArrayBuffer(fromServer.getData());
				int handleLen = data.indexOf((byte) 0);
				byte[] jobHandle2 = data.subArray(0, handleLen);
				println("expected: " + ByteUtils.fromAsciiBytes(jobhandle));
				println("got:" + ByteUtils.fromAsciiBytes(jobHandle2));
				byte[] respBytes = data.subArray(handleLen, data.length());
				String response = ByteUtils.fromAsciiBytes(respBytes);
				println("RESULT:" + response);
			} else {
				println("EEK!");
				break;
			}
		}
	}

	private static Packet readPacket(InputStream in) throws IOException {
		final int EOF = -1;

		byte[] bytes = new byte[12];
		int c = in.read(bytes);
		if (c >= 0 && c != bytes.length) {
			throw new RuntimeException(c + " != " + bytes.length);
		} else if (c == EOF) {
			println("EOF data");
		}

		PacketHeader header = new PacketHeader(bytes);
		byte[] data = new byte[header.getDataLength()];
		if (data.length > 0) {
			c = in.read(data);
			if (c == EOF) {
				println("EOF data");
			}
		}
		Packet p = new Packet(header.getMagic(), header.getType(), data);
		return p;
	}

	private static Packet submitReverseJob(String str) {
		ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(ByteUtils.toAsciiBytes("reverse")); // Function
		buf.append((byte) 0); // Null Terminated
		buf.append((byte) 0); // Unique ID
		buf.append(ByteUtils.toAsciiBytes(str));// Workload
		byte[] data = buf.getBytes();
		return new Packet(PacketMagic.REQ, PacketType.SUBMIT_JOB, data);
	}

	private static Packet grabJob() {
		return new Packet(PacketMagic.REQ, PacketType.GRAB_JOB, new byte[0]);
	}

	public static Packet canDo(String function) {
		ByteArrayBuffer buf = new ByteArrayBuffer();
		buf.append(ByteUtils.toAsciiBytes(function));
		buf.append((byte) 0);
		byte[] data = buf.getBytes();
		return new Packet(PacketMagic.REQ, PacketType.CAN_DO, data);
	}

}
