package com.akavrt.csp.genetic;

import com.akavrt.csp.core.*;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.Gene;
import com.akavrt.csp.solver.genetic.GeneticExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 22.04.13
 * Time: 23:37
 */
public class SequentialStepMutation extends GroupBasedMutation {
    private final Algorithm sequentialProcedure;

    public SequentialStepMutation(PatternGenerator generator) {
        sequentialProcedure = new VahrenkampProcedure(generator);
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        // nothing to initialize
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        Chromosome mutated = new Chromosome(chromosomes[0]);

        // remove randomly selected group
        if (mutated.size() > 0) {
            List<GeneGroup> groups = groupGenes(mutated);
            int groupIndex = rGen.nextInt(groups.size());
            GeneGroup selectedGroup = groups.get(groupIndex);

            for (int i = selectedGroup.size() - 1; i >= 0; i--) {
                int indexToRemove = selectedGroup.getGeneIndex(i);
                mutated.removeGene(indexToRemove);
            }
        }

        Solution partial = getPartialSolution(mutated);

        if (partial != null) {
            for (Pattern pattern : partial.getPatterns()) {
                Gene gene = new Gene(pattern);
                mutated.addGene(gene);
            }
        }

        return mutated;
    }

    private Solution getPartialSolution(final Chromosome chromosome) {
        final Problem residual = formulateResidualProblem(chromosome);

        ExecutionContext localContext = new ExecutionContext() {
            @Override
            public Problem getProblem() {
                return residual;
            }

            @Override
            public boolean isCancelled() {
                return chromosome.getContext().isCancelled();
            }
        };

        List<Solution> solutions = sequentialProcedure.execute(localContext);

        return solutions != null && solutions.size() > 0 ? solutions.get(0) : null;
    }

    private Problem formulateResidualProblem(Chromosome chromosome) {
        List<Roll> spareRolls = getSpareRolls(chromosome);

        List<Order> originalOrders = chromosome.getContext().getProblem().getOrders();
        List<Order> residualOrders = Lists.newArrayList();

        for (int i = 0; i < originalOrders.size(); i++) {
            Order original = originalOrders.get(i);

            double produced = chromosome.getProductionLengthForOrder(i);
            double required = original.getLength();
            double unfulfilled = required > produced ? required - produced : 0;

            Order residual = new Order(original.getId(), unfulfilled, original.getWidth());
            residualOrders.add(residual);
        }

        int allowedCutsNumber = chromosome.getContext().getProblem().getAllowedCutsNumber();

        return new Problem(residualOrders, spareRolls, allowedCutsNumber);
    }

}
