package com.akavrt.csp.tester;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import com.akavrt.csp.tester.params.PatternGeneratorParametersReader;
import com.akavrt.csp.tester.params.VahrenkampProcedureParametersReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * User: akavrt
 * Date: 06.04.13
 * Time: 22:50
 */
public class SequentialBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(SequentialBatchTester.class);
    private VahrenkampProcedureParameters procedureParameters;
    private PatternGeneratorParameters patternParameters;

    public SequentialBatchTester(String directory, int numberOfRuns) {
        super(directory, numberOfRuns);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("You can run batch using this command:");
            System.out.println("    target_dir [runs] [sequential_params] [pattern_params]");
            System.out.println("\n  Where");
            System.out.println("    target_dir        - absolute path to the directory holding problem files");
            System.out.println("                        (these files must have extension ." + PROBLEM_FILE_EXTENSION + " by the contract);");
            System.out.println("    runs              - the number to run algorithm for each problem, optional");
            System.out.println("                        parameter, if not specified the default value of " + DEFAULT_NUMBER_OF_RUNS + " is");
            System.out.println("                        used;");
            System.out.println("    sequential_params - absolute path to the XML file with parameters of");
            System.out.println("                        sequential heuristic procedure, optional parameter, if");
            System.out.println("                        not specified the default set of parameters is used;");
            System.out.println("    pattern_params    - absolute path to the XML file with parameters of");
            System.out.println("                        pattern generation procedure, optional parameter,");
            System.out.println("                        if not specified the default set of parameters is used.");
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

        VahrenkampProcedureParameters procedureParameters = null;
        try {
            String path = args[2];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                procedureParameters = new VahrenkampProcedureParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of sequential heuristic procedure, the default set of parameters will be used.");
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

        SequentialBatchTester tester = new SequentialBatchTester(targetDirectory, numberOfRuns);
        tester.setProcedureParameters(procedureParameters);
        tester.setPatternParameters(patternParameters);

        tester.process();
    }

    public void setProcedureParameters(VahrenkampProcedureParameters params) {
        this.procedureParameters = params;
    }

    public void setPatternParameters(PatternGeneratorParameters params) {
        this.patternParameters = params;
    }

    private PatternGenerator createPatternGenerator() {
        if (patternParameters == null) {
            patternParameters = new PatternGeneratorParameters();
        }

        return new ConstrainedPatternGenerator(patternParameters);
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();

        if (procedureParameters == null) {
            procedureParameters = new VahrenkampProcedureParameters();
        }

        return new VahrenkampProcedure(generator, procedureParameters);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
