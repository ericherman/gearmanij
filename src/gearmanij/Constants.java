/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

/*
 * transcribed from libgearman/constants.h (BSD) 
 * Copyright (C) 2008 Brian Aker, Eric Day
 */
public interface Constants {
	/* Defines. */
	public static final String GEARMAN_DEFAULT_TCP_HOST = "127.0.0.1";
	public static final int GEARMAN_DEFAULT_TCP_PORT = 4730;
	public static final int GEARMAN_DEFAULT_SOCKET_TIMEOUT = 10;
	public static final int GEARMAN_DEFAULT_SOCKET_SEND_SIZE = 32768;
	public static final int GEARMAN_DEFAULT_SOCKET_RECV_SIZE = 32768;
	public static final int GEARMAN_DEFAULT_BACKLOG = 64;
	public static final int GEARMAN_DEFAULT_MAX_QUEUE_SIZE = 0;

	public static final int GEARMAN_MAX_ERROR_SIZE = 1024;
	public static final int GEARMAN_PACKET_HEADER_SIZE = 12;
	public static final int GEARMAN_JOB_HANDLE_SIZE = 64;
	public static final int GEARMAN_OPTION_SIZE = 64;
	public static final int GEARMAN_UNIQUE_SIZE = 64;
	public static final int GEARMAN_MAX_COMMAND_ARGS = 8;
	public static final int GEARMAN_ARGS_BUFFER_SIZE = 128;
	public static final int GEARMAN_SEND_BUFFER_SIZE = 8192;
	public static final int GEARMAN_RECV_BUFFER_SIZE = 8192;
	public static final int GEARMAN_SERVER_CON_ID_SIZE = 128;
	public static final int GEARMAN_JOB_HASH_SIZE = 383;
	public static final int GEARMAN_MAX_FREE_SERVER_CON = 1000;
	public static final int GEARMAN_MAX_FREE_SERVER_PACKET = 2000;
	public static final int GEARMAN_MAX_FREE_SERVER_JOB = 1000;
	public static final int GEARMAN_MAX_FREE_SERVER_CLIENT = 1000;
	public static final int GEARMAN_MAX_FREE_SERVER_WORKER = 1000;
	public static final int GEARMAN_TEXT_RESPONSE_SIZE = 8192;
	public static final int GEARMAN_WORKER_WAIT_TIMEOUT = (10 * 1000); /* Milliseconds */
	public static final int GEARMAN_PIPE_BUFFER_SIZE = 256;

	enum Option {
		READY, PACKET_IN_USE, EXTERNAL_FD
	}

	enum State {
		ADDRINFO, CONNECT, CONNECTING, CONNECTED
	}

	enum SendState {
		NONE, PRE_FLUSH, FORCE_FLUSH, FLUSH, FLUSH_DATA
	}

	enum ReceiveState {
		NONE, READ, READ_DATA
	}

}
