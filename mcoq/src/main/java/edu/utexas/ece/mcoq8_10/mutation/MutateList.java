package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Mutation operator for list constructors.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class MutateList extends ListMutation {

    @Override
    public boolean canMutate(Sexp sexp) {
        if (canMutateCNotation(sexp) || canMutateCApp(sexp)) {
            return true;
        }
        return false;
    }

    protected boolean canMutateCNotation(Sexp sexp) {
        return SexpUtils.isCNotationSexp(sexp, "\"_ :: _\"");
    }

    protected boolean canMutateCApp(Sexp sexp) {
        return canMutateCApp(sexp, "cons");
    }
}
