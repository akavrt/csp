package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.core.xml.SolutionConverter;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 21:36
 */
public class SolutionConverterTest {
    private static final double DELTA = 1e-15;
    private int allowedCutsNumber;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private Problem problem;
    private SolutionConverter converter;

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
        converter = new SolutionConverter(problem);
    }

    @Test
    public void conversion() {
        List<Pattern> patterns = new ArrayList<Pattern>();

        Pattern pattern;

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

        Solution solution = new Solution(patterns);
        assertTrue(solution.isValid(problem));

        Element solutionElm = converter.export(solution);
        Solution extracted = converter.extract(solutionElm);

        assertEquals(solution.getPatterns().size(), extracted.getPatterns().size());
        assertEquals(solution.getUniquePatternsCount(), extracted.getUniquePatternsCount());

        for (Order order : orders) {
            assertEquals(solution.getProductionLengthForOrder(order),
                         extracted.getProductionLengthForOrder(order), DELTA);
        }

        assertEquals(solution.isOrdersFulfilled(orders), extracted.isOrdersFulfilled(orders));
        assertEquals(solution.isValid(problem), extracted.isValid(problem));

    }
}
