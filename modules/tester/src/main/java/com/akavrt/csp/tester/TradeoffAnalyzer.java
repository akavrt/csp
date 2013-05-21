package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.metrics.complex.PatternReductionMetric;
import com.akavrt.csp.metrics.simple.AggregatedTrimLossMetric;
import com.akavrt.csp.metrics.simple.TrimLossMetric;
import com.akavrt.csp.metrics.simple.UniquePatternsMetric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 21:37
 */
public class TradeoffAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(TradeoffAnalyzer.class);
    private Problem problem;

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.error("Specify valid path to the problem file through command line parameter.");
        }

        String problemFilePath = args[0];

        TradeoffAnalyzer analyzer = new TradeoffAnalyzer();
        analyzer.loadProblem(problemFilePath);
        analyzer.run();
    }

    public void run() {
        if (problem == null) {
            LOGGER.warn("No problem was loaded.");
            return;
        }

        SimpleCollector collector = new SimpleCollector();
        collector.addMeasure(new Average());

        collector.addMetric(new AggregatedTrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new UniquePatternsMetric());

        LOGGER.info("*** START OF THE RUN ***");

        double aggregatedTrimFactor = 0;
        int i = 0;
        while (aggregatedTrimFactor <= 1.01) {
            /*
            LOGGER.info("#{}: C_1 = {}, C_2 = {}", ++i, String.format("%.2f", aggregatedTrimFactor),
                        String.format("%.2f", 1 - aggregatedTrimFactor));
            */

            LOGGER.info("#{}: goalmix = {}", ++i, String.format("%.2f", 1 - aggregatedTrimFactor));

            collector.clear();

            Algorithm method = createAlgorithm(aggregatedTrimFactor);
            MultistartSolver solver = new MultistartSolver(problem, method, 10);
            solver.addCollector(collector);
            solver.solve();

            collector.process();

            aggregatedTrimFactor += 0.05;
        }

        LOGGER.info("*** END OF THE RUN ***");
    }

    public void loadProblem(String path) {
        CspReader reader = new CspReader();
        try {
            File problemFile = new File(path);
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        problem = reader.getProblem();
    }

    /*
    private Algorithm createAlgorithm(double aggregatedTrimFactor) {
        PatternGeneratorParameters patternParameters = new PatternGeneratorParameters();
        patternParameters.setGenerationTrialsLimit(5);
        PatternGenerator generator = new ConstrainedPatternGenerator(patternParameters);

        ConstraintAwareMetricParameters objectiveParameters = new ConstraintAwareMetricParameters();
        objectiveParameters.setAggregatedTrimFactor(aggregatedTrimFactor);
        objectiveParameters.setPatternsFactor(1 - aggregatedTrimFactor);
        Metric objectiveFunction = new ConstraintAwareMetric(objectiveParameters);

        EvolutionaryComponentsFactory factory = new BaseStrategyComponentsFactory(generator);

        EvolutionStrategyParameters strategyParameters = new EvolutionStrategyParameters();
        strategyParameters.setPopulationSize(50);
        strategyParameters.setOffspringCount(45);
        strategyParameters.setRunSteps(2000);

        return new EvolutionStrategy(factory, objectiveFunction, strategyParameters);
    }
    */

    private Algorithm createAlgorithm(double aggregatedTrimFactor) {
        PatternGeneratorParameters patternParameters = new PatternGeneratorParameters();
        patternParameters.setGenerationTrialsLimit(5);
        PatternGenerator generator = new ConstrainedPatternGenerator(patternParameters);

        VahrenkampProcedureParameters shpParameters = new VahrenkampProcedureParameters();
        shpParameters.setGoalmix(1 - aggregatedTrimFactor);

        return new VahrenkampProcedure(generator, shpParameters);
    }

}
