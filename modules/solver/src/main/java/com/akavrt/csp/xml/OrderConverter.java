package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 22:57
 */
public class OrderConverter extends StripConverter<Order> {

    @Override
    public String getRootTag() {
        return OrderTags.ORDER;
    }

    @Override
    public Order createStrip(String id, double length, double width) {
        return new Order(id, length, width);
    }

    public interface OrderTags {
        String ORDER = "order";
    }
}
