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
        problemOrders.add(new Order("order1", 500, 50));
        problemOrders.add(new Order("order2", 400, 40));
        problemOrders.add(new Order("order3", 300, 30));

        // preparing rolls
        problemRolls = new ArrayList<Roll>();
        problemRolls.add(new Roll("roll1", 100, 30));
        problemRolls.add(new Roll("roll2", 90, 25));
        problemRolls.add(new Roll("roll3", 80, 20));

        problem = new Problem(problemOrders, problemRolls);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addOrder() {
        // orders are immutable
        List<Order> orders = problem.getOrders();
        orders.add(new Order("order4", 600, 60));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addRoll() {
        // rolls are immutable
        List<Roll> rolls = problem.getRolls();
        rolls.add(new Roll("roll4", 70, 15));
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
        assertEquals("order1", order.getId());

        Roll roll = problem.getRolls().get(0);
        assertEquals("roll1", roll.getId());
    }

    @Test
    public void rearrange() {
        // orders and rolls are sorted in ascending order of strip width
        Problem.Builder builder = new Problem.Builder();
        builder.setOrders(problemOrders).rearrangeOrders()
               .setRolls(problemRolls).rearrangeRolls();

        Problem rearranged = builder.build();

        Order order = rearranged.getOrders().get(0);
        assertEquals("order3", order.getId());

        Roll roll = rearranged.getRolls().get(0);
        assertEquals("roll3", roll.getId());
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

    @Test
    public void extendedBuilderCapabilities() {
        Roll singleRoll = new Roll("roll1", 500, 300);
        Roll groupedRoll = new Roll("roll2", 400, 200);

        Order order = new Order("order4", 600, 60);

        Problem.Builder builder = new Problem.Builder();
        builder.addRoll(singleRoll).addRolls(groupedRoll, 5);
        builder.setOrders(problemOrders);
        builder.addOrder(order);

        Problem tested = builder.build();

        assertEquals(6, tested.getRolls().size());
        assertEquals(problemOrders.size() + 1, tested.getOrders().size());
    }

    @Test
    public void chaining() {
        Order order = new Order("order4", 600, 60);

        Problem.Builder builder = new Problem.Builder();
        builder.setRolls(problemRolls);
        builder.setOrders(problemOrders).addOrder(order);

        Problem tested = builder.build();

        assertEquals(problemOrders.size() + 1, tested.getOrders().size());

        builder = new Problem.Builder();
        builder.setRolls(problemRolls);
        builder.addOrder(order).setOrders(problemOrders);

        tested = builder.build();

        assertEquals(problemOrders.size(), tested.getOrders().size());

    }
}
