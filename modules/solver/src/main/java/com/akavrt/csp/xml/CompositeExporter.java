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
public class CompositeExporter {
    private final static String SOLUTION_ID_TEMPLATE = "solution%d";
    public final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
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
        Element cspElm = new Element(ExporterTags.CSP);

        if (problem != null) {
            // preparing metadata
            if (problem.getMetadata() != null) {
                Element metadataElm = prepareMetadata(problem.getMetadata());
                cspElm.addContent(metadataElm);
            }

            // preparing lists of orders and rolls
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

    private Element prepareMetadata(ProblemMetadata metadata) {
        Element metadataElm = new Element(ExporterTags.METADATA);

        Element nameElm = new Element(ExporterTags.NAME);
        nameElm.setText(metadata.getName());
        metadataElm.addContent(nameElm);

        Element authorElm = new Element(ExporterTags.AUTHOR);
        authorElm.setText(metadata.getAuthor());
        metadataElm.addContent(authorElm);

        Element descriptionElm = new Element(ExporterTags.DESCRIPTION);
        descriptionElm.setText(metadata.getDescription());
        metadataElm.addContent(descriptionElm);

        if (metadata.getDate() != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.ENGLISH);
            String formatted = df.format(metadata.getDate());

            Element dateElm = new Element(ExporterTags.DATE);
            dateElm.setText(formatted);
            metadataElm.addContent(dateElm);
        }

        return metadataElm;
    }

    private Element prepareSolutions() {
        Element solutionsElm = new Element(ExporterTags.SOLUTIONS);

        SolutionConverter converter = new SolutionConverter(problem);
        for (int i = 0; i < solutions.size(); i++) {
            String solutionId = String.format(SOLUTION_ID_TEMPLATE, i + 1);

            Element solutionElm = converter.export(solutions.get(i));
            solutionElm.setAttribute(ExporterTags.ID, solutionId);
            solutionsElm.addContent(solutionElm);
        }

        return solutionsElm;
    }

    public interface ExporterTags {
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
