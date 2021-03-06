package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger(CspReader.class);
    private Document document;
    private Problem loadedProblem;
    private List<Solution> loadedSolutions;

    /**
     * <p>Extract problem definition and list of solutions from prepared XML.</p>
     *
     * @param element org.jdom2.Element to use as a source.
     */
    public void convert(Element element) {
        loadedProblem = loadProblem(element);
        loadedSolutions = loadedProblem == null ? null : loadSolutions(element, loadedProblem);
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
            document = null;
            loadedProblem = null;
            loadedSolutions = null;

            SAXBuilder sax = new SAXBuilder();
            document = sax.build(in);
            convert(document.getRootElement());
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
            document = null;
            loadedProblem = null;
            loadedSolutions = null;

            SAXBuilder sax = new SAXBuilder();
            document = sax.build(file);
            convert(document.getRootElement());
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

    /**
     * <p>Returns org.jdom2.Document parsed from the input.</p>
     *
     * @return Parsed org.jdom2.Document.
     */
    public Document getDocument() {
        return document;
    }

    private Problem loadProblem(Element cspElm) {
        if (cspElm == null) {
            return null;
        }

        Problem problem = null;

        // extracting problem's metadata, constraints, orders and rolls
        Element problemElm = cspElm.getChild(XmlTags.PROBLEM);
        if (problemElm != null) {
            problem = new ProblemConverter().extract(problemElm);
        } else {
            LOGGER.info("<{}> element wasn't found.", XmlTags.PROBLEM);
        }

        return problem;
    }

    private List<Solution> loadSolutions(Element cspElm, Problem problem) {
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
        } else {
            LOGGER.info("<{}> element wasn't found.", XmlTags.SOLUTIONS);
        }

        return solutions;
    }

    private interface XmlTags {
        String PROBLEM = "problem";
        String SOLUTIONS = "solutions";
        String SOLUTION = "solution";
    }
}

