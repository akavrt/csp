package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.*;
import com.akavrt.csp.core.metadata.ProblemMetadata;
import com.akavrt.csp.utils.Unit;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 22:02
 */
public class CspConverterTest {
    private static final double DELTA = 1e-15;
    private Problem problem;
    private Solution solution1;
    private Solution solution2;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUpProblem() {
        int allowedCutsNumber = 10;

        // preparing orders
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order("order1", 500, 50));
        orders.add(new Order("order2", 400, 40));
        orders.add(new Order("order3", 300, 30));

        // preparing rolls
        List<Roll> rolls = new ArrayList<Roll>();

        Roll roll1 = new Roll("roll1", 300, 200);
        Roll roll2 = new Roll("roll2", 500, 300);

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
        CspWriter writer = new CspWriter();
        writer.setProblem(problem);

        Element element = writer.process();

        CspReader reader = new CspReader();
        reader.process(element);

        Problem extractedProblem = reader.getProblem();
        assertFalse(extractedProblem == null);
        assertEquals(problem.getOrders().size(), extractedProblem.getOrders().size());
        assertEquals(problem.getRolls().size(), extractedProblem.getRolls().size());
        assertEquals(problem.getAllowedCutsNumber(), extractedProblem.getAllowedCutsNumber());
    }

    @Test
    public void solutionsConversion() {
        CspWriter writer = new CspWriter();
        writer.setProblem(problem);
        writer.addSolution(solution1);
        writer.addSolution(solution2);

        Element element = writer.process();

        CspReader reader = new CspReader();
        reader.process(element);

        List<Solution> extractedSolutions = reader.getSolutions();
        assertFalse(extractedSolutions == null);
        assertEquals(2, extractedSolutions.size());
        for (Solution solution : extractedSolutions) {
            assertTrue(solution.getTrimArea() == solution1.getTrimArea()
                               || solution.getTrimArea() == solution2.getTrimArea());
        }
    }

    @Test
    public void readFromExternalFile() {
        CspReader reader = new CspReader();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("test-problem.xml");
            reader.read(is);
        } catch (CspParseException e) {
            e.printStackTrace();
        }

        Problem extractedProblem = reader.getProblem();
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

        List<Solution> extractedSolutions = reader.getSolutions();
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

    @Test
    public void writeToExternalFile() throws IOException, CspParseException {
        File file = folder.newFile("problem-write-test.xml");
        assertTrue(file.exists());

        CspWriter writer = new CspWriter();
        ProblemMetadata metadata = new ProblemMetadata();
        metadata.setAuthor("Victor Balabanov");
        metadata.setUnits(Unit.METER);
        problem.setMetadata(metadata);

        writer.setProblem(problem);
        writer.addSolution(solution1);
        writer.addSolution(solution2);

        writer.write(file, true);

        CspReader reader = new CspReader();
        reader.read(file);

        Problem extractedProblem = reader.getProblem();
        assertFalse(extractedProblem == null);
        assertEquals(problem.getOrders().size(), extractedProblem.getOrders().size());
        assertEquals(problem.getRolls().size(), extractedProblem.getRolls().size());
        assertEquals(problem.getAllowedCutsNumber(), extractedProblem.getAllowedCutsNumber());

        assertFalse(extractedProblem.getMetadata() == null);
        assertEquals("Victor Balabanov",  extractedProblem.getMetadata().getAuthor());
        assertEquals(Unit.METER.getName(),  extractedProblem.getMetadata().getUnits().getName());
        assertFalse(Unit.MILLIMETER.getName().equals(extractedProblem.getMetadata().getUnits().getName()));


        List<Solution> extractedSolutions = reader.getSolutions();
        assertFalse(extractedSolutions == null);
        assertEquals(2, extractedSolutions.size());
        for (Solution solution : extractedSolutions) {
            assertTrue(solution.getTrimArea() == solution1.getTrimArea()
                               || solution.getTrimArea() == solution2.getTrimArea());
        }
    }


}
