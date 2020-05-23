package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class MutateMatchCases implements Mutation {

    @Override
    public boolean canMutate(Sexp sexp) {
        if (sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == FIVE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CCases)
                && sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).toString().equals(SEXP_RegularStyle)
                && !sexp.get(ONE).get(FOUR).isAtomic()
                && sexp.get(ONE).get(FOUR).getLength() >= TWO) {
            return true;
        }
        return false;
    }
}
