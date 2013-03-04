package com.akavrt.csp.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.metadata.ProblemMetadata;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // extracting problem's orders and roll
        Problem problem = null;
        Element problemElm = cspElm.getChild(ExtractorTags.PROBLEM);
        if (problemElm != null) {
            problem = new ProblemConverter().extract(problemElm);
        }

        // extracting problem's metadata
        if (problem != null) {
            Element metadataElm = cspElm.getChild(ExtractorTags.METADATA);
            if (metadataElm != null) {
                ProblemMetadata metadata = retrieveMetadata(metadataElm);
                problem.setMetadata(metadata);
            }
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
        Element solutionsElm = cspElm.getChild(ExtractorTags.SOLUTIONS);
        if (solutionsElm != null) {
            solutions = Lists.newArrayList();
            SolutionConverter converter = new SolutionConverter(problem);
            for (Element solutionElm : solutionsElm.getChildren(ExtractorTags.SOLUTION)) {
                Solution solution = converter.extract(solutionElm);
                solutions.add(solution);
            }
        }

        return solutions;
    }

    private ProblemMetadata retrieveMetadata(Element rootElm) {
        ProblemMetadata metadata = new ProblemMetadata();

        Element nameElm = rootElm.getChild(ExtractorTags.NAME);
        if (nameElm != null) {
            metadata.setName(nameElm.getText());
        }

        Element authorElm = rootElm.getChild(ExtractorTags.AUTHOR);
        if (authorElm != null) {
            metadata.setAuthor(authorElm.getText());
        }

        Element descriptionElm = rootElm.getChild(ExtractorTags.DESCRIPTION);
        if (descriptionElm != null) {
            metadata.setDescription(descriptionElm.getText());
        }

        Element dateElm = rootElm.getChild(ExtractorTags.DATE);
        if (dateElm != null) {
            DateFormat df = new SimpleDateFormat(CompositeExporter.DATE_FORMAT_PATTERN,
                                                 Locale.ENGLISH);
            String formatted = dateElm.getText();

            try {
                Date date = df.parse(formatted);
                metadata.setDate(date);
            } catch (ParseException e) {
                // TODO add logger statement
                e.printStackTrace();
            }
        }

        return metadata;
    }


    public interface ExtractorTags {
        String METADATA = "metadata";
        String NAME = "name";
        String AUTHOR = "author";
        String DATE = "date";
        String DESCRIPTION = "description";
        String PROBLEM = "problem";
        String SOLUTIONS = "solutions";
        String SOLUTION = "solution";
    }
}

