/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public interface Task {
  enum TaskOption {
    ALLOCATED, SEND_IN_USE
  }

  enum TaskState {
    NEW, SUBMIT, WORKLOAD, WORK, CREATED, DATA, WARNING, STATUS, COMPLETE, EXCEPTION, FAIL, FINISHED
  }
}
