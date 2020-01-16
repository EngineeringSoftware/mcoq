package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Replaces "head :: tail" with "[head]" or "cons head tail" with "cons head []".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceListWithHead extends MutateList {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutateCNotation(sexp)) {
            Sexp headSexp = sexp.get(ONE).get(TWO).get(ZERO).get(ZERO);
            Sexp tmp = SexpFactory.newNonAtomicSexp();
            tmp.add(headSexp);
            sexp.get(ONE).get(TWO).set(ZERO, tmp);
            sexp.get(ONE).get(ONE).set(ONE, SexpFactory.newAtomicSexp("\"[ _ ]\""));
        } else if (canMutateCApp(sexp)) {
            sexp.get(ONE).get(TWO).get(ZERO).get(ZERO).get(ZERO).set(ONE, SexpUtils.newEmptyListSexp8_9());
        }
    }
}
