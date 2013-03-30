package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Order;
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
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:11
 */
public class GeneticAlgorithm implements Algorithm {
    private static final String METHOD_NAME = "Genetic algorithm based on modGA generation scheme";
    private static final String SHORT_METHOD_NAME = "modGA";
    private final GeneticAlgorithmParameters parameters;
    private final Metric objectiveFunction;
    private final GeneticBinaryOperator crossover;
    private final GeneticUnaryOperator mutation;
    private final Algorithm initializationProcedure;

    public GeneticAlgorithm(GeneticComponentsFactory componentsFactory, Metric objectiveFunction,
                            GeneticAlgorithmParameters parameters) {
        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;
        this.crossover = componentsFactory.createCrossoverOperator();
        this.mutation = componentsFactory.createMutationOperator();
        this.initializationProcedure = componentsFactory.createInitializationProcedure();
    }

    @Override
    public String name() {
        return METHOD_NAME;
    }

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

    protected List<Solution> search(GeneticExecutionContext geneticContext,
                                    GeneticAlgorithmParameters parameters) {
        Population population = new Population(geneticContext, parameters, objectiveFunction);
        population.initialize(initializationProcedure);

        int generation = 0;
        while (!geneticContext.isCancelled() && generation < parameters.getRunSteps()) {
            population.generation(crossover, mutation);

            generation++;
        }

        return population.getSolutions();
    }

    private static class GeneticContext implements GeneticExecutionContext {
        private final ExecutionContext parentContext;
        private double[] orderWidth;
        private double[] orderLength;

        public GeneticContext(ExecutionContext parentContext) {
            this.parentContext = parentContext;

            Problem problem = parentContext.getProblem();

            orderWidth = new double[problem.getOrders().size()];
            orderLength = new double[problem.getOrders().size()];
            for (int i = 0; i < problem.getOrders().size(); i++) {
                Order order = problem.getOrders().get(i);
                orderWidth[i] = order.getWidth();
                orderLength[i] = order.getLength();
            }
        }

        @Override
        public double getOrderWidth(int index) {
            return orderWidth[index];
        }

        @Override
        public double getOrderLength(int index) {
            return orderLength[index];
        }

        @Override
        public int getOrdersSize() {
            return orderWidth.length;
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
