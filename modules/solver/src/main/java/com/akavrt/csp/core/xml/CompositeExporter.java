package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 01:37
 */
public class CompositeExporter {
    private final static String SOLUTION_ID_TEMPLATE = "solution%d";

    private Problem problem;
    private List<Solution> solutions;

    public CompositeExporter() {
        solutions = Lists.newArrayList();
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions.clear();
        this.solutions.addAll(solutions);
    }

    public void addSolution(Solution solution) {
        solutions.add(solution);
    }

    public Document export() {
        Element cspElm = new Element(XmlTags.CSP);

        if (problem != null) {
            // preparing metadata, constraints, lists of orders and rolls
            Element problemElm = new ProblemConverter().export(problem);
            cspElm.addContent(problemElm);
        }

        // preparing solutions
        if (solutions != null && solutions.size() > 0) {
            Element solutionsElm = prepareSolutions();
            cspElm.addContent(solutionsElm);
        }

        return new Document(cspElm);
    }

    private Element prepareSolutions() {
        Element solutionsElm = new Element(XmlTags.SOLUTIONS);

        SolutionConverter converter = new SolutionConverter(problem);
        for (int i = 0; i < solutions.size(); i++) {
            String solutionId = String.format(SOLUTION_ID_TEMPLATE, i + 1);

            Element solutionElm = converter.export(solutions.get(i));
            solutionElm.setAttribute(XmlTags.ID, solutionId);
            solutionsElm.addContent(solutionElm);
        }

        return solutionsElm;
    }

    private interface XmlTags {
        String CSP = "csp";
        String SOLUTIONS = "solutions";
        String ID = "id";
    }
}
