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
public class TrimLossMetricTest {
    private static final double DELTA = 1e-15;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private TrimLossMetric metric;

    @Before
    public void setUpProblem() {
        // preparing orders
        orders = new ArrayList<Order>();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));

        // preparing rolls
        roll1 = new Roll("roll1", 300, 200);
        roll2 = new Roll("roll2", 500, 300);

        metric = new TrimLossMetric();
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution();

        // no active patterns -> no trim
        assertEquals(0, metric.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithoutTrim() {
        Solution solution = new Solution();
        Pattern pattern;

        // 2 *  50 + 1 * 40 + 2 * 30 = 200
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        // 3 * 50 + 0 * 40 + 5 * 30 = 300
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 3);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 5);
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(0, metric.evaluate(solution), DELTA);
    }
}
