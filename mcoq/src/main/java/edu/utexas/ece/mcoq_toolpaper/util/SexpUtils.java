package edu.utexas.ece.mcoq_toolpaper.util;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.MCoq;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.Mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.FIVE;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.FOUR;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.ONE;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEVEN;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_CApp;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_CNotation;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_CPrim;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_CRef;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_DirPath;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_Id;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_Ident;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_InConstrEntrySomeLevel;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_InFile;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_Numeral;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_Qualid;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_SPlus;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_Ser_Qualid;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_VernacExpr;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_VernacRequire;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_emptyString;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_exp;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_false;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_fname;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_frac;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_int;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_true;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SEXP_v;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.SIX;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.THREE;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.TWO;
import static edu.utexas.ece.mcoq_toolpaper.mutation.Mutation.ZERO;

import static edu.utexas.ece.mcoq_toolpaper.util.PrintUtils.safePrintln;

/**
 * Util methods to manipulate s-expressions.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class SexpUtils {

    /**
     * Checks whether sexp is CNotation for the given operation.
     * @param sexp
     * @param operation
     * @return
     */
    public static boolean isCNotationSexp(Sexp sexp, String operation) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == THREE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CNotation)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == TWO
                && sexp.get(ONE).get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ONE).get(ZERO).toString().equals(SEXP_InConstrEntrySomeLevel)
                && sexp.get(ONE).get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).toString().equals(operation)
                && !sexp.get(ONE).get(TWO).isAtomic()
                && sexp.get(ONE).get(TWO).getLength() == FOUR) {
            return true;
        }
        return false;
    }

    public static Sexp stripLocs(Sexp root) {
        Stack<Sexp> toVisit = new Stack<>();
        toVisit.push(root);
        while (!toVisit.isEmpty()) {
            Sexp current = toVisit.pop();
            // strip loc info
            if (!current.isAtomic() && current.getLength() == 2 && current.get(0).isAtomic() && current.get(0).toString().equals("loc")) {
                current.set(1, SexpFactory.newNonAtomicSexp());
                continue;
            }
            if (!current.isAtomic()) {
                Iterator<Sexp> children = current.iterator();
                while (children.hasNext()) {
                    toVisit.push(children.next());
                }
            }
        }
        return root;
    }

    public static String getDebugInfo(Sexp sexp) {
        String result = null;
        Sexp mainLoc = findAnyLocMain(sexp);
        if (mainLoc != null) {
            List<String> locInfo = extractInfoFromLoc(mainLoc);
            if (locInfo.size() == 3) {
                MutationLocation.LINEB = Integer.parseInt(locInfo.get(1));
                MutationLocation.LINEE = Integer.parseInt(locInfo.get(2));
                result = locInfo.get(0) + " at LINEB=" + locInfo.get(1) + " LINEE=" + locInfo.get(2);
            } else {
                result = "";
            }
        }
        return result;
    }

    public static boolean mutateLines(List<String> lines, Mutation mutation, int index, Consumer<Sexp> sexpConsumer, boolean all) throws IOException, SexpParserException {
        List<Sexp> allLines = new ArrayList<>();
        MutationLocation loc = new MutationLocation();
        Sexp sexp = loc.find(lines, mutation, index, allLines, all).getRoot();
        if (MCoq.debug) {
            safePrintln("DEBUG: ----",
                        "DEBUG: Applied " + mutation.getClass().getSimpleName() + " for thread "+Thread.currentThread().getId()+ " in " + getDebugInfo(sexp));
        }
        if (MCoq.skipeq) {
            final String before = stripLocs(SexpFactory.clone(sexp)).toString();
            //if (MCoq.debug) {
            //    Path beforePath = File.createTempFile("before", ".sexp").toPath();
            //    System.out.println("Wrote before to file " + beforePath);
            //    Files.write(beforePath, before.getBytes("UTF-8"));
            //}
            mutation.mutate(sexp);
            final String after = stripLocs(SexpFactory.clone(sexp)).toString();
            //if (MCoq.debug) {
            //    Path afterPath = File.createTempFile("after", ".sexp").toPath();
            //    System.out.println("Wrote after to file " + afterPath);
            //    Files.write(afterPath, after.getBytes("UTF-8"));
            //}
            if (after.equals(before)) {
                return false;
            }
        } else {
            mutation.mutate(sexp);
        }
        for (Sexp eachSexp : allLines) {
            sexpConsumer.accept(eachSexp);
        }
        return true;
    }

    public static boolean mutateLines(File file, Mutation mutation, int index, Consumer<Sexp> sexpConsumer, boolean all) throws IOException, SexpParserException {
        return mutateLines(Files.readAllLines(file.toPath()), mutation, index, sexpConsumer, all);
    }

    public static List<Sexp> getSexpList(Path resourcePath) throws IOException {
        return Files.readAllLines(resourcePath).stream().map(str -> {
            try {
                return SexpFactory.parse(str);
            } catch (SexpParserException e) {
                e.printStackTrace();
            }
            return SexpFactory.newNonAtomicSexp();
        }).collect(Collectors.toList());
    }


    /**
     * Look for any main part of loc in the given s-expression traversing the tree and return the first that is found.
     *
     * @param sexp sexpression to traverse
     * @return any main part of loc from the given sexp or null if there is no main part of loc in sexp
     */
    public static Sexp findAnyLocMain(Sexp sexp) {
        if (sexp == null) {
            return null;
        }
        List<Sexp> sexpList = new LinkedList<>();
        sexpList.add(sexp);
        while (!sexpList.isEmpty()) {
            Sexp currentSexp = ((LinkedList<Sexp>) sexpList).removeFirst();
            if (currentSexp.isAtomic() || (!currentSexp.isAtomic() && currentSexp.getLength() == 0)) {
                continue;
            }
            if (isMainPartOfLocSexp(currentSexp)) {
                return currentSexp;
            }
            for (int i = 0; i < currentSexp.getLength(); i++) {
                sexpList.add(currentSexp.get(i));
            }
        }
        return null;
    }

    /**
     * Check if s-expression is main part of loc s-expression that contains file name and other information.
     *
     * @param sexp s-expression
     * @return true if it is main part of loc, false otherwise
     */
    public static boolean isMainPartOfLocSexp(Sexp sexp) {
        return !sexp.isAtomic()
                && sexp.getLength() == SEVEN
                && !sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).getLength() == TWO
                && sexp.get(ZERO).get(ZERO).isAtomic()
                && sexp.get(ZERO).get(ZERO).toString().equals(SEXP_fname)
                && !sexp.get(ZERO).get(ONE).isAtomic()
                && sexp.get(ZERO).get(ONE).getLength() == TWO
                && sexp.get(ZERO).get(ONE).get(ZERO).isAtomic()
                && sexp.get(ZERO).get(ONE).get(ZERO).toString().equals(SEXP_InFile)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == TWO
                && !sexp.get(TWO).isAtomic()
                && sexp.get(TWO).getLength() == TWO
                && !sexp.get(THREE).isAtomic()
                && sexp.get(THREE).getLength() == TWO
                && !sexp.get(FOUR).isAtomic()
                && sexp.get(FOUR).getLength() == TWO
                && !sexp.get(FIVE).isAtomic()
                && sexp.get(FIVE).getLength() == TWO
                && !sexp.get(SIX).isAtomic()
                && sexp.get(SIX).getLength() == TWO;
    }

    /**
     * Extract filename, line number and last line number from the s-expression.
     *
     * @param sexp s-expression
     * @return list containing 3 elements in the order filename -> line num -> last line num
     * or empty list if sexp is not part of loc s-expression
     */
    public static List<String> extractInfoFromLoc(Sexp sexp) {
        List<String> infoList = new LinkedList<>();
        if (isMainPartOfLocSexp(sexp)) {
            // add filename
            infoList.add(sexp.get(ZERO).get(ONE).get(ONE).toString());
            // add line number
            infoList.add(sexp.get(ONE).get(ONE).toString());
            // add last line number
            infoList.add(sexp.get(THREE).get(ONE).toString());
        }
        return infoList;
    }

    /**
     * Creates s-expression representing given constant.
     * @param constant
     * @return s-expression format for the constant
     */

    public static Sexp newNumeralSexp(String constant) {
        Sexp zeroList = SexpFactory.newNonAtomicSexp();
        zeroList.add(SexpFactory.newAtomicSexp(SEXP_CPrim));

        Sexp secondChild = SexpFactory.newNonAtomicSexp();

        secondChild.add(SexpFactory.newAtomicSexp(SEXP_Numeral));
        secondChild.add(SexpFactory.newAtomicSexp(SEXP_SPlus));

        Sexp thirdChild = SexpFactory.newNonAtomicSexp();
        Sexp intChild = SexpFactory.newNonAtomicSexp();
        intChild.add(SexpFactory.newAtomicSexp(SEXP_int));
        intChild.add(SexpFactory.newAtomicSexp(constant));
        Sexp fracChild = SexpFactory.newNonAtomicSexp();
        fracChild.add(SexpFactory.newAtomicSexp(SEXP_frac));
        fracChild.add(SexpFactory.newAtomicSexp(SEXP_emptyString));
        Sexp expChild = SexpFactory.newNonAtomicSexp();
        expChild.add(SexpFactory.newAtomicSexp(SEXP_exp));
        expChild.add(SexpFactory.newAtomicSexp(SEXP_emptyString));
        thirdChild.add(intChild);
        thirdChild.add(fracChild);
        thirdChild.add(expChild);

        secondChild.add(thirdChild);

        zeroList.add(secondChild);

        return zeroList;
    }

    /**
     * Make s-expression for empty list in Coq8.9.
     *
     * @return s-expression that represents empty list
     */
    public static Sexp newEmptyListSexp8_9() {
        Sexp emptyList = SexpFactory.newNonAtomicSexp();
        emptyList.add(SexpFactory.newAtomicSexp(SEXP_CNotation));
        Sexp secondChild = SexpFactory.newNonAtomicSexp();
        secondChild.add(SexpFactory.newAtomicSexp(Mutation.SEXP_InConstrEntrySomeLevel));
        secondChild.add(SexpFactory.newAtomicSexp("\"[ ]\""));
        emptyList.add(secondChild);

        Sexp thirdChild = SexpFactory.newNonAtomicSexp();
        for (int i = 0; i < Mutation.FOUR; i++)
            thirdChild.add(SexpFactory.newNonAtomicSexp());
        emptyList.add(thirdChild);
        return emptyList;
    }

    /**
     * Make s-expression for empty list in Coq8.8.
     *
     * @return s-expression that represents empty list
     */
    public static Sexp newEmptyListSexp8_8() {
        Sexp emptyList = SexpFactory.newNonAtomicSexp();
        emptyList.add(SexpFactory.newAtomicSexp(SEXP_CNotation));
        emptyList.add(SexpFactory.newAtomicSexp("\"[ ]\""));

        Sexp thirdChild = SexpFactory.newNonAtomicSexp();
        for (int i = 0; i < Mutation.FOUR; i++)
            thirdChild.add(SexpFactory.newNonAtomicSexp());

        emptyList.add(thirdChild);
        return emptyList;
    }

    /**
     * Checks if sexp is CRef s-expression with given id
     * @param sexp
     * @param id
     * @return true if s-expression is CRef with id, false otherwise
     */
    public static boolean isCRefSexp(Sexp sexp, String id) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == THREE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CRef)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(TWO).isAtomic()
                && sexp.get(ONE).get(TWO).getLength() == ZERO
                && isSerQualidSexp(sexp.get(ONE).get(ONE).get(ZERO), id)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the s-expression is Ser_Qualid.
     *
     * @param sexp s-expression
     * @param id   id inside Ser_Qualid s-expression
     * @return true if s-expression is Ser_Qualid with id, false otherwise
     */
    public static boolean isSerQualidSexp(Sexp sexp, String id) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic() && sexp.get(ZERO).toString().equals(SEXP_v)) {
            sexp = sexp.get(ONE);
            if (!sexp.isAtomic() && sexp.getLength() == THREE) {
                if (sexp.get(ZERO).isAtomic() && sexp.get(ZERO).toString().equals(SEXP_Ser_Qualid)
                        && !sexp.get(ONE).isAtomic() && sexp.get(ONE).getLength() == TWO
                        && sexp.get(ONE).get(ZERO).isAtomic() && sexp.get(ONE).get(ZERO).toString().equals(SEXP_DirPath)
                        && !sexp.get(ONE).get(ONE).isAtomic() && sexp.get(ONE).get(ONE).getLength() == ZERO
                        && !sexp.get(TWO).isAtomic() && sexp.get(TWO).getLength() == TWO
                        && sexp.get(TWO).get(ZERO).isAtomic() && sexp.get(TWO).get(ZERO).toString().equals(SEXP_Id)
                        && sexp.get(TWO).get(ONE).isAtomic()) {
                    return sexp.get(TWO).get(ONE).toString().equals(id);
                }
            }
        }

        return false;
    }

    /**
     * Check if the given s-expression is application of the constructor with given name. Supports Coq 8.8 and Coq 8.9.
     *
     * @param sexp        an s-expression
     * @param constructor name of the constructor
     * @return true if sexp is application of the constructor
     */
    public static boolean isCAppSexp(Sexp sexp, String constructor) {
        return isCAppSexp8_8(sexp, constructor) || isCAppSexp8_9(sexp, constructor);
    }

    /**
     * Check if the given s-expression is application of the constructor with given name in the Coq8.9.
     *
     * @param sexp        an s-expression
     * @param constructor name of the constructor
     * @return true if sexp is application of the constructor
     */
    public static boolean isCAppSexp8_9(Sexp sexp, String constructor) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == THREE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CApp)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(ONE).get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).get(ZERO).getLength() == TWO) {
            Sexp cRefSexp = sexp.get(ONE).get(ONE).get(ONE).get(ZERO);
            if (cRefSexp.get(ZERO).isAtomic()
                    && cRefSexp.get(ZERO).toString().equals(SEXP_v)
                    && !cRefSexp.get(ONE).isAtomic()
                    && cRefSexp.get(ONE).getLength() == THREE
                    && cRefSexp.get(ONE).get(ZERO).isAtomic()
                    && cRefSexp.get(ONE).get(ZERO).toString().equals(SEXP_CRef)
                    && !cRefSexp.get(ONE).get(ONE).isAtomic()
                    && cRefSexp.get(ONE).get(ONE).getLength() == TWO
                    && !cRefSexp.get(ONE).get(ONE).get(ZERO).isAtomic()
                    && cRefSexp.get(ONE).get(ONE).get(ZERO).getLength() == TWO) {
                Sexp idSexp = cRefSexp.get(ONE).get(ONE).get(ZERO);
                return isSerQualidSexp(idSexp, constructor);
            }
        }
        return false;
    }

    /**
     * Check if the given s-expression is application of the constructor with given name in the Coq8.8.
     *
     * @param sexp        an s-expression
     * @param constructor name of the constructor
     * @return true if sexp is application of the constructor
     */
    public static boolean isCAppSexp8_8(Sexp sexp, String constructor) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == THREE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CApp)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(ONE).get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).get(ZERO).getLength() == TWO) {
            Sexp cRefSexp = sexp.get(ONE).get(ONE).get(ONE).get(ZERO);
            if (cRefSexp.get(ZERO).isAtomic()
                    && cRefSexp.get(ZERO).toString().equals(SEXP_v)
                    && !cRefSexp.get(ONE).isAtomic()
                    && cRefSexp.get(ONE).getLength() == THREE
                    && cRefSexp.get(ONE).get(ZERO).toString().equals(SEXP_CRef)
                    && !cRefSexp.get(ONE).get(ONE).isAtomic()
                    && cRefSexp.get(ONE).get(ONE).getLength() == TWO
                    && !cRefSexp.get(ONE).get(ONE).get(ZERO).isAtomic()
                    && cRefSexp.get(ONE).get(ONE).get(ZERO).getLength() == TWO) {
                Sexp identSexp = cRefSexp.get(ONE).get(ONE).get(ZERO);
                if (identSexp.get(ZERO).isAtomic()
                        && identSexp.get(ZERO).toString().equals(SEXP_v)
                        && !identSexp.get(ONE).isAtomic()
                        && identSexp.get(ONE).getLength() == TWO
                        && identSexp.get(ONE).get(ZERO).isAtomic()
                        && identSexp.get(ONE).get(ZERO).toString().equals(SEXP_Ident)
                        && !identSexp.get(ONE).get(ONE).isAtomic()
                        && identSexp.get(ONE).get(ONE).getLength() == TWO
                        && identSexp.get(ONE).get(ONE).get(ZERO).isAtomic()
                        && identSexp.get(ONE).get(ONE).get(ZERO).toString().equals(SEXP_Id)
                        && identSexp.get(ONE).get(ONE).get(ONE).isAtomic()
                        && identSexp.get(ONE).get(ONE).get(ONE).toString().equals(constructor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given s-expression is any import from the given namespace
     *
     * @param sexp      s-expression
     * @param namespace namespace
     * @return true if sexp is import from the namespace
     */
    public static boolean isImportSexp(Sexp sexp, String namespace) {
        return isImportQualidSexp(sexp, namespace) || isVernacRequireSexp(sexp, namespace) || isImportSerQualidSexp(sexp, namespace);
    }

    /**
     * Checks if the given s-expression is Require Import from the given namespace
     *
     * @param sexp      s-expression
     * @param namespace namespace
     * @return true if sexp is Require Import from the namespace
     */
    public static boolean isVernacRequireSexp(Sexp sexp, String namespace) {
        if (sexp.isAtomic() || sexp.getLength() != THREE) {
            return false;
        }
        if (sexp.get(ZERO).isAtomic() && sexp.get(ZERO).toString().equals(SEXP_VernacExpr)
                && !sexp.get(ONE).isAtomic() && sexp.get(ONE).getLength() == ZERO
                && !sexp.get(TWO).isAtomic() && sexp.get(TWO).getLength() == FOUR
                && sexp.get(TWO).get(ZERO).isAtomic() && sexp.get(TWO).get(ZERO).toString().equals(SEXP_VernacRequire)
                && !sexp.get(TWO).get(ONE).isAtomic() && sexp.get(TWO).get(ONE).getLength() == ONE
                && !sexp.get(TWO).get(ONE).get(ZERO).isAtomic() && sexp.get(TWO).get(ONE).get(ZERO).getLength() == TWO
                && !sexp.get(TWO).get(TWO).isAtomic() && sexp.get(TWO).get(TWO).getLength() == ONE
                && sexp.get(TWO).get(TWO).get(ZERO).isAtomic() && sexp.get(TWO).get(TWO).get(ZERO).toString().equals(SEXP_false)) {
            Sexp identSexp = sexp.get(TWO).get(ONE).get(ZERO).get(ZERO);
            if (!identSexp.isAtomic() && identSexp.getLength() == TWO
                    && identSexp.get(ZERO).isAtomic() && identSexp.get(ZERO).toString().equals(SEXP_v)
                    && !identSexp.get(ONE).isAtomic() && identSexp.get(ONE).getLength() == TWO
                    && identSexp.get(ONE).get(ZERO).isAtomic() && identSexp.get(ONE).get(ZERO).toString().equals(SEXP_Ident)
                    && !identSexp.get(ONE).get(ONE).isAtomic() && identSexp.get(ONE).get(ONE).getLength() == TWO
                    && identSexp.get(ONE).get(ONE).get(ZERO).isAtomic() && identSexp.get(ONE).get(ONE).get(ZERO).toString().equals(SEXP_Id)) {
                if (identSexp.get(ONE).get(ONE).get(ONE).isAtomic() && identSexp.get(ONE).get(ONE).get(ONE).toString().equals(namespace)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given s-expression is Require Import from the given namespace with Ser_Qualid
     *
     * @param sexp      s-expression
     * @param namespace namespace
     * @return true if sexp is Require Import with Ser_Qualid from the namespace
     */
    public static boolean isImportSerQualidSexp(Sexp sexp, String namespace) {
        if (sexp.isAtomic() || sexp.getLength() != THREE
                || !sexp.get(ZERO).isAtomic() || !sexp.get(ZERO).toString().equals(SEXP_VernacExpr)
                || sexp.get(TWO).isAtomic() || sexp.get(TWO).getLength() != FOUR) {
            return false;
        }
        sexp = sexp.get(TWO);
        if (!sexp.isAtomic() && sexp.getLength() == FOUR) {
            if (sexp.get(ZERO).isAtomic() && sexp.get(ZERO).toString().equals(SEXP_VernacRequire)
                    && !sexp.get(ONE).isAtomic() && sexp.get(ONE).getLength() == ONE
                    && !sexp.get(ONE).get(ZERO).isAtomic() && sexp.get(ONE).get(ZERO).getLength() == TWO
                    && !sexp.get(TWO).isAtomic() && sexp.get(TWO).getLength() == ONE
                    && sexp.get(TWO).get(ZERO).isAtomic() && sexp.get(TWO).get(ZERO).toString().equals(SEXP_false)
                    && !sexp.get(THREE).isAtomic() && sexp.get(THREE).getLength() == ONE
                    && !sexp.get(THREE).get(ZERO).isAtomic() && sexp.get(THREE).get(ZERO).getLength() == TWO) {
                //small tree with Ser_Qualid
                Sexp serQualidSexp = sexp.get(ONE).get(ZERO).get(ZERO);
                if (!serQualidSexp.isAtomic() && serQualidSexp.getLength() == TWO
                        && serQualidSexp.get(ZERO).isAtomic() && serQualidSexp.get(ZERO).toString().equals(SEXP_v)
                        && !serQualidSexp.get(ONE).isAtomic() && serQualidSexp.get(ONE).getLength() == THREE
                        && serQualidSexp.get(ONE).get(ZERO).isAtomic() && serQualidSexp.get(ONE).get(ZERO).toString().equals(SEXP_Ser_Qualid)
                        && !serQualidSexp.get(ONE).get(ONE).isAtomic() && serQualidSexp.get(ONE).get(ONE).getLength() == TWO
                        && !serQualidSexp.get(ONE).get(TWO).isAtomic() && serQualidSexp.get(ONE).get(TWO).getLength() == TWO) {
                    Sexp dirPathSexp = serQualidSexp.get(ONE).get(ONE);
                    Sexp idSexp = serQualidSexp.get(ONE).get(TWO);
                    if (dirPathSexp.get(ZERO).isAtomic() && dirPathSexp.get(ZERO).toString().equals(SEXP_DirPath)
                            && !dirPathSexp.get(ONE).isAtomic() && dirPathSexp.get(ONE).getLength() == ZERO
                            && idSexp.get(ZERO).isAtomic() && idSexp.get(ZERO).toString().equals(SEXP_Id)
                            && idSexp.get(ONE).isAtomic() && idSexp.get(ONE).toString().equals(namespace)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the sexp is import from the namespace
     *
     * @param sexp      s-expression
     * @param namespace namespace
     * @return true if s-expression is import from the namespace, false otherwise
     */
    public static boolean isImportQualidSexp(Sexp sexp, String namespace) {
        if (sexp.isAtomic() || sexp.getLength() != THREE
                || !sexp.get(ZERO).isAtomic() || !sexp.get(ZERO).toString().equals(SEXP_VernacExpr)
                || sexp.get(TWO).isAtomic() || sexp.get(TWO).getLength() != FOUR) {
            return false;
        }
        sexp = sexp.get(TWO);
        if (!sexp.isAtomic() && sexp.getLength() == FOUR) {
            if (sexp.get(ZERO).isAtomic() && sexp.get(ZERO).toString().equals(SEXP_VernacRequire)
                    && !sexp.get(ONE).isAtomic() && sexp.get(ONE).getLength() == ZERO
                    && !sexp.get(TWO).isAtomic() && sexp.get(TWO).getLength() == ONE
                    && sexp.get(TWO).get(ZERO).isAtomic() && sexp.get(TWO).get(ZERO).toString().equals(SEXP_false)
                    && !sexp.get(THREE).isAtomic() && sexp.get(THREE).getLength() == ONE
                    && !sexp.get(THREE).get(ZERO).isAtomic() && sexp.get(THREE).get(ZERO).getLength() == TWO) {
                Sexp qualidSexp = sexp.get(THREE).get(ZERO).get(ZERO);
                if (!qualidSexp.isAtomic() && qualidSexp.getLength() == TWO
                        && qualidSexp.get(ZERO).isAtomic() && qualidSexp.get(ZERO).toString().equals(SEXP_v)
                        && !qualidSexp.get(ONE).isAtomic() && qualidSexp.get(ONE).getLength() == TWO
                        && qualidSexp.get(ONE).get(ZERO).isAtomic() && qualidSexp.get(ONE).get(ZERO).toString().equals(SEXP_Qualid)
                        && !qualidSexp.get(ONE).get(ONE).isAtomic() && qualidSexp.get(ONE).get(ONE).getLength() == THREE) {
                    Sexp serQualidSexp = qualidSexp.get(ONE).get(ONE);
                    if (serQualidSexp.get(ZERO).isAtomic() && serQualidSexp.get(ZERO).toString().equals(SEXP_Ser_Qualid)
                            && !serQualidSexp.get(ONE).isAtomic() && serQualidSexp.get(ONE).getLength() == TWO
                            && serQualidSexp.get(ONE).get(ZERO).isAtomic() && serQualidSexp.get(ONE).get(ZERO).toString().equals(SEXP_DirPath)
                            && !serQualidSexp.get(ONE).get(ONE).isAtomic() && serQualidSexp.get(ONE).get(ONE).getLength() == ONE
                            && !serQualidSexp.get(ONE).get(ONE).get(ZERO).isAtomic() && serQualidSexp.get(ONE).get(ONE).get(ZERO).getLength() == TWO
                            && !serQualidSexp.get(TWO).isAtomic() && serQualidSexp.get(TWO).getLength() == TWO) {
                        Sexp projectSexp = serQualidSexp.get(ONE).get(ONE).get(ZERO);
                        Sexp fileSexp = serQualidSexp.get(TWO);
                        if (projectSexp.get(ZERO).isAtomic() && projectSexp.get(ZERO).toString().equals(SEXP_Id)
                                && projectSexp.get(ONE).isAtomic()
                                && fileSexp.get(ZERO).isAtomic() && fileSexp.get(ZERO).toString().equals(SEXP_Id)
                                && fileSexp.get(ONE).isAtomic()) {
                            return namespace == null || projectSexp.get(ONE).toString().equals(namespace);
                        }
                    }
                }
            }
        }
        return false;
    }
}
