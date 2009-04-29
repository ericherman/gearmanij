package gearmanij;

import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract Worker class that usually should be extended by all Worker implementations.
 */
public abstract class AbstractWorker implements Worker {

  private EnumSet<WorkerOption> options = EnumSet.noneOf(WorkerOption.class);
  private List<Connection> connections = new LinkedList();

  /**
   * Brought over from C implementation. May not be necessary.
   * 
   * @see gearmanij.Worker#getError()
   */
  public String getError() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void clearWorkerOptions() {
    options = EnumSet.noneOf(WorkerOption.class);
  }
  
  public EnumSet<WorkerOption> getWorkerOptions() {
    return options;
  }
  
  public void removeWorkerOptions(WorkerOption... workerOptions) {
    for (WorkerOption option : workerOptions) {
      options.remove(option);
    }
  }
  
  public void setWorkerOptions(WorkerOption... workerOptions) {
    for (WorkerOption option : workerOptions) {
      options.add(option);
    }
  }
  
  public Connection addServer() {
    return addServer(Constants.GEARMAN_DEFAULT_TCP_HOST);
  }

  public Connection addServer(String host) {
    return addServer(host, Constants.GEARMAN_DEFAULT_TCP_PORT);
  }
  
  public Connection addServer(String host, int port) {
    Connection conn = new Connection(host, port);
    if (conn != null) {
      connections.add(conn);
    }
    return conn;
  }

}
