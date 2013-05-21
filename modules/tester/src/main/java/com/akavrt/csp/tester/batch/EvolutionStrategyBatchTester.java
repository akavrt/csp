package com.akavrt.csp.tester.batch;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.EvolutionaryComponentsFactory;
import com.akavrt.csp.solver.evo.es.BaseStrategyComponentsFactory;
import com.akavrt.csp.solver.evo.es.EvolutionStrategy;
import com.akavrt.csp.solver.evo.es.EvolutionStrategyParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.config.EvolutionStrategyBatchConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 25.04.13
 * Time: 23:41
 */
public class EvolutionStrategyBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(EvolutionStrategyBatchTester.class);
    private final EvolutionStrategyBatchConfiguration config;

    public EvolutionStrategyBatchTester(EvolutionStrategyBatchConfiguration config) {
        super(config.getTargetDirectory(), config.getNumberOfRuns());

        this.config = config;
    }

    public static void main(String[] args) {
        EvolutionStrategyBatchConfiguration config = new EvolutionStrategyBatchConfiguration(args);
        if (config.isLoaded()) {
            EvolutionStrategyBatchTester tester = new EvolutionStrategyBatchTester(config);
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
        ConstraintAwareMetricParameters constrainedMetricParameters = config.getConstrainedMetricParameters();
        if (constrainedMetricParameters == null) {
            constrainedMetricParameters = new ConstraintAwareMetricParameters();
        }

        return new ConstraintAwareMetric(constrainedMetricParameters);
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();
        EvolutionaryComponentsFactory factory = new BaseStrategyComponentsFactory(generator);
        Metric objectiveFunction = createConstrainedObjectiveFunction();

        EvolutionStrategyParameters strategyParameters = config.getAlgorithmParameters();
        if (strategyParameters == null) {
            strategyParameters = new EvolutionStrategyParameters();
        }

        return new EvolutionStrategy(factory, objectiveFunction, strategyParameters);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

}
