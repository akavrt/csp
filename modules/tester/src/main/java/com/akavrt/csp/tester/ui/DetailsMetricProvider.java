package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.*;
import com.akavrt.csp.metrics.simple.AggregatedTrimLossMetric;
import com.akavrt.csp.metrics.simple.TrimLossMetric;

/**
 * User: akavrt
 * Date: 12.04.13
 * Time: 00:34
 */
public class DetailsMetricProvider implements SeriesMetricProvider {
    private final Metric sideTrimMetric;
    private final Metric totalTrimMetric;
    private final Metric patternsMetric;
    private final Metric productMetric;
    private final Metric objectiveMetric;
    private final Metric comparativeMetric;

    public DetailsMetricProvider(ConstraintAwareMetricParameters objectiveParams) {
        sideTrimMetric = new TrimLossMetric();
        totalTrimMetric = new AggregatedTrimLossMetric();
        patternsMetric = new PatternReductionMetric();
        productMetric = new ProductDeviationMetric();
        objectiveMetric = new ConstraintAwareMetric(objectiveParams);
        comparativeMetric = new ScalarMetric();
    }

    @Override
    public Metric getSideTrimMetric() {
        return sideTrimMetric;
    }

    @Override
    public Metric getTotalTrimMetric() {
        return totalTrimMetric;
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
    public Metric getObjectiveMetric() {
        return objectiveMetric;
    }

    @Override
    public Metric getComparativeMetric() {
        return comparativeMetric;
    }

}
