package com.akavrt.csp.core;

/**
 * <p>This simple class is used to model multiple cuts of the same width existed within single
 * pattern.</p>
 *
 * <p>MultiCut contains reference to associated order, so the width of the cut is always known.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class MultiCut {
    private final Order order;
    private int quantity;

    /**
     * <p>Create an instance linked with specific order with no active cuts.</p>
     *
     * @param order The linked order.
     */
    public MultiCut(Order order) {
        this(order, 0);
    }

    /**
     * <p>Create an instance linked with specific order with any number of active cuts.</p>
     *
     * @param order    The linked order.
     * @param quantity The number of cuts.
     */
    public MultiCut(Order order, int quantity) {
        this.order = order;
        this.quantity = quantity;
    }

    /**
     * <p>The order associated with instance of MultiCut on creation.</p>
     *
     * @return Linked order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * <p>Number of cuts of the same width.</p>
     *
     * @return The number of cuts.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * <p>Set new number of cuts.</p>
     *
     * @param quantity The number of cuts.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * <p>Add specified number of cuts to the current value.</p>
     *
     * @param quantity The added number of cuts.
     */
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    /**
     * <p>Calculate the total width of all cuts based on the number of cuts and width of the
     * associated order.</p>
     *
     * @return The total width of all cuts.
     */
    public double getWidth() {
        return order.getWidth() * quantity;
    }
}