package com.akavrt.csp.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 21:18
 */
public class PatternTest {
    private static final double DELTA = 1e-15;

    private List<Order> orders;
    private Roll roll;

    @Before
    public void setUpProblem() {
        // preparing orders
        orders = new ArrayList<Order>();
        Order order;

        order = new Order("order1", 500, 50);
        orders.add(order);

        order = new Order("order2", 400, 40);
        orders.add(order);

        order = new Order("order3", 300, 30);
        orders.add(order);

        // preparing roll
        roll = new Roll("roll1", 300, 200);
    }

    @Test
    public void patternActivity() {
        Pattern pattern;

        // no cuts or roll at all
        pattern = new Pattern(orders);
        assertFalse(pattern.isActive());

        // no cuts or roll at all
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 0);
        assertFalse(pattern.isActive());

        // two cuts, no roll
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 1);
        assertFalse(pattern.isActive());

        // everything in place
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll);
        assertTrue(pattern.isActive());
    }

    @Test
    public void patternValidity() {
        Pattern pattern;
        int allowedCutsNumber = 6;

        // with no roll assigned
        pattern = new Pattern(orders);
        assertTrue(pattern.isValid(allowedCutsNumber));

        // actual cuts number: 2 + 2 + 2 = 6 == 6
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        assertTrue(pattern.isValid(allowedCutsNumber));

        // actual cuts number: 2 + 3 + 2 = 7 > 6
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 3);
        pattern.addCut(orders.get(2), 2);
        assertFalse(pattern.isValid(allowedCutsNumber));

        // with assigned roll
        pattern = new Pattern(orders);
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(allowedCutsNumber));

        // actual pattern width: 2 *  50 + 2 * 40 + 2 * 30 = 240 > 200
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll);
        assertFalse(pattern.isValid(allowedCutsNumber));

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170 < 200
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(allowedCutsNumber));

        // actual pattern width: 4 *  50 + 0 * 40 + 0 * 30 = 200 == 200
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 4);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(allowedCutsNumber));
    }

    @Test
    public void patternCharacteristics() {
        Pattern pattern;

        // let's check pattern width
        pattern = new Pattern(orders);
        assertEquals(0, pattern.getWidth(), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        assertEquals(170, pattern.getWidth(), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // roll wasn't set -> trim = 0
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        assertEquals(0, pattern.getTrim(), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // trim: 200 - 170 = 30
        // trim area: trim * roll length = 30 * 300 = 9000
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll);
        assertEquals(30, pattern.getTrim(), DELTA);
        assertEquals(9000, pattern.getTrimArea(), DELTA);

    }

}
