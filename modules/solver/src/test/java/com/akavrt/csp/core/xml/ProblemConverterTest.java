package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.metadata.ProblemMetadata;
import com.google.common.collect.Maps;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 19:17
 */
public class ProblemConverterTest {
    private static final double DELTA = 1e-15;
    private Problem problem;
    private List<Order> problemOrders;
    private List<Roll> problemRolls;

    @Before
    public void setUpProblem() {
        // preparing metadata
        ProblemMetadata metadata = new ProblemMetadata();
        metadata.setName("Composite test");
        metadata.setAuthor("Victor Balabanov");
        metadata.setDescription("Test problem");

        Calendar calendar = Calendar.getInstance();
        calendar.set(1984, Calendar.JULY, 28);
        Date date = new Date(calendar.getTimeInMillis());
        metadata.setDate(date);

        // preparing orders
        problemOrders = new ArrayList<Order>();
        problemOrders.add(new Order("order1", 500, 50));
        problemOrders.add(new Order("order2", 400, 40));
        problemOrders.add(new Order("order3", 300, 30));
        problemOrders.add(new Order("order4", 200, 20));

        // preparing rolls
        problemRolls = new ArrayList<Roll>();
        problemRolls.add(new Roll("roll1", 100, 30));
        problemRolls.add(new Roll("roll2", 90, 25));
        problemRolls.add(new Roll("roll3", 80, 20));

        problem = new Problem.Builder().setOrders(problemOrders).setRolls(problemRolls)
                                       .setAllowedCutsNumber(10).setMetadata(metadata).build();
    }

    @Test
    public void metadataConversion() {
        ProblemMetadata metadata = problem.getMetadata();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(metadata.getDate());

        ProblemConverter converter = new ProblemConverter();
        Element problemElm = converter.export(problem);
        Problem extracted = converter.extract(problemElm);

        ProblemMetadata extractedMetadata = extracted.getMetadata();
        assertFalse(extractedMetadata == null);
        assertEquals(metadata.getName(), extractedMetadata.getName());
        assertEquals(metadata.getAuthor(), extractedMetadata.getAuthor());
        assertEquals(metadata.getDescription(), extractedMetadata.getDescription());

        Calendar extractedCalendar = Calendar.getInstance();
        extractedCalendar.setTime(extractedMetadata.getDate());

        assertEquals(calendar.get(Calendar.YEAR), extractedCalendar.get(Calendar.YEAR));
        assertEquals(calendar.get(Calendar.MONTH), extractedCalendar.get(Calendar.MONTH));
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH),
                     extractedCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY),
                     extractedCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(calendar.get(Calendar.MINUTE), extractedCalendar.get(Calendar.MINUTE));
    }

    @Test
    public void constraintsConversion() {
        ProblemConverter converter = new ProblemConverter();
        Element problemElm = converter.export(problem);
        Problem extracted = converter.extract(problemElm);

        assertEquals(problem.getAllowedCutsNumber(), extracted.getAllowedCutsNumber());
    }

    @Test
    public void ordersConversion() {
        ProblemConverter converter = new ProblemConverter();
        Element problemElm = converter.export(problem);
        Problem extracted = converter.extract(problemElm);

        assertEquals(problem.getOrders().size(), extracted.getOrders().size());

        Map<String, Order> extractedOrders = Maps.newHashMap();
        for (Order order : extracted.getOrders()) {
            extractedOrders.put(order.getId(), order);
        }

        for (Order order : problemOrders) {
            assertTrue(extractedOrders.containsKey(order.getId()));
        }

        for (Order matchingOrder : problemOrders) {
            Order order = extractedOrders.get(matchingOrder.getId());
            assertFalse(order == null);

            assertEquals(matchingOrder.getId(), order.getId());
            assertEquals(matchingOrder.getLength(), order.getLength(), DELTA);
            assertEquals(matchingOrder.getWidth(), order.getWidth(), DELTA);
        }
    }

    @Test
    public void rollsConversion() {
        ProblemConverter converter = new ProblemConverter();
        Element problemElm = converter.export(problem);
        Problem extracted = converter.extract(problemElm);

        assertEquals(problem.getRolls().size(), extracted.getRolls().size());

        Map<String, Roll> extractedRolls = Maps.newHashMap();
        for (Roll roll : extracted.getRolls()) {
            extractedRolls.put(roll.getId(), roll);
        }

        for (Roll roll : problemRolls) {
            assertTrue(extractedRolls.containsKey(roll.getId()));
        }

        for (Roll matchingRoll : problemRolls) {
            Roll roll = extractedRolls.get(matchingRoll.getId());
            assertFalse(roll == null);

            assertEquals(matchingRoll.getId(), roll.getId());
            assertEquals(matchingRoll.getLength(), roll.getLength(), DELTA);
            assertEquals(matchingRoll.getWidth(), roll.getWidth(), DELTA);
        }
    }
}
