package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.analyzer.StandardDeviation;
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
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: akavrt
 * Date: 17.03.13
 * Time: 19:41
 */
public class Tester {
    private static final Logger LOGGER = LogManager.getLogger(Tester.class);

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
            LOGGER.error("Error occurred while loading problem from external file.");
            return;
        }

        System.out.println(TraceUtils.traceProblem(problem, true, true));

        PatternGeneratorParameters generatorParams = new PatternGeneratorParameters();
        generatorParams.setGenerationTrialsLimit(20);
        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

        VahrenkampProcedureParameters methodParams = new VahrenkampProcedureParameters();
        methodParams.setPatternUsageUpperBound(0.5);
        methodParams.setGoalmix(0.5);
        methodParams.setTrimRatioUpperBound(1);
        Algorithm method = new VahrenkampProcedure(generator, methodParams);

        ScalarMetric metric = new ScalarMetric(problem, new ScalarMetricParameters());

        SimpleCollector collector = new SimpleCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());

        collector.addMetric(metric);
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new UniquePatternsMetric());
        collector.addMetric(new ActivePatternsMetric());
        collector.addMetric(new ProductDeviationMetric(problem));

        MultistartSolver solver = new MultistartSolver(problem, method, 10);
        solver.setCollector(collector);
        solver.solve();

        Solution best = solver.getBestSolution(metric);

        if (best != null) {
            String trace = TraceUtils.traceSolution(best, problem, metric, true);
            System.out.println("*** OVERALL BEST solution found:\n" + trace);

            /*
            File file = new File("/Users/akavrt/Sandbox/output-opt10.xml");

            CspWriter writer = new CspWriter();

            writer.setProblem(problem);
            writer.addSolution(best);

            writer.write(file, true);
            */
        } else {
            System.out.println("Best is null.");
        }

    }
}
