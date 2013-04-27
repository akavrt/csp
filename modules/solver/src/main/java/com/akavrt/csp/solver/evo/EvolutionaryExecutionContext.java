package com.akavrt.csp.solver.evo;

import com.akavrt.csp.solver.ExecutionContext;

/**
 * <p>Problem definition contains two immutable lists used to enumerate all orders and stock rolls
 * involved. The order of elements in these lists can't be changed and we can safely access orders
 * and rolls using integer indices.</p>
 *
 * <p>For convenience purposes here we are adding a couple of getter methods to replace calls like
 * ExecutionContext.getProblem().getOrder(index).getWidth() or
 * ExecutionContext.getProblem().getOrders().size() with
 * EvolutionaryExecutionContext.getOrderWidth(index) and
 * EvolutionaryExecutionContext.getOrdersSize(), respectively.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface EvolutionaryExecutionContext extends ExecutionContext {
    /**
     * <p>Returns width of the order accessed using its position in the list.</p>
     *
     * @param index Position of the target order in the list.
     * @return Width of the order, measured in abstract units.
     */
    double getOrderWidth(int index);

    /**
     * <p>Returns length of the order accessed using its position in the list.</p>
     *
     * @param index Position of the target order in the list.
     * @return Length of the order, measured in abstract units.
     */
    double getOrderLength(int index);

    /**
     * <p>Returns the number of orders specified in the the problem definition.</p>
     *
     * @return Number of orders defined within problem being solved.
     */
    int getOrdersSize();
}
