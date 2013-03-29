package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Plan;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.MetricProvider;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:02
 */
public class Chromosome implements Plan {
    private final List<Gene> genes;
    private final ChromosomeMetricProvider metricProvider;

    public Chromosome(GeneticExecutionContext context) {
        this(context, null);
    }

    public Chromosome(GeneticExecutionContext context, Solution solution) {
        genes = Lists.newArrayList();
        metricProvider = new ChromosomeMetricProvider(context, this);

        if (solution != null) {
            for (Pattern pattern : solution.getPatterns()) {
                genes.add(new Gene(pattern));
            }

            commit();
        }

    }

    public int size() {
        return genes.size();
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public Gene getGene(int index) {
        return index < genes.size() ? genes.get(index) : null;
    }

    public void setGene(int index, Gene gene) {
        if (index < genes.size()) {
            genes.set(index, gene);
            commit();
        }
    }

    public void addGene(Gene gene) {
        genes.add(gene);
        commit();
    }

    @Override
    public MetricProvider getMetricProvider() {
        return metricProvider;
    }

    public double getProductionLengthForOrder(int index) {
        double productionLength = 0;

        for (Gene gene : genes) {
            productionLength += gene.getProductionLengthForOrder(index);
        }

        return productionLength;
    }

    /**
     * <p>Commit changes, reset previously calculated (cached) values of base metrics.</p>
     */
    public void commit() {
        metricProvider.resetCachedValues();
    }
}
