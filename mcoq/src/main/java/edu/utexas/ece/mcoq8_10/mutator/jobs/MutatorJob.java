package edu.utexas.ece.mcoq8_10.mutator.jobs;

import edu.utexas.ece.mcoq8_10.mutation.Mutation;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 */
public class MutatorJob {
    private Mutation mutation;
    private String vfile;
    private int index;

    private boolean isLastJob;

    public MutatorJob(Mutation mutation, String vfile, int index, boolean isLastJob) {
        this.mutation = mutation;
        this.vfile = vfile;
        this.index = index;
        this.isLastJob = isLastJob;
    }

    public MutatorJob(Mutation mutation, String vfile, int index) {
        this(mutation, vfile, index, false);
    }

    public MutatorJob(boolean isLastJob) {
        this(null, null, -1, isLastJob);
    }

    public Mutation getMutation() {
        return mutation;
    }

    public int getIndex() {
        return index;
    }

    public boolean isLastJob() {
        return isLastJob;
    }

    public String getVfile() {
        return vfile;
    }
}
