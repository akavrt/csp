package com.akavrt.csp.genetic;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.solver.genetic.Chromosome;
import com.akavrt.csp.solver.genetic.GeneticExecutionContext;
import com.akavrt.csp.solver.pattern.PatternGenerator;

import java.util.List;

/**
 * User: akavrt
 * Date: 16.04.13
 * Time: 19:10
 */
public abstract class PatternBasedMutation extends GroupBasedMutation {
    protected final PatternGenerator generator;

    public PatternBasedMutation(PatternGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void initialize(GeneticExecutionContext context) {
        generator.initialize(context.getProblem());
    }

    protected int[] calcDemand(double stockLength, Chromosome prototype) {
        List<Order> orders = prototype.getContext().getProblem().getOrders();

        int[] demand = new int[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            double produced = prototype.getProductionLengthForOrder(i);
            double orderLength = orders.get(i).getLength();

            if (orderLength > produced) {
                double ratio = (orderLength - produced) / stockLength;
                demand[i] = (int) Math.ceil(ratio);
//                demand[i] = ratio < 1 ? 1 : (int) Math.floor(ratio);
            }
        }

        return demand;
    }

}
