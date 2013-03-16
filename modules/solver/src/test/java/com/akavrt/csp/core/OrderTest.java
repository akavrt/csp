package com.akavrt.csp.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 16:11
 */
public class OrderTest {
    private static final double DELTA = 1e-15;

    @Test
    public void createOrder() {
        String id = "order";
        double length = 500;
        double width = 50;

        Order order = new Order(id, length, width);

        assertEquals(id, order.getId());
        assertEquals(length, order.getLength(), DELTA);
        assertEquals(width, order.getWidth(), DELTA);
    }

    @Test
    public void calculateOrderArea() {
        String id = "order";
        double length = 500;
        double width = 50;

        Order order = new Order(id, length, width);
        assertEquals(length * width, order.getArea(), DELTA);
    }

    @Test
    public void orderValidity() {
        String id = "order";
        double length = 500;
        double width = 50;

        Order order = new Order(id, length, width);
        assertTrue(order.isValid());

        order = new Order(id, 0, width);
        assertFalse(order.isValid());

        order = new Order(id, length, 0);
        assertFalse(order.isValid());

        order = new Order(id, 0, 0);
        assertFalse(order.isValid());
    }

    @Test
    public void internalIdReproducibility() {
        String id = "order";
        double length = 500;
        double width = 50;

        Order firstOrder = new Order(id, length, width);
        Order secondOrder = new Order(id, length, width);
        assertEquals(firstOrder.getInternalId(), secondOrder.getInternalId());
    }


}
