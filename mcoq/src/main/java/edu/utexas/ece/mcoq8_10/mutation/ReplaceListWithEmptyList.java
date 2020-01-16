package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Replaces "head :: tail" with "[]" OR "cons head tail" with "[]".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceListWithEmptyList extends MutateList {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.set(ONE, SexpUtils.newEmptyListSexp8_9());
        }
    }
}
