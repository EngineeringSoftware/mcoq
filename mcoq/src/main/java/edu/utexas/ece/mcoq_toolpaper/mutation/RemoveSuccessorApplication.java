package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * .Replaces "S x" with "x".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class RemoveSuccessorApplication extends MutateSuccessorFunction {
    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            Sexp cRefArg = sexp.get(ONE).get(TWO).get(ZERO).get(ZERO).get(ZERO).get(ONE);
            sexp.set(ONE, cRefArg);
        }
    }
}