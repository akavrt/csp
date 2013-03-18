package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;

import java.util.List;

/**
 * <p>Basic implementation of single run strategy: all you have to do is to provide a valid problem
 * along with configured algorithm during creation of the solver, call solve() and then request
 * solutions found by the optimization method.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SimpleSolver implements Solver {
    protected final Problem problem;
    protected final Algorithm algorithm;
    protected List<Solution> solutions;
    private ExecutionContext context = new ExecutionContext() {

        @Override
        public Problem getProblem() {
            return problem;
        }
    };

    public SimpleSolver(Problem problem, Algorithm algorithm) {
        this.problem = problem;
        this.algorithm = algorithm;
    }

    protected List<Solution> run() {
        return algorithm.execute(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> solve() {
        solutions = null;

        if (problem != null && algorithm != null) {
            solutions = run();
        }

        return solutions;
    }

    /**
     * <p>Return all solutions found.</p>
     */
    public List<Solution> getSolutions() {
        return solutions;
    }

    /**
     * <p>Return best solution found in the last run.</p>
     *
     * @param metric Metric used to compare solutions in terms of overall quality.
     */
    public Solution getBestSolution(Metric metric) {
        if (solutions == null) {
            return null;
        }

        Solution best = null;
        for (Solution solution : solutions) {
            if (best == null || metric.compare(solution, best) > 0) {
                best = solution;
            }
        }

        return best;
    }

}
