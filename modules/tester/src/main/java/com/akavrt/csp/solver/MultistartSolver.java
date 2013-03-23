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
    private Collector collector;

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

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    @Override
    protected List<Solution> run() {
        List<Solution> solutions = Lists.newArrayList();

        if (collector != null) {
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

                if (collector != null) {
                    collector.collect(solution, end - start);
                }
            }

            solutions.addAll(runResult);
        }

        if (collector != null) {
            collector.process();
        }


        return solutions;
    }

}
