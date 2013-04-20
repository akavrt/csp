package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.*;

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

    public DetailsMetricProvider(ScalarMetricParameters scalarParams) {
        trimMetric = new TrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric();
        scalarMetric = new ConstraintAwareMetric();
        comparativeMetric = new ScalarMetric(scalarParams);
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
