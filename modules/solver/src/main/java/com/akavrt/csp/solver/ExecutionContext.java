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

}
