package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Strategy pattern was implemented to support different methods used to find approximate
 * solutions of the cutting stock problem.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Solver implements ExecutionContext {
    private Problem problem;
    private Algorithm algorithm;
    private List<Solution> solutions;

    public Solver() {
        solutions = Lists.newArrayList();
    }

    @Override
    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;

        // clear list with previously collected solutions
        solutions.clear();
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void solve() {
        if (problem == null || algorithm == null) {
            return;
        }

        List<Solution> result = algorithm.execute(this);
        // TODO implement preprocessing to filter out repeated solutions
        solutions.addAll(result);
    }

    public List<Solution> getSolutions() {
        return solutions;
    }
}
