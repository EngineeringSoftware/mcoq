package edu.utexas.ece.mcoq8_10.util;

import edu.utexas.ece.icoq.Icoq;
import edu.utexas.ece.icoq.depends.CoqDep;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class FileGraph {
    private Map<String, CoqDep> deps;
    private Map<String, Set<String>> deps2;
    private Map<String, Set<String>> rDeps;

    public FileGraph(File project) throws IOException {
        this.deps = IcoqUtils.getCoqDepsForProject(project);
        this.rDeps = Icoq.reverseFileGraph(this.deps);
        this.deps2 = Icoq.fileGraph(deps);
    }

    /**
     * Return all r-deps in topo order
     * @param vfile
     * @return
     */
    public List<String> getAllRDependencies(String vfile) {
        return Icoq.topologicalSort(deps, Icoq.transitiveDeps(rDeps, new HashSet<>(Arrays.asList(vfile))), false);
    }

    /**
     * Return topo sorted vfiles level by level
     * @param vfiles
     * @return
     */
    public Map<Integer, List<String>> topoSortByLevel(Collection<String> vfiles) {
        return Icoq.topologicalSortLevel(deps, new HashSet<>(vfiles), false);
    }

    /**
     * Return all deps
     * @param vfile
     * @return
     */
    public Set<String> getAllDependencies(String vfile) {
        return Icoq.transitiveDeps(deps2, new HashSet<>(Arrays.asList(vfile)));
    }

    public boolean isLeaf(String file) {
        return !rDeps.containsKey(file) || rDeps.get(file).isEmpty();
    }

    public Map<String, Set<String>> getDeps() {
        return deps2;
    }

    public Map<String, Set<String>> getrDeps() {
        return rDeps;
    }

    public void dot(PrintStream out) {
        out.println("digraph ahmet {");
        Set<String> visited = new HashSet<>();
        Map<String, Integer> idMap = new HashMap<>();
        int id = 0;
        for (String currentVfile : rDeps.keySet()) {
            // Visit once
            if (visited.contains(currentVfile)) {
                continue;
            } else {
                visited.add(currentVfile);
            }
            // Setup id
            Integer rDepId = idMap.get(currentVfile);
            if (rDepId == null) {
                rDepId = id++;
                idMap.put(currentVfile, rDepId);
            }
            out.printf("n%d [label=\"%s\" shape=none];\n", rDepId, currentVfile);
            Set<String> rDepsOfVFile = rDeps.get(currentVfile);
            if (rDepsOfVFile != null) {
                for (String vfile : rDepsOfVFile) {
                    Integer childRDepId = idMap.get(vfile);
                    if (childRDepId == null) {
                        childRDepId = id++;
                        idMap.put(vfile, childRDepId);
                    }
                    out.printf("n%d -> n%d;\n", rDepId, childRDepId);
                }
            }
        }
        out.println("}");
    }
}
