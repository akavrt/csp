package com.akavrt.csp.solver;

import com.akavrt.csp.analyzer.Collector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
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

        for (int i = 1; i <= numberOfRuns; i++) {
            LOGGER.info("*** run #{}", i);

            long start = System.currentTimeMillis();
            List<Solution> runResult = super.run();
            long end = System.currentTimeMillis();

            if (runResult.size() > 0 && runResult.get(0) != null) {
                Solution solution = runResult.get(0);

                for (Collector collector : collectors) {
                    collector.collect(solution, end - start);
                }
            }

            solutions.addAll(runResult);
        }

        return solutions;
    }

}
