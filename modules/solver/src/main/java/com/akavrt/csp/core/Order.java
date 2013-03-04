package com.akavrt.csp.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <p>This class represents an order. Required length can be composed from any number of separate
 * strips with the same width.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Order extends Strip {
    private static final String FORMAT_TEMPLATE = "Order '%s':\n    W = %.2f\n    L = %.2f";
    private int internalId;

    /**
     * {@inheritDoc}
     */
    public Order(String id, double length, double width) {
        super(id, length, width);
        internalId = calculateInternalId();
    }

    private int calculateInternalId() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    /**
     * <p>Unique internal identifier is used for validity checks and inside the optimization
     * routine. Calculated based on the id of the order.</p>
     *
     * @return The unique internal identifier of the order.
     */
    public int getInternalId() {
        return internalId;
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
