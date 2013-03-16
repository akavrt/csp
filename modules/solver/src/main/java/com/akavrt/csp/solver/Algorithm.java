package com.akavrt.csp.solver;

import com.akavrt.csp.core.Solution;

import java.util.List;

/**
 * <p>Following to strategy pattern algorithm implementation was decoupled from execution context.
 * Thus we have both Algorithm and Solver interfaces, the former one provides common contract for
 * concrete algorithm implementations while the latter one acts as an execution context.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Algorithm {
    /**
     * <p>Standard or commonly accepted name of the optimization method.</p>
     *
     * @return Name of the optimization method.
     */
    String getName();

    /**
     * <p>The main instruction defined as simple as 'run the algorithm once and return the list of
     * solutions obtained'. If specific method produces single solution, there will be only one
     * item in the resulting list.</p>
     *
     * @param context Execution context used by the algorithm.
     * @return The list of the solutions obtained.
     */
    List<Solution> execute(ExecutionContext context);
}
