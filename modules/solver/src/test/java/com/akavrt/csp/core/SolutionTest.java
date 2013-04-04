package com.akavrt.csp.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 01.03.13
 * Time: 00:21
 */
public class SolutionTest {
    private static final double DELTA = 1e-15;
    private Order order1;
    private Order order2;
    private Order order3;
    private Roll roll1;
    private Roll roll2;
    private Problem problem;

    @Before
    public void setUpProblem() {
        int allowedCutsNumber = 10;

        // preparing orders
        List<Order> orders = new ArrayList<Order>();

        order1 = new Order("order1", 500, 50);
        orders.add(order1);

        order2 = new Order("order2", 400, 40);
        orders.add(order2);

        order3 = new Order("order3", 300, 30);
        orders.add(order3);

        // preparing rolls
        List<Roll> rolls = new ArrayList<Roll>();

        roll1 = new Roll("roll1", 300, 200);
        roll2 = new Roll("roll2", 500, 300);

        rolls.add(roll1);
        rolls.add(roll2);

        problem = new Problem(orders, rolls, allowedCutsNumber);
    }

    @Test
    public void managePatterns() {
        // create new empty instance
        Solution solution = new Solution(problem);
        assertEquals(0, solution.getPatterns().size());

        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 0);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        // create new instance with 2 patterns
        solution = new Solution(problem, patterns);
        assertEquals(2, solution.getPatterns().size());

        // clear patterns
        solution.clear();
        assertEquals(0, solution.getPatterns().size());
    }

    @Test
    public void calculateTrimArea() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;

        // 1 * 50 + 1 * 40 + 1 * 30 = 120
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);
        double trimArea1 = pattern.getTrimArea();

        // 2 * 50 + 2 * 40 + 2 * 30 = 240
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);
        double trimArea2 = pattern.getTrimArea();

        Solution solution = new Solution(problem, patterns);
        assertEquals(trimArea1 + trimArea2, solution.getMetricProvider().getTrimArea(), DELTA);
    }

    @Test
    public void uniquePatterns() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // two different patterns is used
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertEquals(2, solution.getMetricProvider().getUniquePatternsCount());

        // same pattern added twice
        patterns.clear();

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution.setPatterns(patterns);
        assertEquals(1, solution.getMetricProvider().getUniquePatternsCount());
    }

    @Test
    public void underProduction() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // the third order isn't produced
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertEquals(0, solution.getProductionLengthForOrder(order3), DELTA);
        assertFalse(solution.isOrdersFulfilled());

        // the first order is produced in insufficient length
        patterns.clear();
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertEquals(300, solution.getProductionLengthForOrder(order1), DELTA);
        assertFalse(solution.isOrdersFulfilled());
    }

    @Test
    public void exactProduction() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // the first order is produced with exact length
        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertEquals(500, solution.getProductionLengthForOrder(order1), DELTA);
        assertTrue(solution.isOrdersFulfilled());
    }

    @Test
    public void overProduction() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // the first order is produced with excess
        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertEquals(800, solution.getProductionLengthForOrder(order1), DELTA);
        assertTrue(solution.isOrdersFulfilled());
    }

    @Test
    public void validity() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // valid solution
        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertTrue(solution.isFeasible());
    }

    @Test
    public void invalidPatterns() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // patterns validity is broken
        pattern = new Pattern(problem);
        pattern.addCut(order1, 0);
        pattern.addCut(order2, 1);
        pattern.addCut(order3, 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        // too much cuts: 12 > 10
        pattern = new Pattern(problem);
        pattern.addCut(order1, 4);
        pattern.addCut(order2, 4);
        pattern.addCut(order3, 4);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertFalse(solution.isFeasible());
    }

    @Test
    public void repeatedRoll() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // same roll used twice
        pattern = new Pattern(problem);
        pattern.addCut(order1, 2);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        pattern = new Pattern(problem);
        pattern.addCut(order1, 1);
        pattern.addCut(order2, 2);
        pattern.addCut(order3, 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(problem, patterns);
        assertFalse(solution.isFeasible());
    }
}
