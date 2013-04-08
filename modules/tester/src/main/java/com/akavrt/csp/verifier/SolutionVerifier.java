package com.akavrt.csp.verifier;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.utils.SolutionFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 16:45
 */
public class SolutionVerifier {
    private static final Logger LOGGER = LogManager.getLogger(SolutionVerifier.class);

    public static void main(String[] args) {
        String path = "/Users/akavrt/Development/source/csp/data/optimal/optimal_10.xml";

        CspReader reader = new CspReader();
        try {
            File problemFile = new File(path);
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        if (reader.getProblem() == null) {
            LOGGER.error("Problem wasn't loaded.");
            return;
        }

        if (reader.getSolutions() == null || reader.getSolutions().size() == 0) {
            LOGGER.error("No solution was loaded.");
            return;
        }

        Solution solution = reader.getSolutions().get(0);
        if (solution != null) {
            ScalarMetric scalarMetric = new ScalarMetric();
            ScalarTracer tracer = new ScalarTracer(scalarMetric);
            String caption = "Solution to test:";
            String trace = tracer.trace(solution);
            String formatted = SolutionFormatter.format(solution, caption, trace, true);

            System.out.println(formatted);
        }

        System.out.println("\n Production:");
        for (Order order : reader.getProblem().getOrders()) {
            System.out.println(String.format("Order '%s': required %.3f m  produced %.3f m",
                                             order.getId(), order.getLength(),
                                             solution.getProductionLengthForOrder(order)));

        }
    }
}
