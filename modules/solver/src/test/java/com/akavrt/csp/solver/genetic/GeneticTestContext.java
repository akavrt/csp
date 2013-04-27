package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.solver.evo.EvolutionaryExecutionContext;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 14:59
 */
public class GeneticTestContext implements EvolutionaryExecutionContext {
    private final Problem problem;
    private final double[] orderWidth;
    private final double[] orderLength;

    public GeneticTestContext(Problem problem) {
        this.problem = problem;

        orderWidth = new double[problem.getOrders().size()];
        orderLength = new double[problem.getOrders().size()];
        for (int i = 0; i < problem.getOrders().size(); i++) {
            Order order = problem.getOrders().get(i);
            orderWidth[i] = order.getWidth();
            orderLength[i] = order.getLength();
        }
    }

    @Override
    public double getOrderWidth(int index) {
        return orderWidth[index];
    }

    @Override
    public double getOrderLength(int index) {
        return orderLength[index];
    }

    @Override
    public int getOrdersSize() {
        return orderWidth.length;
    }

    @Override
    public Problem getProblem() {
        return problem;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}