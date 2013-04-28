package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.ga.GeneticComponentsFactory;
import com.akavrt.csp.solver.evo.operators.*;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 00:06
 */
public class PatternBasedComponentsFactory implements GeneticComponentsFactory {
    private final PatternGenerator patternGenerator;
    private final Metric objectiveFunction;
    private CompositeMutation mutation;

    public PatternBasedComponentsFactory(PatternGenerator generator) {
        this(generator, null);
    }

    public PatternBasedComponentsFactory(PatternGenerator generator, Metric objectiveFunction) {
        this.patternGenerator = generator;
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public EvolutionaryOperator createCrossover() {
        return new Crossover();
    }

    @Override
    public EvolutionaryOperator createMutation() {
        mutation = new CompositeMutation(objectiveFunction);

        mutation.addOperator(new AdaptGroupPatternMutation(patternGenerator));
        mutation.addOperator(new MergeTwoGroupsMutation(patternGenerator));
        mutation.addOperator(new ReplaceGroupMutation(patternGenerator));
        mutation.addOperator(new AddRollMutation(patternGenerator));
        mutation.addOperator(new ReplaceRollMutation());
        mutation.addOperator(new DeleteRollMutation());

        return mutation;
    }

    @Override
    public Algorithm createInitializationProcedure() {
        return new SimplifiedProcedure(patternGenerator);
    }

    public void traceMutation() {
        if (mutation != null) {
            mutation.traceResults();
        }
    }
}
