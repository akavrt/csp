package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;

import java.util.List;

/**
 * <p>This metric corresponds to the minimization of the both under and overproduction of the final
 * strip ordered.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProductDeviationMetric extends MinimizationMetric {
    private final Problem problem;

    /**
     * <p>Creates an instance of evaluator. Can be reused to evaluate different solutions against
     * same problem provided during creation.</p>
     *
     * @param problem The problem in question.
     */
    public ProductDeviationMetric(Problem problem) {
        this.problem = problem;
    }

    /**
     * <p>Calculates product deviation ratio.</p>
     *
     * <p>Evaluated value may vary from 0 to 1. The less is better: zero means that there is no
     * overproduction or underproduction.</p>
     *
     * @param solution The evaluated solution.
     * @return Product deviation fractional ratio.
     */
    @Override
    public double evaluate(Solution solution) {
        double totalProductDeviation = 0;

        List<Order> orders = problem.getOrders();
        for (Order order : orders) {
            double productDeviation = Math.abs(order.getLength() -
                                                       solution.getProductionLengthForOrder(order));
            totalProductDeviation += productDeviation / order.getLength();
        }

        return totalProductDeviation / orders.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String abbreviation() {
        return "PD";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "Product deviation fractional ratio";
    }
}