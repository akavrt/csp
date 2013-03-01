package com.akavrt.csp.core;

import com.akavrt.csp.params.ObjectiveParameters;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 01.03.13
 * Time: 16:05
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
        Order order;

        order = new Order(1, 500, 50);
        orders.add(order);

        order = new Order(2, 400, 40);
        orders.add(order);

        order = new Order(3, 300, 30);
        orders.add(order);

        // preparing rolls
        List<Roll> rolls = new ArrayList<Roll>();
        Roll.Builder builder;

        builder = new Roll.Builder();
        builder.setId(1).setLength(300).setWidth(200);
        roll1 = builder.build();
        rolls.add(roll1);

        builder = new Roll.Builder();
        builder.setId(2).setLength(500).setWidth(300);
        roll2 = builder.build();
        rolls.add(roll2);

        builder = new Roll.Builder();
        builder.setId(3).setLength(100).setWidth(40);
        roll3 = builder.build();
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
    public void solutionWithDifferentUniquePattern() {
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
