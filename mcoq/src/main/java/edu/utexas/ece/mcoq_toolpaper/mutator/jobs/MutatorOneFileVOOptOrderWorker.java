package edu.utexas.ece.mcoq_toolpaper.mutator.jobs;

import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;
import edu.utexas.ece.mcoq_toolpaper.mutator.MutatorOneFileVOOptOrder;
import edu.utexas.ece.mcoq_toolpaper.util.FileUtils;

import static edu.utexas.ece.mcoq_toolpaper.util.SexpUtils.mutateLines;
import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 */
public class MutatorOneFileVOOptOrderWorker implements Runnable {

    private final BlockingQueue<MutatorJob> jobQueue;

    private MutatorOneFileVOOptOrder mutator;
    private Set<String> sortedVFiles;
    private List<String> coqcOptions;
    private List<String> compserOptions;
    private Set<String> visitedVFiles;

    public MutatorOneFileVOOptOrderWorker(BlockingQueue<MutatorJob> jobQueue, MutatorOneFileVOOptOrder mutator, Set<String> sortedVFiles, List<String> coqcOptions, List<String> compserOptions) {
        this.jobQueue = jobQueue;
        this.mutator = mutator;
        this.sortedVFiles = sortedVFiles;
        this.coqcOptions = coqcOptions;
        this.compserOptions = compserOptions;
        this.visitedVFiles = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MutatorJob job = jobQueue.take();

                if (job.isLastJob()) {
                    break;
                }

                Mutation mutation = job.getMutation();
                int mutationIndex = job.getIndex();

                if (MCoq.debug) {
                    safePrintln("DEBUG: Checking mutation " + mutation.getClass().getSimpleName() + " idx=" + mutationIndex + " in directory " + mutator.getProject().toString());
                }

                final String vfile = job.getVfile();
                List<String> linesSexpList = mutator.getFilenameToListOfSexp().get(vfile);
                visitedVFiles.add(vfile);
                if (MCoq.debug) {
                    safePrintln("DEBUG: Visiting " + vfile + " in project " + mutator.getProject());
                }

                final String sexpFile = FileUtils.changeExtension(vfile, "v", "sexp");
                boolean mutate = true;
                // Write mutated sexp lines to file
                try (PrintStream fileOut = new PrintStream(mutator.getProject().toPath().resolve(sexpFile).toString(), "UTF-8")) {
                    List<String> currentSexpList = new ArrayList<>(linesSexpList);
                    mutate = mutateLines(currentSexpList, mutation, mutationIndex, sexp -> fileOut.println(sexp.toString()), false);
                } catch (FileNotFoundException e) {
                    safePrintln("ERROR: File not found " + e.getMessage());
                    System.exit(1);
                } catch (UnsupportedEncodingException e) {
                    safePrintln("ERROR: Unsupported encoding " + e.getMessage());
                    System.exit(1);
                } catch (IOException e) {
                    safePrintln("ERROR: IOException " + e.getMessage());
                    System.exit(1);
                } catch (SexpParserException e) {
                    safePrintln("ERROR: SexpParserException " + e.getMessage());
                    System.exit(1);
                }

                mutator.incrementNumberOfApliedMutations();
                // Check mutation
                if (mutate) {
                    mutator.runMutantAndCheck(vfile, sexpFile, compserOptions, coqcOptions, sortedVFiles, visitedVFiles, mutation);
                } else {
                    if (MCoq.debug) {
                        safePrintln("DEBUG: Skipping checking of equivalent mutant ...");
                    }
                    mutator.incrementNumberOfEquivalentMutations();
                }
            } catch (InterruptedException e) {
                safePrintln("ERROR: Job is interrupted!");
                System.exit(1);
            }
        }
    }
}
