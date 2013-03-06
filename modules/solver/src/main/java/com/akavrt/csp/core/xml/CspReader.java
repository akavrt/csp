package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <p>This class is a counterpart to CspWriter which provides reading capabilities. Data can be
 * read from a stream or file and parsed to XML which in turn is converted to problem definition
 * and list of solutions.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class CspReader {
    private Problem loadedProblem;
    private List<Solution> loadedSolutions;

    /**
     * <p>Extract problem definition and list of solutions from prepared XML.</p>
     *
     * @param doc org.jdom2.Document to use as a source.
     */
    public void process(Document doc) {
        loadedProblem = loadProblem(doc);
        loadedSolutions = loadedProblem == null ? null : loadSolutions(doc, loadedProblem);
    }

    /**
     * <p>Read data from a stream, parse it to XML and the use to extract problem definition and
     * list of solutions.</p>
     *
     * @param in InputStream to use.
     * @throws CspParseException If problem occurs while reading or parsing data from a stream.
     */
    public void read(InputStream in) throws CspParseException {
        try {
            loadedProblem = null;
            loadedSolutions = null;

            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(in);
            process(doc);
        } catch (JDOMException e) {
            throw new CspParseException("Error related to XML parsing.", e);
        } catch (IOException e) {
            throw new CspParseException("IO-related error occurred while trying to parse data.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>Read data from a file, parse it to XML and the use to extract problem definition and
     * list of solutions.</p>
     *
     * @param file File to use.
     * @throws CspParseException If problem occurs while reading or parsing data from a file.
     */
    public void read(File file) throws CspParseException {
        try {
            loadedProblem = null;
            loadedSolutions = null;

            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(file);
            process(doc);
        } catch (JDOMException e) {
            throw new CspParseException("Error related to XML parsing.", e);
        } catch (IOException e) {
            throw new CspParseException("IO-related error occurred while trying to parse data.", e);
        }
    }

    /**
     * <p>Problem definition extracted from XML.</p>
     *
     * @return Extracted problem.
     */
    public Problem getProblem() {
        return loadedProblem;
    }

    /**
     * <p>List of solutions extracted from XML.</p>
     *
     * @return Extracted solutions.
     */
    public List<Solution> getSolutions() {
        return loadedSolutions;
    }

    private Problem loadProblem(Document doc) {
        Element cspElm = doc.getRootElement();
        if (cspElm == null) {
            return null;
        }

        Problem problem = null;

        // extracting problem's metadata, constraints, orders and rolls
        Element problemElm = cspElm.getChild(XmlTags.PROBLEM);
        if (problemElm != null) {
            problem = new ProblemConverter().extract(problemElm);
        }

        return problem;
    }

    private List<Solution> loadSolutions(Document doc, Problem problem) {
        Element cspElm = doc.getRootElement();
        if (cspElm == null) {
            return null;
        }

        // extracting solutions
        List<Solution> solutions = null;
        Element solutionsElm = cspElm.getChild(XmlTags.SOLUTIONS);
        if (solutionsElm != null) {
            solutions = Lists.newArrayList();
            SolutionConverter converter = new SolutionConverter(problem);
            for (Element solutionElm : solutionsElm.getChildren(XmlTags.SOLUTION)) {
                Solution solution = converter.extract(solutionElm);
                solutions.add(solution);
            }
        }

        return solutions;
    }

    private interface XmlTags {
        String PROBLEM = "problem";
        String SOLUTIONS = "solutions";
        String SOLUTION = "solution";
    }
}

