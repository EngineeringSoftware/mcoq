package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

/**
 * Replaces "S x" with "0".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceSuccessorWithZero extends MutateSuccessorFunction {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.set(ONE, SexpUtils.newNumeralSexp("0"));
        }
    }
}
