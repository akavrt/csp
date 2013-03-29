package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 19:54
 */
public class PatternGroupConverterTest {
    private static final double DELTA = 1e-15;
    private Problem problem;
    private List<Order> orders;
    private List<Roll> rolls;
    private PatternGroupConverter converter;

    @Before
    public void setUpProblem() {
        // preparing orders
        orders = new ArrayList<Order>();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));
        orders.add(new Order("order4", 200, 20));

        // preparing rolls
        rolls = new ArrayList<Roll>();
        rolls.add(new Roll("roll1", 100, 30));
        rolls.add(new Roll("roll2", 90, 25));
        rolls.add(new Roll("roll3", 80, 20));
        rolls.add(new Roll("target-roll", 300, 200));

        ProblemBuilder builder = new ProblemBuilder();
        builder.setOrders(orders);
        builder.setRolls(rolls);
        builder.setAllowedCutsNumber(10);

        problem = builder.build();

        converter = new PatternGroupConverter(problem);
    }

    @Test
    public void singleRoll() {
        Pattern pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(rolls.get(3));

        PatternGroup expected = new PatternGroup(pattern.getCuts());
        expected.addRoll(pattern.getRoll());

        assertEquals(1, expected.getRolls().size());

        Element patternElm = converter.export(expected);

        PatternGroup actual = converter.extract(patternElm);

        assertEquals(1, actual.getRolls().size());
        assertNotNull(actual.getRolls().get(0));
        assertEquals(pattern.getRoll().getId(), actual.getRolls().get(0).getId());

        Pattern actualPattern = new Pattern(problem);
        actualPattern.setCuts(actual.getCuts());
        actualPattern.setRoll(actual.getRolls().get(0));

        assertEquals(pattern.getCuts().size(), actualPattern.getCuts().size());

        int allowedCutsNumber = problem.getAllowedCutsNumber();
        assertEquals(pattern.isValid(allowedCutsNumber), actualPattern.isValid(allowedCutsNumber));
        assertEquals(pattern.isActive(), actualPattern.isActive());

        assertEquals(pattern.getTrim(), actualPattern.getTrim(), DELTA);
        assertEquals(pattern.getTrimArea(), actualPattern.getTrimArea(), DELTA);
        for (Order order : orders) {
            assertEquals(pattern.getProductionLengthForOrder(order),
                         actualPattern.getProductionLengthForOrder(order), DELTA);
        }

        assertEquals(pattern.getCutsHashCode(), actualPattern.getCutsHashCode());
    }

    @Test
    public void multipleRolls() {
        Pattern pattern = new Pattern(problem);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(rolls.get(3));

        PatternGroup expected = new PatternGroup(pattern.getCuts());
        expected.addRoll(pattern.getRoll());
        expected.addRoll(rolls.get(0));
        expected.addRoll(rolls.get(1));

        Element patternElm = converter.export(expected);

        PatternGroup actual = converter.extract(patternElm);

        assertEquals(expected.getRolls().size(), actual.getRolls().size());
    }

}
