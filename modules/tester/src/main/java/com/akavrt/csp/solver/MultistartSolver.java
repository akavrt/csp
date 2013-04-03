package com.akavrt.csp.solver;

import com.akavrt.csp.analyzer.Collector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.tester.tracer.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class MultistartSolver extends SimpleSolver {
    private static final Logger LOGGER = LogManager.getLogger(MultistartSolver.class);
    private static final int DEFAULT_NUMBER_OF_RUNS = 1;
    private int numberOfRuns = DEFAULT_NUMBER_OF_RUNS;
    private List<Collector> collectors;

    public MultistartSolver(Algorithm algorithm, int numberOfRuns) {
        this(null, algorithm, numberOfRuns);
    }

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
            // global collectors collect values across different runs,
            // thus resetting of global collectors has to be managed externally
            if (!collector.isGlobal()) {
                collector.clear();
            }
        }

        ScalarMetric metric = new ScalarMetric(getProblem(), new ScalarMetricParameters());

        ScalarTracer tracer = new ScalarTracer(metric, getProblem());

        for (int i = 1; i <= numberOfRuns; i++) {
            LOGGER.info("*** run {}", i);

            long start = System.currentTimeMillis();
            List<Solution> runResult = super.run();
            long end = System.currentTimeMillis();

            if (runResult.size() > 0 && runResult.get(0) != null) {
                Solution solution = runResult.get(0);

                for (Collector collector : collectors) {
                    collector.collect(solution, end - start);
                }

                if (LOGGER.isInfoEnabled()) {
                    String trace = TraceUtils.traceSolution(solution, getProblem(), tracer, false);

                    LOGGER.info("Best solution found in run:");
                    LOGGER.info(trace);
                }
            }

            solutions.addAll(runResult);
        }

        return solutions;
    }

}
