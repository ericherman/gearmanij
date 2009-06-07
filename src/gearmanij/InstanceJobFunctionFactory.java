package gearmanij;

public class InstanceJobFunctionFactory implements JobFunctionFactory {
  private final JobFunction jobFunction;

  public InstanceJobFunctionFactory(JobFunction jobFunction) {
    if (jobFunction == null) {
      throw new IllegalArgumentException();
    }
    this.jobFunction = jobFunction;
  }

  public String getFunctionName() {
    return jobFunction.getName();
  }

  public JobFunction getJobFunction() {
    return jobFunction;
  }

}
