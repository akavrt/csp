package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.*;
import com.akavrt.csp.metrics.complex.*;
import com.akavrt.csp.metrics.simple.TrimLossMetric;

/**
 * User: akavrt
 * Date: 12.04.13
 * Time: 00:34
 */
public class DetailsMetricProvider implements SeriesMetricProvider {
    private final Metric trimMetric;
    private final Metric patternsMetric;
    private final Metric productMetric;
    private final Metric scalarMetric;
    private final Metric comparativeMetric;

    public DetailsMetricProvider(ConstraintAwareMetricParameters objectiveParams) {
        trimMetric = new TrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric();
        scalarMetric = new ConstraintAwareMetric(objectiveParams);
        comparativeMetric = new ScalarMetric();
    }

    @Override
    public Metric getTrimMetric() {
        return trimMetric;
    }

    @Override
    public Metric getPatternsMetric() {
        return patternsMetric;
    }

    @Override
    public Metric getProductMetric() {
        return productMetric;
    }

    @Override
    public Metric getScalarMetric() {
        return scalarMetric;
    }

    @Override
    public Metric getComparativeMetric() {
        return comparativeMetric;
    }

}
