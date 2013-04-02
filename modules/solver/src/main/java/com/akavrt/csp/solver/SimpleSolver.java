package com.akavrt.csp.solver;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.Metric;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * <p>Basic implementation of single run strategy: all you have to do is to provide a valid problem
 * along with configured algorithm during creation of the solver, call solve() and then request
 * solutions found by the optimization method.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SimpleSolver implements Solver {
    private Problem problem;
    private final Algorithm algorithm;
    private List<Solution> solutions;

    public SimpleSolver(Algorithm algorithm) {
        this(null, algorithm);
    }

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
    public final List<Solution> solve() {
        solutions = null;

        if (problem != null && algorithm != null) {
            solutions = run();
        }

        return solutions;
    }

    /**
     * <p>Problem associated with solver.</p>
     */
    public Problem getProblem() {
        return problem;
    }

    /**
     * <p>Set the problem to be solved.</p>
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * <p>Implementation of the optimization routine associated with solver.</p>
     */
    protected Algorithm getAlgorithm() {
        return algorithm;
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

    private final ExecutionContext context = new ExecutionContext() {

        @Override
        public Problem getProblem() {
            return problem;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    };

}
