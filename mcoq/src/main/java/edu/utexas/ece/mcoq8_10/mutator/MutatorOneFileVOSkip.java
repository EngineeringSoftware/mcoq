package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.icoq.Icoq;
import edu.utexas.ece.mcoq8_10.util.FileGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOSkip extends MutatorOneFileVO {

    protected FileGraph fileGraph;

    public MutatorOneFileVOSkip(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
        this.fileGraph = new FileGraph(project);
    }

    public MutatorOneFileVOSkip(File project, MutatorOneFileVOSkip other) {
        super(project, other);
        this.fileGraph = other.fileGraph;
    }

    @Override
    protected void revertBeforeChecking(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions) {
        Set<String> allDeps = Icoq.transitiveDeps(fileGraph.getDeps(), new HashSet<>(otherVFilesToCheck));
        for (String dep : allDeps) {
            if (!otherVFilesToCheck.contains(dep) && !dep.equals(vfile)) {
                super.revertVoFile(dep, coqcOptions);
            }
        }
    }

    @Override
    protected Collection<String> getOtherVFilesToCheck(String vfile, Set<String> topOrderedVFiles, Set<String> visitedVFiles) {
        List<String> vFilesToCheck = new ArrayList<>(topOrderedVFiles.size());
        Set<String> mustReCheck = Icoq.transitiveDeps(fileGraph.getrDeps(), new HashSet<>(Arrays.asList(vfile)));
        for (String vFileToCheck : topOrderedVFiles) {
            if (!visitedVFiles.contains(vFileToCheck) && mustReCheck.contains(vFileToCheck)) {
                vFilesToCheck.add(vFileToCheck);
            }
        }
        return vFilesToCheck;
    }
}
