package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class MultistartSolver extends SimpleSolver {
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

        for (int i = 1; i <= numberOfRuns; i++) {
            List<Solution> runResult = run();
            solutions.addAll(runResult);
        }

        return solutions;
    }
}
