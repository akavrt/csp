package com.akavrt.csp.analyzer;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * User: akavrt
 * Date: 23.03.13
 * Time: 01:17
 */
public class SimpleCollector implements Collector {
    private static final Logger LOGGER = LogManager.getFormatterLogger(SimpleCollector.class);

    protected final List<Metric> metrics;
    protected final List<Measure> measures;
    protected final List<Solution> solutions;
    protected final List<Long> executionTimeInMillis;

    public SimpleCollector() {
        metrics = Lists.newArrayList();
        measures = Lists.newArrayList();
        solutions = Lists.newArrayList();
        executionTimeInMillis = Lists.newArrayList();
    }

    public void addMetric(Metric metric) {
        metrics.add(metric);
    }

    public void addMeasure(Measure measure) {
        measures.add(measure);
    }

    @Override
    public void collect(Solution solution) {
        solutions.add(solution);
        executionTimeInMillis.add(0L);
    }

    @Override
    public void collect(Solution solution, long millis) {
        solutions.add(solution);
        executionTimeInMillis.add(millis);
    }

    @Override
    public void clear() {
        solutions.clear();
        executionTimeInMillis.clear();
    }

    @Override
    public void process() {
        if (solutions.size() == 0) {
            return;
        }

        // process solution-specific metrics
        for (Metric metric : metrics) {
            LOGGER.info("%s:", metric.name());

            for (Measure measure : measures) {
                double value = measure.calculate(solutions, metric);

                LOGGER.info("  %s = %.4f", measure.name(), value);
            }
        }

        // process time
        LOGGER.info("Execution time in milliseconds:");
        for (Measure measure : measures) {
            double value = measure.calculate(executionTimeInMillis);

            LOGGER.info("  %s = %.4f", measure.name(), value);
        }
    }

}
