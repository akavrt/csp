package com.akavrt.csp.metrics;

import com.akavrt.csp.utils.BaseParameters;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;

/**
 * <p>Parameters used with objective function designed to handle production constraints.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ConstraintAwareMetricParameters extends BaseParameters {
    private static final double DEFAULT_AGGREGATED_TRIM_FACTOR = 0.5;
    private static final double DEFAULT_PATTERNS_FACTOR = 0.5;
    private double aggregatedTrimFactor = DEFAULT_AGGREGATED_TRIM_FACTOR;
    private double patternsFactor = DEFAULT_PATTERNS_FACTOR;

    /**
     * <p>Weight associated with aggregated trim loss fractional ratio.</p>
     */
    public double getAggregatedTrimFactor() {
        return aggregatedTrimFactor;
    }

    /**
     * <p>Set weight associated with aggregated trim loss fractional ratio.</p>
     */
    public void setAggregatedTrimFactor(double aggregatedTrimFactor) {
        this.aggregatedTrimFactor = aggregatedTrimFactor;
    }

    /**
     * <p>Weight associated with pattern reduction fractional ratio.</p>
     */
    public double getPatternsFactor() {
        return patternsFactor;
    }

    /**
     * <p>Set weight associated with pattern reduction fractional ratio.</p>
     */
    public void setPatternsFactor(double patternsFactor) {
        this.patternsFactor = patternsFactor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element constraintScalarElm = new Element(XmlTags.CONSTRAINT_SCALAR);

        // optional description
        if (!Utils.isEmpty(getDescription())) {
            Element descriptionElm = new Element(XmlTags.DESCRIPTION);
            descriptionElm.setText(getDescription());
            constraintScalarElm.addContent(descriptionElm);
        }

        Element aggregatedTrimWeightElm = new Element(XmlTags.AGGREGATED_TRIM_WEIGHT);
        aggregatedTrimWeightElm.setText(XmlUtils.formatDouble(getAggregatedTrimFactor()));
        constraintScalarElm.addContent(aggregatedTrimWeightElm);

        Element patternWeightElm = new Element(XmlTags.PATTERN_WEIGHT);
        patternWeightElm.setText(XmlUtils.formatDouble(getPatternsFactor()));
        constraintScalarElm.addContent(patternWeightElm);

        return constraintScalarElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        String description = rootElm.getChildText(XmlTags.DESCRIPTION);
        if (!Utils.isEmpty(description)) {
            setDescription(description);
        }

        double aggregatedTrimWeight = XmlUtils.getDoubleFromText(rootElm,
                                                                 XmlTags.AGGREGATED_TRIM_WEIGHT,
                                                                 DEFAULT_AGGREGATED_TRIM_FACTOR);
        setAggregatedTrimFactor(aggregatedTrimWeight);

        double patternWeight = XmlUtils.getDoubleFromText(rootElm, XmlTags.PATTERN_WEIGHT,
                                                          DEFAULT_PATTERNS_FACTOR);
        setPatternsFactor(patternWeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRootElementName() {
        return XmlTags.CONSTRAINT_SCALAR;
    }

    private interface XmlTags {
        String CONSTRAINT_SCALAR = "constraint-scalar";
        String AGGREGATED_TRIM_WEIGHT = "aggregated-trim-weight";
        String PATTERN_WEIGHT = "pattern-weight";
        String DESCRIPTION = "description";
    }

}
