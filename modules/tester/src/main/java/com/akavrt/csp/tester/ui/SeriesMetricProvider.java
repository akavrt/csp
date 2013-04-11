package com.akavrt.csp.tester.ui;

import com.akavrt.csp.metrics.Metric;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 12:33
 */
public interface SeriesMetricProvider {
    Metric getTrimMetric();
    Metric getPatternsMetric();
    Metric getProductMetric();
    Metric getScalarMetric();
}
