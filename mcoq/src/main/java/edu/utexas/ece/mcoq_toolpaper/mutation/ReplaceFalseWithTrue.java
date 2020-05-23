package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

/**
 * Replaces "false" with "true".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceFalseWithTrue implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.get(ONE).get(ONE).get(ZERO).get(ONE).get(TWO).set(ONE, SexpFactory.newAtomicSexp(SEXP_true));
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        return SexpUtils.isCRefSexp(sexp, SEXP_false);
    }
}
