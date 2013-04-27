package com.akavrt.csp.solver.local;

import com.akavrt.csp.core.*;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.ExecutionContext;
import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;
import com.akavrt.csp.solver.evo.Gene;
import com.akavrt.csp.solver.evo.operators.GeneGroup;
import com.akavrt.csp.solver.evo.operators.GroupBasedMutation;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 24.04.13
 * Time: 15:57
 */
public class SearchStepOperator extends GroupBasedMutation {
    private final Algorithm sequentialProcedure;
    private final Metric objectiveFunction;

    public SearchStepOperator(Algorithm sequentialProcedure, Metric objectiveFunction) {
        this.sequentialProcedure = sequentialProcedure;
        this.objectiveFunction = objectiveFunction;
    }

    @Override
    public void initialize(EvolutionaryExecutionContext context) {
        // nothing to initialize
    }

    @Override
    public Chromosome apply(Chromosome... chromosomes) {
        Chromosome original = new Chromosome(chromosomes[0]);
        Chromosome result = null;

        // remove each group and repair solution using sequential procedure
        // (neighbourhood is defined on groups)
        if (original.size() > 0) {
            List<GeneGroup> groups = groupGenes(original);

            List<Chromosome> neighbours = Lists.newArrayList();
            int groupIndex = 0;
            while (groupIndex < groups.size() && !original.getContext().isCancelled()) {
                GeneGroup group = groups.get(groupIndex);

                Chromosome neighbour = new Chromosome(original);
                for (int i = group.size() - 1; i >= 0; i--) {
                    int indexToRemove = group.getGeneIndex(i);
                    neighbour.removeGene(indexToRemove);
                }

                Solution partial = getPartialSolution(neighbour);
                if (partial != null) {
                    for (Pattern pattern : partial.getPatterns()) {
                        Gene gene = new Gene(pattern);
                        neighbour.addGene(gene);
                    }
                }

                neighbours.add(neighbour);

                groupIndex++;
            }

            // select best neighbour
            for (Chromosome neighbour : neighbours) {
                if (result == null || objectiveFunction.compare(neighbour, result) > 0) {
                    result = neighbour;
                }
            }
        }

        return result != null ? result : original;
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
