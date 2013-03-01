package com.akavrt.csp.metrics;

import com.akavrt.csp.core.*;
import com.akavrt.csp.params.ObjectiveParameters;
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
    private ObjectiveParameters params;
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

        ObjectiveParameters.Builder paramsBuilder = new ObjectiveParameters
                .Builder();
        params = paramsBuilder.setTrimFactor(0.33)
                              .setPatternsFactor(0.33)
                              .setProductionFactor(0.33).build();
        evaluator = new ScalarMetric(problem, params);
    }

    @Test
    public void emptySolution() {
        Solution solution = new Solution();

        // no active patterns -> no trim
        assertEquals(0, evaluator.getTrimRatio(solution), DELTA);
        // if there are less than 2 patterns within solution, patterns ration always equals to zero
        assertEquals(0, evaluator.getPatternsRatio(solution), DELTA);
        // 100% underproduction -> production ration equals to 1
        assertEquals(1, evaluator.getProductionRatio(solution), DELTA);
        assertEquals(1 * params.getProductionFactor(), evaluator.evaluate(solution), DELTA);
    }

    @Test
    public void solutionWithoutTrim() {
        Solution solution = new Solution();
        Pattern pattern;

        // 2 *  50 + 1 * 40 + 2 * 30 = 200
        pattern = new Pattern(new int[]{2, 1, 2});
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        // 3 * 50 + 0 * 40 + 5 * 30 = 300
        pattern = new Pattern(new int[]{3, 0, 5});
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(0, evaluator.getTrimRatio(solution), DELTA);
    }

    @Test
    public void solutionWithExactProduction() {
        Solution solution = new Solution();
        Pattern pattern;

        pattern = new Pattern(new int[]{0, 1, 1});
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(new int[]{1, 0, 0});
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        pattern = new Pattern(new int[]{0, 1, 0});
        pattern.setRoll(roll3);
        solution.addPattern(pattern);

        assertEquals(0, evaluator.getProductionRatio(solution), DELTA);
    }

    @Test
    public void solutionWithOnePattern() {
        Solution solution = new Solution();
        Pattern pattern;

        pattern = new Pattern(new int[]{1, 1, 1});
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        assertEquals(0, evaluator.getPatternsRatio(solution), DELTA);
    }

    @Test
    public void solutionWithOneUniquePattern() {
        Solution solution = new Solution();
        Pattern pattern;

        pattern = new Pattern(new int[]{1, 1, 1});
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(new int[]{1, 1, 1});
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(0, evaluator.getPatternsRatio(solution), DELTA);
    }

    @Test
    public void solutionWithDifferentUniquePatterns() {
        Solution solution = new Solution();
        Pattern pattern;

        pattern = new Pattern(new int[]{1, 1, 1});
        pattern.setRoll(roll1);
        solution.addPattern(pattern);

        pattern = new Pattern(new int[]{2, 2, 2});
        pattern.setRoll(roll2);
        solution.addPattern(pattern);

        assertEquals(1, evaluator.getPatternsRatio(solution), DELTA);
    }

}
