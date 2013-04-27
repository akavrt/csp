package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionPhase;
import com.akavrt.csp.solver.evo.EvolutionProgressChangeListener;
import com.akavrt.csp.solver.evo.EvolutionaryAlgorithm;
import com.akavrt.csp.solver.evo.Population;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 15:05
 */
public class AsyncSolver extends SwingWorker<List<Solution>, GeneticProgressUpdate> implements
        ExecutionContext, EvolutionProgressChangeListener {
    private final static Logger LOGGER = LogManager.getLogger(AsyncSolver.class);
    private final Problem problem;
    private final EvolutionaryAlgorithm algorithm;
    private final OnProblemSolvedListener listener;
    private final SeriesMetricProvider metricProvider;

    public AsyncSolver(Problem problem, EvolutionaryAlgorithm algorithm,
                       OnProblemSolvedListener listener, SeriesMetricProvider metricProvider) {
        this.problem = problem;
        this.algorithm = algorithm;
        this.listener = listener;
        this.metricProvider = metricProvider;

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
        if (listener != null && !isCancelled() && updates.size() > 0) {
            GeneticProgressUpdate lastUpdate = updates.get(updates.size() - 1);
            listener.onGeneticProgressChanged(lastUpdate);
        }
    }

    @Override
    public void done() {
        this.algorithm.removeProgressChangeListener();

        if (listener != null && !isCancelled()) {
            List<Solution> solutions = null;
            try {
                solutions = get();
            } catch (Exception e) {
                LOGGER.catching(e);
            }

            listener.onProblemSolved(solutions);
        }
    }

    @Override
    public void onInitializationProgressChanged(int progress) {
        publish(new GeneticProgressUpdate(progress, EvolutionPhase.INITIALIZATION));
    }

    @Override
    public void onGenerationProgressChanged(int progress, Population population) {
        // calculate data for graph trace
        SeriesData seriesData = new SeriesData(population, metricProvider);
        publish(new GeneticProgressUpdate(progress, EvolutionPhase.GENERATION, seriesData));
    }

    public interface OnProblemSolvedListener {
        void onGeneticProgressChanged(GeneticProgressUpdate update);

        void onProblemSolved(List<Solution> solutions);
    }
}
