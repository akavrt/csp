package com.akavrt.csp.core;

/**
 * <p>This class represents an order. Required length can be composed from any number of separate
 * strips with the same width.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Order extends Strip {
    private static final String FORMAT_TEMPLATE = "Order '%s':\n    W = %.2f\n    L = %.2f";

    /**
     * {@inheritDoc}
     */
    public Order(String id, double length, double width) {
        super(id, length, width);
    }

    /**
     * {@inheritDoc}
     *
     * @return String representation of the order with custom formatting.
     */
    @Override
    public String toString() {
        return String.format(FORMAT_TEMPLATE, getId(), getWidth(), getLength());
    }
}
