package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.MaxValue;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.analyzer.StandardDeviation;
import com.akavrt.csp.analyzer.xml.RunResultWriter;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.solver.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.metrics.*;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithm;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.tracer.Tracer;
import com.akavrt.csp.utils.ProblemFormatter;
import com.akavrt.csp.utils.SolutionFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: akavrt
 * Date: 31.03.13
 * Time: 01:30
 */
public class GeneticTester {
    private static final Logger LOGGER = LogManager.getLogger(GeneticTester.class);

    public static void main(String[] args) throws IOException {
        Problem problem = loadProblem("/Users/akavrt/Sandbox/csp/optimal_10.xml");
        if (problem == null) {
            LOGGER.error("Error occurred while loading problem from external file.");
            return;
        }

        System.out.println(ProblemFormatter.format(problem));

        PatternGenerator generator = createPatternGenerator();
        ScalarMetric metric = new ScalarMetric();
        Algorithm method = createAlgorithm(generator, metric);

        XmlEnabledCollector collector = createXmlCollector();
        SimpleCollector debugCollector = createDebugCollector(collector);

        MultistartSolver solver = new MultistartSolver(problem, method, 10);
        solver.addCollector(collector);
        solver.addCollector(debugCollector);

        solver.solve();

        debugCollector.process();

        File file = new File("/Users/akavrt/Sandbox/csp/optimal_10_ga_run.xml");

        RunResultWriter writer = new RunResultWriter();

        writer.setAlgorithm(method);
        writer.setNumberOfExecutions(solver.getNumberOfRuns());
        writer.setCollector(collector);
        writer.setProblem(problem);

        writer.write(file, true);


        /*
        SimpleSolver solver = new SimpleSolver(problem, method);

        long start = System.currentTimeMillis();
        solver.solve();
        long end = System.currentTimeMillis();

        List<Solution> solutions = solver.getSolutions();

        System.out.println("*** RESULTS ***");
        ScalarTracer tracer = new ScalarTracer(metric);
        tracePopulation(solutions, tracer);
        System.out.println(String.format("Run time: %.2f second", 0.001 * (end - start)));
        */
    }

    private static void tracePopulation(List<Solution> solutions, Tracer<Solution> tracer) {
        int i = 0;
        for (Solution solution : solutions) {
            String caption = String.format("*** Chromosome #%d:", ++i);
            String trace = tracer.trace(solution);
            String formatted = SolutionFormatter.format(solution, caption, trace, i == 1);

            System.out.println(formatted);
        }

    }

    private static Problem loadProblem(String path) {
        CspReader reader = new CspReader();
        try {
            File problemFile = new File(path);
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        return reader.getProblem();
    }

    private static PatternGenerator createPatternGenerator() {
        PatternGeneratorParameters generatorParams = new PatternGeneratorParameters();
        generatorParams.setGenerationTrialsLimit(20);

        return new ConstrainedPatternGenerator(generatorParams);
    }

    private static Algorithm createAlgorithm(PatternGenerator generator, Metric metric) {
        PatternBasedComponentsFactory factory = new PatternBasedComponentsFactory(generator);

        GeneticAlgorithmParameters params = new GeneticAlgorithmParameters();
        params.setPopulationSize(30);
        params.setExchangeSize(20);
        params.setRunSteps(1000);
        params.setCrossoverRate(0.5);

        return new GeneticAlgorithm(factory, metric, params);
    }

    private static XmlEnabledCollector createXmlCollector() {
        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());
        collector.addMeasure(new MaxValue());

        ScalarMetric scalarMetric = new ScalarMetric();
        collector.addMetric(scalarMetric);
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new UniquePatternsMetric());
        collector.addMetric(new ActivePatternsMetric());
        collector.addMetric(new ProductDeviationMetric());
        collector.addMetric(new MaxUnderProductionMetric());
        collector.addMetric(new MaxOverProductionMetric());

        return collector;
    }

    private static SimpleCollector createDebugCollector(SimpleCollector prototype) {
        SimpleCollector collector = new SimpleCollector();
        collector.setMeasures(prototype.getMeasures());
        collector.setMetrics(prototype.getMetrics());

        return collector;
    }

}
