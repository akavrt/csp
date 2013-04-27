package com.akavrt.csp.tester;

import com.akavrt.csp.analyzer.Average;
import com.akavrt.csp.analyzer.MaxValue;
import com.akavrt.csp.analyzer.MinValue;
import com.akavrt.csp.analyzer.StandardDeviation;
import com.akavrt.csp.analyzer.xml.XmlEnabledCollector;
import com.akavrt.csp.metrics.complex.PatternReductionMetric;
import com.akavrt.csp.metrics.complex.ProductDeviationMetric;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.metrics.simple.*;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.solver.BatchProcessor;
import com.akavrt.csp.solver.MultistartSolver;
import com.akavrt.csp.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;

/**
 * User: akavrt
 * Date: 07.04.13
 * Time: 23:25
 */
public abstract class DirectoryBatchTester {
    protected static final String PROBLEM_FILE_EXTENSION = "xml";
    protected static final int DEFAULT_NUMBER_OF_RUNS = 10;
    private final String targetDirectory;
    private final int numberOfRuns;

    protected abstract Algorithm createAlgorithm();
    protected abstract Logger getLogger();

    public DirectoryBatchTester(String directory, int numberOfRuns) {
        this.targetDirectory = directory;
        this.numberOfRuns = numberOfRuns;
    }

    public void process() {
        if (Utils.isEmpty(targetDirectory)) {
            getLogger().info("Can't recognize target path.", targetDirectory);
            return;
        }

        File directory = new File(targetDirectory);
        if (!directory.exists() || !directory.isDirectory() || !directory.canRead() ||
                !directory.canWrite()) {
            getLogger().info("Target path '{}' wasn't recognized.", targetDirectory);
            return;
        }

        File[] problemFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && FilenameUtils.getExtension(file.getName())
                                                     .equalsIgnoreCase(PROBLEM_FILE_EXTENSION);
            }
        });

        if (problemFiles.length == 0) {
            getLogger().info("Problem files wasn't found.");
        }

        Algorithm method = createAlgorithm();

        MultistartSolver solver = new MultistartSolver(method, numberOfRuns);
        BatchProcessor processor = new BatchProcessor(solver);

        for (File problemFile : problemFiles) {
            getLogger().info("Adding problem file '{}'", problemFile.getPath());
            processor.addProblem(problemFile.getPath());
        }

        XmlEnabledCollector globalCollector = createGlobalCollector();
        XmlEnabledCollector problemCollector = createProblemCollector();

        processor.process(globalCollector, problemCollector, targetDirectory);
    }

    private XmlEnabledCollector createProblemCollector() {
        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        collector.addMeasure(new StandardDeviation());
        collector.addMeasure(new MinValue());
        collector.addMeasure(new MaxValue());

        collector.addMetric(new ScalarMetric());
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new UniquePatternsMetric());
        collector.addMetric(new ActivePatternsMetric());
        collector.addMetric(new ProductDeviationMetric());
        collector.addMetric(new MaxUnderProductionMetric());
        collector.addMetric(new MaxOverProductionMetric());

        return collector;
    }

    private XmlEnabledCollector createGlobalCollector() {
        XmlEnabledCollector collector = new XmlEnabledCollector();
        collector.addMeasure(new Average());
        /*
        collector.addMeasure(new StandardDeviation());
        collector.addMeasure(new MinValue());
        collector.addMeasure(new MaxValue());
        */

        collector.addMetric(new ScalarMetric());
        collector.addMetric(new TrimLossMetric());
        collector.addMetric(new PatternReductionMetric());
        collector.addMetric(new ProductDeviationMetric());
        collector.addMetric(new MaxUnderProductionMetric());
        collector.addMetric(new AverageUnderProductionMetric());
        collector.addMetric(new MaxOverProductionMetric());
        collector.addMetric(new AverageOverProductionMetric());

        return collector;
    }
}
