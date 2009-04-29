package gearmanij;

public interface Task {
  enum TaskOption {
    ALLOCATED, SEND_IN_USE
  }

  enum TaskState {
    NEW, SUBMIT, WORKLOAD, WORK, CREATED, DATA, WARNING, STATUS, COMPLETE, EXCEPTION, FAIL, FINISHED
  }
}
