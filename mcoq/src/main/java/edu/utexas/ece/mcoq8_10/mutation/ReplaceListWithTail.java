package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * Replaces "head :: tail" with "tail" OR "cons head tail" with "tail".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceListWithTail extends MutateList {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutateCNotation(sexp)) {
            Sexp cRef = sexp.get(ONE).get(TWO).get(ZERO).get(ONE).get(ZERO).get(ONE);
            sexp.set(ONE, cRef);
        } else if (canMutateCApp(sexp)) {
            Sexp cRef = sexp.get(ONE).get(TWO).get(ZERO).get(ZERO).get(ZERO).get(ONE);
            sexp.set(ONE, cRef);
        }
    }
}