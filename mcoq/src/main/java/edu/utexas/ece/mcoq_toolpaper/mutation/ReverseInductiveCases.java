package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpList;

/**
 * Reverses the order of the cases in Inductive definition.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReverseInductiveCases implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            SexpList inductiveList = (SexpList) sexp.get(FOUR).get(ONE);
            SexpList.reverse(inductiveList);
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        return !sexp.isAtomic() &&
                sexp.getLength() == FIVE &&
                sexp.get(THREE).isAtomic() &&
                sexp.get(THREE).toString().equals(SEXP_Inductive_kw) &&
                !sexp.get(FOUR).isAtomic() &&
                sexp.get(FOUR).getLength() == TWO &&
                sexp.get(FOUR).get(ZERO).isAtomic() &&
                sexp.get(FOUR).get(ZERO).toString().equals(SEXP_Constructors) &&
                !sexp.get(FOUR).get(ONE).isAtomic() &&
                sexp.get(FOUR).get(ONE).getLength() >= TWO;
    }
}
