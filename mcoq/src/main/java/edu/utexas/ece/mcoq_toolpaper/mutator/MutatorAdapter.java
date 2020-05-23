package edu.utexas.ece.mcoq_toolpaper.mutator;

import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;
/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class MutatorAdapter extends Mutator {

    protected int numberOfAppliedMutations;
    protected int numberOfKilledMutations;
    protected int numberOfTimeOuts;
    protected int numberOfEquivalentMutations;

    public MutatorAdapter(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
    }

    public MutatorAdapter(File project, Mutator other) {
        super(project, other);
    }

    @Override
    public void runMutation(Mutation mutation) throws IOException, SexpParserException {
        Set<String> sortedFiles = filenameToListOfSexp.keySet();
        Set<String> visitedVFiles = new HashSet<>();

        numberOfAppliedMutations = 0;
        numberOfKilledMutations = 0;
        numberOfTimeOuts = 0;
        numberOfEquivalentMutations = 0;

        long startTimeMillis = System.currentTimeMillis();

        debug("DEBUG: Starting Mutation " + mutation.getClass().getSimpleName());

        // go through all the files and add one file before calling compser
        runMutationOnAllFiles(mutation, makeCompserOptions(), sortedFiles, visitedVFiles);

        long endTimeMillis = System.currentTimeMillis();
        long elapsed = endTimeMillis - startTimeMillis;
        safePrintln("Mutation: " + mutation.toString(),
                    "Number of files per project: " + visitedVFiles.size(),
                    "Number of mutants: " + numberOfAppliedMutations,
                    "Number of timeouts: " + numberOfTimeOuts,
                    "Number of killed mutants: " + numberOfKilledMutations,
                    "Number of equivalents: " + numberOfEquivalentMutations,
                    "Time elapsed (in ms): " + elapsed);
        numberTotal += numberOfAppliedMutations;
        killedTotal += numberOfKilledMutations;
        timeoutTotal += numberOfTimeOuts;
        equivalentTotal += numberOfEquivalentMutations;
        timeTotal += elapsed;
    }

    public void clean() {

    }

    protected List<String> makeCompserOptions() {
        List<String> compserOptions = new LinkedList<>(flags);
        compserOptions.add(0, "--mode=vo");
        return compserOptions;
    }


    protected List<String> makeSerloadOptions() {
        List<String> compserOptions = new LinkedList<>(flags);
        return compserOptions;
    }

    protected List<String> makeCoqcOptions() {
        List<String> coqcOptions = new LinkedList<>();
        String lastFlag = "";
        for (String flag : flags) {
            if ("-Q".equals(flag) || "-R".equals(flag)) {
                coqcOptions.add(flag);
            } else {
                String[] dirMapping = flag.split(",");
                if (dirMapping.length != 2) {
                    throw new RuntimeException("ERROR: An illegal flag " + flag + " is passed!");
                }
                coqcOptions.addAll(Arrays.asList(dirMapping));
            }
        }
        return coqcOptions;
    }

    protected void debug(String msg) {
        if (MCoq.debug) {
            safePrintln(msg);
        }
    }

    protected abstract void revertVoFile(String vfile, List<String> coqcOptions);

    protected abstract void revertOtherVFiles(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions);

    protected abstract Collection<String> getOtherVFilesToCheck(String vfile, Set<String> topOrderedVFiles, Set<String> visitedVFiles);

    protected abstract void runMutationOnAllFiles(Mutation mutation, List<String> compserOptions, Set<String> sortedFiles, Set<String> visitedVFiles) throws IOException, SexpParserException;
}
