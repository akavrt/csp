package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.*;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 13:58
 */
public class StandardMetricProvider implements SeriesMetricProvider {
    private final Metric trimMetric;
    private final Metric patternsMetric;
    private final Metric productMetric;
    private final Metric scalarMetric;
    private final Metric comparativeMetric;

    public StandardMetricProvider(ScalarMetricParameters scalarParams) {
        trimMetric = new TrimLossMetric();
        patternsMetric = new UniquePatternsMetric();
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
