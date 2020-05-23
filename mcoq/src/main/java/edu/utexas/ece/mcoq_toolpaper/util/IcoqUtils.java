package edu.utexas.ece.mcoq_toolpaper.util;

import edu.utexas.ece.icoq.CoqProjectFile;
import edu.utexas.ece.icoq.Icoq;
import edu.utexas.ece.icoq.depends.CoqDep;
import edu.utexas.ece.icoq.exec.CoqRun;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;
/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class IcoqUtils {

    public static CoqProjectFile getCoqProjectFile(File project) throws IOException {
        final String projectRootDirectory = project.toString();
        System.setProperty("user.dir", projectRootDirectory);
        CoqProjectFile coqProjectFile = new CoqProjectFile(projectRootDirectory + "/_CoqProject", false);
        coqProjectFile.parse();
        return coqProjectFile;
    }

    /**
     * Call coqdep to get file dependencies.
     * @param project
     * @return Map from a file to its dependencies
     * @throws IOException
     */
    public static Map<String, CoqDep> getCoqDepsForProject(File project) throws IOException {
        final Path projectRootPath = project.toPath().normalize();
        final String projectRootDirectory = projectRootPath.toString();

        System.setProperty("user.dir", projectRootDirectory);
        CoqRun coqRun = new CoqRun(projectRootDirectory, "./configure");

        CoqProjectFile coqProjectFile = getCoqProjectFile(project);

        // Change absolute paths with relative paths
        Map<String, CoqDep> oldMap = Icoq.coqdepVFiles(coqRun, coqProjectFile.getVernacFiles().values(), coqProjectFile.getDirMapping());
        Map<String, CoqDep> newMap = new HashMap<>(oldMap.size());

        for (Map.Entry<String, CoqDep> oldEntry: oldMap.entrySet()) {
            final String newKey = projectRootPath.relativize(Paths.get(oldEntry.getKey()).normalize()).toString();
            Set<String> oldDeps = oldEntry.getValue().getDeps();
            Set<String> newDeps = new HashSet<>(oldDeps.size());
            for (String oldDep: oldDeps) {
                newDeps.add(projectRootPath.relativize(Paths.get(oldDep).normalize()).toString());
            }
            oldEntry.getValue().setDeps(newDeps);
            newMap.put(newKey, oldEntry.getValue());
        }
        return newMap;
    }

    /**
     * Computes the dependencies in the Coq project.
     * @param project
     * @return list of .v files in the order of dependencies
     * @throws IOException
     */
    public static List<String> getDependenciesForProject(File project) throws IOException {
        Map<String, CoqDep> depsMap = getCoqDepsForProject(project);
        // do topological sort
        Set<String> changedFiles = new HashSet<>(depsMap.keySet());
        safePrintln("Number of files in project: " + depsMap.size());
        return Icoq.topologicalSort(depsMap, changedFiles, false);
    }

    /**
     * Computes the namespace of the Coq project.
     * @param project
     * @return namespace of the Coq project
     * @throws IOException
     */
    public static String getNamespace(File project) throws IOException {
        CoqProjectFile coqProjectFile = getCoqProjectFile(project);

        String[] projectOpts = coqProjectFile.getDirMapping().split("\\s+");
        if (projectOpts.length < 3) {
            throw new IOException("Invalid -Q option arguments, missing namespace");
        }
        return projectOpts[2];
    }

    public static List<String> getDpdGraph(File project) throws IOException {
        CoqProjectFile coqProjectFile = getCoqProjectFile(project);
        return ExecUtils.coqcDpdGraph(coqProjectFile.getVernacFiles().keySet(), project,
                Arrays.asList(coqProjectFile.getDirMapping()
                        .split("\\s+"))
                        .stream()
                        .map(s -> s.equals("\"\"") ? "" : s)
                        .collect(Collectors.toList()));
    }
}
