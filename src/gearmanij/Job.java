/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

public interface Job extends Task {
  enum JobOption {
    ALLOCATED, ASSIGNED_IN_USE, WORK_IN_USE
  }
  
  enum JobPriority {
    HIGH, NORMAL, LOW
  }
}
