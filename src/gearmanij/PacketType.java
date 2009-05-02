/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import gearmanij.util.ByteUtils;

enum PacketType {
	NULL(0), //
	CAN_DO(1), //
	CANT_DO(2), //
	RESET_ABILITIES(3), //
	PRE_SLEEP(4), //
	unused_5(5), //
	NOOP(6), //
	SUBMIT_JOB(7), //
	JOB_CREATED(8), //
	GRAB_JOB(9), //
	NO_JOB(10), //
	JOB_ASSIGN(11), //
	WORK_STATUS(12), //
	WORK_COMPLETE(13), //
	WORK_FAIL(14), //
	GET_STATUS(15), //
	ECHO_REQ(16), //
	ECHO_RES(17), //
	SUBMIT_JOB_BG(18), //
	ERROR(19), //
	STATUS_RES(20), //
	SUBMIT_JOB_HIGH(21), //
	SET_CLIENT_ID(22), //
	CAN_DO_TIMEOUT(23), //
	ALL_YOURS(24), //
	WORK_EXCEPTION(25), //
	OPTION_REQ(26), //
	OPTION_RES(27), //
	WORK_DATA(28), //
	WORK_WARNING(29), //
	GRAB_JOB_UNIQ(30), //
	JOB_ASSIGN_UNIQ(31), //
	SUBMIT_JOB_HIGH_BG(32), //
	SUBMIT_JOB_LOW(33), //
	SUBMIT_JOB_LOW_BG(34), //
	SUBMIT_JOB_SCHED(35), //
	SUBMIT_JOB_EPOCH(36), //
	;

	/*
	 * A big-endian (network-job) integer containing an enumerated packet
	 * type.
	 */
	byte[] type;
	
	private final int code;

	private PacketType(int i) {
		type = ByteUtils.toBigEndian(i);
		code = i;
	}

	public byte[] toBytes() {
		return type;
	}
	
	/**
	 * Returns the PacketType for the specified <code>code</code>. If an invalid code
	 * is specified, the NULL PacketType is returned.
	 * 
	 * @param code
	 *          a PacketType code
	 * @return the PacketType for the specified <code>code</code>
	 */
	public static PacketType get(int code) {
	  for (PacketType packetType : PacketType.class.getEnumConstants()) {
	    if (code == packetType.code) {
	      return packetType;
	    }
	  }
	  // Invalid code, so return the NULL PacketType
	  return get(0);
	}
}
