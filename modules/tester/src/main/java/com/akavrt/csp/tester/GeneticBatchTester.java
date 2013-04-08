package com.akavrt.csp.tester;

import com.akavrt.csp.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.genetic.GeneticAlgorithm;
import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 07.04.13
 * Time: 23:50
 */
public class GeneticBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(GeneticBatchTester.class);

    public GeneticBatchTester(String directory, int numberOfRuns) {
        super(directory, numberOfRuns);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No path was specified.\n");
            System.out.println("You can run batch using this command:");
            System.out.println("    target [runs]");
            System.out.println("Where");
            System.out.println("    target - absolute path to the directory holding problem files");
            System.out.println("             (these files must have extension ." + PROBLEM_FILE_EXTENSION + " by the contract);");
            System.out.println("    runs   - the number to run algorithm for each problem, optional parameter,");
            System.out.println("             if not specified the default value of " + DEFAULT_NUMBER_OF_RUNS + " is used.");
            System.exit(0);
        }

        String targetDirectory = args[0];

        int numberOfRuns = DEFAULT_NUMBER_OF_RUNS;
        try {
            numberOfRuns = Integer.parseInt(args[1]);
        } catch (Exception e) {
            LOGGER.info("Can't find optional parameter 'number of runs', " +
                                "the default value of {} will be used.", DEFAULT_NUMBER_OF_RUNS);
        }

        GeneticBatchTester tester = new GeneticBatchTester(targetDirectory, numberOfRuns);
        tester.process();
    }

    private PatternGenerator createPatternGenerator() {
        PatternGeneratorParameters generatorParams = new PatternGeneratorParameters();
        generatorParams.setGenerationTrialsLimit(20);
        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

        return generator;
    }

    private Metric createObjectiveFunction() {
        return new ScalarMetric();
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();

        PatternBasedComponentsFactory factory = new PatternBasedComponentsFactory(generator);

        Metric objectiveFunction = createObjectiveFunction();

        GeneticAlgorithmParameters params = new GeneticAlgorithmParameters();
        params.setPopulationSize(100);
        params.setExchangeSize(70);
        params.setRunSteps(1000);
        params.setCrossoverRate(0.5);

        return new GeneticAlgorithm(factory, objectiveFunction, params);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}