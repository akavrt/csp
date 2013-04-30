package com.akavrt.csp.tester.tracer;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.es.BaseStrategyComponentsFactory;
import com.akavrt.csp.solver.evo.operators.CompositeMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 00:06
 */
public class TraceableStrategyComponentsFactory extends BaseStrategyComponentsFactory {
    private final Metric objectiveFunction;
    private CompositeMutation mutation;

    public TraceableStrategyComponentsFactory(PatternGenerator generator,
                                              Metric objectiveFunction) {
        super(generator);
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public EvolutionaryOperator createMutation() {
        mutation = new CompositeMutation(objectiveFunction);

        // add operators
        prepareMutationOperator(mutation);

        return mutation;
    }

    public void traceMutation() {
        if (mutation != null) {
            mutation.traceResults();
        }
    }
}
