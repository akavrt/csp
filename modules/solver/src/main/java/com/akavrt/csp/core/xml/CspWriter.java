package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.xml.XmlWriter;
import com.google.common.collect.Lists;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.DOMOutputter;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>Here we use own format to store problem definition and solutions in XML. This format is
 * highly inspired by NestingXML. One of the key features of NestingXML is that both problem
 * definition (orders and stock) and solutions (cutting plans) are stored together in one file. See
 * <a href="http://paginas.fe.up.pt/~esicup/tiki-index.php?page=NestingXML">ESICUP website</a> for
 * more information about NestingXM).</p>
 *
 * <p>This class adds another level of abstraction and links solutions of the roll trimming problem
 * to problem definition. Before we can write XML content to file or stream, we need to supply
 * CspWriter with a valid instance of Problem and any number of solutions. The later is optional:
 * problem can be converted to XML separately from solutions, but solutions can't be converted
 * to XML if corresponding problem wasn't set.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class CspWriter extends XmlWriter {
    private final static String SOLUTION_ID_TEMPLATE = "solution%d";
    private Problem exportedProblem;
    private List<Solution> exportedSolutions;

    /**
     * <p>Convert problem definition and list of solution into XML represented as an instance of
     * org.jdom2.Document.</p>
     *
     * @return Data structure with problem and solutions converted to XML.
     */
    public Element convert() {
        Element cspElm = new Element(XmlTags.CSP);

        if (exportedProblem != null) {
            // preparing metadata, constraints, lists of orders and rolls
            Element problemElm = new ProblemConverter().export(exportedProblem);
            cspElm.addContent(problemElm);

            // solutions can't be converted to XML if no problem was set
            if (exportedSolutions != null && exportedSolutions.size() > 0) {
                // preparing solutions
                Element solutionsElm = prepareSolutions();
                cspElm.addContent(solutionsElm);
            }
        }

        return cspElm;
    }

    /**
     * <p>Set problem which will be converted to XML.</p>
     *
     * @param problem The Problem to convert.
     */
    public void setProblem(Problem problem) {
        this.exportedProblem = problem;
    }

    /**
     * <p>Set list of solutions which will be converted to XML.</p>
     *
     * @param solutions The list of solutions to convert.
     */
    public void setSolutions(List<Solution> solutions) {
        if (exportedSolutions == null) {
            exportedSolutions = Lists.newArrayList();
        } else {
            exportedSolutions.clear();
        }

        if (solutions != null) {
            exportedSolutions.addAll(solutions);
        }
    }

    /**
     * <p>Add solution to the list of solutions which will be converted to XML.</p>
     *
     * @param solution The solution to convert.
     */
    public void addSolution(Solution solution) {
        if (exportedSolutions == null) {
            exportedSolutions = Lists.newArrayList();
        }

        exportedSolutions.add(solution);
    }

    private Element prepareSolutions() {
        Element solutionsElm = new Element(XmlTags.SOLUTIONS);

        SolutionConverter converter = new SolutionConverter(exportedProblem);
        int exported = 0;
        for (int i = 0; i < exportedSolutions.size(); i++) {
            Solution solution = exportedSolutions.get(i);
            if (solution != null) {
                String solutionId = String.format(SOLUTION_ID_TEMPLATE, ++exported);

                Element solutionElm = converter.export(solution);
                solutionElm.setAttribute(XmlTags.ID, solutionId);
                solutionsElm.addContent(solutionElm);
            }
        }

        return solutionsElm;
    }

    private interface XmlTags {
        String CSP = "csp";
        String SOLUTIONS = "solutions";
        String ID = "id";
    }
}
