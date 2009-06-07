package gearmanij;

import gearmanij.util.Exceptions;

public class ClassJobFunctionFactory implements JobFunctionFactory {
  private Class<? extends JobFunction> functionClass;

  public ClassJobFunctionFactory(Class<? extends JobFunction> functionClass) {
    if (functionClass == null) {
      throw new IllegalArgumentException();
    }
    this.functionClass = functionClass;
  }

  public String getFunctionName() {
    return getJobFunction().getName();
  }

  public JobFunction getJobFunction() {
    try {
      return functionClass.newInstance();
    } catch (Exception e) {
      throw Exceptions.toRuntime(e);
    }
  }

}
