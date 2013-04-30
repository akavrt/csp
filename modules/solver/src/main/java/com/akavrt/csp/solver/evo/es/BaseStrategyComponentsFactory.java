package com.akavrt.csp.solver.evo.es;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.EvolutionaryComponentsFactory;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
import com.akavrt.csp.solver.evo.operators.CompositeMutation;
import com.akavrt.csp.solver.evo.operators.group.*;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 13:40
 */
public class BaseStrategyComponentsFactory  implements EvolutionaryComponentsFactory {
    private final PatternGenerator patternGenerator;

    public BaseStrategyComponentsFactory(PatternGenerator generator) {
        this.patternGenerator = generator;
    }

    @Override
    public EvolutionaryOperator createMutation() {
        CompositeMutation mutation = new CompositeMutation();

        // add operators
        prepareMutationOperator(mutation);

        return mutation;
    }

    protected void prepareMutationOperator(CompositeMutation mutation) {
        mutation.addOperator(new AddRollMutation(patternGenerator));
        mutation.addOperator(new ReplaceRollMutation());
        mutation.addOperator(new DeleteRollMutation(patternGenerator));
        mutation.addOperator(new AdaptGroupMutation(patternGenerator));
        mutation.addOperator(new ReplaceGroupMutation(patternGenerator));
        mutation.addOperator(new MergeTwoGroupsMutation(patternGenerator));
    }

    @Override
    public Algorithm createInitializationProcedure() {
        return new SimplifiedProcedure(patternGenerator);
    }

}

