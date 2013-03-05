package com.akavrt.csp.core;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 05.03.13
 * Time: 19:13
 */
public class ProblemBuilderTest {
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
    }

    @Test
    public void rearrange() {
        // orders and rolls are sorted in ascending order of strip width
        ProblemBuilder builder = new ProblemBuilder();
        builder.setOrders(problemOrders);
        builder.setRolls(problemRolls);

        Problem rearranged = builder.build(true, true);

        Order order = rearranged.getOrders().get(0);
        assertEquals("order3", order.getId());

        Roll roll = rearranged.getRolls().get(0);
        assertEquals("roll3", roll.getId());
    }

    @Test
    public void extendedBuilderCapabilities() {
        Roll singleRoll = new Roll("roll1", 500, 300);
        Roll groupedRoll = new Roll("roll2", 400, 200);

        Order order = new Order("order4", 600, 60);

        ProblemBuilder builder = new ProblemBuilder();
        builder.addRoll(singleRoll);
        builder.addRolls(groupedRoll, 5);
        builder.setOrders(problemOrders);
        builder.addOrder(order);

        Problem tested = builder.build();

        assertEquals(6, tested.getRolls().size());
        assertEquals(problemOrders.size() + 1, tested.getOrders().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedRoll() {
        Roll singleRoll = new Roll("roll1", 500, 300);
        Roll groupedRoll = new Roll("roll2", 400, 200);
        Roll duplicatedRoll = new Roll("roll1", 100, 10);

        ProblemBuilder builder = new ProblemBuilder();
        builder.addRoll(singleRoll);
        builder.addRolls(groupedRoll, 5);
        builder.addRoll(duplicatedRoll);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedSetRolls() {
        Roll singleRoll = new Roll("roll1", 500, 300);
        Roll duplicatedRoll = new Roll("roll1", 100, 10);

        List<Roll> rolls = Lists.newArrayList();
        rolls.add(singleRoll);
        rolls.add(duplicatedRoll);

        ProblemBuilder builder = new ProblemBuilder();
        builder.setRolls(rolls);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedAddRolls() {
        Roll roll = new Roll("roll1", 500, 300);

        ProblemBuilder builder = new ProblemBuilder();
        builder.addRoll(roll);
        builder.addRolls(problemRolls);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedOrder() {
        Order order = new Order("order1", 500, 300);
        Order duplicate = new Order("order1", 200, 20);

        ProblemBuilder builder = new ProblemBuilder();
        builder.addOrder(order);
        builder.addOrder(duplicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedSetOrders() {
        Order order = new Order("order1", 500, 300);
        Order duplicate = new Order("order1", 200, 20);

        List<Order> orders = Lists.newArrayList();
        orders.add(order);
        orders.add(duplicate);

        ProblemBuilder builder = new ProblemBuilder();
        builder.setOrders(orders);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicatedAddOrders() {
        Order order = new Order("order1", 500, 300);

        ProblemBuilder builder = new ProblemBuilder();
        builder.addOrder(order);
        builder.addOrders(problemOrders);
    }

}
