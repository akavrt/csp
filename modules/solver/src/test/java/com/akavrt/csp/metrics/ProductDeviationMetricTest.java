package com.akavrt.csp.metrics;

import com.akavrt.csp.core.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 23.03.13
 * Time: 00:50
 */
public class ProductDeviationMetricTest {
    private static final double DELTA = 1e-15;
    private int allowedCutsNumber;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private Roll roll3;
    private Problem problem;
    private ProductDeviationMetric metric;

    @Before
    public void setUpProblem() {
        allowedCutsNumber = 10;

        // preparing orders
        orders = new ArrayList<Order>();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));

        // preparing rolls
        List<Roll> rolls = new ArrayList<Roll>();
        roll1 = new Roll("roll1", 300, 200);
        roll2 = new Roll("roll2", 500, 300);
        roll3 = new Roll("roll3", 100, 40);

        rolls.add(roll1);
        rolls.add(roll2);
        rolls.add(roll3);

        problem = new Problem(orders, rolls, allowedCutsNumber);

        metric = new ProductDeviationMetric(problem);
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution();

        // 100% underproduction -> production ration equals to 1
        assertEquals(1, metric.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithExactProduction() {
        Solution solution = new Solution();
        Pattern pattern;

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll3);
        solution.addPattern(pattern);

        assertEquals(0, metric.evaluate(solution), DELTA);
    }

}
