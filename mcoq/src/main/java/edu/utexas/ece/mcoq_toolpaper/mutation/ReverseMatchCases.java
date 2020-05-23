package edu.utexas.ece.mcoq_toolpaper.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpList;

/**
 * Reverses the order of the cases in the Match Cases expression.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReverseMatchCases extends MutateMatchCases {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            SexpList casesList = (SexpList) sexp.get(ONE).get(FOUR);
            SexpList.reverse(casesList);
        }
    }
}
