package edu.utexas.ece.mcoq8_10.mutator;

import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq8_10.MCoq;
import edu.utexas.ece.mcoq8_10.mutation.Mutation;
import edu.utexas.ece.mcoq8_10.util.ExecUtils;
import edu.utexas.ece.mcoq8_10.util.IcoqUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static edu.utexas.ece.mcoq8_10.util.FileUtils.backupAll;
import static edu.utexas.ece.mcoq8_10.util.IcoqUtils.getDependenciesForProject;
import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class Mutator {

    protected File project;
    protected List<String> flags;
    protected boolean applyAll;
    protected long timeout;

    // counters
    protected int numberTotal;
    protected int killedTotal;
    protected int timeoutTotal;
    protected int equivalentTotal;
    protected long timeTotal;
    protected long sercompTotal;

    protected Map<String, List<String>> filenameToListOfSexp;
    protected String namespace;

    public Mutator(File project, List<String> flags, boolean applyAll, long timeout) throws IOException {
        this.project = project;
        this.flags = flags;
        this.applyAll = applyAll;
        this.timeout = timeout;
        this.numberTotal = 0;
        this.killedTotal = 0;
        this.timeoutTotal = 0;
        this.timeTotal = 0L;
        this.filenameToListOfSexp = new LinkedHashMap<>();
        this.namespace = IcoqUtils.getNamespace(project);

        // get the dependencies of the project
        List<String> sortedVFiles = getDependenciesForProject(project);
        sercompAll(sortedVFiles);
        backupAll(sortedVFiles);
    }

    public Mutator(File project, Mutator other) {
        this.project = project;
        this.flags = other.flags;
        this.applyAll = other.applyAll;
        this.timeout = other.timeout;
        // counters are zero
        this.filenameToListOfSexp = other.filenameToListOfSexp;
        this.namespace = other.namespace;
    }

    /**
     * Create sexp for each vfile.
     * @param vfiles vfiles should be in topological order
     * @throws IOException
     */
    private void sercompAll(List<String> vfiles) throws IOException {
        long start = System.currentTimeMillis();
        // Use sexp mode to generate
        List<String> sercompOptions = new LinkedList<>(flags);
        sercompOptions.add(0, "--mode=sexp");

        for (String vfile : vfiles) {
            if (MCoq.debug) {
                safePrintln("DEBUG: Generating sexp lines for " + vfile);
            }
            filenameToListOfSexp.put(vfile, ExecUtils.runSercompAndCheck(project, Paths.get(vfile).toString(), sercompOptions));
        }
        sercompTotal = System.currentTimeMillis() - start;
    }
    
    public abstract void runMutation(Mutation mutation) throws IOException, SexpParserException;

    public abstract void clean();

    public String getSummary() {
        return "Total number of mutants: " + numberTotal +
               "\nTotal number of killed mutants:" + killedTotal +
               "\nTotal number of timeouts: " + timeoutTotal +
               "\nTotal number of equivalent mutants: " + equivalentTotal +
               "\nTotal sercomp time: " + sercompTotal +
               "\nTotal running time: " + timeTotal;
    }

    public File getProject() {
        return project;
    }

    public List<String> getFlags() {
        return flags;
    }

    public boolean isApplyAll() {
        return applyAll;
    }

    public long getTimeout() {
        return timeout;
    }

    public Map<String, List<String>> getFilenameToListOfSexp() {
        return filenameToListOfSexp;
    }

    public String getNamespace() {
        return namespace;
    }
}
