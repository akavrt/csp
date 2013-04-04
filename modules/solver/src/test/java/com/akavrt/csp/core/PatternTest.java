package com.akavrt.csp.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 28.02.13
 * Time: 21:18
 */
public class PatternTest {
    private static final double DELTA = 1e-15;
    private List<Order> orders;
    private Order order1;
    private Order order2;
    private Order order3;
    private Roll roll;
    private Problem problem;

    @Before
    public void setUpProblem() {
        // preparing orders
        orders = new ArrayList<Order>();

        order1 = new Order("order1", 500, 50);
        orders.add(order1);

        order2 = new Order("order2", 400, 40);
        orders.add(order2);

        order3 = new Order("order3", 300, 30);
        orders.add(order3);

        // preparing roll
        roll = new Roll("roll", 300, 200);
        
        ProblemBuilder builder = new ProblemBuilder();
        builder.addOrders(orders);
        builder.addRoll(roll);
        
        problem = builder.build();
    }

    @Test
    public void patternActivity() {
        Pattern pattern;

        // no cuts or roll at all
        pattern = new Pattern(problem);
        assertFalse(pattern.isActive());

        // no cuts or roll at all
        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 0);
        pattern.addCut(order3, 0);
        assertFalse(pattern.isActive());

        // two cuts, no roll
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 0);
        pattern.addCut(order3, 1);
        assertFalse(pattern.isActive());

        // everything in place
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 0);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll);
        assertTrue(pattern.isActive());
    }

    @Test
    public void patternValidity() {
        Pattern pattern;
        int allowedCutsNumber = 6;

        // with no roll assigned
        pattern = new Pattern(problem);
        assertTrue(pattern.isFeasible(allowedCutsNumber));

        // actual cuts number: 2 + 2 + 2 = 6 == 6
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        assertTrue(pattern.isFeasible(allowedCutsNumber));

        // actual cuts number: 2 + 3 + 2 = 7 > 6
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 3);
        pattern.addCut(order3, 2);
        assertFalse(pattern.isFeasible(allowedCutsNumber));

        // with assigned roll
        pattern = new Pattern(problem);
        pattern.setRoll(roll);
        assertTrue(pattern.isFeasible(allowedCutsNumber));

        // actual pattern width: 2 *  50 + 2 * 40 + 2 * 30 = 240 > 200
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll);
        assertFalse(pattern.isFeasible(allowedCutsNumber));

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170 < 200
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll);
        assertTrue(pattern.isFeasible(allowedCutsNumber));

        // actual pattern width: 4 *  50 + 0 * 40 + 0 * 30 = 200 == 200
        pattern = new Pattern(problem);
        pattern.addCut(order1, 4);
        pattern.addCut(order2, 0);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll);
        assertTrue(pattern.isFeasible(allowedCutsNumber));
    }

    @Test
    public void patternWidth() {
        Pattern pattern;

        // let's check pattern width
        pattern = new Pattern(problem);
        assertEquals(0, pattern.getWidth(), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        assertEquals(170, pattern.getWidth(), DELTA);
    }

    @Test
    public void patternTrim() {
        Pattern pattern;

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // roll wasn't set -> trim = 0
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        assertEquals(0, pattern.getTrimWidth(), DELTA);

        // actual pattern width: 2 *  50 + 1 * 40 + 1 * 30 = 170
        // trim: 200 - 170 = 30
        // trim area: trim * roll length = 30 * 300 = 9000
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll);
        assertEquals(30, pattern.getTrimWidth(), DELTA);
        assertEquals(9000, pattern.getTrimArea(), DELTA);
    }

    @Test
    public void calculateNumberOfCuts() {
        Pattern pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        assertEquals(4, pattern.getTotalNumberOfCuts());
    }

    @Test
    public void unknownOrder() {
        Pattern pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);

        int cutsBeforeUpdate = pattern.getTotalNumberOfCuts();

        Order unknown = new Order("unknown", 200, 20);
        pattern.addCut(unknown, 5);

        // unknown orders (not specified during pattern creation) are ignored
        assertTrue(cutsBeforeUpdate == pattern.getTotalNumberOfCuts());
    }

    @Test
    public void calculateProduction() {
        Pattern pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 2);

        pattern.setRoll(roll);

        assertEquals(0 * roll.getLength(), pattern.getProductionLengthForOrder(order1), DELTA);
        assertEquals(1 * roll.getLength(), pattern.getProductionLengthForOrder(order2), DELTA);
        assertEquals(2 * roll.getLength(), pattern.getProductionLengthForOrder(order3), DELTA);

        // unknown order is ignored
        Order unknown = new Order("unknown", 200, 20);
        pattern.addCut(unknown, 5);
        assertEquals(0, pattern.getProductionLengthForOrder(unknown), DELTA);
    }
}
