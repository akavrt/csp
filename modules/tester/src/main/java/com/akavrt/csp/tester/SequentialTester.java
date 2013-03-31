package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.analyzer.StandardDeviation;
import com.akavrt.csp.analyzer.xml.RunResultWriter;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.metrics.*;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.SimplifiedProcedure;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.tester.tracer.TraceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: akavrt
 * Date: 17.03.13
 * Time: 19:41
 */
public class SequentialTester {
    private static final Logger LOGGER = LogManager.getLogger(SequentialTester.class);

    public static void main(String[] args) throws IOException {
        CspReader reader = new CspReader();
        try {
            InputStream is = SequentialTester.class.getClassLoader().getResourceAsStream("optimal_01.xml");
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
        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

//        VahrenkampProcedureParameters methodParams = new VahrenkampProcedureParameters();
//        methodParams.setPatternUsageUpperBound(0.5);
//        methodParams.setGoalmix(0.5);
//        methodParams.setTrimRatioUpperBound(1);
        Algorithm method = new SimplifiedProcedure(generator);

        ScalarMetric metric = new ScalarMetric(problem, new ScalarMetricParameters());

        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());

        collector.addMetric(metric);
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new UniquePatternsMetric());
        collector.addMetric(new ActivePatternsMetric());
        collector.addMetric(new ProductDeviationMetric(problem));

        SimpleCollector debugCollector = new SimpleCollector();
        debugCollector.setMeasures(collector.getMeasures());
        debugCollector.setMetrics(collector.getMetrics());

        MultistartSolver solver = new MultistartSolver(problem, method, 10);
        solver.addCollector(collector);
        solver.addCollector(debugCollector);

        solver.solve();

        debugCollector.process();

        Solution best = solver.getBestSolution(metric);

        if (best != null) {
            ScalarTracer tracer = new ScalarTracer(metric, problem);
            String trace = TraceUtils.traceSolution(best, problem, tracer, true);
            System.out.println("*** OVERALL BEST solution found:\n" + trace);

            /*
            File file = new File("/Users/akavrt/Sandbox/output-opt10.xml");

            CspWriter writer = new CspWriter();

            writer.setProblem(problem);
            writer.addSolution(best);

            writer.write(file, true);
            */
            File file = new File("/Users/akavrt/Sandbox/output-opt01run.xml");

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
}
