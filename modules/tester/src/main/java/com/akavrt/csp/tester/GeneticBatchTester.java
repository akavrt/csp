package com.akavrt.csp.tester;

import com.akavrt.csp.metrics.Metric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithm;
import com.akavrt.csp.solver.evo.ga.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.params.ConstraintAwareMetricParametersReader;
import com.akavrt.csp.tester.params.GeneticAlgorithmParametersReader;
import com.akavrt.csp.tester.params.PatternGeneratorParametersReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 07.04.13
 * Time: 23:50
 */
public class GeneticBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(GeneticBatchTester.class);
    private GeneticAlgorithmParameters geneticParameters;
    private PatternGeneratorParameters patternParameters;
    private ConstraintAwareMetricParameters constrainedMetricParameters;

public GeneticBatchTester(String directory, int numberOfRuns) {
        super(directory, numberOfRuns);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You can run batch using this command:");
            System.out.println("    target_dir [runs] [genetic_params] [pattern_params] [objective_params]");
            System.out.println("\n  Where");
            System.out.println("    target_dir       - absolute path to the directory holding problem files");
            System.out.println("                       (these files must have extension ." + PROBLEM_FILE_EXTENSION + " by the contract);");
            System.out.println("    runs             - the number to run algorithm for each problem, optional");
            System.out.println("                       parameter, if not specified the default value of " + DEFAULT_NUMBER_OF_RUNS + " is");
            System.out.println("                       used;");
            System.out.println("    genetic_params   - absolute path to the XML file with parameters of");
            System.out.println("                       genetic algorithm, optional parameter, if not specified");
            System.out.println("                       the default set of parameters is used;");
            System.out.println("    pattern_params   - absolute path to the XML file with parameters of");
            System.out.println("                       pattern generation procedure, optional parameter,");
            System.out.println("                       if not specified the default set of parameters is used;");
            System.out.println("    objective_params - absolute path to the XML file with parameters of");
            System.out.println("                       objective function (weights used in linear scalar),");
            System.out.println("                       optional parameter, if not specified the default set");
            System.out.println("                       of parameters is used.");
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

        GeneticAlgorithmParameters geneticParameters = null;
        try {
            String path = args[2];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                geneticParameters = new GeneticAlgorithmParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of genetic algorithm, the default set of parameters will be used.");
        }

        PatternGeneratorParameters patternParameters = null;
        try {
            String path = args[3];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                patternParameters = new PatternGeneratorParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of pattern generator procedure, the default set of parameters will be used.");
        }

        ConstraintAwareMetricParameters metricParameters = null;
        try {
            String path = args[4];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                metricParameters = new ConstraintAwareMetricParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of objective function, the default set of parameters will be used.");
        }

        GeneticBatchTester tester = new GeneticBatchTester(targetDirectory, numberOfRuns);
        tester.setGeneticParameters(geneticParameters);
        tester.setPatternParameters(patternParameters);
        tester.setConstrainedMetricParameters(metricParameters);

        tester.process();
    }

    public void setGeneticParameters(GeneticAlgorithmParameters params) {
        this.geneticParameters = params;
    }

    public void setPatternParameters(PatternGeneratorParameters params) {
        this.patternParameters = params;
    }

    public void setConstrainedMetricParameters(ConstraintAwareMetricParameters params) {
        this.constrainedMetricParameters = params;
    }

    private PatternGenerator createPatternGenerator() {
        if (patternParameters == null) {
            patternParameters = new PatternGeneratorParameters();
        }

        return new ConstrainedPatternGenerator(patternParameters);
    }

    private Metric createConstrainedObjectiveFunction() {
        if (constrainedMetricParameters == null) {
            constrainedMetricParameters = new ConstraintAwareMetricParameters();
        }

        return new ConstraintAwareMetric(constrainedMetricParameters);
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();

        PatternBasedComponentsFactory factory = new PatternBasedComponentsFactory(generator);

        Metric objectiveFunction = createConstrainedObjectiveFunction();

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