package com.akavrt.csp.tester;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.core.xml.CspWriter;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.SimpleSolver;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.HaesslerProcedure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: akavrt
 * Date: 17.03.13
 * Time: 19:41
 */
public class Tester {
    public static void main(String[] args) throws IOException {
        CspReader reader = new CspReader();
        try {
            InputStream is = Tester.class.getClassLoader().getResourceAsStream("optimal_10.xml");
            reader.read(is);
        } catch (CspParseException e) {
            e.printStackTrace();
        }

        Problem problem = reader.getProblem();
        if (problem == null) {
            System.out.println("Error occurred while loading problem from external file.");
            return;
        }

        String message = String.format("Problem definition:\n    %d orders;\n    %d rolls.",
                                       problem.getOrders().size(),
                                       problem.getRolls().size());
        System.out.println(message);

        PatternGeneratorParameters generatorParams = new PatternGeneratorParameters();
        generatorParams.setGenerationTrialsLimit(20);
        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

        Algorithm method = new HaesslerProcedure(generator);

        SimpleSolver solver = new SimpleSolver(problem, method);
        solver.solve();

        Metric metric = new ScalarMetric(problem, new ScalarMetricParameters());
        Solution best = solver.getBestSolution(metric);

        if (best != null) {
            System.out.print(String.format("Best solution found: metric = %.2f; %s",
                                             metric.evaluate(best),
                                             best.isValid(problem) ? "valid" : "invalid"));

            if (!best.isValid(problem)) {
                System.out.print(" -> ");
                if (!best.isPatternsValid(problem)) {
                    System.out.print("problem with patterns; ");
                }

                if (!best.isRollsValid()) {
                    System.out.print("problem with repeated rolls; ");
                }

                if (!best.isOrdersFulfilled(problem.getOrders())) {
                    System.out.print("unfulfilled orders;");
                }
            }
            System.out.print(".\n");

            System.out.println(best);

            File file = new File("/Users/akavrt/Sandbox/sample-output-10.xml");

            CspWriter writer = new CspWriter();

            writer.setProblem(problem);
            writer.addSolution(best);

            writer.write(file, true);
        } else {
            System.out.println("Best is null.");
        }

    }
}
