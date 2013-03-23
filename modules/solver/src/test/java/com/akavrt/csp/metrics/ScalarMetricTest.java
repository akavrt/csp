package com.akavrt.csp.metrics;

import com.akavrt.csp.core.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 01:52
 */
public class ScalarMetricTest {
    private static final double DELTA = 1e-15;
    private int allowedCutsNumber;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private Roll roll3;
    private Problem problem;
    private ScalarMetricParameters params;
    private ScalarMetric evaluator;

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

        params = new ScalarMetricParameters();
        params.setTrimFactor(0.33);
        params.setPatternsFactor(0.33);
        params.setProductionFactor(0.33);

        evaluator = new ScalarMetric(problem, params);
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution();

        // no active patterns -> no trim
        // if there are less than 2 patterns within solution,
        // patterns ration always equals to zero
        // 100% underproduction -> production ration equals to 1
        assertEquals(1 * params.getProductionFactor(), evaluator.evaluate(solution), DELTA);
    }

}
