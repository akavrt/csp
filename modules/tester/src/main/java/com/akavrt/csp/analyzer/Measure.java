package com.akavrt.csp.analyzer;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;

import java.util.List;

/**
 * User: akavrt
 * Date: 23.03.13
 * Time: 00:43
 */
public interface Measure {
    // average, variance, etc.
    String name();
    double calculate(List<Solution> solutions, Metric metric);
    double calculate(List<Long> times);
}
