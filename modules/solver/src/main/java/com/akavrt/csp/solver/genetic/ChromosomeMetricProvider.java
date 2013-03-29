package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.metrics.MetricProvider;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 23:43
 */
public class ChromosomeMetricProvider implements MetricProvider {
    private final GeneticExecutionContext context;
    private final Chromosome chromosome;
    private double cachedTrimArea;
    private double cachedTrimRatio;
    private int cachedActivePatternsCount;
    private int cachedUniquePatternsCount;
    private double cachedAverageUnderProductionRatio;
    private double cachedAverageOverProductionRatio;

    public ChromosomeMetricProvider(GeneticExecutionContext context, Chromosome chromosome) {
        this.context = context;
        this.chromosome = chromosome;

        resetCachedValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTrimArea() {
        if (cachedTrimArea < 0) {
            cachedTrimArea = 0;
            for (Gene gene : chromosome.getGenes()) {
                cachedTrimArea += gene.getTrimArea(context);
            }
        }

        return cachedTrimArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTrimRatio() {
        if (cachedTrimRatio < 0) {
            double trimArea = 0;
            double totalArea = 0;

            for (Gene gene : chromosome.getGenes()) {
                if (gene.getRoll() != null) {
                    trimArea += gene.getTrimArea(context);
                    totalArea += gene.getRoll().getArea();
                }
            }

            cachedTrimRatio = totalArea == 0 ? 0 : trimArea / totalArea;
        }

        return cachedTrimRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActivePatternsCount() {
        if (cachedActivePatternsCount < 0) {
            cachedActivePatternsCount = 0;
            for (Gene gene : chromosome.getGenes()) {
                if (gene.getRoll() != null) {
                    cachedActivePatternsCount++;
                }
            }
        }
        return cachedActivePatternsCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniquePatternsCount() {
        if (cachedUniquePatternsCount < 0) {
            Set<Integer> set = Sets.newHashSet();

            for (Gene gene : chromosome.getGenes()) {
                if (gene.getRoll() != null) {
                    set.add(gene.getPatternHashCode());
                }
            }

            cachedUniquePatternsCount = set.size();
        }

        return cachedUniquePatternsCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAverageUnderProductionRatio(Problem problem) {
        if (cachedAverageUnderProductionRatio < 0) {
            double underProductionRatio = 0;

            for (int i = 0; i < context.getOrdersSize(); i++) {
                double production = chromosome.getProductionLengthForOrder(i);
                double orderLength = context.getOrderLength(i);

                if (production < orderLength) {
                    underProductionRatio += 1 - production / orderLength;
                }
            }

            cachedAverageUnderProductionRatio = underProductionRatio / context.getOrdersSize();
        }

        return cachedAverageUnderProductionRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAverageOverProductionRatio(Problem problem) {
        if (cachedAverageOverProductionRatio < 0) {
            double overProductionRatio = 0;

            for (int i = 0; i < context.getOrdersSize(); i++) {
                double production = chromosome.getProductionLengthForOrder(i);
                double orderLength = context.getOrderLength(i);

                if (production > orderLength) {
                    overProductionRatio += production / orderLength - 1;
                }
            }

            cachedAverageOverProductionRatio = overProductionRatio / context.getOrdersSize();
        }

        return cachedAverageOverProductionRatio;
    }

    public void resetCachedValues() {
        cachedTrimArea = -1;
        cachedTrimRatio = -1;
        cachedActivePatternsCount = -1;
        cachedUniquePatternsCount = -1;
        cachedAverageUnderProductionRatio = -1;
        cachedAverageOverProductionRatio = -1;
    }
}
