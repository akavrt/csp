package com.akavrt.csp.xml;

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
public class CompositeConverter {
    private final static String SOLUTION_ID_TEMPLATE = "solution%d";

    private Problem problem;
    private List<Solution> solutions;

    public CompositeConverter() {
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

    public Document convert() {
        Element cspElm = new Element(CompositeTags.CSP);

        if (problem != null) {
            Element problemElm = new ProblemConverter().convert(problem);
            cspElm.addContent(problemElm);
        }

        if (solutions != null && solutions.size() > 0) {
            Element solutionsElm = new Element(CompositeTags.SOLUTIONS);
            cspElm.addContent(solutionsElm);

            SolutionConverter converter = new SolutionConverter(problem.getOrders());
            for (int i = 0; i < solutions.size(); i++) {
                String solutionId = String.format(SOLUTION_ID_TEMPLATE, i + 1);

                Element solutionElm = converter.convert(solutions.get(i));
                solutionElm.setAttribute(CompositeTags.ID, solutionId);
                solutionsElm.addContent(solutionElm);
            }
        }

        return new Document(cspElm);
    }

    public interface CompositeTags {
        String CSP = "csp";
        String SOLUTIONS = "solutions";
        String ID = "id";
    }
}
