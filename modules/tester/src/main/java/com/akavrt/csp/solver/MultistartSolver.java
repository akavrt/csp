package com.akavrt.csp.solver;

import com.akavrt.csp.analyzer.Collector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.tester.TraceUtils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class MultistartSolver extends SimpleSolver {
    private static final int DEFAULT_NUMBER_OF_RUNS = 1;
    private int numberOfRuns = DEFAULT_NUMBER_OF_RUNS;
    private List<Collector> collectors;

    public MultistartSolver(Problem problem, Algorithm algorithm, int numberOfRuns) {
        super(problem, algorithm);
        this.numberOfRuns = numberOfRuns;
        collectors = Lists.newArrayList();
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public List<Collector> getCollectors() {
        return collectors;
    }

    public void addCollector(Collector collector) {
        collectors.add(collector);
    }

    public void clearCollectors() {
        collectors.clear();
    }

    @Override
    protected List<Solution> run() {
        List<Solution> solutions = Lists.newArrayList();

        for (Collector collector : collectors) {
            collector.clear();
        }

        ScalarMetric metric = new ScalarMetric(getProblem(), new ScalarMetricParameters());

        for (int i = 1; i <= numberOfRuns; i++) {
            System.out.println("*** run " + i);

            long start = System.currentTimeMillis();
            List<Solution> runResult = super.run();
            long end = System.currentTimeMillis();

            if (runResult.size() > 0 && runResult.get(0) != null) {
                Solution solution = runResult.get(0);
                String trace = TraceUtils.traceSolution(solution, getProblem(), metric, false);
                System.out.println("Best solution found in run:\n" + trace);

                for (Collector collector : collectors) {
                    collector.collect(solution, end - start);
                }
            }

            solutions.addAll(runResult);
        }

        return solutions;
    }

}
