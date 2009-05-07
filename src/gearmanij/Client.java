/*
 * Copyright (C) 2009 by Eric Herman <eric@freesa.org>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import java.io.PrintStream;

public interface Client {

  byte[] execute(String function, String uniqueId, byte[] data);

  void shutdown();

  void setErr(PrintStream err);

  void printErr(String msg);

}
