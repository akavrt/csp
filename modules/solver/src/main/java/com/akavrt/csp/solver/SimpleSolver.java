package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Strategy pattern was implemented to support different methods used to find approximate
 * solutions of the cutting stock problem.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SimpleSolver implements Solver, ExecutionContext {
    private static final int DEFAULT_NUMBER_OF_RUNS = 1;
    private Problem problem;
    private Algorithm algorithm;
    private PatternGenerator patternGenerator;
    private Metric metric;
    private List<Solution> solutions;

    private int numberOfRuns = DEFAULT_NUMBER_OF_RUNS;

    public SimpleSolver() {
        solutions = Lists.newArrayList();
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setPatternGenerator(PatternGenerator patternGenerator) {
        this.patternGenerator = patternGenerator;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @Override
    public Problem getProblem() {
        return problem;
    }

    @Override
    public PatternGenerator getPatternGenerator() {
        return patternGenerator;
    }

    @Override
    public Metric getMetric() {
        return metric;
    }

    protected void run() {
        List<Solution> result = algorithm.execute(this);
        // TODO implement preprocessing to filter out repeated solutions
        solutions.addAll(result);
    }

    @Override
    public void solve() {
        // not every algorithm will need pattern generator,
        // so we left null-check for pattern generator to the algorithm itself
        if (problem == null || algorithm == null) {
            return;
        }

        // clear list with previously collected solutions
        solutions.clear();

        for (int i = 1; i <= numberOfRuns; i++) {
            run();
        }
    }

    @Override
    public List<Solution> getSolutions() {
        return solutions;
    }

    public Solution getBestSolution() {
        return getBestSolution(null);
    }

    public Solution getBestSolution(Metric inMetric) {
        if (inMetric == null) {
            inMetric = metric;
        }

        // we are dealing with minimization problem:
        // given two solutions, solution with smaller
        // value of objective function will be better
        Solution best =  null;
        for (Solution solution : solutions) {
            if (best == null || inMetric.evaluate(solution) < inMetric.evaluate(best)) {
                best = solution;
            }
        }

        return best;
    }
}
