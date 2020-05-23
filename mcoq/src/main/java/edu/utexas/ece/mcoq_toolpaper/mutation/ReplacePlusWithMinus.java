package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

/**
 * Replaces operator "+" with operator "-".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplacePlusWithMinus implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.get(ONE).get(ONE).set(ONE, SexpFactory.newAtomicSexp("\"_ - _\""));
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        return SexpUtils.isCNotationSexp(sexp, "\"_ + _\"");
    }
}
