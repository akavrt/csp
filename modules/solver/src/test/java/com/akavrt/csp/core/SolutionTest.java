package com.akavrt.csp.core;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 01.03.13
 * Time: 00:21
 */
public class SolutionTest {
    private static final double DELTA = 1e-15;
    private int allowedCutsNumber;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private Problem problem;

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

        rolls.add(roll1);
        rolls.add(roll2);

        problem = new Problem(orders, rolls, allowedCutsNumber);
    }

    @Test
    public void managePatterns() {
        // create new empty instance
        Solution solution = new Solution();
        assertEquals(0, solution.getPatterns().size());

        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 0);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        // create new instance with 2 patterns
        solution = new Solution(patterns);
        assertEquals(2, solution.getPatterns().size());

        // clear patterns
        solution.clear();
        assertEquals(0, solution.getPatterns().size());
    }

    @Test
    public void trim() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;

        // 1 * 50 + 1 * 40 + 1 * 30 = 120
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);
        double trimArea1 = pattern.getTrimArea();

        // 2 * 50 + 2 * 40 + 2 * 30 = 240
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);
        double trimArea2 = pattern.getTrimArea();

        Solution solution = new Solution(patterns);
        assertEquals(trimArea1 + trimArea2, solution.getTrimArea(), DELTA);
    }

    @Test
    public void uniquePatterns() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // two different patterns is used
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertEquals(2, solution.getUniquePatternsCount());

        // same pattern added twice
        patterns.clear();

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution.setPatterns(patterns);
        assertEquals(1, solution.getUniquePatternsCount());
    }

    @Test
    public void production() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // the third order isn't produced
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertEquals(0, solution.getProductionLengthForOrder(orders.get(2)), DELTA);
        assertFalse(solution.isOrdersFulfilled(orders));

        // the first order is produced in insufficient length
        patterns.clear();
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertEquals(300, solution.getProductionLengthForOrder(orders.get(0)), DELTA);
        assertFalse(solution.isOrdersFulfilled(orders));

        // the first order is produced with exact length
        patterns.clear();
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertEquals(500, solution.getProductionLengthForOrder(orders.get(0)), DELTA);
        assertTrue(solution.isOrdersFulfilled(orders));
    }

    @Test
    public void validity() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;
        Solution solution;

        // valid solution
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertTrue(solution.isValid(problem));

        // patterns validity is broken
        patterns.clear();
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 0);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(roll1);
        patterns.add(pattern);

        // too much cuts: 12 > 10
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 4);
        pattern.addCut(orders.get(1), 4);
        pattern.addCut(orders.get(2), 4);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertFalse(solution.isValid(problem));

        // same roll used twice
        patterns.clear();
        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 0);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 1);
        pattern.addCut(orders.get(1), 2);
        pattern.addCut(orders.get(2), 2);
        pattern.setRoll(roll2);
        patterns.add(pattern);

        solution = new Solution(patterns);
        assertFalse(solution.isValid(problem));

    }
}
