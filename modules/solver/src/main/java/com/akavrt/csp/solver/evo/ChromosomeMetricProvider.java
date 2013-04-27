package com.akavrt.csp.solver.evo;

import com.akavrt.csp.metrics.MetricProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * <p>Implements all base metrics defined within MetricProvider interface for Chromosome.</p>
 *
 * <p>Simple caching strategy is used to speed up evaluations: metric value is recalculated only if
 * there is no cached value or chromosome structure has been changed since last evaluation.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ChromosomeMetricProvider implements MetricProvider {
    private final EvolutionaryExecutionContext context;
    private final Chromosome chromosome;
    private double cachedTrimArea;
    private double cachedTrimRatio;
    private int cachedActivePatternsCount;
    private int cachedUniquePatternsCount;
    private double cachedAverageUnderProductionRatio;
    private double cachedMaxUnderProductionRatio;
    private double cachedAverageOverProductionRatio;
    private double cachedMaxOverProductionRatio;
    private double cachedAggregatedTrimArea;
    private double cachedAggregatedTrimRatio;

    /**
     * <p>Creates context-aware instance of ChromosomeMetricProvider tied with specific instance of
     * Chromosome.</p>
     *
     * @param context    Context provides access to the problem definition (information about
     *                   orders is required to evaluate a number of basic metrics).
     * @param chromosome The chromosome to be evaluated using predefined set of basic metrics.
     */
    public ChromosomeMetricProvider(EvolutionaryExecutionContext context, Chromosome chromosome) {
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
    public double getAverageUnderProductionRatio() {
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
    public double getMaximumUnderProductionRatio() {
        if (cachedMaxUnderProductionRatio < 0) {
            double maximum = 0;
            for (int i = 0; i < context.getOrdersSize(); i++) {
                double demand = context.getOrderLength(i);
                double production = chromosome.getProductionLengthForOrder(i);
                double ratio = production < demand ? (1 - production / demand) : 0;

                if (i == 0 || ratio > maximum) {
                    maximum = ratio;
                }
            }

            cachedMaxUnderProductionRatio = maximum;
        }

        return cachedMaxUnderProductionRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAverageOverProductionRatio() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximumOverProductionRatio() {
        if (cachedMaxOverProductionRatio < 0) {
            double maximum = 0;
            for (int i = 0; i < context.getOrdersSize(); i++) {
                double demand = context.getOrderLength(i);
                double production = chromosome.getProductionLengthForOrder(i);
                double ratio = production > demand ? (production / demand - 1) : 0;

                if (i == 0 || ratio > maximum) {
                    maximum = ratio;
                }
            }

            cachedMaxOverProductionRatio = maximum;
        }

        return cachedMaxOverProductionRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAggregatedTrimArea() {
        if (cachedAggregatedTrimArea < 0) {
            double overProducedArea = 0;

            for (int i = 0; i < context.getOrdersSize(); i++) {
                double production = chromosome.getProductionLengthForOrder(i);
                double orderLength = context.getOrderLength(i);

                if (production > orderLength) {
                    overProducedArea += (production - orderLength) * context.getOrderWidth(i);
                }
            }

            cachedAggregatedTrimArea = getTrimArea() + overProducedArea;
        }

        return cachedAggregatedTrimArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAggregatedTrimRatio() {
        if (cachedAggregatedTrimRatio < 0) {
            double totalArea = 0;
            for (Gene gene : chromosome.getGenes()) {
                if (gene.getRoll() != null) {
                    totalArea += gene.getRoll().getArea();
                }
            }

            cachedAggregatedTrimRatio = totalArea == 0 ? 0 : getAggregatedTrimArea() / totalArea;
        }

        return cachedAggregatedTrimRatio;
    }

    /**
     * <p>Resets cached values, should be manually called every time when chromosome structure is
     * changed.</p>
     */
    public void resetCachedValues() {
        cachedTrimArea = -1;
        cachedTrimRatio = -1;
        cachedActivePatternsCount = -1;
        cachedUniquePatternsCount = -1;
        cachedAverageUnderProductionRatio = -1;
        cachedMaxUnderProductionRatio = -1;
        cachedAverageOverProductionRatio = -1;
        cachedMaxOverProductionRatio = -1;
        cachedAggregatedTrimArea = -1;
        cachedAggregatedTrimRatio = -1;
    }

    public double getAverageGroupSize() {
        if (chromosome.size() == 0) {
            return 0;
        }

        Map<Integer, Integer> groups = Maps.newHashMap();
        for (Gene gene : chromosome.getGenes()) {
            int patternHash = gene.getPatternHashCode();
            int count = groups.containsKey(patternHash) ? groups.get(patternHash) : 0;

            groups.put(patternHash, ++count);
        }

        double size = 0;
        for (Integer count : groups.values()) {
            size += count;
        }

        return size / groups.size();
    }

}
