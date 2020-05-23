package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutations;
import edu.utexas.ece.mcoq_toolpaper.mutator.Mutator;
import edu.utexas.ece.mcoq_toolpaper.util.FileGraph;
import edu.utexas.ece.mcoq_toolpaper.util.FileUtils;
import edu.utexas.ece.mcoq_toolpaper.util.IcoqUtils;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutations.BLACKLISTED_MUTATIONS;
import static edu.utexas.ece.mcoq_toolpaper.util.ExecUtils.checkSercompAndCompserOnProjectFiles;
import static edu.utexas.ece.mcoq_toolpaper.util.ExecUtils.checkSercompAndSerloadOnProjectFiles;
import static edu.utexas.ece.mcoq_toolpaper.util.IcoqUtils.getDependenciesForProject;
import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

/**
 * This is the entry class to the mutation tool.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MCoq {

    public static final int TIMEOUT_CODE = 124;
    public static boolean debug = false;
    public static boolean skipeq = false;
    public static boolean toolpaper = false;
    
    public static void main(String... args) {
        ArgumentParser parser = ArgumentParsers.newFor("mcoq_toolpaper").build().defaultHelp(true).description("Mutate output of sercomp.");
        parser.addArgument("--flag").action(Arguments.append()).type(String.class).help("Flags for the coq project of the s-expression file.");
        parser.addArgument("--rflag").action(Arguments.append()).type(String.class).help("-R flags for the coq project of the s-expression file.");
        parser.addArgument("--project").type(File.class).help("A path to the coq project to run.");
        parser.addArgument("--toolpaper").action(Arguments.storeTrue()).help("Indicates using mcoq toolpaper (supports newer Coq versions)");
        parser.addArgument("--file").metavar("FILE.sexp").type(File.class).help("A s-expression file to mutate.");
        parser.addArgument("-m", "--mutation").type(Mutations.class).choices(Mutations.values()).help("Selected mutation.");
        parser.addArgument("-o", "--out").metavar("OUT.sexp").setDefault(new File("out.sexp")).type(File.class).help("Optional output file for mutated s-expression.");
        parser.addArgument("--all").action(Arguments.storeTrue()).help("Apply the given mutation to all Vernac Types.");
        parser.addArgument("--noMutation").action(Arguments.storeTrue()).help("Just run one sexpression at a time without mutating.");
        parser.addArgument("--debug").action(Arguments.storeTrue()).help("Enable debug info during the execution");
        parser.addArgument("--skipeq").action(Arguments.storeTrue()).help("Skip equivalent mutants");
        parser.addArgument("--timeout").type(long.class).setDefault(60L).help("Timeout for commands in seconds.");
        parser.addArgument("--mutator").type(String.class).setDefault("MutatorOneFile").help("Simple name for the Mutator class.");
        parser.addArgument("--threads").type(int.class).setDefault(1).help("Number of threads for MutatorOneFileVOParCheck mutator.");
        parser.addArgument("--mutations").type(String.class).setDefault("").help("Comma separated mutation names");
        
        MutuallyExclusiveGroup operations = parser.addMutuallyExclusiveGroup().required(true).description("Operation for selected mutation.");
        operations.addArgument("-c", "--count").action(Arguments.storeTrue()).help("Count all locations that we can apply the given mutation.");
        operations.addArgument("-a", "--apply").metavar("IDX").type(int.class).help("Apply the given mutation to the given location.");
        operations.addArgument("-d", "--dot").action(Arguments.storeTrue()).help("Output the given S-expression to dot file.");
        operations.addArgument("-e", "--expByExp").action(Arguments.storeTrue()).help("Give the s-expressions to compser one by one (starting with first and extending previously given prefix).");
        operations.addArgument("-f", "--checkFiles").action(Arguments.storeTrue()).help("Call sercomp and compser on all files in project separately.");
        operations.addArgument("-l", "--leaves").action(Arguments.storeTrue()).help("Count the number of the files that are leaves in the project");
        operations.addArgument("--fileTree").action(Arguments.storeTrue()).help("Dump file graph (rdeps).");
        operations.addArgument("--dpdGraph").action(Arguments.storeTrue()).help("Dump coq-dpdgraph of the project");

        try {
            Namespace res = parser.parseArgs(args);
            File file = res.get("file");
            File out = res.get("out");
            File project = res.get("project");
            List<String> flags = res.get("flag");
            List<String> dirMappings = new ArrayList<>();
            if (flags != null) {
                for (String flag : flags) {
                    dirMappings.add("-Q");
                    dirMappings.add(flag);
                }
            }
            List<String> rflags = res.get("rflag");
            if (rflags != null) {
                for (String rflag : rflags) {
                    dirMappings.add("-R");
                    dirMappings.add(rflag);
                }
            }
            skipeq = res.get("skipeq");
            toolpaper = res.get("toolpaper");
            
            MutationLocation loc = new MutationLocation();
            Mutation mutation = null;
            if (res.get("mutation") != null) {
                mutation = Mutations.toMutation(res.get("mutation"));
            }
            List<Mutation> mutations = new ArrayList<>();
            if (!((String)res.get("mutations")).isEmpty()) {
                for (String m : ((String) res.get("mutations")).split(",")) {
                    mutations.add(Mutations.toMutation(Mutations.valueOf(m.trim())));
                }
            }
            boolean applyAll = res.get("all");
            boolean noMutation = res.get("noMutation");
            debug = res.get("debug");
            long timeout = res.get("timeout");

            try (PrintStream output = new PrintStream(new BufferedOutputStream(new FileOutputStream(out)), true)) {
                if (res.get("count")) { // count mutation locations
                    output.println(loc.count(file, mutation, applyAll));
                } else if (res.get("dot")) {
                    FileUtils.streamOfLines(file).findFirst().ifPresent(
                            line ->
                            {
                                try {
                                    SexpFactory.dot(SexpFactory.parse(line), output);
                                } catch (SexpParserException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                } else if (res.get("expByExp")) {
                    if (!noMutation) {
                        String mutatorClassName = res.get("mutator");

                        Mutator mutator;
                        switch (mutatorClassName) {
                            case "MutatorOneFileVOParCheck":
                            case "MutatorOneFileVOParAsync":
                            case "MutatorOneFileVOParQuick":
                            case "MutatorOneFileVOParMutant":
                                int numOfThreads = res.get("threads");
                                mutator = (Mutator) Class
                                        .forName(Mutator.class.getPackage().getName() + "." + mutatorClassName)
                                        .getConstructor(File.class, List.class, boolean.class, long.class, int.class)
                                        .newInstance(project, dirMappings, applyAll, timeout, numOfThreads);
                                break;
                            default:
                                mutator = (Mutator) Class
                                .forName(Mutator.class.getPackage().getName() + "." + mutatorClassName)
                                .getConstructor(File.class, List.class, boolean.class, long.class)
                                .newInstance(project, dirMappings, applyAll, timeout);
                                break;
                        }
                        if (mutations.isEmpty()) {
                            for (Mutations m : Mutations.values()) {
                                if (!BLACKLISTED_MUTATIONS.contains(m)) {
                                    mutator.runMutation(Mutations.toMutation(m));
                                }
                            }
                        } else {
                            for (Mutation m : mutations) {
                                mutator.runMutation(m);
                            }
                        }
                        mutator.clean();
                        safePrintln(mutator.getSummary());
                    } else {
                        throw new UnsupportedOperationException("noMutation option is not valid for -e");
                    }
                } else if (res.get("checkFiles")) {
                    checkSercompAndCompserOnProjectFiles(project, dirMappings);
                } else if (res.get("leaves")) {
                    List<String> sortedFiles = getDependenciesForProject(project);
                    FileGraph fileGraph = new FileGraph(project);
                    int numberOfLeaves = 0;
                    for (String filename : sortedFiles) {
                        if (fileGraph.isLeaf(filename)) {
                            if (MCoq.debug) {
                                safePrintln("DEBUG: " + filename);
                            }
                            numberOfLeaves++;
                        }
                    }
                    safePrintln("Number of leaves: " + numberOfLeaves);
                } else if (res.get("fileTree")) {
                    FileGraph fileGraph = new FileGraph(project);
                    fileGraph.dot(output);
                } else if (res.get("dpdGraph")) {
                    List<String> result = IcoqUtils.getDpdGraph(project);
                    if (result == null) {
                        System.exit(1);
                    }
                    result.forEach(output::println);
                } else { // apply mutation
                    int index = res.get("apply");
                    SexpUtils.mutateLines(file, mutation, index, output::println, applyAll);
                }
            }
        } catch (SexpParserException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
