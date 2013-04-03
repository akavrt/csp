package com.akavrt.csp.analyzer.xml;

import com.akavrt.csp.analyzer.Measure;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Solution;
import com.akavrt.csp.xml.XmlUtils;
import com.akavrt.csp.metrics.Metric;
import org.jdom2.Element;

/**
 * <p>This implementation of the Collector interface should be used to collect data in test runs,
 * prepare measures and convert calculated values into XML.</p>
 */
public class XmlEnabledCollector extends SimpleCollector {
    private Element result;

    public XmlEnabledCollector() {
        this(false);
    }

    public XmlEnabledCollector(boolean isGlobal) {
        super(isGlobal);
    }

    @Override
    public void process(Problem problem) {
        if (solutions.size() == 0) {
            return;
        }

        Element rootElm = new Element(XmlTags.METRICS);

        // process solution-specific metrics
        for (Metric metric : metrics) {
            Element metricElm = new Element(XmlTags.METRIC);
            rootElm.addContent(metricElm);

            Element nameElm = new Element(XmlTags.NAME);
            nameElm.setText(metric.name());
            metricElm.addContent(nameElm);

            for (Measure measure : measures) {
                double value = measure.calculate(solutions, metric);

                Element measureElm = new Element(measure.name());
                measureElm.setText(XmlUtils.formatDouble(value));
                metricElm.addContent(measureElm);
            }
        }

        // process time
        Element metricElm = new Element(XmlTags.METRIC);
        rootElm.addContent(metricElm);

        Element nameElm = new Element(XmlTags.NAME);
        nameElm.setText("Execution time in milliseconds");
        metricElm.addContent(nameElm);
        for (Measure measure : measures) {
            double value = measure.calculate(executionTimeInMillis);

            Element measureElm = new Element(measure.name());
            measureElm.setText(XmlUtils.formatDouble(value));
            metricElm.addContent(measureElm);
        }

        // calculate feasibility ratio
        if (problem != null) {
            int valid = 0;
            for (Solution solution : solutions) {
                if (solution.isValid(problem)) {
                    valid++;
                }
            }

            double feasibilityRatio = 100 * valid / (double) solutions.size();

            Element feasibilityMetricElm = new Element(XmlTags.METRIC);
            rootElm.addContent(feasibilityMetricElm);

            Element feasibilityNameElm = new Element(XmlTags.NAME);
            feasibilityNameElm.setText("Feasibility ratio");
            feasibilityMetricElm.addContent(feasibilityNameElm);

            Element feasibilityValueElm = new Element(XmlTags.VALUE);
            feasibilityValueElm.setText(XmlUtils.formatDouble(feasibilityRatio) + "%");
            feasibilityMetricElm.addContent(feasibilityValueElm);
        }

        result = rootElm;
    }

    public Element getResult() {
        return result;
    }

    private interface XmlTags {
        String METRICS = "metrics";
        String METRIC = "metric";
        String NAME = "name";
        String VALUE = "value";
    }

}
