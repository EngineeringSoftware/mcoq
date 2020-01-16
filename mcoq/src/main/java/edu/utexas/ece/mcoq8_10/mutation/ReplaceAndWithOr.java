package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Replaces operator "&&" with operator "||".
 *
 * @author Kush Jain <kjain14@utexas.edu>
 */
public class ReplaceAndWithOr implements Mutation {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            sexp.get(ONE).get(ONE).set(ONE, SexpFactory.newAtomicSexp("\"_ || _\""));
        }
    }

    @Override
    public boolean canMutate(Sexp sexp) {
        return SexpUtils.isCNotationSexp(sexp, "\"_ && _\"");
    }
}
