package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.location.MutationLocation;
import edu.utexas.ece.mcoq8_10.mutation.Mutation;
import edu.utexas.ece.mcoq8_10.mutator.jobs.MutatorJob;
import edu.utexas.ece.mcoq8_10.mutator.jobs.MutatorOneFileVOOptOrderWorker;
import edu.utexas.ece.mcoq8_10.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;
/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOParMutant extends MutatorOneFileVOOptOrder {

    private final int numOfThreads;
    private final int JOB_QUEUE_SIZE = 64;

    public MutatorOneFileVOParMutant(File project, List<String> flags, boolean applyAll, long timeout, int numOfThreads) throws IOException {
        super(project, flags, applyAll, timeout);
        this.numOfThreads = numOfThreads;
        if (MCoq.debug) {
            safePrintln("DEBUG: Num of threads " + numOfThreads);
        }
    }

    @Override
    public void runMutationOnAllFiles(Mutation mutation, List<String> compserOptions, Set<String> sortedFiles, Set<String> visitedVFiles) throws IOException {
        List<String> coqcOptions = makeCoqcOptions();

        // Create copy of workspaces
        Path[] copyProjects = new Path[numOfThreads];
        for (int i = 0; i < numOfThreads; ++i) {
            copyProjects[i] = Files.createTempDirectory(project.getName());
            FileUtils.copyDirectory(project.toPath(), copyProjects[i]);
        }

        MutatorOneFileVOOptOrder[] mutators = new MutatorOneFileVOOptOrder[numOfThreads];
        for (int i = 0; i < numOfThreads; ++i) {
            mutators[i] = new MutatorOneFileVOOptOrder(copyProjects[i].toFile(), this);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        final BlockingQueue<MutatorJob> jobQueue = new LinkedBlockingQueue<>(JOB_QUEUE_SIZE);
        MutatorOneFileVOOptOrderWorker[] workers = new MutatorOneFileVOOptOrderWorker[numOfThreads];
        Future<Boolean>[] futures = new Future[numOfThreads];

        for (int i = 0; i < numOfThreads; ++i) {
            workers[i] = new MutatorOneFileVOOptOrderWorker(jobQueue, mutators[i], sortedFiles, coqcOptions, compserOptions);
            futures[i] = executorService.submit(workers[i], true);
        }

        // Iterate in topological order
        for (String vfile : sortedFiles) {
            visitedVFiles.add(vfile);

            // get s-expressions for the .v file
            List<String> linesSexpList = filenameToListOfSexp.get(vfile);
            MutationLocation loc = new MutationLocation();
            int countMutations = loc.count(linesSexpList, mutation, applyAll);

            // Apply each mutation
            for (int mutationIndex = 0; mutationIndex < countMutations; ++mutationIndex) {
                // Create mutation job and submit here
                // with new copy visitedVFiles
                // runMutantAndCheck(vfile, sexpFile, compserOptions, coqcOptions, sortedFiles, visitedVFiles, mutation);
                try {
                    jobQueue.put(new MutatorJob(mutation, vfile, mutationIndex));
                } catch (InterruptedException e) {
                    safePrintln("ERROR: Interrupted while putting new work!");
                    System.exit(1);
                }
            }
        }

        // Put last jobs
        for (int i = 0; i < numOfThreads; ++i) {
            try {
                jobQueue.put(new MutatorJob(true));
            } catch (InterruptedException e) {
                safePrintln("ERROR: Interrupted while putting new work!");
                System.exit(1);
            }
        }

        // Wait all to be done
        for (int i = 0; i < numOfThreads; ++i) {
            try {
                if (!futures[i].get()) {
                    safePrintln("ERROR: Unexpected return of threads!");
                    System.exit(1);
                }
            } catch (InterruptedException e) {
                safePrintln("ERROR: Interrupted while waiting for future!");
                System.exit(1);
            } catch (ExecutionException e) {
                safePrintln("ERROR: Execution exception of the thread!");
                System.exit(1);
            }
        }

        // Update stats
        for (int i = 0; i < numOfThreads; ++i) {
            numberOfAppliedMutations += mutators[i].numberOfAppliedMutations;
            numberOfKilledMutations += mutators[i].numberOfKilledMutations;
            numberOfTimeOuts += mutators[i].numberOfTimeOuts;
            numberOfEquivalentMutations += mutators[i].numberOfEquivalentMutations;
        }

        for (int i = 0; i < numOfThreads; ++i) {
            FileUtils.deleteDirectory(copyProjects[i]);
        }

        executorService.shutdown();
    }
}
