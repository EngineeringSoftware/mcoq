package edu.utexas.ece.mcoq8_10.location;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq8_10.mutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq8_10.mutation.Mutation.ONE;
import static edu.utexas.ece.mcoq8_10.mutation.Mutation.SEXP_VALID_VERNAC_TYPES;
import static edu.utexas.ece.mcoq8_10.mutation.Mutation.TWO;
import static edu.utexas.ece.mcoq8_10.mutation.Mutation.ZERO;


/**
 * Abstraction of a location where a mutation can be done.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutationLocation {

    /** Root of the location */
    private Sexp root;

    public static int LINEB;
    public static int LINEE;

    public MutationLocation(Sexp root) {
        this.root = root;
    }

    public MutationLocation(){
        this.root = null;
    }

    public Sexp getRoot() {
        return root;
    }

    public void findAll(Sexp sExp, Mutation mutation, List<MutationLocation> accumulator) {
        if (mutation.canMutate(sExp)) {
            accumulator.add(new MutationLocation(sExp));
        }
        if (!sExp.isAtomic()) {
            for (Sexp subSexp : sExp) {
                findAll(subSexp, mutation, accumulator);
            }
        }
    }

    public List<MutationLocation> findAll(Sexp sExp, Mutation mutation, boolean allVernacTypes) {
        List<MutationLocation> mutationLocations = new ArrayList<>();
        if (!sExp.get(ZERO).isAtomic()) {
            sExp = sExp.get(ZERO).get(ONE);
        }
        final Sexp subSExp = sExp.get(TWO);
        if (!subSExp.isAtomic()) {
            final String vernacType = subSExp.get(ZERO).toString();
            if (allVernacTypes || SEXP_VALID_VERNAC_TYPES.contains(vernacType)) {
                findAll(sExp, mutation, mutationLocations);
            }
        }
        return mutationLocations;
    }

    public MutationLocation find(File file, Mutation mutation, int index, List<Sexp> allSexps, boolean all) throws IOException {
        return find(Files.readAllLines(file.toPath()), mutation, index, allSexps, all);
    }

    public MutationLocation find(List<String> lines, Mutation mutation, int index, List<Sexp> allSexps, boolean all) {
        return lines.stream().map(line -> {
            try {
                Sexp sexp = SexpFactory.parse(line);
                allSexps.add(sexp);
                return sexp;
            } catch (SexpParserException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).flatMap(sexp -> findAll(sexp, mutation, all).stream()).collect(Collectors.toList()).get(index);
    }

    public int count(File file, Mutation mutation, boolean all) throws IOException {
        return count(Files.readAllLines(file.toPath()), mutation, all);

    }

    public int count(List<String> lines, Mutation mutation, boolean all) {
        return lines.stream().map(line -> {
            try {
                return SexpFactory.parse(line);
            } catch (SexpParserException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).map(sexp -> findAll(sexp, mutation, all).size()).reduce(0, Integer::sum);
    }
}
