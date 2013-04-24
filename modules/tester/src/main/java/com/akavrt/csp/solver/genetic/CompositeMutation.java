package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 23:56
 */
public class CompositeMutation implements GeneticOperator {
    private static final Logger LOGGER = LogManager.getLogger(CompositeMutation.class);
    private final List<GeneticOperator> operators;
    private final List<Integer> successCounter;
    private final List<Integer> executionCounter;
    private final Random rGen;
    private final Metric objectiveFunction;

    public CompositeMutation(PatternGenerator generator, Metric objectiveFunction) {
        this.objectiveFunction = objectiveFunction;

        successCounter = Lists.newArrayList();
        executionCounter = Lists.newArrayList();

        operators = Lists.newArrayList();

        addOperator(new AdaptGroupPatternMutation(generator));
        addOperator(new MergeTwoGroupsMutation(generator));
        addOperator(new ReplaceGroupMutation(generator));
        addOperator(new AddRollMutation(generator));
        addOperator(new ReplaceRollMutation());
        addOperator(new DeleteRollMutation());

        rGen = new Random();
    }

    private void addOperator(GeneticOperator operator) {
        operators.add(operator);
        successCounter.add(0);
        executionCounter.add(0);
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        for (GeneticOperator operator : operators) {
            operator.initialize(context);
        }
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        if (chromosomes.length < 1 || chromosomes[0] == null) {
            return null;
        }

        int operatorIndex = rGen.nextInt(operators.size());
        Chromosome original = chromosomes[0];
        Chromosome mutated = operators.get(operatorIndex).apply(original);

        int executions = executionCounter.get(operatorIndex);
        executionCounter.set(operatorIndex, ++executions);

        if (objectiveFunction != null && mutated != null
                && objectiveFunction.compare(mutated, original) > 0) {
            // diagnostic comparison of the original one and mutated chromosomes
            int successes = successCounter.get(operatorIndex);
            successCounter.set(operatorIndex, ++successes);
        }

        return mutated;
    }

    public void traceResults() {
        for (int i = 0; i < operators.size(); i++) {
            String rate = String.format("%.2f%%", 100 * successCounter.get(i) / (double) executionCounter.get(i));
            LOGGER.info("Mutation operator #{}: {} successful executions from {} total, success rate is {}",
                        i + 1, successCounter.get(i), executionCounter.get(i), rate);
        }
    }
}
