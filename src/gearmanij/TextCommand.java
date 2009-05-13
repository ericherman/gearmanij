/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

/**
 * Text mode commands that can be sent to a job server.
 */
public enum TextCommand {
  WORKERS("workers"),
  STATUS("status"),
  MAXQUEUE("maxqueue"),
  SHUTDOWN("shutdown"),
  VERSION("version");
  
  private String command;

  private TextCommand(String command) {
    this.command = command;
  }
  
  public String toString() {
    return command;
  }
  
}
