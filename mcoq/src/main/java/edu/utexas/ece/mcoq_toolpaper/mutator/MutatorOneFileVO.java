package edu.utexas.ece.mcoq_toolpaper.mutator;

import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;
import edu.utexas.ece.mcoq_toolpaper.util.ExecUtils;
import edu.utexas.ece.mcoq_toolpaper.util.FileUtils;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static edu.utexas.ece.mcoq_toolpaper.MCoq.TIMEOUT_CODE;
import static edu.utexas.ece.mcoq_toolpaper.util.ExecUtils.getKilledOrTimeoutInfo;
import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVO extends MutatorAdapter {

    public MutatorOneFileVO(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
    }

    public MutatorOneFileVO(File project, Mutator other) {
        super(project, other);
    }

    @Override
    protected Collection<String> getOtherVFilesToCheck(String vfile, Set<String> topOrderedVFiles, Set<String> visitedVFiles) {
        List<String> vfilesToCheck = new ArrayList<>(topOrderedVFiles.size());
        for (String vfileToCheck : topOrderedVFiles) {
            if (!visitedVFiles.contains(vfileToCheck)) {
                vfilesToCheck.add(vfileToCheck);
            }
        }
        return vfilesToCheck;
    }

    @Override
    public void runMutationOnAllFiles(Mutation mutation, List<String> compserOptions, Set<String> sortedFiles, Set<String> visitedVFiles) throws IOException, SexpParserException {
        List<String> coqcOptions = makeCoqcOptions();
        MutationLocation loc = new MutationLocation();
        // Iterate in topological order
        for (String vfile : sortedFiles) {
            visitedVFiles.add(vfile);
            String sexpFile = "";
            sexpFile = FileUtils.changeExtension(vfile, "v", "sexp");
            

            // get s-expressions for the .v file
            List<String> linesSexpList = filenameToListOfSexp.get(vfile);

            int countMutations = loc.count(linesSexpList, mutation, applyAll);

            if (MCoq.debug) {
                safePrintln("DEBUG: ====",
                            "DEBUG: Applying operator " + mutation.getClass().getSimpleName() + " to " + vfile + ": " + countMutations +
                        (countMutations == 1 ? " location" : " locations"));
            }

            // Apply each mutation
            for (int mutationIndex = 0; mutationIndex < countMutations; ++mutationIndex) {
                boolean mutate;
                // Write mutated sexp lines to file
                try (PrintStream fileOut = new PrintStream(sexpFile, "UTF-8")) {
                    List<String> currentSexpList = new ArrayList<>(linesSexpList);
                    mutate = SexpUtils.mutateLines(currentSexpList, mutation, mutationIndex, sexp -> fileOut.println(sexp.toString()), applyAll);
                }
                ++numberOfAppliedMutations;
                // Check mutation
                if (mutate) {
                    runMutantAndCheck(vfile, sexpFile, compserOptions, coqcOptions, sortedFiles, visitedVFiles, mutation);
                } else {
                    if (MCoq.debug) {
                        safePrintln("DEBUG: Skipping checking of equivalent mutant ...");
                    }
                    ++numberOfEquivalentMutations;
                }
            }

            revertVoFile(vfile, coqcOptions);
        }
    }

    public void runMutantAndCheck(String vfile, String sexpfile, List<String> compserOptions, List<String> coqcOptions, Set<String> sortedFiles, Set<String> visitedVFiles, Mutation mutation) {
        boolean killed = false;
        boolean timeout = false;

        // Check mutated file using compser
        int exitCode = checkMutatedFile(vfile, sexpfile, compserOptions);
        if (exitCode != 0) {
            if (exitCode == TIMEOUT_CODE) {
                timeout = true;
            } else {
                killed = true;
            }
        }

        Collection<String> otherVFilesToCheck = getOtherVFilesToCheck(vfile, sortedFiles, visitedVFiles);

        revertBeforeChecking(vfile, otherVFilesToCheck, coqcOptions);

        // Check other vfiles using coqc
        if (!timeout && !killed) {
            exitCode = checkOtherVFiles(otherVFilesToCheck, coqcOptions, compserOptions);
            if (exitCode != 0) {
                if (exitCode == TIMEOUT_CODE) {
                    timeout = true;
                } else {
                    killed = true;
                }
            }
        }

        if (killed) {
            ++numberOfKilledMutations;
        }
        if (timeout) {
            ++numberOfTimeOuts;
        }

        if (MCoq.debug) {
            safePrintln(getKilledOrTimeoutInfo(vfile, mutation, killed, timeout));
        }

        // Revert othervfiles if necessary
        revertOtherVFiles(vfile, otherVFilesToCheck, coqcOptions);
        if (MCoq.debug) {
            safePrintln("DEBUG: ----");
        }
    }

    protected int checkOtherVFiles(Collection<String> otherVFilesToCheck, List<String> coqcOptions, List<String> compserOptions) {
        for (String othervfile : otherVFilesToCheck) {
            final int exitCode = ExecUtils.runCoqcAndCheck(project, othervfile, coqcOptions);
            if (exitCode != 0) {
                return exitCode;
            }
        }
        return 0;

    }

    protected int checkMutatedFile(String vfile, String sexpfile, List<String> compserOptions) {
        return ExecUtils.runCompserAndCheck(project, sexpfile, compserOptions);
    }

    protected void revertBeforeChecking(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions) {
        // do nothing
    }

    @Override
    protected void revertOtherVFiles(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions) {
        // do nothing
    }

    public void revertVoFile(String vfile, List<String> coqcOptions) {
        if (MCoq.debug) {
            debug("DEBUG: Reverting " + vfile);
        }
        try {
            FileUtils.revertVOFile(project.toPath().resolve(vfile).toString());
        } catch (IOException e) {
            safePrintln("ERROR: Could not revert " + vfile);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void incrementNumberOfApliedMutations() {
        ++numberOfAppliedMutations;
    }

    public void incrementNumberOfEquivalentMutations() {
        ++numberOfEquivalentMutations;
    }
}
