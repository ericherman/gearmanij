package org.gearman.worker;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.gearman.Job;
import org.gearman.JobFunction;
import org.gearman.Job.JobState;
import org.gearman.util.ByteUtils;

public class JavaFunction implements JobFunction {
    private final boolean sandbox;

    public JavaFunction(boolean sandbox) {
        this.sandbox = sandbox;
    }

    @SuppressWarnings("unchecked")
    public void execute(Job job) {
        try {
            byte[] resultBytes = ByteUtils.EMPTY;
            Serializable obj = ByteUtils.toObject(job.getData(), sandbox);
            if (obj instanceof Callable<?>) {
                Serializable result = ((Callable<Serializable>) obj).call();
                resultBytes = ByteUtils.toByteArray(result);
            } else if (obj instanceof Runnable) {
                ((Runnable) obj).run();
            } else {
                throw new IllegalArgumentException("" + obj);
            }
            job.setResult(resultBytes);
            job.setState(JobState.COMPLETE);
        } catch (Exception e) {
            e.printStackTrace();
            job.setState(JobState.EXCEPTION);
            job.setResult(ByteUtils.toUTF8Bytes(e.toString()));
        }
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String toString() {
        String not = (sandbox) ? "" : "not ";
        return getName() + " (" + not + "sanboxed)";
    }

}
