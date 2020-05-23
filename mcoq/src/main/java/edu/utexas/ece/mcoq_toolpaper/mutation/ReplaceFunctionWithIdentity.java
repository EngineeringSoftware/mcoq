package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

/**
 * Replaces functions from the set {"pred", "succ", "square", "sqrt", "div2", "log2", "double"} with identity function "id".
 * This should reflect the deletion of the statement in imperative language.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceFunctionWithIdentity implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.get(ONE).get(ONE).get(ONE).get(ZERO).get(ONE).get(ONE).get(ZERO).get(ONE).get(TWO).set(ONE, SexpFactory.newAtomicSexp("id"));
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        for (String functionName : Mutation.NAT_UNARY_FUNCTIONS) {
            if (SexpUtils.isCAppSexp(sexp, functionName)) {
                return true;
            }
        }
        return false;
    }
}
