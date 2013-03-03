package com.akavrt.csp.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metadata.ProblemMetadata;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 01:37
 */
public class CompositeConverter {
    private final static String SOLUTION_ID_TEMPLATE = "solution%d";
    private final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

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

        if (problem.getMetadata() != null) {
            Element metadataElm = prepareMetadata(problem.getMetadata());
            cspElm.addContent(metadataElm);
        }

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

    private Element prepareMetadata(ProblemMetadata metadata) {
        Element metadataElm = new Element(CompositeTags.METADATA);

        Element nameElm = new Element(CompositeTags.NAME);
        nameElm.setText(metadata.getName());
        metadataElm.addContent(nameElm);

        Element authorElm = new Element(CompositeTags.AUTHOR);
        authorElm.setText(metadata.getAuthor());
        metadataElm.addContent(authorElm);

        Element descriptionElm = new Element(CompositeTags.DESCRIPTION);
        descriptionElm.setText(metadata.getDescription());
        metadataElm.addContent(descriptionElm);

        if (metadata.getDate() != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
            String formatted = df.format(metadata.getDate());

            Element dateElm = new Element(CompositeTags.DATE);
            dateElm.setText(formatted);
            metadataElm.addContent(dateElm);
        }

        return metadataElm;
    }

    public interface CompositeTags {
        String CSP = "csp";
        String METADATA = "metadata";
        String NAME = "name";
        String AUTHOR = "author";
        String DATE = "date";
        String DESCRIPTION = "description";
        String SOLUTIONS = "solutions";
        String ID = "id";
    }
}
