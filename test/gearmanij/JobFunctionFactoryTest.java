/*
 * Copyright (C) 2009 by Robert Stewart <robert@wombatnation.com>
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

import static org.junit.Assert.assertTrue;
import gearmanij.example.ReverseFunction;
import gearmanij.util.TestUtil;

import org.junit.Before;
import org.junit.Test;

public class JobFunctionFactoryTest {
  private Worker worker;
  private Connection conn;
  private ConnectionAdminClient connAdmin;

  /**
   * Returns the same ReverseFunction each time.
   */
  public class SingletonReverseFunctionFactory implements JobFunctionFactory {
    private JobFunction function = new ReverseFunction();

    public String getFunctionName() {
      return "reverse";
    }

    public JobFunction getJobFunction() {
      return function;
    }
  }

  @Before
  public void setUp() {
    worker = new StandardWorker();
  }

  @Test
  public void testRegisterFactory() {

    newSocketConnection();
    String id = "RegisterFactory";
    JobFunctionFactory factory = new SingletonReverseFunctionFactory();
    String name = factory.getFunctionName();

    worker.setWorkerID(id);
    worker.registerFunctionFactory(factory);
    assertTrue(TestUtil.isFunctionRegisteredForWorker(connAdmin, id, name));

  }

  private void newSocketConnection() {
    conn = new SocketConnection();
    connAdmin = new ConnectionAdminClient(conn);
    worker.addServer(conn);
  }
}
