package com.akavrt.csp.solver.evo.operators;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.EvolutionaryOperator;
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
public class CompositeMutation implements EvolutionaryOperator {
    private static final Logger LOGGER = LogManager.getLogger(CompositeMutation.class);
    private final List<EvolutionaryOperator> operators;
    private final Random rGen;
    // used for diagnostic means
    private final Metric objectiveFunction;
    private final List<Integer> successCounter;
    private final List<Integer> executionCounter;

    public CompositeMutation() {
        this(null);
    }

    public CompositeMutation(Metric objectiveFunction) {
        this.objectiveFunction = objectiveFunction;

        operators = Lists.newArrayList();

        successCounter = Lists.newArrayList();
        executionCounter = Lists.newArrayList();

        rGen = new Random();
    }

    public void addOperator(EvolutionaryOperator operator) {
        operators.add(operator);
        successCounter.add(0);
        executionCounter.add(0);
    }

    @Override
    public void initialize(EvolutionaryExecutionContext context) {
        for (EvolutionaryOperator operator : operators) {
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
            String rate = String.format("%.2f%%", 100 * successCounter.get(i)
                    / (double) executionCounter.get(i));
            LOGGER.info("#{} {} : {} successful from {} executions, success rate is {}",
                        i + 1, operators.get(i).getClass().getSimpleName(),
                        successCounter.get(i), executionCounter.get(i), rate);
        }
    }
}
