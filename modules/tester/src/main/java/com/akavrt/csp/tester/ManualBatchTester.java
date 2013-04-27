package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.StandardDeviation;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.metrics.complex.PatternReductionMetric;
import com.akavrt.csp.metrics.complex.ProductDeviationMetric;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.metrics.complex.ScalarMetricParameters;
import com.akavrt.csp.metrics.simple.ActivePatternsMetric;
import com.akavrt.csp.metrics.simple.TrimLossMetric;
import com.akavrt.csp.metrics.simple.UniquePatternsMetric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.BatchProcessor;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;

/**
 * User: akavrt
 * Date: 04.04.13
 * Time: 15:44
 */
public class ManualBatchTester {
    public static void main(String[] args) {
        PatternGenerator generator = createPatternGenerator();
        Algorithm method = createAlgorithm(generator);

        MultistartSolver solver = new MultistartSolver(method, 10);
        BatchProcessor processor = new BatchProcessor(solver);

        processor.addProblem("/Users/akavrt/Sandbox/csp/optimal_01.xml");
        processor.addProblem("/Users/akavrt/Sandbox/csp/optimal_10.xml");

        XmlEnabledCollector globalCollector = createGlobalCollector();
        XmlEnabledCollector problemCollector = createProblemCollector();

        String outputPath = "/Users/akavrt/Sandbox/csp";
        processor.process(globalCollector, problemCollector, outputPath);
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

        return new VahrenkampProcedure(generator, methodParams);
    }

    private static XmlEnabledCollector createProblemCollector() {
        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());

        ScalarMetric scalarMetric = new ScalarMetric();
        collector.addMetric(scalarMetric);
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new UniquePatternsMetric());
        collector.addMetric(new ActivePatternsMetric());
        collector.addMetric(new ProductDeviationMetric());

        return collector;
    }

    private static XmlEnabledCollector createGlobalCollector() {
        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());

        ScalarMetric scalarMetric = new ScalarMetric(new ScalarMetricParameters());
        collector.addMetric(scalarMetric);
        collector.addMetric(new ProductDeviationMetric());

        return collector;
    }

}


