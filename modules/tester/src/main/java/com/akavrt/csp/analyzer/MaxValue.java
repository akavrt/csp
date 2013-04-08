package com.akavrt.csp.analyzer;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;

import java.util.List;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 19:09
 */
public class MaxValue implements Measure {
    @Override
    public String name() {
        return "maximum";
    }

    @Override
    public double calculate(List<Solution> solutions, Metric metric) {
        if (solutions == null || solutions.size() == 0) {
            return 0;
        }

        double maximum = 0;
        int i = 0;
        for (Solution solution : solutions) {
            double value = metric.evaluate(solution);
            if (i == 0 || value > maximum) {
                maximum = value;
            }

            i++;
        }

        return maximum;
    }

    @Override
    public double calculate(List<Long> times) {
        if (times == null || times.size() == 0) {
            return 0;
        }

        double maximum = 0;
        int i = 0;
        for (Long time : times) {
            if (i == 0 || time > maximum) {
                maximum = time;
            }

            i++;
        }

        return maximum;
    }
}

