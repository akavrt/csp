package com.akavrt.csp.tester.batch;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.solver.sequential.HaesslerProcedure;
import com.akavrt.csp.solver.sequential.SequentialProcedureParameters;
import com.akavrt.csp.tester.config.SequentialProcedureBatchConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 06.04.13
 * Time: 22:50
 */
public class HaesslerProcedureBatchTester extends DirectoryBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(HaesslerProcedureBatchTester.class);
    private final SequentialProcedureBatchConfiguration config;

    public HaesslerProcedureBatchTester(SequentialProcedureBatchConfiguration config) {
        super(config.getTargetDirectory(), config.getNumberOfRuns());

        this.config = config;
    }

    public static void main(String[] args) {
        SequentialProcedureBatchConfiguration config = new SequentialProcedureBatchConfiguration(args);
        if (config.isLoaded()) {
            HaesslerProcedureBatchTester tester = new HaesslerProcedureBatchTester(config);
            tester.process();
        } else {
            config.printHelp();
            System.exit(0);
        }
    }

    protected PatternGenerator createPatternGenerator() {
        PatternGeneratorParameters patternParameters = config.getPatternParameters();
        if (patternParameters == null) {
            patternParameters = new PatternGeneratorParameters();
        }

        return new ConstrainedPatternGenerator(patternParameters);
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();

        SequentialProcedureParameters procedureParameters = config.getAlgorithmParameters();
        if (procedureParameters == null) {
            procedureParameters = new SequentialProcedureParameters();
        }

        return new HaesslerProcedure(generator, procedureParameters);
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    protected SequentialProcedureBatchConfiguration getBatchConfig() {
        return config;
    }
}
