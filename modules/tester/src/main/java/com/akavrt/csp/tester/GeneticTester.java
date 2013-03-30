package com.akavrt.csp.tester;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.tester.tracer.TraceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 01:30
 */
public class GeneticTester {
    private static final Logger LOGGER = LogManager.getLogger(GeneticTester.class);

    public static void main(String[] args) throws IOException {
        CspReader reader = new CspReader();
        try {
            InputStream is = SequentialTester.class.getClassLoader().getResourceAsStream("optimal_10.xml");
            reader.read(is);
        } catch (CspParseException e) {
            e.printStackTrace();
        }

        Problem problem = reader.getProblem();
        if (problem == null) {
            LOGGER.error("Error occurred while loading problem from external file.");
            return;
        }

        System.out.println(TraceUtils.traceProblem(problem, true, true));

        // compose genetic algorithm and run it
    }
}
