package com.akavrt.csp.metrics;

import com.akavrt.csp.core.*;
import com.akavrt.csp.metrics.complex.PatternReductionMetric;
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
public class PatternReductionMetricTest {
    private static final double DELTA = 1e-15;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private PatternReductionMetric metric;
    private Problem problem;
    
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

        ProblemBuilder builder = new ProblemBuilder();
        builder.addOrders(orders);
        builder.addRoll(roll1);
        builder.addRoll(roll2);
        
        problem = builder.build();
                
        metric = new PatternReductionMetric();
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution(problem);

        // if there are less than 2 patterns within solution, 
        // patterns ration always equals to zero
        assertEquals(0, metric.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithOnePattern() {
        Solution solution = new Solution(problem);
        Pattern pattern;

        pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        assertEquals(0, metric.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithOneUniquePattern() {
        Solution solution = new Solution(problem);
        Pattern pattern;

        pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(0, metric.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithDifferentUniquePatterns() {
        Solution solution = new Solution(problem);
        Pattern pattern;

        pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(1, metric.evaluate(solution), DELTA);
    }
    
}
