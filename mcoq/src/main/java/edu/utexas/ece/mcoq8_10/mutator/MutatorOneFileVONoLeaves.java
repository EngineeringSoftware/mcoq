package edu.utexas.ece.mcoq8_10.mutator;

import edu.utexas.ece.mcoq8_10.util.ExecUtils;
import edu.utexas.ece.mcoq8_10.util.FileGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutatorOneFileVONoLeaves extends MutatorOneFileVO {
    private FileGraph fileGraph;

    public MutatorOneFileVONoLeaves(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        super(project, flags, applyAll, timeout);
        this.fileGraph = new FileGraph(project);
    }

    @Override
    protected int checkMutatedFile(String vfile, String sexpfile, List<String> compserOptions) {
        if (fileGraph.isLeaf(vfile)) {
            List<String> leafOptions = new ArrayList<>(compserOptions.size());
            for (String flag : compserOptions) {
                if (flag.equals("--mode=vo")) {
                    leafOptions.add("--mode=check");
                } else {
                    leafOptions.add(flag);
                }
            }
            return ExecUtils.runCompserAndCheck(project, sexpfile, leafOptions);
        } else {
            return ExecUtils.runCompserAndCheck(project, sexpfile, compserOptions);
        }
    }

    @Override
    public void revertVoFile(String vfile, List<String> compserOptions) {
        if (!fileGraph.isLeaf(vfile)) {
            safePrintln("Not Leaf: " + vfile);
            super.revertVoFile(vfile, Collections.emptyList());
        } else {
            safePrintln("Leaf: " + vfile);
        }
    }
}
