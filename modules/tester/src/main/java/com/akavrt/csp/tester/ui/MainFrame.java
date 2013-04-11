package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.core.xml.CspWriter;
import com.akavrt.csp.genetic.PatternBasedComponentsFactory;
import com.akavrt.csp.metrics.ScalarMetric;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.genetic.GeneticAlgorithm;
import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.tester.utils.Utils;
import com.akavrt.csp.utils.ProblemFormatter;
import com.akavrt.csp.utils.SolutionFormatter;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 13:10
 */
public class MainFrame extends JFrame implements MainToolBar.OnActionPerformedListener,
        AsyncSolver.OnProblemSolvedListener {
    private static final Logger LOGGER = LogManager.getLogger(MainFrame.class);
    private static final double SCREEN_DIV = 2;
    private static final String APP_NAME = "csp";
    private static final String APP_NAME_TEMPLATE = APP_NAME + " - %s";
    private JFileChooser chooser;
    private MainToolBar toolBar;
    private PresetsPanel presetsPanel;
    private ContentPanel contentPanel;
    private Problem problem;
    private List<Solution> solutions;
    private File problemFile;
    private AsyncSolver solver;
    private ScalarMetric metric;

    public MainFrame() {
        prepareFileChooser();

        setupFrame();
        setupViews();
        setupParameters();

        pack();
        setVisible(true);

        contentPanel.setFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        new MainFrame();
    }

    private void prepareFileChooser() {
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
        chooser.addChoosableFileFilter(filter);
    }

    private void setupFrame() {
        setTitle(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // calc window size and position
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int windowWidth = (int) (screenWidth / SCREEN_DIV);
        int windowHeight = (int) (screenHeight / SCREEN_DIV);

        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setLocation((screenWidth - windowWidth) / 2, (screenHeight - windowHeight) / 2);
    }

    private void setupViews() {
        setLayout(new GridBagLayout());

        toolBar = new MainToolBar(this);
        presetsPanel = new PresetsPanel();
        contentPanel = new ContentPanel();

        add(toolBar, new GBC(0, 0, 2, 1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(presetsPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(0, 100));
        add(contentPanel, new GBC(1, 1).setFill(GBC.BOTH).setWeight(100, 100));
    }

    private void setupParameters() {
        presetsPanel.setPatternGeneratorParameters(new PatternGeneratorParameters());
        presetsPanel.setGeneticAlgorithmParameters(new GeneticAlgorithmParameters());
        presetsPanel.setObjectiveFunctionParameters(new ScalarMetricParameters());
    }

    @Override
    public void loadProblem() {
        chooser.setCurrentDirectory(new File("/Users/akavrt/Sandbox/csp"));
        //        chooser.setCurrentDirectory(new File("."));
        chooser.setSelectedFile(new File(""));
        chooser.setMultiSelectionEnabled(false);

        int r = chooser.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        problemFile = chooser.getSelectedFile();

        CspReader reader = new CspReader();
        try {
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        problem = reader.getProblem();
        solutions = reader.getSolutions();

        if (problem == null || reader.getDocument() == null) {
            setTitle(APP_NAME);
            contentPanel.appendText("\nProblem wasn't loaded.");
            contentPanel.setXml("");
        } else {
            String problemName = Utils.extractProblemName(problem, problemFile.getPath());
            setTitle(String.format(APP_NAME_TEMPLATE, problemName));

            try {
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                StringWriter writer = new StringWriter();
                outputter.output(reader.getDocument(), writer);

                contentPanel.setXml(writer.toString());
            } catch (IOException e) {
                LOGGER.catching(e);
            }

            contentPanel.appendText(String.format("\nProblem %s from '%s' file was loaded " +
                                                          "successfully.",
                                                  problemName, problemFile.getPath()));
            String formattedProblem = ProblemFormatter.format(problem);
            contentPanel.appendText(formattedProblem);
        }
    }

    @Override
    public void saveData() {
        if (problem == null) {
            JOptionPane.showMessageDialog(this, "Nothing to save.", "Warning",
                                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (problemFile != null) {
            chooser.setCurrentDirectory(problemFile.getParentFile());
            chooser.setSelectedFile(problemFile);
        } else {
            chooser.setCurrentDirectory(new File("."));
            chooser.setSelectedFile(new File("problem.xml"));
        }

        chooser.setMultiSelectionEnabled(false);

        int r = chooser.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try {
            CspWriter writer = new CspWriter();
            writer.setProblem(problem);
            writer.setSolutions(solutions);
            writer.write(file, true);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    @Override
    public void clearTrace() {
        contentPanel.clearTextArea();
        contentPanel.clearSeries();
    }

    @Override
    public boolean startCalculations() {
        if (problem == null) {
            JOptionPane.showMessageDialog(this, "Nothing to solve.", "Warning",
                                          JOptionPane.WARNING_MESSAGE);
            return false;
        }

        contentPanel.appendText("\nExecuting genetic algorithm.");
        contentPanel.clearSeries();

        GeneticAlgorithm algorithm = prepareAlgorithm();
        SeriesMetricProvider metricProvider = createMetricProvider();

        solver = new AsyncSolver(problem, algorithm, this, metricProvider);
        solver.execute();

        return true;
    }

    @Override
    public void stopCalculations() {
        if (solver != null && !solver.isDone()) {
            solver.cancel(true);

            contentPanel.appendText("\nExecution was canceled.");
        }
    }

    @Override
    public void onGeneticProgressChanged(GeneticProgressUpdate update) {
        toolBar.onGeneticProgressChanged(update);

        if (presetsPanel.isGraphTraceEnabled()) {
            contentPanel.updateSeries(update.seriesData);
        }
    }

    @Override
    public void onProblemSolved(List<Solution> obtained) {
        toolBar.setProgressBarVisible(false);
        toolBar.setStartActionEnabled(true);
        toolBar.setStopActionEnabled(false);

        if (solutions == null) {
            solutions = Lists.newArrayList();
        }

        if (obtained == null || obtained.isEmpty()) {
            // notify user with update in text trace
            contentPanel.appendText("\nNo solution was found in run.");
        } else if (obtained.get(0) != null) {
            Solution best = obtained.get(0);

            solutions.add(best);

            // print out best solution in text trace
            String caption = "\nBest solution found in run:";
            String formatted;
            if (metric != null) {
                ScalarTracer tracer = new ScalarTracer(metric);
                String trace = tracer.trace(best);
                formatted = SolutionFormatter.format(best, caption, trace, true);
            } else {
                formatted = SolutionFormatter.format(best, caption, true);
            }

            contentPanel.appendText(formatted);
        }

    }

    private GeneticAlgorithm prepareAlgorithm() {
        PatternGeneratorParameters generatorParams = presetsPanel.getPatternGeneratorParameters();
        if (generatorParams == null) {
            generatorParams = new PatternGeneratorParameters();
        }

        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

        ScalarMetricParameters scalarParams = presetsPanel.getObjectiveFunctionParameters();
        if (scalarParams == null) {
            scalarParams = new ScalarMetricParameters();
        }

        metric = new ScalarMetric(scalarParams);

        GeneticAlgorithmParameters geneticParams = presetsPanel.getGeneticAlgorithmParameters();
        if (geneticParams == null) {
            geneticParams = new GeneticAlgorithmParameters();
        }

        PatternBasedComponentsFactory factory = new PatternBasedComponentsFactory(generator);

        return new GeneticAlgorithm(factory, metric, geneticParams);
    }

    private SeriesMetricProvider createMetricProvider() {
        ScalarMetricParameters scalarParams = presetsPanel.getObjectiveFunctionParameters();
        if (scalarParams == null) {
            scalarParams = new ScalarMetricParameters();
        }

        return new StandardMetricProvider(scalarParams);
    }
}
