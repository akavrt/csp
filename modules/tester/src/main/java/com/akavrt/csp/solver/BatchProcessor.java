package com.akavrt.csp.solver;

import com.akavrt.csp.analyzer.xml.RunResultWriter;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.utils.Utils;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: akavrt
 * Date: 03.04.13
 * Time: 16:24
 */
public class BatchProcessor {
    private static final String RESULTS_EXTENSION = "xml";
    private static final String RESULTS_SUFFIX= "_run";
    private static final Logger LOGGER = LogManager.getLogger(BatchProcessor.class);
    private final List<String> problemPaths;
    private final MultistartSolver solver;

    public BatchProcessor(MultistartSolver solver) {
        this(solver, null);
    }

    public BatchProcessor(MultistartSolver solver, List<String> paths) {
        this.solver = solver;
        this.problemPaths = Lists.newArrayList();

        if (paths != null) {
            problemPaths.addAll(paths);
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

    public void process(XmlEnabledCollector globalCollector, String globalPath) {
        process(globalCollector, globalPath, null);
    }

    public void process(XmlEnabledCollector problemCollector) {
        process(null, null, problemCollector);
    }

    public void process(XmlEnabledCollector globalCollector, String globalPath,
                        XmlEnabledCollector problemCollector) {
        if (solver == null || (globalCollector == null && problemCollector == null)) {
            return;
        }

        List<LoadedProblem> loadedProblems = loadProblems();
        if (loadedProblems.size() == 0) {
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

            solver.setProblem(loadedProblem.problem);
            solver.solve();

            if (problemCollector != null) {
                writeProblemResults(loadedProblem.path, loadedProblem.problem, problemCollector);
            }
        }

        if (globalCollector != null) {
            writeGlobalResults(globalPath, globalCollector, loadedProblems.size());
        }
    }

    private boolean writeProblemResults(String path, Problem problem,
                                        XmlEnabledCollector problemCollector) {
        File problemFile = new File(path);

        // remove extension from original name
        String problemFileName = FilenameUtils.removeExtension(problemFile.getName());

        // and suffix and extension
        String resultsFileName = problemFileName + RESULTS_SUFFIX + "." + RESULTS_EXTENSION;

        File resultsFile = new File(problemFile.getParent(), resultsFileName);
        try {
            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(solver.getAlgorithm());
            writer.setNumberOfExecutions(solver.getNumberOfRuns());
            writer.setCollector(problemCollector);
            writer.setProblem(problem);

            writer.write(resultsFile, true);
        } catch (IOException e) {
            LOGGER.catching(e);
            return false;
        }

        return true;
    }

    private boolean writeGlobalResults(String globalPath, XmlEnabledCollector globalCollector,
                                       int numberOfProblemsSolved) {
        if (Utils.isEmpty(globalPath)) {
            return false;
        }

        File resultsFile = new File(globalPath);
        try {
            RunResultWriter writer = new RunResultWriter();

            writer.setAlgorithm(solver.getAlgorithm());
            writer.setNumberOfProblemsSolved(numberOfProblemsSolved);
            writer.setNumberOfExecutions(solver.getNumberOfRuns());
            writer.setCollector(globalCollector);

            writer.write(resultsFile, true);
        } catch (IOException e) {
            LOGGER.catching(e);
            return false;
        }

        return true;
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
