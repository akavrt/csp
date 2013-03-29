package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 19:58
 */
public class OrderManagerTest {
    private static final double DELTA = 1e-15;
    private List<Order> orders;
    private OrderManager manager;

    @Before
    public void setUpManager() {
        orders = Lists.newArrayList();
        orders.add(new Order("order1", 1000, 100));
        orders.add(new Order("order2", 1500, 150));
        orders.add(new Order("order3", 2100, 200));

        manager = new OrderManager(orders);
    }

    @Test
    public void getUnfulfilledOrdersArea() {
        double area = orders.get(0).getArea() + orders.get(1).getArea() + orders.get(2).getArea();
        assertEquals(area, manager.getUnfulfilledOrdersArea(), DELTA);
    }

    @Test
    public void calcDemand() {
        Roll roll1 = new Roll("roll1", 800, 400);
        Roll roll2 = new Roll("roll2", 700, 300);

        List<Roll> group = Lists.newArrayList(roll1, roll2);

        // 1000 / 1500 -> 0; 1500 / 1500 -> 1; 2100 / 1500 -> 1
        int[] actual = manager.calcGroupDemand(group);
        int[] expected = new int[]{0, 1, 1};
        assertArrayEquals(expected, actual);

        roll1 = new Roll("roll1", 400, 100);
        roll2 = new Roll("roll2", 300, 80);

        group = Lists.newArrayList(roll1, roll2);

        // 1000 / 700 -> 1; 1500 / 700 -> 2; 2100 / 700 -> 3
        actual = manager.calcGroupDemand(group);
        expected = new int[]{1, 2, 3};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getPatternWidth() {
        double width;

        width = manager.getPatternWidth(new int[]{0, 0, 0});
        assertEquals(0, width, DELTA);

        width = manager.getPatternWidth(new int[]{5, 3, 1});
        double expected = 5 * orders.get(0).getWidth() + 3 * orders.get(1).getWidth() + 1 *
                orders.get(2).getWidth();
        assertEquals(expected, width, DELTA);
    }

    @Test
    public void getTrimLossRatio() {
        Roll roll = new Roll("roll", 1800, 900);
        List<Roll> group = Lists.newArrayList(roll);

        double ratio;

        ratio = manager.getPatternTrimRatio(group, new int[]{0, 0, 0});
        assertEquals(1, ratio, DELTA);

        ratio = manager.getPatternTrimRatio(group, new int[]{1, 1, 1});
        assertEquals(0.5, ratio, DELTA);

        ratio = manager.getPatternTrimRatio(group, new int[]{2, 2, 2});
        assertEquals(0, ratio, DELTA);
    }

    @Test
    public void productionManagement() {
        assertFalse(manager.isOrdersFulfilled());

        Roll roll;
        List<Roll> group;

        roll = new Roll("roll1", 500, 200);
        group = Lists.newArrayList(roll);

        manager.updateProduction(group, new int[]{2, 0, 0});
        assertFalse(manager.isOrdersFulfilled());

        group = Lists.newArrayList();
        group.add(new Roll("roll2", 400, 300));
        group.add(new Roll("roll3", 400, 300));

        manager.updateProduction(group, new int[]{0, 2, 0});
        assertFalse(manager.isOrdersFulfilled());

        group = Lists.newArrayList();
        group.add(new Roll("roll4", 1100, 400));

        manager.updateProduction(group, new int[]{0, 0, 2});
        assertTrue(manager.isOrdersFulfilled());
    }

}
