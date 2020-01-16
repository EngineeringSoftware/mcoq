package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Replaces "l1 ++ l2" with "l1" OR "app l1 l2" with "l1".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class RightEmptyConcat extends MutateConcat {

    private Sexp expToMutate(Sexp sexp) {
        return sexp.get(ONE).get(TWO).get(ZERO).get(ONE).get(ZERO);
    }

    @Override
    public void mutate(Sexp sexp) {
        if (canMutateCNotation(sexp)) {
            expToMutate(sexp).set(ONE, SexpUtils.newEmptyListSexp8_9());
        }
        else if(canMutateCApp(sexp)) {
            sexp.get(ONE).get(TWO).get(ONE).get(ZERO).get(ZERO).set(ONE, SexpUtils.newEmptyListSexp8_9());
        }
    }
}
