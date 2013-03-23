package com.akavrt.csp.analyzer;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;

import java.util.List;

/**
 * User: akavrt
 * Date: 23.03.13
 * Time: 02:39
 */
public class Average implements Measure {
    @Override
    public String name() {
        return "average";
    }

    @Override
    public double calculate(List<Solution> solutions, Metric metric) {
        if (solutions == null || solutions.size() == 0) {
            return 0;
        }

        double result = 0;
        for (Solution solution : solutions) {
            result += metric.evaluate(solution);
        }

        return result / solutions.size();
    }

    @Override
    public double calculate(List<Long> times) {
        if (times == null || times.size() == 0) {
            return 0;
        }

        double result = 0;
        for (Long time : times) {
            result += time;
        }

        return result / times.size();
    }
}
