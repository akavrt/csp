package com.akavrt.csp.core;

import com.akavrt.csp.core.metadata.OrderMetadata;
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
    private OrderMetadata metadata;

    /**
     * <p>Create an instance of Order with specified parameters.<p/>
     *
     * @param id     The user-defined identifier, have to be unique across all orders added to the
     *               problem.
     * @param length The length or the ordered strip.
     * @param width  The width or the ordered strip.
     */
    public Order(String id, double length, double width) {
        super(id, length, width);
        internalId = calculateInternalId();
    }

    /**
     * <p>This value is calculated based on the value of order id only when the instance is
     * created. Used internally in optimization routing and for validity checks.</p>
     *
     * @return The internal id of the order.
     */
    private int calculateInternalId() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    /**
     * <p>Unique internal identifier is used for validity checks and inside the optimization
     * routine. Calculated based on the id of the order. Uniqueness of this value is ensured by the
     * uniqueness of String order id.</p>
     *
     * @return The internal identifier of the order.
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

    /**
     * <p>Provide extended description of the order.</p>
     *
     * @return Additional information about the order.
     */
    public OrderMetadata getMetadata() {
        return metadata;
    }

    /**
     * <p>Set additional information about the order.</p>
     *
     * @param metadata Additional information about the order.
     */
    public void setMetadata(OrderMetadata metadata) {
        this.metadata = metadata;
    }
}
