package com.akavrt.csp.analyzer.xml;

import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.core.xml.CspWriter;
import com.akavrt.csp.solver.Algorithm;
import com.akavrt.csp.utils.ParameterSet;
import com.akavrt.csp.xml.XmlUtils;
import com.akavrt.csp.xml.XmlWriter;
import com.google.common.collect.Lists;
import org.jdom2.Element;

import java.util.Date;
import java.util.List;

/**
 * <p>Results of the test run could be converted to XML and written into stream or file. This
 * utility class helps to accomplish these tasks.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class RunResultWriter extends XmlWriter {
    private Problem problem;
    private List<Solution> solutions;
    private Algorithm algorithm;
    private int numberOfExecutions;
    private int numberOfProblemsSolved;
    private XmlEnabledCollector collector;

    /**
     * <p>Create empty instance of RunResultWriter. To fill in data fields use setter methods.</p>
     */
    public RunResultWriter() {
        solutions = Lists.newArrayList();
    }

    /**
     * <p>Set problem which will be converted to XML.</p>
     *
     * @param problem The Problem to convert.
     */
    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    /**
     * <p>Set list of solutions which will be converted to XML.</p>
     *
     * @param solutions The list of solutions to convert.
     */
    public void setSolutions(List<Solution> solutions) {
        this.solutions.clear();
        this.solutions.addAll(solutions);
    }

    /**
     * <p>Add solution to the list of solutions which will be converted to XML.</p>
     *
     * @param solution The solution to convert.
     */
    public void addSolution(Solution solution) {
        solutions.add(solution);
    }

    /**
     * <p>Set method used to solve problem.</p>
     *
     * @param algorithm Solution method.
     */
    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * <p>Set number of times the algorithm was executed in test run.</p>
     *
     * @param numberOfExecutions Number of times the algorithm was executed.
     */
    public void setNumberOfExecutions(int numberOfExecutions) {
        this.numberOfExecutions = numberOfExecutions;
    }

    /**
     * <p>Set number of problems solved in test run.</p>
     *
     * <p>We can run same algorithm to solve different problems (representing a class of problems
     * with similar characteristics, for example). In this case it's useful to store the number of
     * problems solved in test run.</p>
     *
     * @param numberOfProblemsSolved Number of problems solved in test run.
     */
    public void setNumberOfProblemsSolved(int numberOfProblemsSolved) {
        this.numberOfProblemsSolved = numberOfProblemsSolved;
    }

    /**
     * <p>Set an instance of collector used to collect and prepare run results.</p>
     *
     * @param collector Collector used to collect and prepare run results.
     */
    public void setCollector(XmlEnabledCollector collector) {
        this.collector = collector;
    }

    /**
     * <p>Convert run results along with problem definition and list of solution into XML
     * represented as an instance of org.jdom2.Document.</p>
     *
     * @return Run results converted to XML.
     */
    public Element convert() {
        Element runElm = new Element(XmlTags.RUN);

        Element dateElm = new Element(XmlTags.DATE);
        dateElm.setText(XmlUtils.formatDate(new Date()));
        runElm.addContent(dateElm);

        if (algorithm != null) {
            Element methodElm = new Element(XmlTags.METHOD);
            runElm.addContent(methodElm);

            Element nameElm = new Element(XmlTags.NAME);
            nameElm.setText(algorithm.name());
            methodElm.addContent(nameElm);

            if (algorithm.getParameters().size() > 0) {
                Element parametersElm = new Element(XmlTags.PARAMETERS);
                methodElm.addContent(parametersElm);

                for (ParameterSet parameters : algorithm.getParameters()) {
                    parametersElm.addContent(parameters.save());
                }
            }
        }

        if (numberOfProblemsSolved > 0) {
            Element executionsElm = new Element(XmlTags.PROBLEMS);
            executionsElm.setText(Integer.toString(numberOfProblemsSolved));
            runElm.addContent(executionsElm);
        }

        if (numberOfExecutions > 0) {
            Element executionsElm = new Element(XmlTags.EXECUTIONS);
            executionsElm.setText(Integer.toString(numberOfExecutions));
            runElm.addContent(executionsElm);
        }

        if (collector != null) {
            collector.process();
            Element metrics = collector.getResult();

            runElm.addContent(metrics);
        }

        CspWriter cspWriter = new CspWriter();
        cspWriter.setProblem(problem);
        cspWriter.setSolutions(solutions);

        Element cspElm = cspWriter.convert();

        Element rootElm = new Element(XmlTags.RESULTS);
        rootElm.addContent(runElm);
        rootElm.addContent(cspElm);

        return rootElm;
    }

    /**
     * <p>Clear all data fields.</p>
     */
    public void clear() {
        problem = null;
        solutions.clear();
        algorithm = null;
        numberOfProblemsSolved = 0;
        numberOfExecutions = 0;
        collector = null;
    }

    private interface XmlTags {
        String RESULTS = "results";
        String RUN = "run";
        String DATE = "date";
        String METHOD = "method";
        String NAME = "name";
        String PARAMETERS = "parameters";
        String PROBLEMS = "problems";
        String EXECUTIONS = "executions";
    }
}
