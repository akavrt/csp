package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class MultistartSolver extends SimpleSolver {
    private static final Logger logger = LogManager.getLogger(MultistartSolver.class);
    private static final int DEFAULT_NUMBER_OF_RUNS = 1;
    private int numberOfRuns = DEFAULT_NUMBER_OF_RUNS;

    public MultistartSolver(Problem problem, Algorithm algorithm, int numberOfRuns) {
        super(problem, algorithm);
        this.numberOfRuns = numberOfRuns;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    @Override
    public List<Solution> solve() {
        if (problem == null || algorithm == null) {
            return null;
        }

        solutions = Lists.newArrayList();
        Metric metric = new ScalarMetric(problem, new ScalarMetricParameters());

        for (int i = 1; i <= numberOfRuns; i++) {
            logger.debug("*** run {}", i);
            List<Solution> runResult = run();
            System.out.println(String.format("* #%d * Best solution found: metric = %.3f; %s",
                                             i,
                                             metric.evaluate(runResult.get(0)),
                                             runResult.get(0).isValid(problem) ? "valid." : "invalid."));

            solutions.addAll(runResult);
        }

        return solutions;
    }
}
