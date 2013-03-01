package com.akavrt.csp.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 16:11
 */
public class OrderTest {
    private static final double DELTA = 1e-15;

    @Test
    public void orderCreation() {
        String id = "order1";
        double length = 500;
        double width = 50;

        Order order = new Order(id, length, width);

        assertEquals(id, order.getId());
        assertEquals(length, order.getLength(), DELTA);
        assertEquals(width, order.getWidth(), DELTA);
    }

}
