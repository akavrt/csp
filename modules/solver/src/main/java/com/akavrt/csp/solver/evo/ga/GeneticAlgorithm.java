package com.akavrt.csp.solver.evo.ga;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.EvolutionaryAlgorithm;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.Population;
import com.akavrt.csp.utils.ParameterSet;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * <p>Generic implementation of genetic algorithm which conforms to the contract defined by the
 * Algorithm interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class GeneticAlgorithm extends EvolutionaryAlgorithm {
    private static final String METHOD_NAME = "Genetic algorithm based on modGA generation scheme";
    private static final String SHORT_METHOD_NAME = "GA";
    private final GeneticAlgorithmParameters parameters;
    private final Metric objectiveFunction;
    private final EvolutionaryOperator crossover;
    private final EvolutionaryOperator mutation;

    public GeneticAlgorithm(GeneticComponentsFactory componentsFactory, Metric objectiveFunction,
                            GeneticAlgorithmParameters parameters) {
        super(componentsFactory.createInitializationProcedure(), parameters);

        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;
        this.crossover = componentsFactory.createCrossover();
        this.mutation = componentsFactory.createMutation();
    }

    @Override
    protected void initializeOperators(EvolutionaryExecutionContext evoContext) {
        crossover.initialize(evoContext);
        mutation.initialize(evoContext);
    }

    @Override
    protected void applyOperators(Population population) {
        population.generation(crossover, mutation);
    }

    @Override
    protected Population createPopulation(EvolutionaryExecutionContext evoContext) {
        return new GeneticPopulation(evoContext, parameters, objectiveFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return METHOD_NAME;
    }

    @Override
    protected String getShortMethodName() {
        return SHORT_METHOD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ParameterSet> getParameters() {
        List<ParameterSet> params = Lists.newArrayList();

        parameters.setDescription("Parameters of the master method.");
        params.add(parameters);

        for (ParameterSet parameterSet : getInitializationProcedure().getParameters()) {
            parameterSet.setDescription("Parameters of the initialization procedure.");
            params.add(parameterSet);
        }

        return params;
    }

}
