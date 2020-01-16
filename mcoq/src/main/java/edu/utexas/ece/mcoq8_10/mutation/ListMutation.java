package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Top of the hierarchy for all mutation operators over list.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class ListMutation implements Mutation {

    protected boolean canMutateCApp(Sexp sexp, String constructor) {
        return SexpUtils.isCAppSexp(sexp, constructor);
    }
}
