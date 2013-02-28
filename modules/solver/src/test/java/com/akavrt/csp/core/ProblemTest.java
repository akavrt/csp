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
 * Time: 18:32
 */
public class ProblemTest {
    private Problem problem;
    private List<Order> problemOrders;
    private List<Roll> problemRolls;

    @Before
    public void setUpProblem() {
        // preparing orders
        problemOrders = new ArrayList<Order>();
        Order order;

        order = new Order(1, 500, 50);
        problemOrders.add(order);

        order = new Order(2, 400, 40);
        problemOrders.add(order);

        order = new Order(3, 300, 30);
        problemOrders.add(order);

        // preparing rolls
        problemRolls = new ArrayList<Roll>();
        Roll.Builder builder;

        builder = new Roll.Builder();
        builder.setId(1).setLength(100).setWidth(30);
        problemRolls.add(builder.build());

        builder = new Roll.Builder();
        builder.setId(2).setLength(90).setWidth(25).setStartTrimLength(5).setEndTrimLength(5);
        problemRolls.add(builder.build());

        builder = new Roll.Builder();
        builder.setId(3).setLength(80).setWidth(20).setLeftTrimWidth(2).setRightTrimWidth(5);
        problemRolls.add(builder.build());

        problem = new Problem(problemOrders, problemRolls);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addOrder() {
        // orders are immutable
        List<Order> orders = problem.getOrders();
        orders.add(new Order(4, 600, 60));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addRoll() {
        // rolls are immutable
        List<Roll> rolls = problem.getRolls();
        rolls.add(new Roll(4, 70, 15));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeOrder() {
        // orders are immutable
        List<Order> orders = problem.getOrders();
        orders.remove(orders.size() - 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeRoll() {
        // rolls are immutable
        List<Roll> rolls = problem.getRolls();
        rolls.remove(rolls.size() - 1);
    }

    @Test
    public void unsorted() {
        Order order = problem.getOrders().get(0);
        assertEquals(1, order.getId());

        Roll roll = problem.getRolls().get(0);
        assertEquals(1, roll.getId());
    }

    @Test
    public void rearrange() {
        // orders and rolls are sorted in ascending order of strip width
        Problem.Builder builder = new Problem.Builder();
        builder.setOrders(problemOrders).rearrangeOrders()
               .setRolls(problemRolls).rearrangeRolls();

        Problem rearranged = builder.build();

        Order order = rearranged.getOrders().get(0);
        assertEquals(3, order.getId());

        Roll roll = rearranged.getRolls().get(0);
        assertEquals(3, roll.getId());
    }

    @Test
    public void constraints() {
        // constraint wasn't set
        assertFalse(problem.isCutsNumberRestricted());

        // constraining the number of cuts in pattern
        Problem.Builder builder = new Problem.Builder();
        builder.setOrders(problemOrders).setRolls(problemRolls).setAllowedCutsNumber(20);
        Problem constrained = builder.build();
        assertTrue(constrained.isCutsNumberRestricted());
    }
}
