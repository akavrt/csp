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

        order = new Order(1, 500, 50);
        orders.add(order);

        order = new Order(2, 400, 40);
        orders.add(order);

        order = new Order(3, 300, 30);
        orders.add(order);

        // preparing roll
        Roll.Builder builder;

        builder = new Roll.Builder();
        builder.setId(1).setLength(300).setWidth(200);
        roll = builder.build();
    }

    @Test
    public void patternActivity() {
        Pattern pattern;

        // no cuts or roll at all
        pattern = new Pattern(orders.size());
        assertFalse(pattern.isActive());

        // no cuts or roll at all
        pattern = new Pattern(new int[]{0, 0 , 0});
        assertFalse(pattern.isActive());

        // two cuts, no roll
        pattern = new Pattern(new int[]{1, 0 , 1});
        assertFalse(pattern.isActive());

        // everything in place
        pattern = new Pattern(new int[]{1, 0 , 1});
        pattern.setRoll(roll);
        assertTrue(pattern.isActive());
    }

    @Test
    public void patternValidity() {
        Pattern pattern;
        int allowedCutsNumber = 6;

        // with no roll assigned
        pattern = new Pattern(orders.size());
        assertTrue(pattern.isValid(orders, allowedCutsNumber));

        // actual cuts number: 2 + 2 + 2 = 6 == 6
        pattern = new Pattern(new int[]{2, 2 , 2});
        assertTrue(pattern.isValid(orders, allowedCutsNumber));

        // actual cuts number: 2 + 3 + 2 = 7 > 6
        pattern = new Pattern(new int[]{2, 3 , 2});
        assertFalse(pattern.isValid(orders, allowedCutsNumber));

        // with assigned roll
        pattern = new Pattern(orders.size());
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(orders, allowedCutsNumber));

        // actual pattern width: 2 *  50 + 2 * 40 + 2 * 30 = 240 > 200
        pattern = new Pattern(new int[]{2, 2 , 2});
        pattern.setRoll(roll);
        assertFalse(pattern.isValid(orders, allowedCutsNumber));

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170 < 200
        pattern = new Pattern(new int[]{2, 1 , 1});
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(orders, allowedCutsNumber));

        // actual pattern width: 4 *  50 + 0 * 40 + 0 * 30 = 200 == 200
        pattern = new Pattern(new int[]{4, 0 , 0});
        pattern.setRoll(roll);
        assertTrue(pattern.isValid(orders, allowedCutsNumber));
    }

    @Test
    public void patternCharacteristics() {
        Pattern pattern;

        // let's check pattern width
        pattern = new Pattern(new int[]{0, 0 , 0});
        assertEquals(0, pattern.getWidth(orders), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        pattern = new Pattern(new int[]{2, 1 , 1});
        assertEquals(170, pattern.getWidth(orders), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // roll wasn't set -> trim = 0
        pattern = new Pattern(new int[]{2, 1 , 1});
        assertEquals(0, pattern.getTrim(orders), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // trim: 200 - 170 = 30
        // trim area: trim * roll length = 30 * 300 = 9000
        pattern = new Pattern(new int[]{2, 1 , 1});
        pattern.setRoll(roll);
        assertEquals(30, pattern.getTrim(orders), DELTA);
        assertEquals(9000, pattern.getTrimArea(orders), DELTA);

    }

}
