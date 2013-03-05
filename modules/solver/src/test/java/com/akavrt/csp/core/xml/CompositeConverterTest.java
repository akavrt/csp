package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.core.metadata.ProblemMetadata;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 22:02
 */
public class CompositeConverterTest {
    private static final double DELTA = 1e-15;

    private int allowedCutsNumber;
    private List<Order> orders;
    private Roll roll1;
    private Roll roll2;
    private Problem problem;
    private Solution solution1;
    private Solution solution2;

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

        solution1 = new Solution(patterns);

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

        solution2 = new Solution(patterns);
    }

    @Test
    public void problemConversion() {
        CompositeExporter exporter = new CompositeExporter();
        exporter.setProblem(problem);

        Document doc = exporter.export();

        CompositeExtractor extractor = new CompositeExtractor();
        extractor.extract(doc);

        Problem extractedProblem = extractor.getProblem();
        assertFalse(extractedProblem == null);
        assertEquals(problem.getOrders().size(), extractedProblem.getOrders().size());
        assertEquals(problem.getRolls().size(), extractedProblem.getRolls().size());
        assertEquals(problem.getAllowedCutsNumber(), extractedProblem.getAllowedCutsNumber());
    }

    @Test
    public void solutionsConversion() {
        CompositeExporter exporter = new CompositeExporter();
        exporter.setProblem(problem);
        exporter.addSolution(solution1);
        exporter.addSolution(solution2);

        Document doc = exporter.export();

        CompositeExtractor extractor = new CompositeExtractor();
        extractor.extract(doc);

        List<Solution> extractedSolutions = extractor.getSolutions();
        assertFalse(extractedSolutions == null);
        assertEquals(2, extractedSolutions.size());
        for (Solution solution : extractedSolutions) {
            assertTrue(solution.getTrimArea() == solution1.getTrimArea()
                               || solution.getTrimArea() == solution2.getTrimArea());
        }
    }

    @Test
    public void extractionFromExternalFile() {
        InputStream is = null;
        Document doc = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream("test-problem.xml");

            SAXBuilder sax = new SAXBuilder();
            doc = sax.build(is);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        assertFalse(doc == null);

        CompositeExtractor extractor = new CompositeExtractor();
        extractor.extract(doc);

        Problem extractedProblem = extractor.getProblem();
        assertFalse(extractedProblem == null);
        assertEquals(3, extractedProblem.getOrders().size());
        assertEquals(5, extractedProblem.getRolls().size());
        assertEquals(10, extractedProblem.getAllowedCutsNumber());

        ProblemMetadata metadata = extractedProblem.getMetadata();
        assertFalse(metadata == null);
        assertEquals("Victor Balabanov", metadata.getAuthor());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, Calendar.MARCH, 5, 18, 32);

        Calendar extractedCalendar = Calendar.getInstance();
        extractedCalendar.setTime(metadata.getDate());

        assertEquals(calendar.get(Calendar.YEAR), extractedCalendar.get(Calendar.YEAR));
        assertEquals(calendar.get(Calendar.MONTH), extractedCalendar.get(Calendar.MONTH));
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH),
                     extractedCalendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY),
                     extractedCalendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(calendar.get(Calendar.MINUTE), extractedCalendar.get(Calendar.MINUTE));

        List<Solution> extractedSolutions = extractor.getSolutions();
        assertFalse(extractedSolutions == null);
        assertEquals(2, extractedSolutions.size());

        Solution solution = extractedSolutions.get(0);
        assertTrue(solution.isValid(extractedProblem));

        Order targetOrder = null;
        for (Order order : extractedProblem.getOrders()) {
            if (order.getId().equals("order1")) {
                targetOrder = order;
                break;
            }
        }

        assertFalse(targetOrder == null);
        assertEquals(500, targetOrder.getLength(), DELTA);
        assertEquals(50, targetOrder.getWidth(), DELTA);
        assertEquals(500, solution.getProductionLengthForOrder(targetOrder), DELTA);
        assertTrue(solution.isOrdersFulfilled(extractedProblem.getOrders()));

        solution = extractedSolutions.get(1);
        assertFalse(solution.isValid(extractedProblem));
        assertEquals(300, solution.getProductionLengthForOrder(targetOrder), DELTA);
        assertFalse(solution.isOrdersFulfilled(extractedProblem.getOrders()));
    }

}
