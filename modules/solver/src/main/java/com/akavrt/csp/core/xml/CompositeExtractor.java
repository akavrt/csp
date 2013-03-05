package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * User: akavrt
 * Date: 04.03.13
 * Time: 02:42
 */
public class CompositeExtractor {
    private Problem loadedProblem;
    private List<Solution> loadedSolutions;

    public Problem getProblem() {
        return loadedProblem;
    }

    public List<Solution> getSolutions() {
        return loadedSolutions;
    }

    public void extract(Document doc) {
        loadedProblem = loadProblem(doc);
        loadedSolutions = loadedProblem == null ? null : loadSolutions(doc, loadedProblem);
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

