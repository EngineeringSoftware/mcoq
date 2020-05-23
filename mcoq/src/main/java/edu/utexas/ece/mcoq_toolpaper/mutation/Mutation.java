package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Mutation operator.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public interface Mutation {

    static final int ZERO = 0;
    static final int ONE = 1;
    static final int TWO = 2;
    static final int THREE = 3;
    static final int FOUR = 4;
    static final int FIVE = 5;
    static final int SIX = 6;
    static final int SEVEN = 7;

    static final String SEXP_v = "v";
    static final String SEXP_CNotation = "CNotation";
    static final String SEXP_CApp = "CApp";
    static final String SEXP_CRef = "CRef";
    static final String SEXP_Ident = "Ident";
    static final String SEXP_Id = "Id";
    static final String SEXP_CCases = "CCases";
    static final String SEXP_RegularStyle = "RegularStyle";
    static final String SEXP_Inductive_kw = "Inductive_kw";
    static final String SEXP_Constructors = "Constructors";
    static final String SEXP_VernacExpr = "VernacExpr";
    static final String SEXP_VernacRequire = "VernacRequire";
    static final String SEXP_false = "false";
    static final String SEXP_true = "true";
    static final String SEXP_Qualid = "Qualid";
    static final String SEXP_DirPath = "DirPath";
    static final String SEXP_Ser_Qualid = "Ser_Qualid";
    static final String SEXP_InConstrEntrySomeLevel = "InConstrEntrySomeLevel";
    static final String SEXP_fname = "fname";
    static final String SEXP_InFile = "InFile";
    static final String SEXP_CPrim = "CPrim";
    static final String SEXP_Numeral = "Numeral";
    static final String SEXP_CIf = "CIf";
    static final String SEXP_SPlus = "SPlus";
    static final String SEXP_int = "int";
    static final String SEXP_frac = "frac";
    static final String SEXP_exp = "exp";
    static final String SEXP_emptyString = "\"\"";

    static final Set<String> SEXP_VALID_VERNAC_TYPES = new HashSet<>(Arrays.asList("VernacFixpoint", "VernacInductive", "VernacDefinition"));
    static final Set<String> NAT_UNARY_FUNCTIONS = new HashSet<>(Arrays.asList("pred", "succ", "square", "sqrt", "div2", "log2", "double"));

    /**
     * Mutates the given sexpression.  We assume that a user has
     * already checked if mutation operator can be applied.
     */
    void mutate(Sexp sexp);

    /**
     * Check if the given expression can be mutated by this mutation
     * operator.
     */
    boolean canMutate(Sexp sexp);

}
