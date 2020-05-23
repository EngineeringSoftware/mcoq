package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * Replaces "l1 ++ l2" with "l2 ++ l1" OR "app l1 l2" with "app l2 l1".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReorderConcat extends MutateConcat {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutateCNotation(sexp)) {
            Sexp leftChild = sexp.get(ONE).get(TWO).get(ZERO).get(ZERO);
            Sexp rightChild = sexp.get(ONE).get(TWO).get(ZERO).get(ONE);
            sexp.get(ONE).get(TWO).get(ZERO).set(ZERO, rightChild);
            sexp.get(ONE).get(TWO).get(ZERO).set(ONE, leftChild);
        } else if (canMutateCApp(sexp)) {
            Sexp leftChild = sexp.get(ONE).get(TWO).get(ZERO);
            Sexp rightChild = sexp.get(ONE).get(TWO).get(ONE);
            sexp.get(ONE).get(TWO).set(ZERO, rightChild);
            sexp.get(ONE).get(TWO).set(ONE, leftChild);
        }
    }
}
