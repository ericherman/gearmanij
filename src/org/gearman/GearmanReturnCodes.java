/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package org.gearman;

/*
 * transcribed from libgearman/constants.h (BSD) 
 * Copyright (C) 2008 Brian Aker, Eric Day
 */
public enum GearmanReturnCodes {
    GEARMAN_SUCCESS, //
    GEARMAN_IO_WAIT, //
    GEARMAN_SHUTDOWN, //
    GEARMAN_SHUTDOWN_GRACEFUL, //
    GEARMAN_ERRNO, //
    GEARMAN_EVENT, //
    GEARMAN_TOO_MANY_ARGS, //
    GEARMAN_NO_ACTIVE_FDS, //
    GEARMAN_INVALID_MAGIC, //
    GEARMAN_INVALID_COMMAND, //
    GEARMAN_INVALID_PACKET, //
    GEARMAN_UNEXPECTED_PACKET, //
    GEARMAN_GETADDRINFO, //
    GEARMAN_NO_SERVERS, //
    GEARMAN_LOST_CONNECTION, //
    GEARMAN_MEMORY_ALLOCATION_FAILURE, //
    GEARMAN_JOB_EXISTS, //
    GEARMAN_JOB_QUEUE_FULL, //
    GEARMAN_SERVER_ERROR, //
    GEARMAN_WORK_ERROR, //
    GEARMAN_WORK_DATA, //
    GEARMAN_WORK_WARNING, //
    GEARMAN_WORK_STATUS, //
    GEARMAN_WORK_EXCEPTION, //
    GEARMAN_WORK_FAIL, //
    GEARMAN_NOT_CONNECTED, //
    GEARMAN_COULD_NOT_CONNECT, //
    GEARMAN_SEND_IN_PROGRESS, //
    GEARMAN_RECV_IN_PROGRESS, //
    GEARMAN_NOT_FLUSHING, //
    GEARMAN_DATA_TOO_LARGE, //
    GEARMAN_INVALID_FUNCTION_NAME, //
    GEARMAN_INVALID_WORKER_FUNCTION, //
    GEARMAN_NO_REGISTERED_FUNCTIONS, //
    GEARMAN_NO_JOBS, //
    GEARMAN_ECHO_DATA_CORRUPTION, //
    GEARMAN_NEED_WORKLOAD_FN, //
    GEARMAN_PAUSE, //
    GEARMAN_UNKNOWN_STATE, //
    GEARMAN_PTHREAD, //
    GEARMAN_PIPE_EOF, //
    GEARMAN_MAX_RETURN
    /* Always add new error code before GEARMAN_MAX_RETURN */
}
