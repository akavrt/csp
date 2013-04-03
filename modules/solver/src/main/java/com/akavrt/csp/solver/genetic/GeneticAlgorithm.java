package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.metadata.SolutionMetadata;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.utils.ParameterSet;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * <p>Generic implementation of genetic algorithm which conforms to the contract defined by the
 * Algorithm interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class GeneticAlgorithm implements Algorithm {
    private static final String METHOD_NAME = "Genetic algorithm based on modGA generation scheme";
    private static final String SHORT_METHOD_NAME = "GA";
    private final GeneticAlgorithmParameters parameters;
    private final Metric objectiveFunction;
    private final GeneticOperator crossover;
    private final GeneticOperator mutation;
    private final Algorithm initializationProcedure;

    public GeneticAlgorithm(GeneticComponentsFactory componentsFactory, Metric objectiveFunction,
                            GeneticAlgorithmParameters parameters) {
        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;
        this.crossover = componentsFactory.createCrossover();
        this.mutation = componentsFactory.createMutation();
        this.initializationProcedure = componentsFactory.createInitializationProcedure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return METHOD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParameterSet> getParameters() {
        List<ParameterSet> params = Lists.newArrayList();

        parameters.setDescription("Parameters of the master method.");
        params.add(parameters);

        for (ParameterSet parameterSet : initializationProcedure.getParameters()) {
            parameterSet.setDescription("Parameters of the initialization procedure.");
            params.add(parameterSet);
        }

        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Solution> execute(ExecutionContext context) {
        if (context.getProblem() == null) {
            return null;
        }

        GeneticContext geneticContext = new GeneticContext(context);
        List<Solution> solutions = search(geneticContext, parameters);

        for (Solution solution : solutions) {
            // create separate instance for each solution found
            SolutionMetadata metadata = prepareMetadata();
            solution.setMetadata(metadata);
        }

        return solutions;
    }

    private SolutionMetadata prepareMetadata() {
        SolutionMetadata metadata = new SolutionMetadata();
        metadata.setDescription("Solution obtained with " + SHORT_METHOD_NAME + ".");
        metadata.setDate(new Date());
        metadata.setParameters(getParameters());

        return metadata;
    }

    /**
     * <p>Represents single run of the genetic algorithm. Supports early termination.</p>
     *
     * @param geneticContext The context to run with.
     * @param parameters     Parameters of genetic algorithm.
     * @return Sorted list of solutions representing state of the population on the end of the run.
     */
    protected List<Solution> search(GeneticExecutionContext geneticContext,
                                    GeneticAlgorithmParameters parameters) {
        crossover.initialize(geneticContext);
        mutation.initialize(geneticContext);

        Population population = new Population(geneticContext, parameters, objectiveFunction);
        population.initialize(initializationProcedure);

        while (!geneticContext.isCancelled() && population.getAge() < parameters.getRunSteps()) {
            population.generation(crossover, mutation);
        }

        population.sort();

        return population.getSolutions();
    }

    private static class GeneticContext implements GeneticExecutionContext {
        private final ExecutionContext parentContext;

        public GeneticContext(ExecutionContext parentContext) {
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
