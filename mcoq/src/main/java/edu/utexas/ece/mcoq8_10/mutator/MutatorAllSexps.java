package edu.utexas.ece.mcoq8_10.mutator;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.location.MutationLocation;
import edu.utexas.ece.mcoq8_10.mutation.Mutation;
import edu.utexas.ece.mcoq8_10.util.ExecUtils;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static edu.utexas.ece.mcoq8_10.MCoq.TIMEOUT_CODE;
import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorAllSexps extends Mutator {

    public MutatorAllSexps(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
    }

    @Override
    public void clean() {

    }

    @Override
    public void runMutation(Mutation mutation) throws IOException, SexpParserException {

        // go through all the files and add everything after applying mutation
        int numberOfMutations;
        int numberOfKilledMutations = 0;
        int numberOfTimeOuts = 0;

        long startTimeMillis = System.currentTimeMillis();
        List<String> allSexpList = new LinkedList<>();
        Set<String> sortedFiles = filenameToListOfSexp.keySet();

        for (String vfile : sortedFiles) {
            List<String> currentList = filenameToListOfSexp.get(vfile);
            for (String sexpString : currentList) {
                Sexp sexp = SexpFactory.parse(sexpString);
                if (!SexpUtils.isImportSexp(sexp, namespace)) {
                    allSexpList.add(sexpString);
                }
            }
        }
        MutationLocation loc = new MutationLocation();
        numberOfMutations = loc.count(allSexpList, mutation, applyAll);

        Path filepath = Files.createTempFile("mcoq", ".sexp");

        for (int currentPos = 0; currentPos < numberOfMutations; currentPos++) {
            PrintStream outputForSexps = new PrintStream(new BufferedOutputStream(new FileOutputStream(filepath.toFile())), true);
            SexpUtils.mutateLines(allSexpList, mutation, currentPos, outputForSexps::println, applyAll);

            if (MCoq.debug) {
                safePrintln("DEBUG: Last before calling compser",
                            SexpUtils.getDebugInfo(SexpFactory.parse(allSexpList.get(allSexpList.size() - 1))));
            }

            int exitCode = ExecUtils.runCompserAndCheck(project, filepath.toString(), flags);
            if (exitCode != 0) {
                if (exitCode == TIMEOUT_CODE) {
                    numberOfTimeOuts++;
                } else {
                    numberOfKilledMutations++;
                }
            }
        }

        long endTimeMillis = System.currentTimeMillis();
        long elapsed = endTimeMillis - startTimeMillis;
        safePrintln("Mutation: " + mutation.toString(),
                    "Number of files per project: " + sortedFiles.size(),
                    "Number of s-expressions: " + allSexpList.size(),
                    "Number of mutants: " + numberOfMutations,
                    "Number of timeouts: " + numberOfTimeOuts,
                    "Number of killed mutants: " + numberOfKilledMutations,
                    "Time elapsed (in ms): " + elapsed);
        numberTotal += numberOfMutations;
        killedTotal += numberOfKilledMutations;
        timeoutTotal += numberOfTimeOuts;
        timeTotal += elapsed;
    }
}
    
