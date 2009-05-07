package gearmanij;

import java.io.PrintStream;

public interface Client {

  byte[] execute(String function, String uniqueId, byte[] data);

  void shutdown();

  void setErr(PrintStream err);

  void printErr(String msg);

}