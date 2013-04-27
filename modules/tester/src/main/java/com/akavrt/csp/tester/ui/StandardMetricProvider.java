package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.metrics.complex.ProductDeviationMetric;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.metrics.simple.AggregatedTrimLossMetric;
import com.akavrt.csp.metrics.simple.TrimLossMetric;
import com.akavrt.csp.metrics.simple.UniquePatternsMetric;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 13:58
 */
public class StandardMetricProvider implements SeriesMetricProvider {
    private final Metric sideTrimMetric;
    private final Metric totalTrimMetric;
    private final Metric patternsMetric;
    private final Metric productMetric;
    private final Metric objectiveMetric;
    private final Metric comparativeMetric;

    public StandardMetricProvider(ConstraintAwareMetricParameters objectiveParams) {
        sideTrimMetric = new TrimLossMetric();
        totalTrimMetric = new AggregatedTrimLossMetric();
        patternsMetric = new UniquePatternsMetric();
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
