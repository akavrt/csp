package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.SimpleSolver;
import com.akavrt.csp.solver.genetic.GeneticAlgorithm;
import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.tester.tracer.TraceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
            InputStream is = SequentialTester.class.getClassLoader()
                                                   .getResourceAsStream("optimal_10.xml");
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

        PatternGeneratorParameters generatorParams = new PatternGeneratorParameters();
        generatorParams.setGenerationTrialsLimit(20);
        PatternGenerator generator = new ConstrainedPatternGenerator(problem, generatorParams);

        PatternBasedComponentsFactory factory = new PatternBasedComponentsFactory(generator);
        ScalarMetric metric = new ScalarMetric(problem, new ScalarMetricParameters());
        ScalarTracer tracer = new ScalarTracer(metric, problem);

        GeneticAlgorithmParameters params = new GeneticAlgorithmParameters();
        params.setPopulationSize(30);
        params.setExchangeSize(20);
        params.setRunSteps(1000);
        params.setCrossoverRate(0.5);

        long start = System.currentTimeMillis();
        Algorithm method = new GeneticAlgorithm(factory, metric, params);
        SimpleSolver solver = new SimpleSolver(problem, method);
        solver.solve();
        long end = System.currentTimeMillis();

        List<Solution> solutions = solver.getSolutions();
//        Collections.sort(solutions, metric.getReverseComparator());

        System.out.println("*** RESULTS ***");
        tracePopulation(solutions, problem, tracer);
        System.out.println(String.format("Run time: %.2f second", 0.001 * (end - start)));

        /*
        Solution best = solver.getBestSolution(metric);

        if (best != null) {
            String trace = TraceUtils.traceSolution(best, problem, tracer, true);
            System.out.println("*** OVERALL BEST solution found:\n" + trace);

            File file = new File("/Users/akavrt/Sandbox/output-opt10-ga.xml");

            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(method);
            writer.setNumberOfExecutions(1);
//            writer.setCollector(collector);
            writer.setProblem(problem);
            writer.addSolution(best);

            writer.write(file, true);
        } else {
            System.out.println("Best is null.");
        }
        */
    }

    private static void tracePopulation(List<Solution> solutions, Problem problem,
                                        ScalarTracer tracer) {
        int i = 0;
        for (Solution solution : solutions) {
            String trace = TraceUtils.traceSolution(solution, problem, tracer, i == 0);
            System.out.println(String.format("*** Chromosome #%d:\n%s", ++i, trace));
        }

    }
}
