package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.pattern.PatternGenerator;

/**
 * <p>This interface defines context used by optimization routine to retrieve everything it may be
 * needed to start computations. These includes problem definition, parameters, configurable
 * building blocks such as external pattern generators or objective functions used by specific
 * method etc.</p>
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
     * <p>Some methods for solving CSP are based on pattern generation. To keep design more
     * flexible, optimization method doesn't have to create pattern generator by itself, instead it
     * can use the one provided by execution context.</p>
     *
     * @return Pattern generator configured and provided by the context.
     */
    PatternGenerator getPatternGenerator();

    /**
     * <p>As in case with pattern generator, optimization method can request metric with concrete
     * implementation of objective function from context.</p>
     *
     * @return Metric provided by the context.
     */
    Metric getMetric();
}
