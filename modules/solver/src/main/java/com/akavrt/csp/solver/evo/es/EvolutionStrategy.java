package com.akavrt.csp.solver.evo.es;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.*;
import com.akavrt.csp.utils.ParameterSet;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 25.04.13
 * Time: 14:09
 */
public class EvolutionStrategy extends EvolutionaryAlgorithm {
    private static final String METHOD_NAME = "(mu + lambda) evolution strategy";
    private static final String SHORT_METHOD_NAME = "(mu + lambda)-ES";
    private final EvolutionStrategyParameters parameters;
    private final Metric objectiveFunction;
    private final EvolutionaryOperator mutation;

    public EvolutionStrategy(EvolutionaryComponentsFactory componentsFactory,
                             Metric objectiveFunction, EvolutionStrategyParameters parameters) {
        super(componentsFactory.createInitializationProcedure(), parameters);

        this.parameters = parameters;
        this.objectiveFunction = objectiveFunction;
        this.mutation = componentsFactory.createMutation();
    }

    @Override
    protected void initializeOperators(EvolutionaryExecutionContext evoContext) {
        mutation.initialize(evoContext);
    }

    @Override
    protected void applyOperators(Population population) {
        population.generation(mutation);
    }

    @Override
    protected Population createPopulation(EvolutionaryExecutionContext evoContext) {
        return new ModStrategyPopulation(evoContext, parameters, objectiveFunction);
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
