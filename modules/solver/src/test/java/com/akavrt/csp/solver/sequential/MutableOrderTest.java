package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Order;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 02:37
 */
public class MutableOrderTest {
    private static final double DELTA = 1e-15;
    private Order order;

    @Before
    public void setUpOrder() {
        order = new Order("order", 1000, 100);
    }

    @Test
    public void zeroProduction() {
        MutableOrder mutable = new MutableOrder(order);
        assertEquals(0, mutable.getProducedLength(), DELTA);
    }

    @Test
    public void addProduction() {
        MutableOrder mutable = new MutableOrder(order);
        double firstProduction = 200;
        double secondProduction = 300;
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);

        assertEquals(firstProduction + secondProduction, mutable.getProducedLength(), DELTA);
    }

    @Test
    public void isFulfilled() {
        MutableOrder mutable = new MutableOrder(order);
        double firstProduction = 200;
        double secondProduction = 300;

        // +500, total is 500 < 1000
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);

        assertFalse(mutable.isFulfilled());

        // +500, total is 500 + 500 = 1000
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);
        assertTrue(mutable.isFulfilled());
    }

    @Test
    public void unfulfilledLengthAndArea() {
        MutableOrder mutable = new MutableOrder(order);
        double firstProduction = 200;
        double secondProduction = 300;

        // +500, total is 500 < 1000
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);

        double expected;
        expected = mutable.getOrder().getLength() - mutable.getProducedLength();
        assertEquals(expected, mutable.getUnfulfilledLength(), DELTA);
        assertEquals(expected * mutable.getOrder().getWidth(), mutable.getUnfulfilledArea(), DELTA);

        // +500, total is 500 + 500 = 1000
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);

        assertEquals(0, mutable.getUnfulfilledLength(), DELTA);
        assertEquals(0, mutable.getUnfulfilledArea(), DELTA);

        // +500, total is 500 + 500 + 500 > 1000
        mutable.addProducedLength(firstProduction);
        mutable.addProducedLength(secondProduction);

        assertEquals(0, mutable.getUnfulfilledLength(), DELTA);
        assertEquals(0, mutable.getUnfulfilledArea(), DELTA);
    }
}
