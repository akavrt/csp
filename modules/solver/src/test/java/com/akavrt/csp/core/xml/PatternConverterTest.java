package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.core.xml.PatternConverter;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 19:54
 */
public class PatternConverterTest {
    private static final double DELTA = 1e-15;
    private Problem problem;
    private List<Order> orders;
    private List<Roll> rolls;
    private PatternConverter converter;

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

        converter = new PatternConverter(problem);
    }

    @Test
    public void conversion() {
        Pattern pattern = new Pattern(orders);
        pattern.addCut(orders.get(0), 2);
        pattern.addCut(orders.get(1), 1);
        pattern.addCut(orders.get(2), 1);
        pattern.setRoll(rolls.get(3));

        assertEquals(orders.size(), pattern.getCuts().size());

        Element patternElm = converter.export(pattern);

        Pattern extracted = converter.extract(patternElm);

        assertEquals(pattern.getCuts().size(), extracted.getCuts().size());
        assertEquals(pattern.getRoll().getId(), extracted.getRoll().getId());

        int allowedCutsNumber = problem.getAllowedCutsNumber();
        assertEquals(pattern.isValid(allowedCutsNumber), extracted.isValid(allowedCutsNumber));
        assertEquals(pattern.isActive(), extracted.isActive());

        assertEquals(pattern.getTrim(), extracted.getTrim(), DELTA);
        assertEquals(pattern.getTrimArea(), extracted.getTrimArea(), DELTA);
        for (Order order : orders) {
            assertEquals(pattern.getProductionLengthForOrder(order),
                         extracted.getProductionLengthForOrder(order), DELTA);
        }

        assertEquals(pattern.getCutsHashCode(), extracted.getCutsHashCode());
    }

}
