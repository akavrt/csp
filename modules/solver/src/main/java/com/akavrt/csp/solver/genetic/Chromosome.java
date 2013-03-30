package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Pattern;
import com.akavrt.csp.core.Plan;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:02
 */
public class Chromosome implements Plan {
    private final List<Gene> genes;
    private final GeneticExecutionContext context;
    private final ChromosomeMetricProvider metricProvider;
    private int cachedHashCode;
    private boolean useCachedHashCode;

    public Chromosome(GeneticExecutionContext context) {
        this(context, null);
    }

    public Chromosome(GeneticExecutionContext context, Solution solution) {
        this.context = context;

        metricProvider = new ChromosomeMetricProvider(context, this);

        genes = Lists.newArrayList();
        if (solution != null) {
            for (Pattern pattern : solution.getPatterns()) {
                genes.add(new Gene(pattern));
            }

            commit();
        }

    }

    public Chromosome(Chromosome chromosome) {
        this.context = chromosome.context;

        metricProvider = new ChromosomeMetricProvider(context, this);

        genes = Lists.newArrayList();
        for (Gene gene : chromosome.genes) {
            // create a deep copy for each gene
            // and add copy to the chain
            Gene copy = new Gene(gene);
            genes.add(copy);
        }

        commit();
    }

    public GeneticExecutionContext getContext() {
        return context;
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

    public void addGene(int index, Gene gene) {
        if (index <= genes.size()) {
            genes.add(index, gene);
            commit();
        }
    }

    public Gene removeGene(int index) {
        Gene gene = null;
        if (index < genes.size()) {
            gene = genes.remove(index);
            commit();
        }

        return gene;
    }

    public double getProductionLengthForOrder(int index) {
        double productionLength = 0;

        for (Gene gene : genes) {
            productionLength += gene.getProductionLengthForOrder(index);
        }

        return productionLength;
    }

    public boolean isPatternsValid() {
        boolean isPatternValid = true;

        // exit on the first invalid pattern
        for (Gene gene : genes) {
            if (!gene.isValid(context)) {
                isPatternValid = false;
                break;
            }
        }

        return isPatternValid;
    }

    public boolean isRollsValid() {
        Set<Integer> rollIds = Sets.newHashSet();
        boolean isRepeatedRollFound = false;
        for (Gene gene : genes) {
            if (gene.getRoll() != null && !rollIds.add(gene.getRoll().getInternalId())) {
                // we can't use same roll twice
                isRepeatedRollFound = true;
                break;
            }
        }

        return !isRepeatedRollFound;
    }

    public boolean isOrdersFulfilled() {
        boolean isOrderFulfilled = true;

        // exit on the first unfulfilled order
        for (int i = 0; i < context.getProblem().getOrders().size(); i++) {
            double production = getProductionLengthForOrder(i);
            double orderLength = context.getProblem().getOrders().get(i).getLength();

            isOrderFulfilled = DoubleMath.fuzzyCompare(production, orderLength,
                                                       Constants.TOLERANCE) >= 0;
            if (!isOrderFulfilled) {
                break;
            }
        }

        return isOrderFulfilled;
    }

    public boolean isValid() {
        return isPatternsValid() && isRollsValid() && isOrdersFulfilled();
    }

    /**
     * <p>Commit changes, reset previously calculated (cached) values of the base metrics and
     * chromosome's hash code.</p>
     */
    public void commit() {
        useCachedHashCode = false;
        metricProvider.resetCachedValues();
    }

    @Override
    public ChromosomeMetricProvider getMetricProvider() {
        return metricProvider;
    }

    @Override
    public int hashCode() {
        if (!useCachedHashCode) {
            useCachedHashCode = true;

            if (genes.size() == 0) {
                cachedHashCode = 0;
            } else {
                int[] geneHashes = new int[genes.size()];
                int i = 0;
                for (Gene gene : genes) {
                    geneHashes[i++] = gene.hashCode();
                }

                Arrays.sort(geneHashes);

                cachedHashCode = Arrays.hashCode(geneHashes);
            }
        }

        return cachedHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Chromosome)) {
            return false;
        }

        Chromosome lhs = (Chromosome) obj;

        return hashCode() == lhs.hashCode();
    }

}
