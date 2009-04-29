package gearmanij;

public interface Job extends Task {
  enum JobOption {
    ALLOCATED, ASSIGNED_IN_USE, WORK_IN_USE
  }
  
  enum JobPriority {
    HIGH, NORMAL, LOW
  }
}
