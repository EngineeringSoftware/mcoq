package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.icoq.Icoq;
import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.util.ExecUtils;
import edu.utexas.ece.mcoq8_10.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq8_10.util.ExecUtils.runCoqcVioCheckAndCheck;
import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOParQuick extends MutatorOneFileVOSkip {

    private final ExecutorService executorService;
    private final int numOfThreads;

    public MutatorOneFileVOParQuick(File project, List<String> flags, boolean applyAll, long timeout, int numOfThreads) throws IOException {
        super(project, flags, applyAll, timeout);
        this.numOfThreads = numOfThreads;
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
    protected void revertBeforeChecking(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions) {
        Set<String> allDeps = Icoq.transitiveDeps(fileGraph.getDeps(), new HashSet<>(otherVFilesToCheck));
        for (String dep : allDeps) {
            if (!otherVFilesToCheck.contains(dep) && !dep.equals(vfile)) {
                final String vioFile = FileUtils.changeExtension(dep, "v", "vio");
                try {
                    Files.deleteIfExists(Paths.get(vioFile));
                } catch (IOException e) {
                    safePrintln("ERROR: Could not delete file " + vioFile + ".");
                    System.exit(1);
                }
                // super.revertVoFile(dep, coqcOptions);
            }
        }
    }

    @Override
    protected int checkOtherVFiles(Collection<String> otherVFilesToCheck, List<String> coqcOptions, List<String> compserOptions) {
        for (List<String> vfiles : fileGraph.topoSortByLevel(otherVFilesToCheck).values()) {
            List<List<String>> commands = vfiles.stream().map(vfile -> ExecUtils.coqcQuickCommand(vfile, coqcOptions)).collect(Collectors.toList());
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
                        safePrintln("ERROR: COQC QUICK EXIT CODE " + execResult.code,
                                    "ERROR: COQC QUICK ERROR: " + execResult.stdErr);
                        return execResult.code;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                executorService.shutdown();
                System.exit(1);
            }
        }
        if (!otherVFilesToCheck.isEmpty()) {
            int exitCode = runCoqcVioCheckAndCheck(project, new ArrayList<>(otherVFilesToCheck), coqcOptions, numOfThreads);
            if (exitCode != 0) {
                return exitCode;
            }
        }
        return 0;
    }
}
