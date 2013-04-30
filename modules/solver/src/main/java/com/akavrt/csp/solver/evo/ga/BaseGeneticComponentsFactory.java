package com.akavrt.csp.solver.evo.ga;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.operators.CompositeMutation;
import com.akavrt.csp.solver.evo.operators.Crossover;
import com.akavrt.csp.solver.evo.operators.gene.ReplaceGenePatternMutation;
import com.akavrt.csp.solver.evo.operators.gene.ReplaceGeneRollMutation;
import com.akavrt.csp.solver.evo.operators.gene.SpreadGenePatternMutation;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 13:36
 */
public class BaseGeneticComponentsFactory implements GeneticComponentsFactory {
    private final PatternGenerator patternGenerator;

    public BaseGeneticComponentsFactory(PatternGenerator generator) {
        this.patternGenerator = generator;
    }

    @Override
    public EvolutionaryOperator createCrossover() {
        return new Crossover();
    }

    @Override
    public EvolutionaryOperator createMutation() {
        CompositeMutation mutation = new CompositeMutation();

        mutation.addOperator(new ReplaceGenePatternMutation(patternGenerator));
        mutation.addOperator(new ReplaceGeneRollMutation(patternGenerator));
        mutation.addOperator(new SpreadGenePatternMutation());

        return mutation;
    }

    @Override
    public Algorithm createInitializationProcedure() {
        return new SimplifiedProcedure(patternGenerator);
    }

}
