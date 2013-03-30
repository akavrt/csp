package com.akavrt.csp.genetic;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.genetic.GeneticBinaryOperator;
import com.akavrt.csp.solver.genetic.GeneticComponentsFactory;
import com.akavrt.csp.solver.genetic.GeneticUnaryOperator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 00:06
 */
public class PatternBasedComponentsFactory implements GeneticComponentsFactory {
    private final PatternGenerator patternGenerator;

    public PatternBasedComponentsFactory(PatternGenerator generator) {
        this.patternGenerator = generator;
    }

    @Override
    public GeneticBinaryOperator createCrossoverOperator() {
        return new Crossover();
    }

    @Override
    public GeneticUnaryOperator createMutationOperator() {
        return new Mutation(patternGenerator);
    }

    @Override
    public Algorithm createInitializationProcedure() {
        return new SimplifiedProcedure(patternGenerator);
    }
}
