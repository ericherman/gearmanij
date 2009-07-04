/*
 * Copyright (C) 2009 by Robert Stewart
 * Use and distribution licensed under the 
 * GNU Lesser General Public License (LGPL) version 2.1.
 * See the COPYING file in the parent directory for full text.
 */
package gearmanij;

/**
 * A Job is the unit of work for Gearman. Clients submit jobs to the job
 * server and the job server distributes jobs to workers.
 */
public interface Job {
  enum JobState {
    NEW, COMPLETE, FAIL, EXCEPTION, PARTIAL_DATA, STATUS, WARNING
  }
  
  // Copied in from C implementation. May not be needed.
//  enum JobOption {
//    ASSIGNED_IN_USE, WORK_IN_USE
//  }

  enum JobPriority {
    HIGH, NORMAL, LOW
  }
  
  /**
   * Represents the amount of progress towards completion of a job.
   */
  interface JobProgress {
    int getNumerator();
    void setNumerator(int numerator);
    int getDenominator();
    void setDenominator(int denominator);
  }

  /**
   * Returns the data the client has sent to the worker to be processed.
   * 
   * @return data the client has sent to the worker to be processed
   */
  byte[] getData();

  /**
   * Returns the name of the function the worker should perform on the data.
   * 
   * @return name of the function the worker should perform on the data
   */
  String getFunctionName();

  /**
   * Returns the handle assigned to the job by the job server.
   * 
   * @return handle assigned to the job by the job server
   */
  byte[] getHandle();

  /**
   * Returns the ID assigned to the job by the client. Not all Jobs have client-
   * assigned IDs.
   * 
   * @return ID assigned to the job by the client
   */
  byte[] getID();

  /**
   * Gets the result that will be sent back to the client.
   * 
   * return result that will be sent back to the client
   */
  byte[] getResult();

  /**
   * Sets the result to be sent back to the client.
   * 
   * @param result
   *          data that will be sent back to the client
   */
  void setResult(byte[] result);
  
  
  /**
   * Returns the current state of a Job.
   * 
   * @return the current state of a Job
   */
  Job.JobState getState();

  /**
   * Sets the current state of a job.
   * @param state
   *          the new JobState
   */
  void setState(Job.JobState state);
  
  /**
   * Returns the current progress of a Job.
   * 
   * @return the current progress of a Job
   */
  Job.JobProgress getProgress();
}
