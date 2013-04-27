package com.akavrt.csp.metrics.complex;

import com.akavrt.csp.core.Plan;
import com.akavrt.csp.metrics.MinimizationMetric;
import com.akavrt.csp.metrics.simple.TrimLossMetric;

/**
 * <p>Implementation of the objective function used in a previous version. Based on a linear
 * scalarization when three partial objectives (minimization of trim loss, pattern reduction and
 * minimization of product deviation) are substituted with a single objective and a set of weights
 * used to express importance of each partial objective.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ScalarMetric extends MinimizationMetric {
    private final TrimLossMetric trimMetric;
    private final PatternReductionMetric patternsMetric;
    private final ProductDeviationMetric productMetric;
    private final ScalarMetricParameters params;

    /**
     * <p>Creates an instance of evaluator with default set of parameters. Can be reused to
     * evaluate different solutions against problem and objective function with specific parameters
     * provided during creation.</p>
     */
    public ScalarMetric() {
        this(new ScalarMetricParameters());
    }

    /**
     * <p>Creates an instance of evaluator. Can be reused to evaluate different solutions against
     * problem and objective function with specific parameters provided during creation.</p>
     *
     * @param params Parameters of objective function.
     */
    public ScalarMetric(ScalarMetricParameters params) {
        this.params = params;

        trimMetric = new TrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric();
    }

    /**
     * <p>Using simple objective function of the following form:</p>
     *
     * <p>Z = C1 * trimLossRatio + C2 * patternsRatio + C3 * productDeviationRation,</p>
     *
     * <p>where C1 + C2 + C3 = 1.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public double evaluate(Plan plan) {
        return params.getTrimFactor() * trimMetric.evaluate(plan) +
                params.getPatternsFactor() * patternsMetric.evaluate(plan) +
                params.getProductionFactor() * productMetric.evaluate(plan);
    }

    /**
     * <p>Parameter set provided to the scalar metric during instantiation.</p>
     *
     * @return Current set of parameters.
     */
    public ScalarMetricParameters getParameters() {
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "SCALAR";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Linearly scalarized objective";
    }

}
