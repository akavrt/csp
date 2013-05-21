package com.akavrt.csp.tester.batch;

import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.sequential.VahrenkampProcedure;
import com.akavrt.csp.solver.sequential.VahrenkampProcedureParameters;
import com.akavrt.csp.tester.config.SequentialProcedureBatchConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * User: akavrt
 * Date: 21.05.13
 * Time: 20:56
 */
public class VahrenkampProcedureBatchTester extends HaesslerProcedureBatchTester {
    private static final Logger LOGGER = LogManager.getLogger(VahrenkampProcedureBatchTester.class);

    public VahrenkampProcedureBatchTester(SequentialProcedureBatchConfiguration config) {
        super(config);
    }

    public static void main(String[] args) {
        SequentialProcedureBatchConfiguration config = new SequentialProcedureBatchConfiguration(args);
        if (config.isLoaded()) {
            VahrenkampProcedureBatchTester tester = new VahrenkampProcedureBatchTester(config);
            tester.process();
        } else {
            config.printHelp();
            System.exit(0);
        }
    }

    @Override
    protected Algorithm createAlgorithm() {
        PatternGenerator generator = createPatternGenerator();

        VahrenkampProcedureParameters procedureParameters = getBatchConfig().getAlgorithmParameters();
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
