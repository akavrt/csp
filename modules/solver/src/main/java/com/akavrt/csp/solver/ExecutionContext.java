package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;

/**
 * <p>This interface defines context used by optimization routine to retrieve everything it may be
 * needed to start computations. These includes problem definition, parameters, etc.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface ExecutionContext {
    /**
     * <p>Consistent and precise definition of the cutting stock problem being solved.</p>
     *
     * @return Problem being solved.
     */
    Problem getProblem();

    /**
     * <p>Algorithms could be executed in both synchronous and asynchronous modes. Environment for
     * asynchronous execution and control logic is implemented by the solver.</p>
     *
     * <p>On the other side each algorithm should provide support for early termination. The
     * easiest way to achieve this is to constantly check the value returned by this method during
     * all potentially long-running operations (inside loops, for example) and stop execution
     * immediately on the first dispatch of the cancel request.</p>
     *
     * @return true if execution have to be terminated early.
     */
    boolean isCancelled();
}
