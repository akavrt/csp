package com.akavrt.csp.tester;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.genetic.*;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;
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
        params.setRunSteps(20000);
        params.setCrossoverRate(0.5);

        GeneticExecutionContext context = new GeneticContext(problem);
        Population population = new Population(context, params, metric);

        Algorithm initProcedure = new SimplifiedProcedure(generator);
        population.initialize(initProcedure);
        population.sort();

        System.out.println("*** INITIAL ***");
        tracePopulation(population, problem, tracer);

        GeneticBinaryOperator crossover = factory.createCrossoverOperator();
        GeneticUnaryOperator mutation = factory.createMutationOperator();

        for (int i = 0; i < params.getRunSteps(); i++) {
            population.generation(crossover, mutation);

            if (i == params.getRunSteps() - 1) {
                population.sort();

                System.out.println("*** GENERATION #" + (i + 1) + "***");
                tracePopulation(population, problem, tracer);
            }
        }


        /*
        Algorithm method = new GeneticAlgorithm(factory, metric, params);
        SimpleSolver solver = new SimpleSolver(problem, method);
        solver.solve();

        Solution best = solver.getBestSolution(metric);

        if (best != null) {
            ScalarTracer tracer = new ScalarTracer(metric, problem);
            String trace = TraceUtils.traceSolution(best, problem, tracer, true);
            System.out.println("*** OVERALL BEST solution found:\n" + trace);
        } else {
            System.out.println("Best is null.");
        }
        */
    }

    private static void tracePopulation(Population population, Problem problem,
                                        ScalarTracer tracer) {
        List<Solution> solutions = population.getSolutions();
        int i = 0;
        for (Solution solution : solutions) {
            String trace = TraceUtils.traceSolution(solution, problem, tracer, true);
            System.out.println(String.format("*** Chromosome #%d:\n%s", ++i, trace));
        }

    }
}
