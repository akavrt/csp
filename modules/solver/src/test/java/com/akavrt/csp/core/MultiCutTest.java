package com.akavrt.csp.core;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 13.03.13
 * Time: 01:02
 */
public class MultiCutTest {
    private static final double DELTA = 1e-15;

    Order order;

    @Before
    public void setUpOrder() {
        order = new Order("order", 500, 40);
    }

    @Test
    public void createEmptyCut() {
        MultiCut cut = new MultiCut(order);
        assertEquals(0, cut.getQuantity());
    }

    @Test
    public void setCutQuantity() {
        MultiCut cut = new MultiCut(order);
        int quantity = 5;
        cut.setQuantity(quantity);
        assertEquals(quantity, cut.getQuantity());
    }

    @Test
    public void addCutQuantity() {
        MultiCut cut = new MultiCut(order);
        int quantity = 5;
        cut.addQuantity(quantity);
        cut.addQuantity(quantity);
        assertEquals(quantity + quantity, cut.getQuantity());
    }

    @Test
    public void calculateCutWidth() {
        MultiCut cut = new MultiCut(order);
        int quantity = 5;
        double totalWidth = quantity * order.getWidth();

        cut.setQuantity(quantity);
        assertEquals(totalWidth, cut.getWidth(), DELTA);
    }

}
