package com.akavrt.csp.solver.evo;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.solver.ExecutionContext;

/**
 * <p>Implementation of the EvolutionaryExecutionContext used by evolutionary algorithms.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class EvolutionaryContext implements EvolutionaryExecutionContext {
    private final ExecutionContext parentContext;

    public EvolutionaryContext(ExecutionContext parentContext) {
        this.parentContext = parentContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getOrderWidth(int index) {
        return parentContext.getProblem().getOrders().get(index).getWidth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getOrderLength(int index) {
        return parentContext.getProblem().getOrders().get(index).getLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrdersSize() {
        return parentContext.getProblem().getOrders().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Problem getProblem() {
        return parentContext.getProblem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return parentContext.isCancelled();
    }
}

