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
import com.akavrt.csp.metrics.complex.PatternReductionMetric;
import com.akavrt.csp.metrics.complex.ProductDeviationMetric;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.metrics.simple.*;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.solver.local.LocalSearch;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.utils.ProblemFormatter;
import com.akavrt.csp.utils.SolutionFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * User: akavrt
 * Date: 24.04.13
 * Time: 16:44
 */
public class LocalSearchTester {
    private static final Logger LOGGER = LogManager.getLogger(LocalSearchTester.class);

    public static void main(String[] args) throws IOException {
        Problem problem = loadProblem("/Users/akavrt/Sandbox/csp/optimal/optimal_10.xml");
        if (problem == null) {
            LOGGER.error("Error occurred while loading problem from external file.");
            return;
        }

        System.out.println(ProblemFormatter.format(problem));

        PatternGenerator generator = createPatternGenerator();
        Algorithm method = createAlgorithm(generator);

        XmlEnabledCollector collector = createXmlCollector();
        SimpleCollector debugCollector = createDebugCollector(collector);

        MultistartSolver solver = new MultistartSolver(problem, method, 10);
        solver.addCollector(collector);
        solver.addCollector(debugCollector);

        solver.solve();

        debugCollector.process();

        ScalarMetric scalarMetric = new ScalarMetric();
        Solution best = solver.getBestSolution(scalarMetric);
        if (best != null) {
            ScalarTracer tracer = new ScalarTracer(scalarMetric);
            String caption = "*** OVERALL BEST solution found:";
            String trace = tracer.trace(best);
            String formatted = SolutionFormatter.format(best, caption, trace, true);

            System.out.println(formatted);

            File file = new File("/Users/akavrt/Sandbox/csp/optimal_10_ls_run.xml");

            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(method);
            writer.setNumberOfExecutions(solver.getNumberOfRuns());
            writer.setCollector(collector);
            writer.setProblem(problem);
            writer.addSolution(best);

            writer.write(file, true);
        } else {
            System.out.println("Best is null.");
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

    private static Algorithm createAlgorithm(PatternGenerator generator) {
        VahrenkampProcedureParameters methodParams = new VahrenkampProcedureParameters();
        methodParams.setPatternUsageUpperBound(0.5);
        methodParams.setGoalmix(0.5);
        methodParams.setTrimRatioUpperBound(1);

        Algorithm constructiveProcedure = new VahrenkampProcedure(generator, methodParams);

        return new LocalSearch(constructiveProcedure, generator, new ScalarMetric());
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

