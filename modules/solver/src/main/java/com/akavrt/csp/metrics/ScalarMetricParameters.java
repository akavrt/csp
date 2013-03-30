package com.akavrt.csp.metrics;

import com.akavrt.csp.utils.Utils;
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
    private String description;

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
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element scalarElm = new Element(XmlTags.SCALAR);

        // optional description
        if (!Utils.isEmpty(description)) {
            Element descriptionElm = new Element(XmlTags.DESCRIPTION);
            descriptionElm.setText(description);
            scalarElm.addContent(descriptionElm);
        }

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
    public void load(Element rootElm) {
        String description = rootElm.getChildText(XmlTags.DESCRIPTION);
        if (!Utils.isEmpty(description)) {
            setDescription(description);
        }

        double trimWeight = XmlUtils.getDoubleFromText(rootElm, XmlTags.TRIM_WEIGHT,
                                                       DEFAULT_TRIM_FACTOR);
        setTrimFactor(trimWeight);

        double patternWeight = XmlUtils.getDoubleFromText(rootElm, XmlTags.PATTERN_WEIGHT,
                                                          DEFAULT_PATTERNS_FACTOR);
        setPatternsFactor(patternWeight);

        double productWeight = XmlUtils.getDoubleFromText(rootElm, XmlTags.PRODUCT_WEIGHT,
                                                          DEFAULT_PRODUCTION_FACTOR);
        setProductionFactor(productWeight);

    }

    private interface XmlTags {
        String SCALAR = "scalar";
        String TRIM_WEIGHT = "trim-weight";
        String PATTERN_WEIGHT = "pattern-weight";
        String PRODUCT_WEIGHT = "product-weight";
        String DESCRIPTION = "description";
    }

}
