package com.akavrt.csp.analyzer.xml;

import com.akavrt.csp.analyzer.Measure;
import com.akavrt.csp.analyzer.SimpleCollector;
import com.akavrt.csp.xml.XmlUtils;
import com.akavrt.csp.metrics.Metric;
import org.jdom2.Element;

/**
 * <p>This implementation of the Collector interface should be used to collect data in test runs,
 * prepare measures and convert calculated values into XML.</p>
 */
public class XmlEnabledCollector extends SimpleCollector {
    private Element result;

    @Override
    public void process() {
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

        result = rootElm;
    }

    public Element getResult() {
        return result;
    }

    private interface XmlTags {
        String METRICS = "metrics";
        String METRIC = "metric";
        String NAME = "name";
    }

}
