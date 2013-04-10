package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.genetic.GeneticAlgorithm;
import com.akavrt.csp.solver.genetic.GeneticPhase;
import com.akavrt.csp.solver.genetic.GeneticProgressChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 15:05
 */
public class AsyncSolver extends SwingWorker<List<Solution>, GeneticProgressUpdate> implements
        ExecutionContext, GeneticProgressChangeListener {
    private final static Logger LOGGER = LogManager.getLogger(AsyncSolver.class);
    private final Problem problem;
    private final GeneticAlgorithm algorithm;
    private final OnProblemSolvedListener listener;

    public AsyncSolver(Problem problem, GeneticAlgorithm algorithm,
                       OnProblemSolvedListener listener) {
        this.problem = problem;
        this.algorithm = algorithm;
        this.listener = listener;

        this.algorithm.setProgressChangeListener(this);
    }

    @Override
    public Problem getProblem() {
        return problem;
    }

    @Override
    protected List<Solution> doInBackground() {
        return algorithm.execute(this);
    }

    @Override
    protected void process(List<GeneticProgressUpdate> updates) {
        if (listener != null) {
            for (GeneticProgressUpdate update : updates) {
                listener.onGeneticProgressChanged(update);
            }
        }
    }

    @Override
    public void done() {
        this.algorithm.removeProgressChangeListener();

        if (listener != null) {
            try {
                List<Solution> solutions = get();
                listener.onProblemSolved(solutions);
            } catch (InterruptedException e) {
                LOGGER.catching(e);
            } catch (ExecutionException e) {
                LOGGER.catching(e);
            }
        }
    }

    @Override
    public void onGeneticProgressChanged(int progress, GeneticPhase phase) {
        publish(new GeneticProgressUpdate(progress, phase));
    }

    public interface OnProblemSolvedListener {
        void onGeneticProgressChanged(GeneticProgressUpdate update);

        void onProblemSolved(List<Solution> solutions);
    }
}
