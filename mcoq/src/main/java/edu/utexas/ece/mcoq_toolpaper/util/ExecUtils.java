package edu.utexas.ece.mcoq_toolpaper.util;

import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq_toolpaper.util.IcoqUtils.getCoqProjectFile;
import static edu.utexas.ece.mcoq_toolpaper.util.IcoqUtils.getDependenciesForProject;
import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

/**
 * Util methods for managing external processes.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ExecUtils {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String EXEC_PATH = Paths.get(USER_HOME, "projects", "coq-mutation", "coq-serapi").normalize().toString();
    private static final String SERCOMP = MCoq.toolpaper ? "sercomp" : Paths.get(EXEC_PATH, "sercomp.native").toString();
    private static final String SERLOAD = Paths.get(EXEC_PATH, "serload.native").toString();
    private static final String COQC = Paths.get("coqc").toString();

    public static int runCompserAndCheck(File directory, String filename, List<String> flags) {
        ExecUtils.ExecResult result = ExecUtils.compser(directory, filename, flags);
        if (result.code != 0) {
            safePrintln("ERROR: COMPSER EXIT CODE " + result.code,
                        "ERROR: COMPSER ERROR: " + result.stdErr);
        }
        return result.code;
    }

    public static int runSerloadAndCheck(File directory, String filename, List<String> flags) {
        ExecUtils.ExecResult result = ExecUtils.serload(directory, filename, flags);
        if (result.code != 0) {
            safePrintln("ERROR: SERLOAD EXIT CODE " + result.code,
                        "ERROR: SERLOAD ERROR: " + result.stdErr);
        }
        return result.code;
    }

    public static List<String> runSercompAndCheck(File directory, String filename, List<String> flags) {
        ExecUtils.ExecResult result = ExecUtils.sercomp(directory, filename, flags);
        if (result.code != 0) {
            safePrintln("ERROR: SERCOMP EXIT CODE " + result.code,
                        "ERROR: SERCOMP ERROR: " + result.stdErr);
            System.exit(1);
        }
        return result.stdOut;
    }

    public static int runCoqcAndCheck(File directory, String filename, List<String> flags) {
        ExecUtils.ExecResult result = ExecUtils.coqc(directory, filename, flags);
        if (result.code != 0) {
            safePrintln("ERROR: COQC EXIT CODE " + result.code,
                        "ERROR: COQC ERROR: " + result.stdErr);
        }
        return result.code;
    }

    public static int runCoqcVioCheckAndCheck(File directory, List<String> vfiles, List<String> flags, int jobs) {
        ExecUtils.ExecResult result = ExecUtils.coqcVioCheck(directory, vfiles, flags, jobs);
        if (result.code != 0) {
            safePrintln("ERROR: COQC VIO CHECK EXIT CODE " + result.code,
                        "ERROR: COQC VIO CHECK ERROR: " + result.stdErr);
        }
        return result.code;
    }

    public static List<Future<ExecResult>> execParallel(File directory, ExecutorService executor, List<List<String>> commands) throws InterruptedException {
        return executor.invokeAll(commands.stream().map(command -> new Exec(directory, command)).collect(Collectors.toList()));
    }

//    public static ExecResult exec(List<String> command) {
//        return exec(command, null);
//    }

    public static ExecResult exec(List<String> command, File directory) {
        return exec(command, directory, "60");
    }

    public static ExecResult exec(List<String> command, File directory, String timeout) {
        try {
            command.add(0, timeout);
            command.add(0, "timeout");

            // we do not output debug for sercomp
            //if (MCoq.debug && (command.contains("coqc") || command.contains("--mode=vo") || command.contains("--mode=check"))) {
            if (MCoq.debug){
                safePrintln("DEBUG: Executing command '" +String.join(" ",
                        command.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList())) + "'");
            }

            ProcessBuilder builder = new ProcessBuilder(command);
            if (directory != null) builder.directory(directory);
            Process process = builder.start();

            List<String> stdOutList = new ArrayList<>();
            List<String> stdErrList = new ArrayList<>();

            Thread stdOutThread = new Thread(new StreamToString(process.getInputStream(), stdOutList::add));
            Thread stdErrThread = new Thread(new StreamToString(process.getErrorStream(), stdErrList::add));

            stdOutThread.start();
            stdErrThread.start();

            int exitCode = process.waitFor();

            stdOutThread.join();
            stdErrThread.join();

            return new ExecResult(exitCode, stdOutList, stdErrList);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ExecResult(-1, Collections.emptyList(), Collections.emptyList());
    }

    public static ExecResult sercomp(File directory, String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(SERCOMP);
        command.addAll(flags);
        command.add(file);
        return exec(command, directory, "180");
    }

    public static ExecResult serload(File directory, String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(SERLOAD);
        command.addAll(flags);
        command.add(file);
        return exec(command, directory, "180");
    }

    public static ExecResult compser(File directory, String file, List<String> flags) {
        return exec(compserCommand(file, flags), directory, "180");
    }

    public static List<String> compserCommand(String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(SERCOMP);
        command.add("--input=sexp");
        command.addAll(flags);
        command.add(file);
        return command;
    }

    public static ExecResult coqc(File directory, String file, List<String> flags) {
        return exec(coqcCommand(file, flags), directory);
    }

    public static ExecResult coqcVioCheck(File directory, List<String> vfiles, List<String> flags, int jobs) {
        return exec(coqcVioCheckCommand(vfiles, flags, jobs), directory);
    }

    public static List<String> coqcCommand(String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(COQC);
        command.addAll(flags);
        command.add(file);
        return command;
    }

    public static List<String> coqcQuickCommand(String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(COQC);
        command.add("-quick");
        command.addAll(flags);
        command.add(file);
        return command;
    }

    public static List<String> coqcVioCheckCommand(List<String> vfiles, List<String> flags, int jobs) {
        List<String> command = new ArrayList<>();
        command.add(COQC);
        command.addAll(flags);
        command.add("-schedule-vio-checking");
        command.add(String.valueOf(jobs));
        vfiles.stream().map(vfile -> FileUtils.changeExtension(vfile, "v", "vio")).forEach(command::add);
        return command;
    }

    public static List<String> coqcDpdGraph(Iterable<String> modules, File directory, List<String> flags) throws IOException {
        Path vfilePath = Files.createTempFile("graph", ".v");
        Path dpdfilePath = Files.createTempFile("graph", ".dpd");
        final String joined = String.join(" ", modules);
        final String vfileContent = "Require dpdgraph.dpdgraph.\n" +
                                    "Require " + joined + ".\n" +
                                    "Set DependGraph File \"" + dpdfilePath.toString() + "\".\n" +
                                    "Print FileDependGraph " + joined +".\n";
        if (MCoq.debug) {
            safePrintln("DEBUG: VFile for DPD:",
                        vfileContent);
        }
        Files.write(vfilePath, vfileContent.getBytes("UTF-8"));
        ExecResult result = exec(coqcDpdGraphCommand(vfilePath.toString(), flags), directory);
        Files.deleteIfExists(vfilePath);
        List<String> allLines = null;
        if (result.code == 0) {
            allLines = Files.readAllLines(dpdfilePath);
        } else {
            safePrintln("ERROR: COQC DPDGRAPH EXIT CODE " + result.code,
                        "ERROR: COQC DPDGRAPH ERROR: " + result.stdErr);
        }
        Files.deleteIfExists(dpdfilePath);
        return allLines;
    }

    public static List<String> coqcDpdGraphCommand(String file, List<String> flags) {
        List<String> command = new ArrayList<>();
        command.add(COQC);
        command.addAll(flags);
        command.add(file);
        return command;
    }


    public static void checkSercompAndCompserOnProjectFiles(File project, List<String> flags) throws IOException, SexpParserException {
        // get the dependencies of the project
        List<String> sortedFiles = getDependenciesForProject(project);

        List<String> includeDirs = getCoqProjectFile(project).getIncludeDirs();

        List<String> sercompOptions = new LinkedList<>(flags);
        sercompOptions.add(0, "--mode=sexp");
        addIncludeDirFlags(sercompOptions, includeDirs);
        List<String> compserOptions = new LinkedList<>(flags);
        compserOptions.add(0, "--mode=vo");
        addIncludeDirFlags(compserOptions, includeDirs);

        // go through all the files and call sercomp and compser
        long startTimeMillis = System.currentTimeMillis();
        for (String vfile : sortedFiles) {
            safePrintln("Calling sercomp on: " + vfile);
            // get s-expressions for the .v file
            List<String> linesSexpList = ExecUtils.runSercompAndCheck(project, Paths.get(vfile).toString(), sercompOptions);

            Path sexpFile = Paths.get(vfile.replaceAll("\\.v$", ".sexp"));
            Files.write(sexpFile, linesSexpList);

            if (MCoq.debug) {
                safePrintln("DEBUG: Last before calling compser",
                            SexpUtils.getDebugInfo(SexpFactory.parse(linesSexpList.get(linesSexpList.size() - 1))));
            }
            // run compser on the created file
            safePrintln("Calling compser on: " + sexpFile.toString());
            ExecUtils.runCompserAndCheck(project, sexpFile.toString(), compserOptions);
        }
        long endTimeMillis = System.currentTimeMillis();
        long elapsed = endTimeMillis - startTimeMillis;
        safePrintln("Number of files per project: " + sortedFiles.size(),
                    "Time elapsed (in ms): " + elapsed);
    }

    public static void checkSercompAndSerloadOnProjectFiles(File project, List<String> flags) throws IOException, SexpParserException {
        // get the dependencies of the project
        List<String> sortedFiles = getDependenciesForProject(project);

        List<String> includeDirs = getCoqProjectFile(project).getIncludeDirs();

        List<String> sercompOptions = new LinkedList<>(flags);
        sercompOptions.add(0, "--mode=trace");
        addIncludeDirFlags(sercompOptions, includeDirs);
        List<String> serloadOptions = new LinkedList<>(flags);
        addIncludeDirFlags(serloadOptions, includeDirs);

        // go through all the files and call sercomp and compser
        long startTimeMillis = System.currentTimeMillis();
        for (String vfile : sortedFiles) {
            safePrintln("Calling sercomp on: " + vfile);
            // get s-expressions for the .v file
            List<String> linesTraceList = ExecUtils.runSercompAndCheck(project, Paths.get(vfile).toString(), sercompOptions);

            Path traceFile = Paths.get(vfile.replaceAll("\\.v$", ".ktrace"));
            Files.write(traceFile, linesTraceList);

            // run serload on the created file
            safePrintln("Calling serload on: " + traceFile.toString());
            ExecUtils.runSerloadAndCheck(project, traceFile.toString(), serloadOptions);
        }
        long endTimeMillis = System.currentTimeMillis();
        long elapsed = endTimeMillis - startTimeMillis;
        safePrintln("Number of files per project: " + sortedFiles.size(),
                    "Time elapsed (in ms): " + elapsed);
    }

    public static class ExecResult {
        public int code;
        public List<String> stdOut;
        public List<String> stdErr;

        public ExecResult(int code, List<String> stdOut, List<String> stdErr) {
            this.code = code;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
        }
    }

    public static class Exec implements Callable<ExecResult> {

        private final List<String> command;
        private final File directory;

        public Exec(File directory, List<String> command) {
            this.command = command;
            this.directory = directory;
        }

        @Override
        public ExecResult call() {
            return exec(command, directory);
        }
    }

    public static String getKilledOrTimeoutInfo(String vfile, Mutation mutation, boolean killed, boolean timeout) {
        return "DEBUG: Results for " + mutation.getClass().getSimpleName() + " for thread "+Thread.currentThread().getId()+ " in " + vfile +
               " at LINEB=" + MutationLocation.LINEB + " LINEE=" + MutationLocation.LINEE +
               ": KILLED=" + killed + " TIMEOUT=" + timeout;
    }

    public static void addIncludeDirFlags(List<String> flags, List<String> includeDirs) {
        for (String includeDir : includeDirs) {
            flags.add("-I");
            flags.add(includeDir);
        }
    }

}
