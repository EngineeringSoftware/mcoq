package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.mcoq8_10.util.FileGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVOOptOrder extends MutatorOneFileVO {

    private FileGraph fileGraph;

    public MutatorOneFileVOOptOrder(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
        this.fileGraph = new FileGraph(project);
    }

    public MutatorOneFileVOOptOrder(File project, MutatorOneFileVOOptOrder other) {
        super(project, other);
        this.fileGraph = other.fileGraph;
    }

    @Override
    protected Collection<String> getOtherVFilesToCheck(String vfile, Set<String> topOrderedVFiles, Set<String> visitedVFiles) {
        List<String> vFilesToCheck = new ArrayList<>(topOrderedVFiles.size());
        for (String vFileToCheck : fileGraph.getAllRDependencies(vfile)) {
            if (!visitedVFiles.contains(vFileToCheck)) {
                vFilesToCheck.add(vFileToCheck);
            }
        }
        return vFilesToCheck;
    }

    @Override
    protected void revertOtherVFiles(String vfile, Collection<String> otherVFilesToCheck, List<String> coqcOptions) {
        super.revertVoFile(vfile, coqcOptions);
        for (String otherVFileToCheck : otherVFilesToCheck) {
            super.revertVoFile(otherVFileToCheck, coqcOptions);
        }
    }

    @Override
    public void revertVoFile(String vfile, List<String> coqcOptions) {
        // do nothing
    }
}
