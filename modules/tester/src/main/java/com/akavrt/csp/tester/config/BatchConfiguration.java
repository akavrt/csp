package com.akavrt.csp.tester.config;

import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.batch.DirectoryBatchTester;
import com.akavrt.csp.tester.params.PatternGeneratorParametersReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public abstract class BatchConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(BatchConfiguration.class);
    private String targetDirectory;
    private int numberOfRuns;
    private PatternGeneratorParameters patternParameters;
    private boolean isLoaded;

    public BatchConfiguration(String[] args) {
        parseParameters(args);
    }

    protected abstract void parseAlgorithmParameters(String[] args);

    public abstract void printHelp();

    protected void parseParameters(String[] args) {
        targetDirectory = args.length > 0 ? args[0] : null;
        parseNumberOfRuns(args);
        parseAlgorithmParameters(args);
        parsePatternGeneratorParameters(args);

        isLoaded = true;
    }

    private void parseNumberOfRuns(String[] args) {
        numberOfRuns = DirectoryBatchTester.DEFAULT_NUMBER_OF_RUNS;
        try {
            numberOfRuns = Integer.parseInt(args[1]);
        } catch (Exception e) {
            LOGGER.info("Can't find optional parameter 'number of runs', " +
                                "the default value of {} will be used.",
                        DirectoryBatchTester.DEFAULT_NUMBER_OF_RUNS);
        }
    }

    private void parsePatternGeneratorParameters(String[] args) {
        try {
            String path = args[3];
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                patternParameters = new PatternGeneratorParametersReader().read(file);
            }
        } catch (Exception e) {
            LOGGER.info("Can't find parameters of pattern generator procedure, " +
                                "the default set of parameters will be used.");
        }
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public PatternGeneratorParameters getPatternParameters() {
        return patternParameters;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
