package com.akavrt.csp.solver;

import com.akavrt.csp.core.Solution;

import java.util.List;

/**
 * <p>Solver provides basic infrastructure for running optimization methods and collecting obtained
 * results.</p>
 *
 * <p>Concrete implementations of this interface may provide facilities for calculation of useful
 * metrics, logging and storing any meaningful information about solutions or computations itself
 * to external destination.</p>
 *
 * <p>Also logic to allow non-blocking asynchronous usage with all heavy number crunching moved to
 * a separate thread may be implemented inside Solver.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Solver {
    /**
     * <p>Initialize internal structures and start computations.</p>
     */
    void solve();

    /**
     * <p>Return all collected solutions.</p>
     *
     * @return List of solutions collected during single execution of method solve().
     */
    List<Solution> getSolutions();
}
