package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;

/**
 * Replaces "l1 ++ l2" with "l2" OR "app l1 l2" with "l2".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class LeftEmptyConcat extends MutateConcat {

    private Sexp expToMutate(Sexp sexp) {
        return sexp.get(ONE).get(TWO).get(ZERO).get(ZERO).get(ZERO);
    }

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            expToMutate(sexp).set(ONE, SexpUtils.newEmptyListSexp8_9());
        }
    }
}
