package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.util.ExecUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;
/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOParCheck extends MutatorOneFileVOSkip {

    private final ExecutorService executorService;

    public MutatorOneFileVOParCheck(File project, List<String> flags, boolean applyAll, long timeout, int numOfThreads) throws IOException {
        super(project, flags, applyAll, timeout);
        if (MCoq.debug) {
            safePrintln("DEBUG: Num of threads " + numOfThreads);
        }
        this.executorService = Executors.newFixedThreadPool(numOfThreads);
    }

    @Override
    public void clean() {
        this.executorService.shutdown();
    }

    @Override
    protected int checkOtherVFiles(Collection<String> otherVFilesToCheck, List<String> coqcOptions, List<String> compserOptions) {
        for (List<String> vfiles : fileGraph.topoSortByLevel(otherVFilesToCheck).values()) {
            List<List<String>> commands = vfiles.stream().map(vfile -> ExecUtils.coqcCommand(vfile, coqcOptions)).collect(Collectors.toList());
            try {
                if (MCoq.debug) {
                    final int commandsSize = commands.size();
                    safePrintln("DEBUG: Executing in parallel: ",
                                "DEBUG: Num of parallel jobs " + commandsSize);
                    ListIterator<List<String>> itr = commands.listIterator();
                    int i = 0;
                    while (itr.hasNext()) {
                        List<String> command = itr.next();
                        safePrintln("DEBUG:    Command [" + i + "] = " + String.join(" ",
                                command.stream().map(s -> s.isEmpty() ? "\"\"" : s).collect(Collectors.toList())));
                        ++i;
                    }
                }
                for (Future<ExecUtils.ExecResult> result : ExecUtils.execParallel(project, executorService, commands)) {
                    final ExecUtils.ExecResult execResult = result.get();
                    if (execResult.code != 0) {
                        safePrintln("ERROR: COQC EXIT CODE " + execResult.code,
                                    "ERROR: COQC ERROR: " + execResult.stdErr);
                        return execResult.code;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                executorService.shutdown();
                System.exit(1);
            }
        }
        return 0;
    }
}
