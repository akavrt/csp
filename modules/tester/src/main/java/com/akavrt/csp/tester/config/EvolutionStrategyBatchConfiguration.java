package com.akavrt.csp.tester.config;

import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.solver.evo.es.EvolutionStrategyParameters;
import com.akavrt.csp.tester.batch.DirectoryBatchTester;
import com.akavrt.csp.tester.params.ConstraintAwareMetricParametersReader;
import com.akavrt.csp.tester.params.EvolutionStrategyParametersReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 21.05.13
 * Time: 21:16
 */
public class EvolutionStrategyBatchConfiguration extends BatchConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(EvolutionStrategyBatchConfiguration.class);
    private EvolutionStrategyParameters strategyParameters;
    private ConstraintAwareMetricParameters constrainedMetricParameters;

    public EvolutionStrategyBatchConfiguration(String[] args) {
        super(args);
    }

    @Override
    public void printHelp() {
        System.out.println("You can run batch using this command:");
        System.out.println("    target_dir [runs] [es_params] [pattern_params] [objective_params]");
        System.out.println("\n  Where");
        System.out.println("    target_dir       - absolute path to the directory holding problem files");
        System.out.println("                       (these files must have extension ." + DirectoryBatchTester.PROBLEM_FILE_EXTENSION + " by the contract);");
        System.out.println("    runs             - the number to run algorithm for each problem, optional");
        System.out.println("                       parameter, if not specified the default value of " + DirectoryBatchTester.DEFAULT_NUMBER_OF_RUNS + " is");
        System.out.println("                       used;");
        System.out.println("    es_params        - absolute path to the XML file with parameters of");
        System.out.println("                       evolution strategy, optional parameter, if not");
        System.out.println("                       specified the default set of parameters is used;");
        System.out.println("    pattern_params   - absolute path to the XML file with parameters of");
        System.out.println("                       pattern generation procedure, optional parameter,");
        System.out.println("                       if not specified the default set of parameters is used;");
        System.out.println("    objective_params - absolute path to the XML file with parameters of");
        System.out.println("                       objective function (weights used in linear scalar),");
        System.out.println("                       optional parameter, if not specified the default set");
        System.out.println("                       of parameters is used.");
    }

    @Override
    protected void parseParameters(String[] args) {
        parseObjectiveMetricParameters(args);
        super.parseParameters(args);
    }

    @Override
    protected void parseAlgorithmParameters(String[] args) {
        try {
            String path = args[2];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                strategyParameters = new EvolutionStrategyParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of evolution strategy, " +
                                "the default set of parameters will be used.");
        }
    }

    private void parseObjectiveMetricParameters(String[] args) {
        try {
            String path = args[4];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                constrainedMetricParameters = new ConstraintAwareMetricParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of objective function, the default set of parameters will be used.");
        }
    }

    public EvolutionStrategyParameters getAlgorithmParameters() {
        return strategyParameters;
    }

    public ConstraintAwareMetricParameters getConstrainedMetricParameters() {
        return constrainedMetricParameters;
    }
}
