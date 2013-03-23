package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.utils.Tracer;

/**
 * <p>Implementation of the objective function used in a previous version. Based on a linear
 * scalarization when three partial objectives (minimization of trim loss, pattern reduction and
 * minimization of product deviation) are substituted with a single objective and a set of weights
 * used to express importance of each partial objective.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ScalarMetric extends MinimizationMetric implements Tracer<Solution> {
    private final TrimLossMetric trimMetric;
    private final PatternReductionMetric patternsMetric;
    private final ProductDeviationMetric productMetric;
    private final ScalarMetricParameters params;

    /**
     * <p>Creates an instance of evaluator. Can be reused to evaluate different solutions against
     * problem and objective function with specific parameters provided during creation.</p>
     *
     * @param problem The problem in question.
     * @param params  Parameters of objective function.
     */
    public ScalarMetric(Problem problem, ScalarMetricParameters params) {
        this.params = params;

        trimMetric = new TrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric(problem);
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
    public double evaluate(Solution solution) {
        return params.getTrimFactor() * trimMetric.evaluate(solution) +
                params.getPatternsFactor() * patternsMetric.evaluate(solution) +
                params.getProductionFactor() * productMetric.evaluate(solution);
    }

    /**
     * <p>Set of parameter provided to the scalar metric during instantiation.</p>
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String trace(Solution solution) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("  SCALAR:  %.3f", evaluate(solution)));
        builder.append(String.format("\n      TL:  %.2f * %.2f",
                                     params.getTrimFactor(), trimMetric.evaluate(solution)));
        builder.append(String.format("\n      PR:  %.2f * %.2f  (%d unique of %d total)",
                                     params.getPatternsFactor(), patternsMetric.evaluate(solution),
                                     solution.getUniquePatternsCount(),
                                     solution.getActivePatternsCount()));
        builder.append(String.format("\n      PD:  %.2f * %.2f",
                                     params.getProductionFactor(),
                                     productMetric.evaluate(solution)));

        return builder.toString();
    }

}
