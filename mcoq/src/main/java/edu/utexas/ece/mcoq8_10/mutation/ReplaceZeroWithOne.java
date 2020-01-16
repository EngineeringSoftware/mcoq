package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;

/**
 * Replaces "0" with "1".
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceZeroWithOne implements Mutation {
    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.get(ZERO).get(ONE).get(ONE).get(TWO).get(ZERO).set(ONE, SexpFactory.newAtomicSexp("1"));
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        if (sexp.isAtomic() || sexp.getLength() < ONE) {
            return false;
        }
        sexp = sexp.get(ZERO);
        if (!sexp.isAtomic() && sexp.getLength() == TWO
                && sexp.get(ZERO).isAtomic()
                && sexp.get(ZERO).toString().equals(SEXP_v)
                && !sexp.get(ONE).isAtomic()
                && sexp.get(ONE).getLength() == TWO
                && sexp.get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ZERO).toString().equals(SEXP_CPrim)
                && !sexp.get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).getLength() == THREE
                && sexp.get(ONE).get(ONE).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ONE).get(ZERO).toString().equals(SEXP_Numeral)
                && sexp.get(ONE).get(ONE).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).get(ONE).toString().equals(SEXP_SPlus)
                && !sexp.get(ONE).get(ONE).get(TWO).isAtomic()
                && sexp.get(ONE).get(ONE).get(TWO).getLength() == THREE
                && !sexp.get(ONE).get(ONE).get(TWO).get(ZERO).isAtomic()
                && sexp.get(ONE).get(ONE).get(TWO).get(ZERO).getLength() == TWO
                && sexp.get(ONE).get(ONE).get(TWO).get(ZERO).get(ZERO).toString().equals(SEXP_int)
                && sexp.get(ONE).get(ONE).get(TWO).get(ZERO).get(ONE).toString().equals("0")
                && !sexp.get(ONE).get(ONE).get(TWO).get(ONE).isAtomic()
                && sexp.get(ONE).get(ONE).get(TWO).get(ONE).getLength() == TWO
                && sexp.get(ONE).get(ONE).get(TWO).get(ONE).get(ZERO).toString().equals(SEXP_frac)
                && sexp.get(ONE).get(ONE).get(TWO).get(ONE).get(ONE).toString().equals("\"\"")
                && !sexp.get(ONE).get(ONE).get(TWO).get(TWO).isAtomic()
                && sexp.get(ONE).get(ONE).get(TWO).get(TWO).getLength() == TWO
                && sexp.get(ONE).get(ONE).get(TWO).get(TWO).get(ZERO).toString().equals(SEXP_exp)
                && sexp.get(ONE).get(ONE).get(TWO).get(TWO).get(ONE).toString().equals("\"\"")) {
            return true;
        }
        return false;
    }
}
