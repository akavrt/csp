package com.akavrt.csp.tester.tracer;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.PatternReductionMetric;
import com.akavrt.csp.metrics.ProductDeviationMetric;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.TrimLossMetric;

/**
 * User: akavrt
 * Date: 24.03.13
 * Time: 12:49
 */
public class ScalarTracer implements Tracer<Solution> {
    private ScalarMetric scalarMetric;
    private TrimLossMetric trimMetric;
    private PatternReductionMetric patternsMetric;
    private ProductDeviationMetric productMetric;

    public ScalarTracer(ScalarMetric metric) {
        this.scalarMetric = metric;

        trimMetric = new TrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String trace(Solution solution) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("  SCALAR:  %.3f", scalarMetric.evaluate(solution)));
        builder.append(String.format("\n      TL:  %.2f * %.4f",
                                     scalarMetric.getParameters().getTrimFactor(),
                                     trimMetric.evaluate(solution)));
        builder.append(String.format("\n      PR:  %.2f * %.4f  (%d unique of %d total)",
                                     scalarMetric.getParameters().getPatternsFactor(),
                                     patternsMetric.evaluate(solution),
                                     solution.getMetricProvider().getUniquePatternsCount(),
                                     solution.getMetricProvider().getActivePatternsCount()));
        builder.append(String.format("\n      PD:  %.2f * %.4f",
                                     scalarMetric.getParameters().getProductionFactor(),
                                     productMetric.evaluate(solution)));

        return builder.toString();
    }
}
