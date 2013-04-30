package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetric;
import com.akavrt.csp.metrics.complex.ConstraintAwareMetricParameters;
import com.akavrt.csp.metrics.complex.ScalarMetric;
import com.akavrt.csp.solver.evo.EvolutionPhase;
import com.akavrt.csp.solver.evo.EvolutionaryAlgorithm;
import com.akavrt.csp.solver.evo.es.EvolutionStrategy;
import com.akavrt.csp.solver.evo.es.EvolutionStrategyParameters;
import com.akavrt.csp.solver.pattern.ConstrainedPatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGenerator;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.tracer.ScalarTracer;
import com.akavrt.csp.tester.tracer.TraceableStrategyComponentsFactory;
import com.akavrt.csp.tester.ui.content.ContentPanel;
import com.akavrt.csp.tester.ui.presets.PresetsPanel;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.SolutionFormatter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 13:10
 */
public class MainFrame extends JFrame implements MainToolBar.OnActionPerformedListener,
        AsyncSolver.OnProblemSolvedListener, ContentPanel.OnSolutionsSelectedListener {
    private static final double SCREEN_DIV = 2;
    private static final String APP_NAME = "csp";
    private static final String APP_NAME_TEMPLATE = APP_NAME + " - %s";
    private static final String DEFAULT_CURRENT_DIRECTORY_PATH = "/Users/akavrt/Sandbox/csp";
    private final DataManager dataManager;
    // views
    private JFileChooser chooser;
    private MainToolBar toolBar;
    private PresetsPanel presetsPanel;
    private ContentPanel contentPanel;
    // core
    private File problemFile;
    private AsyncSolver solver;
    private TraceableStrategyComponentsFactory factory;

    public MainFrame() {
        prepareFileChooser();

        setupFrame();
        setupViews();
        setupParameters();

        dataManager = new DataManager(this);

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
        resetTitle();
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
        contentPanel = new ContentPanel(this);

        add(toolBar, new GBC(0, 0, 2, 1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
        add(presetsPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(0, 100));
        add(contentPanel, new GBC(1, 1).setFill(GBC.BOTH).setWeight(100, 100));
    }

    private void setupParameters() {
        PatternGeneratorParameters patternParams = new PatternGeneratorParameters();
        patternParams.setGenerationTrialsLimit(5);
        presetsPanel.setPatternGeneratorParameters(patternParams);

        EvolutionStrategyParameters strategyParams = new EvolutionStrategyParameters();
        strategyParams.setPopulationSize(50);
        strategyParams.setOffspringCount(45);
        strategyParams.setRunSteps(2000);
        presetsPanel.setEvolutionStrategyParameters(strategyParams);

        presetsPanel.setObjectiveFunctionParameters(new ConstraintAwareMetricParameters());
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public void resetTitle() {
        setTitle(APP_NAME);
    }

    public void setProblemTitle(String problemName) {
        setTitle(String.format(APP_NAME_TEMPLATE, problemName));
    }

    @Override
    public void loadData() {
        File currentDirectory = new File(DEFAULT_CURRENT_DIRECTORY_PATH);
        chooser.setCurrentDirectory(currentDirectory.exists() ? currentDirectory : new File("."));
        chooser.setSelectedFile(new File(""));
        chooser.setMultiSelectionEnabled(false);

        int r = chooser.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // at this point we can clear trace,
        // old data is not valid any more
        contentPanel.clearSeries();
        contentPanel.clearAnalyzerData();

        problemFile = chooser.getSelectedFile();

        dataManager.loadData(problemFile);
    }

    @Override
    public void saveData() {
        if (!dataManager.isDataLoaded()) {
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
        dataManager.saveData(file);
    }

    @Override
    public void clearTrace() {
        contentPanel.clearTextArea();
        contentPanel.clearSeries();
    }

    @Override
    public boolean startCalculations() {
        if (!dataManager.isDataLoaded()) {
            JOptionPane.showMessageDialog(this, "Nothing to solve.", "Warning",
                                          JOptionPane.WARNING_MESSAGE);
            return false;
        }

        contentPanel.appendText("\nExecuting evolution strategy.\n");
        contentPanel.clearSeries();
        contentPanel.clearAnalyzerData();

        if (presetsPanel.isGraphTraceEnabled()) {
            // switch to graph trace tab automatically
            contentPanel.setSelectedIndex(1);
        }

        EvolutionaryAlgorithm algorithm = prepareAlgorithm();
        SeriesMetricProvider metricProvider = createSeriesMetricProvider();

        solver = new AsyncSolver(dataManager.getProblem(), algorithm, this, metricProvider);
        solver.execute();

        return true;
    }

    @Override
    public void stopCalculations() {
        if (solver != null && !solver.isDone()) {
            solver.cancel(true);

            contentPanel.appendText("\nExecution was canceled.\n");
        }
    }

    @Override
    public void onEvolutionProgressChanged(EvolutionProgressUpdate update) {
        toolBar.onEvolutionProgressChanged(update);

        if (presetsPanel.isGraphTraceEnabled()) {
            contentPanel.updateSeries(update.seriesData);
        }

        if (presetsPanel.isTextTraceEnabled()) {
            // append population age and evaluated objective
            // function for the best solution found so far
            if (update.phase != EvolutionPhase.INITIALIZATION && update.seriesData != null) {
                contentPanel.appendText(String.format("Generation #%d, best's objective = %.3f",
                                                      update.seriesData.age,
                                                      update.seriesData.tradeoffObjectiveRatio));
            }
        }
    }

    @Override
    public void onProblemSolved(List<Solution> obtained) {
        toolBar.setProgressBarVisible(false);
        toolBar.setStartActionEnabled(true);
        toolBar.setStopActionEnabled(false);

        // TODO remove
        if (factory != null) {
            factory.traceMutation();
        }

        if (obtained == null || obtained.isEmpty()) {
            // notify user with update in text trace
            contentPanel.appendText("\nNo solution was found in run.");
        } else if (obtained.get(0) != null) {
            contentPanel.setAnalyzerData(dataManager.getProblem(), obtained,
                                         createDetailsMetricProvider());

            Solution best = obtained.get(0);

            // print out best solution in text trace
            String caption = "\nBest solution found in run:";
            ScalarTracer tracer = new ScalarTracer(new ScalarMetric());
            String trace = tracer.trace(best);
            String formatted = SolutionFormatter.format(best, caption, trace, true);

            contentPanel.appendText(formatted);
        }

    }

    private EvolutionaryAlgorithm prepareAlgorithm() {
        PatternGeneratorParameters generatorParams = presetsPanel.getPatternGeneratorParameters();
        if (generatorParams == null) {
            generatorParams = new PatternGeneratorParameters();
        }

        PatternGenerator generator = new ConstrainedPatternGenerator(generatorParams);

        ConstraintAwareMetricParameters objectiveParams = presetsPanel
                .getObjectiveFunctionParameters();
        if (objectiveParams == null) {
            objectiveParams = new ConstraintAwareMetricParameters();
        }

        ConstraintAwareMetric metric = new ConstraintAwareMetric(objectiveParams);

        EvolutionStrategyParameters strategyParams = presetsPanel.getEvolutionStrategyParameters();
        if (strategyParams == null) {
            strategyParams = new EvolutionStrategyParameters();
        }

        factory = new TraceableStrategyComponentsFactory(generator, new ConstraintAwareMetric());

        return new EvolutionStrategy(factory, metric, strategyParams);
    }

    private SeriesMetricProvider createSeriesMetricProvider() {
        ConstraintAwareMetricParameters params = presetsPanel.getObjectiveFunctionParameters();
        if (params == null) {
            params = new ConstraintAwareMetricParameters();
        }

        return new StandardMetricProvider(params);
    }

    private SeriesMetricProvider createDetailsMetricProvider() {
        ConstraintAwareMetricParameters params = presetsPanel.getObjectiveFunctionParameters();
        if (params == null) {
            params = new ConstraintAwareMetricParameters();
        }

        return new DetailsMetricProvider(params);
    }

    @Override
    public void onSolutionsSelected(List<Solution> selection) {
        if (!dataManager.isDataLoaded() || selection == null || selection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to export.", "Selection is empty",
                                          JOptionPane.WARNING_MESSAGE);
            return;
        }

        dataManager.handleSelection(selection);
    }

}
