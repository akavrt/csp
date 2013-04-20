package com.akavrt.csp.core;

import com.akavrt.csp.metrics.MetricProvider;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * <p>Implements all base metrics defined within MetricProvider interface for Solution.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SolutionMetricProvider implements MetricProvider {
    private final Solution solution;
    private final Problem problem;

    public SolutionMetricProvider(Problem problem, Solution solution) {
        this.problem = problem;
        this.solution = solution;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTrimArea() {
        double trimArea = 0;
        for (Pattern pattern : solution.getPatterns()) {
            trimArea += pattern.getTrimArea();
        }

        return trimArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTrimRatio() {
        double trimArea = 0;
        double totalArea = 0;

        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                trimArea += pattern.getTrimArea();
                totalArea += pattern.getRoll().getArea();
            }
        }

        return totalArea == 0 ? 0 : trimArea / totalArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActivePatternsCount() {
        int activePatternsCount = 0;
        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                activePatternsCount++;
            }
        }

        return activePatternsCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUniquePatternsCount() {
        Set<Integer> set = Sets.newHashSet();

        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                set.add(pattern.getCutsHashCode());
            }
        }

        return set.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAverageUnderProductionRatio() {
        double underProductionRatio = 0;

        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double production = solution.getProductionLengthForOrder(order);
            if (production < order.getLength()) {
                underProductionRatio += 1 - production / order.getLength();
            }
        }

        return underProductionRatio / orders.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximumUnderProductionRatio() {
        double maximum = 0;
        int i = 0;
        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double demand = order.getLength();
            double production = solution.getProductionLengthForOrder(order);
            double ratio = production < demand ? (1 - production / demand) : 0;

            if (i == 0 || ratio > maximum) {
                maximum = ratio;
            }

            i++;
        }

        return maximum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAverageOverProductionRatio() {
        double overProductionRatio = 0;

        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double production = solution.getProductionLengthForOrder(order);
            if (production > order.getLength()) {
                overProductionRatio += production / order.getLength() - 1;
            }
        }

        return overProductionRatio / orders.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximumOverProductionRatio() {
        double maximum = 0;
        int i = 0;
        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double demand = order.getLength();
            double production = solution.getProductionLengthForOrder(order);
            double ratio = production > demand ? (production / demand - 1) : 0;

            if (i == 0 || ratio > maximum) {
                maximum = ratio;
            }

            i++;
        }

        return maximum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAggregatedTrimArea() {
        double overProducedArea = 0;

        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double production = solution.getProductionLengthForOrder(order);
            if (production > order.getLength()) {
                overProducedArea += (production - order.getLength()) * order.getWidth();
            }
        }

        return getTrimArea() + overProducedArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAggregatedTrimRatio() {
        double totalArea = 0;
        for (Pattern pattern : solution.getPatterns()) {
            if (pattern.isActive()) {
                totalArea += pattern.getRoll().getArea();
            }
        }

        return totalArea == 0 ? 0 : getAggregatedTrimArea() / totalArea;
    }

}
