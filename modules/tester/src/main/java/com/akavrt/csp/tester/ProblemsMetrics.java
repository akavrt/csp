package com.akavrt.csp.tester;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;

/**
 * User: akavrt
 * Date: 22.05.13
 * Time: 01:21
 */
public class ProblemsMetrics {
    private static final String PROBLEM_FILE_EXTENSION = "xml";
    private static final Logger LOGGER = LogManager.getLogger(ProblemsMetrics.class);
    private final CspReader reader;

    public ProblemsMetrics() {
        reader = new CspReader();
    }

    public static void main(String[] args) {
        ProblemsMetrics metrics = new ProblemsMetrics();
        String basePath = "/Users/akavrt/Sandbox/csp/paper";
        metrics.process(new File(basePath, "optimal").getPath());
        metrics.process(new File(basePath, "production").getPath());
        metrics.process(new File(basePath, "random").getPath());
    }

    public void process(String directoryPath) {
        File[] problemFiles = loadProblemFiles(directoryPath);

        if (problemFiles == null || problemFiles.length == 0) {
            LOGGER.info("Problem files wasn't found.");
            return;
        }

        int minOrdersCount = 0;
        int maxOrdersCount = 0;
        int minRollsCount = 0;
        int maxRollsCount = 0;
        for (File problemFile : problemFiles) {
            LOGGER.info("Checking problem file '{}'", problemFile.getPath());
            Problem problem = loadProblem(problemFile);

            int ordersCount = problem.getOrders().size();
            if (minOrdersCount == 0 || ordersCount < minOrdersCount) {
                minOrdersCount = ordersCount;
            }

            if (maxOrdersCount == 0 || ordersCount > maxOrdersCount) {
                maxOrdersCount = ordersCount;
            }

            int rollsCount = problem.getRolls().size();
            if (minRollsCount == 0 || rollsCount < minRollsCount) {
                minRollsCount = rollsCount;
            }

            if (maxRollsCount == 0 || rollsCount > maxRollsCount) {
                maxRollsCount = rollsCount;
            }
        }

        LOGGER.info("Path: {}", directoryPath);
        LOGGER.info("min(m) = {}, max(m) = {}", minOrdersCount, maxOrdersCount);
        LOGGER.info("min(n) = {}, max(n) = {}", minRollsCount, maxRollsCount);
    }

    private File[] loadProblemFiles(String directoryPath) {
        if (Utils.isEmpty(directoryPath)) {
            LOGGER.info("Can't recognize target path.", directoryPath);
            return null;
        }

        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead() ||
                !directory.canWrite()) {
            LOGGER.info("Target path '{}' wasn't recognized.", directoryPath);
            return null;
        }

        File[] problemFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && FilenameUtils.getExtension(file.getName())
                                                     .equalsIgnoreCase(PROBLEM_FILE_EXTENSION);
            }
        });

        return problemFiles;
    }

    private Problem loadProblem(File problemFile ) {
        try {
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        return reader.getProblem();
    }

}
