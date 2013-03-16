package com.akavrt.csp.solver.pattern;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.ProblemBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * User: akavrt
 * Date: 15.03.13
 * Time: 01:23
 */
public class ConstrainedPatternGeneratorTest {
    private Problem problem;

    @Before
    public void setUpProblem() {
        ProblemBuilder builder = new ProblemBuilder();

        builder.addOrder(new Order("order1", 100, 10));
        builder.addOrder(new Order("order2", 200, 20));
        builder.addOrder(new Order("order3", 300, 30));
        builder.addOrder(new Order("order4", 400, 40));
        builder.addOrder(new Order("order5", 500, 50));
        builder.addOrder(new Order("order6", 600, 60));
        builder.addOrder(new Order("order7", 700, 70));
        builder.addOrder(new Order("order8", 800, 80));
        builder.addOrder(new Order("order9", 900, 90));
        builder.addOrder(new Order("order10", 1000, 100));

        builder.setAllowedCutsNumber(15);

        problem = builder.build();
    }

    @Test
    public void numberOfCuts() {
        // let's use default parameters
        PatternGenerator generator = new ConstrainedPatternGenerator(problem);
        int[] demand = {9, 6, 7, 5, 11, 2, 9, 4, 7, 3};
        double rollWidth = 1000;

        for (int i = 0; i < 100; i++) {
            int[] pattern = generator.generate(rollWidth, demand, 0);
            int totalCuts = 0;

            for (int j = 0; j < pattern.length; j++) {
                totalCuts += pattern[j];
            }
            assertTrue(totalCuts <= problem.getAllowedCutsNumber());
        }
    }

    @Test
    public void widthOfThePattern() {
        PatternGeneratorParameters params = new PatternGeneratorParameters();
        params.setGenerationTrialsLimit(10);

        PatternGenerator generator = new ConstrainedPatternGenerator(problem, params);
        int[] demand = {9, 6, 7, 5, 11, 2, 9, 4, 7, 3};
        double rollWidth = 1000;

        for (int i = 0; i < 100; i++) {
            int[] pattern = generator.generate(rollWidth, demand, 0);

            double totalWidth = 0;
            for (int j = 0; j < pattern.length; j++) {
                totalWidth += pattern[j] * problem.getOrders().get(j).getWidth();
            }
            assertTrue(totalWidth <= rollWidth);
        }
    }

    @Test
    public void greedyPlacement() {
        PatternGeneratorParameters params = new PatternGeneratorParameters();
        params.setGenerationTrialsLimit(200);

        PatternGenerator generator = new ConstrainedPatternGenerator(problem, params);
        int[] demand = {2, 2, 2, 2, 2, 2, 2, 2, 2, 0};
        double rollWidth = 1000;

        for (int i = 0; i < 100; i++) {
            int[] pattern = generator.generate(1000, demand, 0);

            int totalCuts = 0;
            double totalWidth = 0;
            for (int j = 0; j < pattern.length; j++) {
                totalCuts += pattern[j];
                totalWidth += pattern[j] * problem.getOrders().get(j).getWidth();
            }
            assertTrue(totalCuts <= problem.getAllowedCutsNumber());
            assertTrue(totalWidth <= rollWidth);

            // maximum possible total width of the pattern is 900, so trim always must be >= 100
            double trimLoss = rollWidth - totalWidth;
            assertTrue(trimLoss >= 100);
        }
    }
}
