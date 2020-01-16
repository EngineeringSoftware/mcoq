package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.util.ExecUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOParAsync extends MutatorOneFileVOSkip {

    private List<String> compserOptions;

    public MutatorOneFileVOParAsync(File project, List<String> flags, boolean applyAll, long timeout, int numOfThreads) throws IOException {
        super(project, flags, applyAll, timeout);
        this.compserOptions = Arrays.asList("--async=coqtop", "--async-workers=" + numOfThreads);
        if (MCoq.debug) {
            safePrintln("DEBUG: Num of threads " + numOfThreads);
        }
    }

    @Override
    protected int checkMutatedFile(String vfile, String sexpfile, List<String> options) {
        List<String> localOptions = new ArrayList<>(compserOptions);
        localOptions.addAll(options);
        return ExecUtils.runCompserAndCheck(project, sexpfile, localOptions);
    }
}
