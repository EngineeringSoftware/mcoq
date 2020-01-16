package edu.utexas.ece.mcoq8_10.mutation;

import de.tudresden.inf.lat.jsexp.Sexp;

/**
 * Replaces
 * "match s with
 * | s1 => exp1
 * | s2 => exp2
 * | ...
 * | sn => expn" with
 * "match s with
 * | s1 => exp1
 * | s2 => exp1
 * | ...
 * | sn => expn"
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class ReplaceMatchExpression extends MutateMatchCases {

    @Override
    public void mutate(Sexp sexp) {
        if (canMutate(sexp)) {
            Sexp casesList = sexp.get(ONE).get(FOUR);
            Sexp caseExp1 = casesList.get(ZERO).get(ZERO).get(ONE).get(ONE).get(ZERO).get(ONE);
            casesList.get(ONE).get(ZERO).get(ONE).get(ONE).get(ZERO).set(ONE, caseExp1);
        }
    }
}
