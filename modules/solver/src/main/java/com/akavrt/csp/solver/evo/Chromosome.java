package com.akavrt.csp.solver.evo;

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
 * <p>All implemented evolutionary algorithms are based on a pretty straightforward representation
 * scheme: genes corresponds to cutting patterns with rolls attached to them (see Pattern), while
 * chromosomes corresponds to cutting plans (see Solution) - i.e. no additional coding is used.</p>
 *
 * <p>Chromosome mimics Solution and internally stores cutting plan as an ordered list of cutting
 * patterns (chain of genes in this case).</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Chromosome implements Plan {
    private final List<Gene> genes;
    private final EvolutionaryExecutionContext context;
    private final ChromosomeMetricProvider metricProvider;
    private int cachedHashCode;
    private boolean useCachedHashCode;

    /**
     * <p>Creates context-aware instance of Chromosome with empty chain of genes.</p>
     *
     * @param context Context provides access to the problem definition.
     */
    public Chromosome(EvolutionaryExecutionContext context) {
        this(context, null);
    }

    /**
     * <p>Converts an instance of Solution into new context-aware instance of Chromosome.</p>
     *
     * @param context  Context provides access to the problem definition.
     * @param solution Solution to be converted into chromosome.
     */
    public Chromosome(EvolutionaryExecutionContext context, Solution solution) {
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

    /**
     * <p>Copy constructor.</p>
     *
     * @param chromosome Chromosome to be copied.
     */
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

    /**
     * <p>Converts chromosome into equivalent instance of Solution.</p>
     *
     * @return An instance of Solution equivalent to the current chromosome.
     */
    public Solution convert() {
        Solution solution = new Solution(context.getProblem());
        for (Gene gene : genes) {
            solution.addPattern(gene.convert(context));
        }

        return solution;
    }

    /**
     * <p>Returns context provided to the chromosome on creation.</p>
     *
     * @return Associated context.
     */
    public EvolutionaryExecutionContext getContext() {
        return context;
    }

    /**
     * <p>Returns length of the chain of genes.</p>
     *
     * @return Length of the chain of genes.
     */
    public int size() {
        return genes.size();
    }

    /**
     * <p>Returns list of genes added to the chromosome's chain.</p>
     *
     * @return List of genes.
     */
    public List<Gene> getGenes() {
        return genes;
    }

    /**
     * <p>Returns the gene at the specified position in this chain.</p>
     *
     * @param index Index of the gene to return.
     * @return The gene at the specified position in this chain.
     */
    public Gene getGene(int index) {
        return index < genes.size() ? genes.get(index) : null;
    }

    /**
     * <p>Replaces the gene at the specified position in the chromosome's chain of genes with the
     * specified gene.</p>
     *
     * @param index Index of the gene to replace.
     * @param gene  Gene to be stored at the specified position.
     */
    public void setGene(int index, Gene gene) {
        if (index < genes.size()) {
            genes.set(index, gene);
            commit();
        }
    }

    /**
     * <p>Appends the specified gene to the end of the chromosome's chain og genes.</p>
     *
     * @param gene Gene to be appended to the chain.
     */
    public void addGene(Gene gene) {
        genes.add(gene);
        commit();
    }

    /**
     * <p>Inserts the specified gene at the specified position in the chromosome's chain of genes.
     * Shifts the gene currently at that position (if any) and any subsequent genes to the right
     * (adds one to their indices).</p>
     *
     * @param index Index at which the specified gene is to be inserted.
     * @param gene  Gene to be inserted.
     */
    public void addGene(int index, Gene gene) {
        if (index <= genes.size()) {
            genes.add(index, gene);
            commit();
        }
    }

    /**
     * <p>Removes the gene at the specified position in the chromosome's chain of genes. Shifts any
     * subsequent genes to the left (subtracts one from their indices). Returns the gene that was
     * removed from the chain.</p>
     *
     * @param index The index of the gene to be removed.
     * @return The gene previously at the specified position.
     */
    public Gene removeGene(int index) {
        Gene gene = null;
        if (index < genes.size()) {
            gene = genes.remove(index);
            commit();
        }

        return gene;
    }

    /**
     * <p>Calculate the length of the finished product we will get for specific order after
     * executing cutting plan. Measured in abstract units.</p>
     *
     * @param index Position of the order in the list of orders provided by problem definition.
     * @return The length of produced strip.
     */
    public double getProductionLengthForOrder(int index) {
        double productionLength = 0;

        for (Gene gene : genes) {
            productionLength += gene.getProductionLengthForOrder(index);
        }

        return productionLength;
    }

    /**
     * <p>Check feasibility of used patterns.</p>
     *
     * @return true if plan represented by this chromosome consists of feasible patterns only, false
     *         otherwise.
     */
    private boolean isPatternsFeasible() {
        boolean isPatternFeasible = true;

        // exit on the first infeasible pattern
        for (Gene gene : genes) {
            if (!gene.isFeasible(context)) {
                isPatternFeasible = false;
                break;
            }
        }

        return isPatternFeasible;
    }

    /**
     * <p>Feasible plans can cut each roll (attach it to the pattern) only once. Multiple usage of
     * the rolls isn't allowed.</p>
     *
     * @return true if plan represented by this chromosome doesn't contain rolls which are used
     *         more than once, false otherwise.
     */
    private boolean isRollUsageFeasible() {
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

    /**
     * <p>Check whether all requirements for order's length are met. If any order with insufficient
     * length can be found, solution should be treated as infeasible one.</p>
     *
     * @return true if strip of sufficient length will be produced for all orders, false otherwise.
     */
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

    /**
     * <p>Check all characteristics of the cutting plan represented by this chromosome to determine
     * whether it is feasible or not.</p>
     *
     * @return true if cutting plan is feasible, false otherwise.
     */
    public boolean isFeasible() {
        return isPatternsFeasible() && isRollUsageFeasible() && isOrdersFulfilled();
    }

    /**
     * <p>Commit changes, reset previously calculated (cached) values of the basic metrics and
     * chromosome's hash code.</p>
     */
    private void commit() {
        useCachedHashCode = false;
        metricProvider.resetCachedValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChromosomeMetricProvider getMetricProvider() {
        return metricProvider;
    }

    /**
     * <p>Hash code is extensively used to discover repeating cutting plans represented by a
     * different chromosomes, eliminate them and preserve diversity in the population.</p>
     *
     * <p>Calculation of the hash code intentionally designed to ignore the order of genes in the
     * chain. This way we can ensure that original chromosome and chromosome obtained by applying
     * any number of permutations to the chain of genes stored within original chromosome will have
     * same hash code.</p>
     *
     * <p>To speed up calculation of hash code gene caching is used: fresh hash code value is
     * calculated only if there is no cached value or chromosome structure has been changed since
     * last calculation.</p>
     *
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
