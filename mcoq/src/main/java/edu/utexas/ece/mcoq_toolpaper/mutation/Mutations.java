package edu.utexas.ece.mcoq_toolpaper.mutation;

/**
 * Constants assigned to mutation operators.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Mutations {
    REVERSE_INDUCTIVE_CASES,
    REVERSE_MATCH_CASES,
    REPLACE_LIST_WITH_TAIL,
    REPLACE_LIST_WITH_EMPTY_LIST,
    REPLACE_LIST_WITH_HEAD,
    REORDER_CONCAT_LISTS,
    LEFT_EMPTY_CONCAT_LISTS,
    RIGHT_EMPTY_CONCAT_LISTS,
    REPLACE_MATCH_EXPRESSION,
    REPLACE_FUNCTION_WITH_IDENTITY,
    REPLACE_PLUS_WITH_MINUS,
    REPLACE_ZERO_WITH_ONE,
    REORDER_IF_BRANCHES,
    REPLACE_SUCCESSOR_WITH_ZERO,
    REMOVE_SUCCESSOR_APPLICATION,
    REPLACE_FALSE_WITH_TRUE,
    REPLACE_TRUE_WITH_FALSE;
    //REPLACE_AND_WITH_OR,

    public static Mutation toMutation(Mutations mutation) {
        switch (mutation) {
            case REVERSE_INDUCTIVE_CASES:
                return new ReverseInductiveCases();
            case REVERSE_MATCH_CASES:
                return new ReverseMatchCases();
            case REPLACE_LIST_WITH_TAIL:
                return new ReplaceListWithTail();
            case REPLACE_LIST_WITH_EMPTY_LIST:
                return new ReplaceListWithEmptyList();
            case REPLACE_LIST_WITH_HEAD:
                return new ReplaceListWithHead();
            case REORDER_CONCAT_LISTS:
                return new ReorderConcat();
            case LEFT_EMPTY_CONCAT_LISTS:
                return new LeftEmptyConcat();
            case RIGHT_EMPTY_CONCAT_LISTS:
                return new RightEmptyConcat();
            case REPLACE_MATCH_EXPRESSION:
                return new ReplaceMatchExpression();
            case REPLACE_FUNCTION_WITH_IDENTITY:
                return new ReplaceFunctionWithIdentity();
            case REPLACE_PLUS_WITH_MINUS:
                return new ReplacePlusWithMinus();
            case REPLACE_ZERO_WITH_ONE:
                return new ReplaceZeroWithOne();
            case REORDER_IF_BRANCHES:
                return new ReorderIfBranches();
            case REPLACE_SUCCESSOR_WITH_ZERO:
                return new ReplaceSuccessorWithZero();
            case REMOVE_SUCCESSOR_APPLICATION:
                return new RemoveSuccessorApplication();
            case REPLACE_FALSE_WITH_TRUE:
                return new ReplaceFalseWithTrue();
            case REPLACE_TRUE_WITH_FALSE:
                return new ReplaceTrueWithFalse();
            //case REPLACE_AND_WITH_OR:
                 //return new ReplaceAndWithOr();
            default:
                throw new RuntimeException("Unknown mutation!");
        }
    }

    public static final Set<Mutations> BLACKLISTED_MUTATIONS = new HashSet<>(Arrays.asList(Mutations.REVERSE_MATCH_CASES, Mutations.REPLACE_FUNCTION_WITH_IDENTITY));
}
