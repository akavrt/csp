package com.akavrt.csp.tester.ui;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspParseException;
import com.akavrt.csp.core.xml.CspReader;
import com.akavrt.csp.core.xml.CspWriter;
import com.akavrt.csp.tester.utils.Utils;
import com.akavrt.csp.utils.ProblemFormatter;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * User: akavrt
 * Date: 30.04.13
 * Time: 14:10
 */
public class DataManager {
    private static final Logger LOGGER = LogManager.getLogger(DataManager.class);
    private final MainFrame frame;
    private Problem problem;
    private List<Solution> solutions;

    public DataManager(MainFrame frame) {
        this.frame = frame;
    }

    public Problem getProblem() {
        return problem;
    }

    public boolean isDataLoaded() {
        return problem != null;
    }

    public void loadData(File problemFile) {
        CspReader reader = new CspReader();
        try {
            reader.read(problemFile);
        } catch (CspParseException e) {
            LOGGER.catching(e);
        }

        problem = reader.getProblem();
        solutions = reader.getSolutions();

        if (problem == null || reader.getDocument() == null) {
            frame.resetTitle();
            frame.getContentPanel().appendText("\nProblem wasn't loaded.");
            frame.getContentPanel().setXml("");
        } else {
            String problemName = Utils.extractProblemName(problem, problemFile.getPath());
            frame.setProblemTitle(problemName);

            try {
                XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                StringWriter writer = new StringWriter();
                outputter.output(reader.getDocument(), writer);

                frame.getContentPanel().setXml(writer.toString());
            } catch (IOException e) {
                LOGGER.catching(e);
            }

            frame.getContentPanel().appendText(String.format("\nLoaded problem %s from '%s' file.",
                                                             problemName, problemFile.getPath()));
            String formattedProblem = ProblemFormatter.format(problem);
            frame.getContentPanel().appendText(formattedProblem);
        }
    }

    public void saveData(File problemFile) {
        try {
            CspWriter writer = new CspWriter();
            writer.setProblem(problem);
            writer.setSolutions(solutions);
            writer.write(problemFile, true);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    public void handleSelection(List<Solution> selection) {
        if (solutions == null) {
            solutions = Lists.newArrayList();
        }

        solutions.addAll(selection);

        try {
            CspWriter writer = new CspWriter();
            writer.setProblem(problem);
            writer.setSolutions(solutions);

            Document doc = new Document(writer.convert());

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            StringWriter stringWriter = new StringWriter();
            outputter.output(doc, stringWriter);

            frame.getContentPanel().setXml(stringWriter.toString());
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

}
