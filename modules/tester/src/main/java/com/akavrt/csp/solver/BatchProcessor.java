package com.akavrt.csp.solver;

import com.akavrt.csp.analyzer.xml.RunResultWriter;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.tester.utils.Utils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * User: akavrt
 * Date: 03.04.13
 * Time: 16:24
 */
public class BatchProcessor {
    private static final Logger LOGGER = LogManager.getLogger(BatchProcessor.class);
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd_HH-mm";
    private static final String RESULTS_DIRECTORY_PREFIX = "csp-run_";
    private static final String RESULTS_SUFFIX = "_run";
    private static final String RESULTS_EXTENSION = "xml";
    private static final String RUN_RESULTS_FILE_NAME = "csp-run-results" + "." + RESULTS_EXTENSION;
    private final List<String> problemPaths;
    private final MultistartSolver solver;

    public BatchProcessor(MultistartSolver solver) {
        this(solver, null);
    }

    public BatchProcessor(MultistartSolver solver, List<String> problemPaths) {
        this.solver = solver;

        this.problemPaths = Lists.newArrayList();
        if (problemPaths != null) {
            this.problemPaths.addAll(problemPaths);
        }
    }

    public void addProblem(String path) {
        problemPaths.add(path);
    }

    public void addProblems(List<String> paths) {
        problemPaths.addAll(paths);
    }

    public void clearProblems() {
        problemPaths.clear();
    }

    public void process(XmlEnabledCollector globalCollector, String outputPath) {
        process(globalCollector, null, outputPath);
    }

    public void process(XmlEnabledCollector globalCollector, XmlEnabledCollector problemCollector,
                        String outputPath) {
        if (solver == null || (globalCollector == null && problemCollector == null)) {
            return;
        }

        long start = System.currentTimeMillis();
        List<LoadedProblem> loadedProblems = loadProblems();
        if (loadedProblems.size() == 0) {
            LOGGER.info("This batch is empty, halting batch processing.");
            return;
        }

        File outputDirectory = createOutputDirectory(outputPath);
        if (outputDirectory == null) {
            return;
        }

        solver.clearCollectors();

        if (globalCollector != null) {
            // reset global collector
            globalCollector.clear();
            solver.addCollector(globalCollector);
        }

        if (problemCollector != null) {
            solver.addCollector(problemCollector);
        }

        for (LoadedProblem loadedProblem : loadedProblems) {
            if (problemCollector != null) {
                // reset problem (local) collector
                // before each execution of the solver
                problemCollector.clear();
            }

            LOGGER.info("Solving problem {} loaded from '{}'",
                        extractProblemName(loadedProblem), loadedProblem.path);
            solver.setProblem(loadedProblem.problem);
            solver.solve();

            if (problemCollector != null) {
                writeProblemResults(outputDirectory, loadedProblem, problemCollector);
            }
        }

        long end = System.currentTimeMillis();
        if (globalCollector != null) {
            writeGlobalResults(outputDirectory,
                               globalCollector, loadedProblems.size(), end - start);
        }
    }

    private List<LoadedProblem> loadProblems() {
        CspReader reader = new CspReader();
        List<LoadedProblem> problems = Lists.newArrayList();
        for (String problemPath : problemPaths) {
            File file = new File(problemPath);

            if (file.exists() && file.isFile() && file.canRead()) {
                try {
                    reader.read(file);

                    Problem problem = reader.getProblem();
                    if (problem == null) {
                        LOGGER.error("Couldn't load problem using path '{}'", problemPath);
                    } else {
                        problems.add(new LoadedProblem(problemPath, problem));
                    }
                } catch (CspParseException e) {
                    LOGGER.catching(e);
                }
            }
        }

        return problems;
    }

    private File createOutputDirectory(String outputPath) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
        String directoryName = RESULTS_DIRECTORY_PREFIX + dateFormat.format(new Date());

        File outputDirectory = new File(outputPath, directoryName);
        outputDirectory.mkdirs();

        LOGGER.info("Creating output directory '{}'", outputDirectory.getPath());

        if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
            return null;
        }

        return outputDirectory;
    }

    private boolean writeProblemResults(File outputDirectory, LoadedProblem loadedProblem,
                                        XmlEnabledCollector problemCollector) {
        File problemFile = new File(loadedProblem.path);

        // remove extension from original name
        String problemFileName = FilenameUtils.removeExtension(problemFile.getName());

        // and suffix and extension
        String resultsFileName = problemFileName + RESULTS_SUFFIX + "." + RESULTS_EXTENSION;

        File resultsFile = new File(outputDirectory, resultsFileName);
        try {
            LOGGER.info("Writing results for problem {} into '{}'",
                        extractProblemName(loadedProblem), resultsFile.getPath());

            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(solver.getAlgorithm());
            writer.setNumberOfExecutions(solver.getNumberOfRuns());
            writer.setCollector(problemCollector);
            writer.setProblem(loadedProblem.problem);

            writer.write(resultsFile, true);
        } catch (IOException e) {
            LOGGER.catching(e);
            return false;
        }

        return true;
    }

    private boolean writeGlobalResults(File outputDirectory, XmlEnabledCollector globalCollector,
                                       int numberOfProblemsSolved, long totalProcessTimeInMillis) {
        File resultsFile = new File(outputDirectory, RUN_RESULTS_FILE_NAME);
        try {
            LOGGER.info("Writing run results into '{}'", resultsFile.getPath());

            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(solver.getAlgorithm());
            writer.setNumberOfProblemsSolved(numberOfProblemsSolved);
            writer.setNumberOfExecutions(solver.getNumberOfRuns());
            writer.setTotalProcessingTime(totalProcessTimeInMillis);
            writer.setCollector(globalCollector);

            writer.write(resultsFile, true);
        } catch (IOException e) {
            LOGGER.catching(e);
            return false;
        }

        return true;
    }

    private String extractProblemName(LoadedProblem loadedProblem) {
        return Utils.extractProblemName(loadedProblem.problem, loadedProblem.path);
    }

    private static class LoadedProblem {
        public final String path;
        public final Problem problem;

        public LoadedProblem(String path, Problem problem) {
            this.path = path;
            this.problem = problem;
        }
    }
}