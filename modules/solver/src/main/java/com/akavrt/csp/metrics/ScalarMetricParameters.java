package com.akavrt.csp.metrics;

import com.akavrt.csp.xml.XmlUtils;
import com.akavrt.csp.utils.ParameterSet;
import org.jdom2.Element;

/**
 * <p>Parameters used with objective function obtained through linear scalarization.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ScalarMetricParameters implements ParameterSet {
    private static final double DEFAULT_TRIM_FACTOR = 0.33;
    private static final double DEFAULT_PATTERNS_FACTOR = 0.33;
    private static final double DEFAULT_PRODUCTION_FACTOR = 0.33;
    private double trimFactor = DEFAULT_TRIM_FACTOR;
    private double patternsFactor = DEFAULT_PATTERNS_FACTOR;
    private double productionFactor = DEFAULT_PRODUCTION_FACTOR;

    /**
     * <p>Weight associated with trim loss fractional ratio.</p>
     */
    public double getTrimFactor() {
        return trimFactor;
    }

    /**
     * <p>Set weight associated with trim loss fractional ratio.</p>
     */
    public void setTrimFactor(double trimFactor) {
        this.trimFactor = trimFactor;
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
     * <p>Weight associated with product deviation fractional ratio.</p>
     */
    public double getProductionFactor() {
        return productionFactor;
    }

    /**
     * <p>Set weight associated with product deviation fractional ratio.</p>
     */
    public void setProductionFactor(double productionFactor) {
        this.productionFactor = productionFactor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element scalarElm = new Element(XmlTags.SCALAR);

        Element trimWeightElm = new Element(XmlTags.TRIM_WEIGHT);
        trimWeightElm.setText(XmlUtils.formatDouble(getTrimFactor()));
        scalarElm.addContent(trimWeightElm);

        Element patternWeightElm = new Element(XmlTags.PATTERN_WEIGHT);
        patternWeightElm.setText(XmlUtils.formatDouble(getPatternsFactor()));
        scalarElm.addContent(patternWeightElm);

        Element productWeightElm = new Element(XmlTags.PRODUCT_WEIGHT);
        productWeightElm.setText(XmlUtils.formatDouble(getProductionFactor()));
        scalarElm.addContent(productWeightElm);

        return scalarElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element element) {
        if (element == null) {
            return;
        }

        double trimWeight = XmlUtils.getDoubleFromText(element, XmlTags.TRIM_WEIGHT,
                                                       DEFAULT_TRIM_FACTOR);
        setTrimFactor(trimWeight);

        double patternWeight = XmlUtils.getDoubleFromText(element, XmlTags.PATTERN_WEIGHT,
                                                          DEFAULT_PATTERNS_FACTOR);
        setPatternsFactor(patternWeight);

        double productWeight = XmlUtils.getDoubleFromText(element, XmlTags.PRODUCT_WEIGHT,
                                                          DEFAULT_PRODUCTION_FACTOR);
        setProductionFactor(productWeight);

    }

    private interface XmlTags {
        String SCALAR = "scalar";
        String TRIM_WEIGHT = "trim-weight";
        String PATTERN_WEIGHT = "pattern-weight";
        String PRODUCT_WEIGHT = "product-weight";
    }

}
