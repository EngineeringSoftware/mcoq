package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * Replaces
 * "if condition then exp1 else exp2"
 * with
 * "if condition then exp2 else exp1".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReorderIfBranches implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            Sexp thenBranch = sexp.get(ONE).get(THREE);
            Sexp elseBranch = sexp.get(ONE).get(FOUR);
            sexp.get(ONE).set(THREE, elseBranch);
            sexp.get(ONE).set(FOUR, thenBranch);
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == FIVE
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CIf)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == TWO
                && !sexp.get(ONE).get(TWO).isAtomic()
                && sexp.get(ONE).get(TWO).getLength() == TWO
                && !sexp.get(ONE).get(THREE).isAtomic()
                && sexp.get(ONE).get(THREE).getLength() == TWO
                && !sexp.get(ONE).get(FOUR).isAtomic()
                && sexp.get(ONE).get(FOUR).getLength() == TWO) {
            return true;
        }
        return false;
    }
}
