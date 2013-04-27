package com.akavrt.csp.metrics;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.metrics.complex.ScalarMetricParameters;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 01:52
 */
public class ScalarMetricTest {
    private static final double DELTA = 1e-15;
    private Problem problem;
    private ScalarMetricParameters params;
    private ScalarMetric evaluator;

    @Before
    public void setUpProblem() {
        int allowedCutsNumber = 10;

        // preparing orders
        List<Order> orders = Lists.newArrayList();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));

        // preparing rolls
        List<Roll> rolls = Lists.newArrayList();
        rolls.add(new Roll("roll1", 300, 200));
        rolls.add(new Roll("roll2", 500, 300));
        rolls.add(new Roll("roll3", 100, 40));

        problem = new Problem(orders, rolls, allowedCutsNumber);

        params = new ScalarMetricParameters();
        params.setTrimFactor(0.33);
        params.setPatternsFactor(0.33);
        params.setProductionFactor(0.33);

        evaluator = new ScalarMetric(params);
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution(problem);

        // no active patterns -> no trim
        // if there are less than 2 patterns within solution,
        // patterns ration always equals to zero
        // 100% underproduction -> production ration equals to 1
        assertEquals(1 * params.getProductionFactor(), evaluator.evaluate(solution), DELTA);
    }

}
