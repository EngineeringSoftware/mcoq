package edu.utexas.ece.mcoq_toolpaper.mutator;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;
import edu.utexas.ece.mcoq_toolpaper.util.ExecUtils;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static edu.utexas.ece.mcoq_toolpaper.MCoq.TIMEOUT_CODE;
import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFile extends Mutator {

    public MutatorOneFile(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
    }

    @Override
    public void clean() {

    }

    /**
     * Run compser on the coq project giving the one by one
     * file and applying given mutation on all possible
     * locations one after the other.
     */
    @Override
    public void runMutation(Mutation mutation) throws IOException, SexpParserException {
        Set<String> sortedFiles = filenameToListOfSexp.keySet();

        // go through all the files and add one file before calling compser
        int numberOfAppliedMutations = 0;
        int numberOfKilledMutations = 0;
        int numberOfTimeOuts = 0;

        long startTimeMillis = System.currentTimeMillis();

        List<String> currentPrefixSexpList = new LinkedList<>();

        Set<String> visitedVFiles = new HashSet<>();
        for (String vfile : sortedFiles) {
            visitedVFiles.add(vfile);

            // get s-expressions for the .v file
            List<String> linesSexpList = filenameToListOfSexp.get(vfile);
            Set<String> addedFromCurrentSexpFile = new HashSet<>();
            for (String line : linesSexpList) {
                Sexp currentSexp = SexpFactory.parse(line);
                // if the sexpression is NOT import of another file add it to the list
                if (!SexpUtils.isImportSexp(currentSexp, namespace)) {
                    currentPrefixSexpList.add(line);
                    addedFromCurrentSexpFile.add(line);
                } else {
                    // TODO: we may need here to change import with something else
                    continue;
                }

                Path filepath = Files.createTempFile("mcoq", ".sexp");
                MutationLocation loc = new MutationLocation();
                int countMutations = loc.count(currentPrefixSexpList, mutation, applyAll);
                if (countMutations > numberOfAppliedMutations) {
                    List<String> currentSexpList = new ArrayList<>(2 * currentPrefixSexpList.size());
                    SexpUtils.mutateLines(currentPrefixSexpList, mutation, countMutations - 1, sexp -> currentSexpList.add(sexp.toString()), applyAll);
                    numberOfAppliedMutations++;

                    boolean killed = false;
                    boolean timeout = false;

                    for (String lineSexp : linesSexpList) {
                        if (!addedFromCurrentSexpFile.contains(lineSexp)
                                && !SexpUtils.isImportSexp(SexpFactory.parse(lineSexp), namespace)) {
                            currentSexpList.add(lineSexp);
                        }
                    }

                    Files.write(filepath, currentSexpList);
                    if (MCoq.debug) {
                        safePrintln("DEBUG: Last before calling compser",
                                    SexpUtils.getDebugInfo(SexpFactory.parse(currentSexpList.get(currentSexpList.size() - 1))));
                    }

                    int exitCode = ExecUtils.runCompserAndCheck(project, filepath.toString(), flags);
                    if (exitCode != 0) {
                        if (exitCode == TIMEOUT_CODE) {
                            timeout = true;
                        } else {
                            killed = true;
                        }
                    }

                    for (String othervfile : sortedFiles) {
                        if (timeout || killed) {
                            break;
                        }
                        if (visitedVFiles.contains(othervfile)) {
                            continue;
                        }
                        // get sexpression from the standard output of sercomp
                        List<String> sexpLinesList = filenameToListOfSexp.get(othervfile);
                        for (String sexpToAdd : sexpLinesList) {
                            if (!SexpUtils.isImportSexp(SexpFactory.parse(sexpToAdd), namespace)) {
                                currentSexpList.add(sexpToAdd);
                            }
                        }
                        Files.write(filepath, currentSexpList);
                        if (MCoq.debug) {
                            safePrintln("DEBUG: Last before calling compser",
                                        SexpUtils.getDebugInfo(SexpFactory.parse(currentSexpList.get(currentSexpList.size() - 1))));
                        }
                        exitCode = ExecUtils.runCompserAndCheck(project, filepath.toString(), flags);
                        if (exitCode != 0) {
                            if (exitCode == TIMEOUT_CODE) {
                                timeout = true;
                                break;
                            } else {
                                killed = true;
                            }
                        }
                    }

                    if (killed) {
                        numberOfKilledMutations++;
                    }
                    if (timeout) {
                        numberOfTimeOuts++;
                    }
                }
            }
        }
        long endTimeMillis = System.currentTimeMillis();
        long elapsed = endTimeMillis - startTimeMillis;
        safePrintln("Mutation: " + mutation.toString(),
                    "Number of files per project: " + visitedVFiles.size(),
                    "Number of s-expressions: " + currentPrefixSexpList.size(),
                    "Number of mutants: " + numberOfAppliedMutations,
                    "Number of timeouts: " + numberOfTimeOuts,
                    "Number of killed mutants: " + numberOfKilledMutations,
                    "Time elapsed (in ms): " + elapsed);
        numberTotal += numberOfAppliedMutations;
        killedTotal += numberOfKilledMutations;
        timeoutTotal += numberOfTimeOuts;
        timeTotal += elapsed;
    }
}
