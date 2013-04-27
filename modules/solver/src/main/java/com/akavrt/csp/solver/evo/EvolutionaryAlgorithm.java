package com.akavrt.csp.solver.evo;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;

import java.util.Date;
import java.util.List;

/**
 * <p>Generic (template) implementation of evolutionary algorithm which conforms to the contract
 * defined by the Algorithm interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class EvolutionaryAlgorithm implements Algorithm {
    private final Algorithm initializationProcedure;
    private final EvolutionaryAlgorithmParameters parameters;
    private EvolutionProgressChangeListener progressChangeListener;

    public EvolutionaryAlgorithm(Algorithm initializationProcedure,
                                 EvolutionaryAlgorithmParameters parameters) {
        this.parameters = parameters;
        this.initializationProcedure = initializationProcedure;
    }

    protected abstract void initializeOperators(EvolutionaryExecutionContext evoContext);

    protected abstract void applyOperators(Population population);

    protected abstract Population createPopulation(EvolutionaryExecutionContext evoContext);

    protected abstract String getShortMethodName();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> execute(ExecutionContext context) {
        if (context.getProblem() == null) {
            return null;
        }

        EvolutionaryContext evolutionaryContext = new EvolutionaryContext(context);
        List<Solution> solutions = search(evolutionaryContext);

        for (Solution solution : solutions) {
            // create separate instance of metadata for each solution found
            SolutionMetadata metadata = prepareMetadata();
            solution.setMetadata(metadata);
        }

        return solutions;
    }

    /**
     * <p>Represents single run of the evolutionary algorithm. Supports early termination.</p>
     *
     * @param evoContext The context to run with.
     * @return Sorted list of solutions representing state of the population on the end of the run.
     */
    protected List<Solution> search(EvolutionaryExecutionContext evoContext) {
        initializeOperators(evoContext);

        Population population = createPopulation(evoContext);

        initializationPhase(population);
        generationalPhase(evoContext, population);

        return population.getSolutions();
    }

    private void initializationPhase(Population population) {
        population.initialize(initializationProcedure, progressChangeListener);
    }

    private void generationalPhase(EvolutionaryExecutionContext evoContext, Population population) {
        while (!evoContext.isCancelled() && population.getAge() < parameters.getRunSteps()) {
            applyOperators(population);

            if (progressChangeListener != null) {
                int progress = 100 * population.getAge() / parameters.getRunSteps();
                progress = Math.min(progress, 100);

                progressChangeListener.onGenerationProgressChanged(progress, population);
            }
        }

        population.sort();
    }

    public void setProgressChangeListener(EvolutionProgressChangeListener listener) {
        this.progressChangeListener = listener;
    }

    public void removeProgressChangeListener() {
        this.progressChangeListener = null;
    }

    protected Algorithm getInitializationProcedure() {
        return initializationProcedure;
    }

    private SolutionMetadata prepareMetadata() {
        SolutionMetadata metadata = new SolutionMetadata();
        metadata.setDescription("Solution obtained with " + getShortMethodName() + ".");
        metadata.setDate(new Date());
        metadata.setParameters(getParameters());

        return metadata;
    }

    private static class EvolutionaryContext implements EvolutionaryExecutionContext {
        private final ExecutionContext parentContext;

        public EvolutionaryContext(ExecutionContext parentContext) {
            this.parentContext = parentContext;
        }

        @Override
        public double getOrderWidth(int index) {
            return parentContext.getProblem().getOrders().get(index).getWidth();
        }

        @Override
        public double getOrderLength(int index) {
            return parentContext.getProblem().getOrders().get(index).getLength();
        }

        @Override
        public int getOrdersSize() {
            return parentContext.getProblem().getOrders().size();
        }

        @Override
        public Problem getProblem() {
            return parentContext.getProblem();
        }

        @Override
        public boolean isCancelled() {
            return parentContext.isCancelled();
        }
    }

}
