package com.akavrt.csp.tester.config;

import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import com.akavrt.csp.tester.batch.DirectoryBatchTester;
import com.akavrt.csp.tester.params.VahrenkampProcedureParametersReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 21.05.13
 * Time: 20:38
 */
public class SequentialProcedureBatchConfiguration extends BatchConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(SequentialProcedureBatchConfiguration.class);
    private VahrenkampProcedureParameters procedureParameters;

    public SequentialProcedureBatchConfiguration(String[] args) {
        super(args);
    }

    @Override
    public void printHelp() {
        System.out.println("You can run batch using this command:");
        System.out.println("    target_dir [runs] [sequential_params] [pattern_params]");
        System.out.println("\n  Where");
        System.out.println("    target_dir        - absolute path to the directory holding problem files");
        System.out.println("                        (these files must have extension ." + DirectoryBatchTester.PROBLEM_FILE_EXTENSION + " by the contract);");
        System.out.println("    runs              - the number to run algorithm for each problem, optional");
        System.out.println("                        parameter, if not specified the default value of " + DirectoryBatchTester.DEFAULT_NUMBER_OF_RUNS + " is");
        System.out.println("                        used;");
        System.out.println("    sequential_params - absolute path to the XML file with parameters of");
        System.out.println("                        sequential heuristic procedure, optional parameter, if");
        System.out.println("                        not specified the default set of parameters is used;");
        System.out.println("    pattern_params    - absolute path to the XML file with parameters of");
        System.out.println("                        pattern generation procedure, optional parameter,");
        System.out.println("                        if not specified the default set of parameters is used.");
    }

    @Override
    protected void parseAlgorithmParameters(String[] args) {
        try {
            String path = args[2];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                procedureParameters = new VahrenkampProcedureParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of sequential heuristic procedure, " +
                                "the default set of parameters will be used.");
        }
    }

    public VahrenkampProcedureParameters getAlgorithmParameters() {
        return procedureParameters;
    }
}
