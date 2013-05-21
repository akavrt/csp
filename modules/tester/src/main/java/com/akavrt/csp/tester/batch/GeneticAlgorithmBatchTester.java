package com.akavrt.csp.tester.batch;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.ga.BaseGeneticComponentsFactory;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithm;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.evo.ga.GeneticComponentsFactory;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.config.GeneticAlgorithmBatchConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 07.04.13
 * Time: 23:50
 */
public class GeneticAlgorithmBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(GeneticAlgorithmBatchTester.class);
    private GeneticAlgorithmBatchConfiguration config;

    public GeneticAlgorithmBatchTester(GeneticAlgorithmBatchConfiguration config) {
        super(config.getTargetDirectory(), config.getNumberOfRuns());

        this.config = config;
    }

    public static void main(String[] args) {
        GeneticAlgorithmBatchConfiguration config = new GeneticAlgorithmBatchConfiguration(args);
        if (config.isLoaded()) {
            GeneticAlgorithmBatchTester tester = new GeneticAlgorithmBatchTester(config);
            tester.process();
        } else {
            config.printHelp();
            System.exit(0);
        }
    }

    private PatternGenerator createPatternGenerator() {
        PatternGeneratorParameters patternParameters = config.getPatternParameters();
        if (patternParameters == null) {
            patternParameters = new PatternGeneratorParameters();
        }

        return new ConstrainedPatternGenerator(patternParameters);
    }

    private Metric createConstrainedObjectiveFunction() {
        ConstraintAwareMetricParameters constrainedMetricParameters = config
                .getConstrainedMetricParameters();
        if (constrainedMetricParameters == null) {
            constrainedMetricParameters = new ConstraintAwareMetricParameters();
        }

        return new ConstraintAwareMetric(constrainedMetricParameters);
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();
        GeneticComponentsFactory factory = new BaseGeneticComponentsFactory(generator);
        Metric objectiveFunction = createConstrainedObjectiveFunction();

        GeneticAlgorithmParameters geneticParameters = config.getAlgorithmParameters();
        if (geneticParameters == null) {
            geneticParameters = new GeneticAlgorithmParameters();
        }

        return new GeneticAlgorithm(factory, objectiveFunction, geneticParameters);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

}