package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.metrics.ScalarMetricParameters;
import com.akavrt.csp.solver.genetic.GeneticAlgorithmParameters;
import com.akavrt.csp.solver.pattern.PatternGeneratorParameters;
import com.akavrt.csp.tester.ui.utils.GBC;
import com.akavrt.csp.utils.ProblemFormatter;
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

/**
 * User: akavrt
 * Date: 09.04.13
 * Time: 13:10
 */
public class MainFrame extends JFrame implements MainToolBar.OnActionPerformedListener {
    private static final Logger LOGGER = LogManager.getLogger(MainFrame.class);
    private static final double SCREEN_DIV = 2;
    private final JFileChooser chooser;
    private PresetsPanel presetsPanel;
    private ContentPanel contentPanel;
    private Problem problem;

    public MainFrame() {
        chooser = new JFileChooser();

        setupFrame();
        setupViews();
        setupParameters();

        pack();
        setVisible(true);
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

    private void setupFrame() {
        setTitle("csp");
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

        presetsPanel = new PresetsPanel();
        contentPanel = new ContentPanel();

        add(new MainToolBar(this), new GBC(0, 0, 2, 1).setFill(GBC.HORIZONTAL).setWeight(100, 0));
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

        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML files", "xml");
        chooser.setFileFilter(filter);

        int r = chooser.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File problemFile = chooser.getSelectedFile();

        CspReader reader = new CspReader();
        try {
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        problem = reader.getProblem();

        if (problem == null || reader.getDocument() == null) {
            contentPanel.appendText("\nProblem wasn't loaded.");
            contentPanel.setXml("");
        } else {
            try {
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                StringWriter writer = new StringWriter();
                outputter.output(reader.getDocument(), writer);

                contentPanel.setXml(writer.toString());
            } catch (IOException e) {
                LOGGER.catching(e);
            }

            contentPanel.appendText(String.format("\nProblem from '%s' file was successfully loaded.", problemFile.getPath()));
            String formattedProblem = ProblemFormatter.format(problem);
            contentPanel.appendText(formattedProblem);
        }
    }

    @Override
    public void saveData() {
        if (problem == null) {
            JOptionPane.showMessageDialog(this, "Nothing to save.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void clearTrace() {
        contentPanel.clearTextArea();
    }

    @Override
    public boolean startCalculations() {
        if (problem == null) {
            JOptionPane.showMessageDialog(this, "Nothing to solve.", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void stopCalculations() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
