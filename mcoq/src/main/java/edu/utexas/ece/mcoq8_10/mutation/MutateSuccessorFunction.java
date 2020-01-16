package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;

/**
 * Superclass for mutations over successor function.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public abstract class MutateSuccessorFunction implements Mutation {
    @Override
    public boolean canMutate(Sexp sexp) {
        return SexpUtils.isCAppSexp8_9(sexp, "S");
    }
}
