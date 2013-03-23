package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.xml.XmlUtils;
import com.akavrt.csp.utils.ParameterSet;
import org.jdom2.Element;

/**
 * <p>Parameters of the original sequential heuristic procedure proposed by Haessler, see
 * SequentialProcedure for more details.</p>
 *
 * <p>An instance of this class can be saved to XML and extracted from it using methods defined in
 * XmlCompatible interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class SequentialProcedureParameters implements ParameterSet {
    private static final double DEFAULT_TRIM_RATIO_RELAX_STEP = 0.01;
    private static final double DEFAULT_TRIM_RATIO_LOWER_BOUND = 0;
    private static final double DEFAULT_TRIM_RATIO_UPPER_BOUND = 1;
    private static final int DEFAULT_PATTERN_USAGE_RELAX_STEP = 1;
    private static final double DEFAULT_PATTER_USAGE_UPPER_BOUND = 1;
    private double trimRatioRelaxStep = DEFAULT_TRIM_RATIO_RELAX_STEP;
    private double trimRatioLowerBound = DEFAULT_TRIM_RATIO_LOWER_BOUND;
    private double trimRatioUpperBound = DEFAULT_TRIM_RATIO_UPPER_BOUND;
    private double patternUsageRatioUpperBound = DEFAULT_PATTER_USAGE_UPPER_BOUND;
    private int patternUsageRelaxStep = DEFAULT_PATTERN_USAGE_RELAX_STEP;

    /**
     * <p>The fractional value by which aspiration level corresponding to trim loss is relaxed if
     * pattern search wasn't succeeded. Defined as a fractional ratio, i.e. it value may vary from
     * 0 to 1.</p>
     */
    public double getTrimRatioRelaxStep() {
        return trimRatioRelaxStep;
    }

    /**
     * <p>Set the fractional value by which aspiration level corresponding to trim loss is relaxed
     * if pattern search wasn't succeeded.</p>
     *
     * <p>Default value is 0.01.</p>
     *
     * @param trimRatioRelaxStep The step value added to the current value of the aspiration level
     *                           each time when relaxation of the aspiration level is needed.
     */
    public void setTrimRatioRelaxStep(double trimRatioRelaxStep) {
        this.trimRatioRelaxStep = trimRatioRelaxStep;
    }

    /**
     * <p>Lower bound set for ratio defining allowed trim loss level. Used in search.</p>
     */
    public double getTrimRatioLowerBound() {
        return trimRatioLowerBound;
    }

    /**
     * <p>Set lower bound for ratio defining allowed trim loss level.</p>
     *
     * <p>Default value is 0.</p>
     *
     * <p>We can relax pattern requirements for allowed trim loss levels and immediately start
     * searching for patterns with high trim loss levels.</p>
     */
    public void setTrimRatioLowerBound(double lowerBound) {
        this.trimRatioLowerBound = lowerBound;
    }

    /**
     * <p>Upper bound set for ratio defining allowed trim loss level. Used in search.</p>
     */
    public double getTrimRatioUpperBound() {
        return trimRatioUpperBound;
    }

    /**
     * <p>Set upper bound for ratio defining allowed trim loss level.</p>
     *
     * <p>Default value is 1.</p>
     *
     * <p>We can restrict pattern requirements for allowed trim loss levels and reject patterns
     * with high trim loss levels during search.</p>
     */
    public void setTrimRatioUpperBound(double upperBound) {
        this.trimRatioUpperBound = upperBound;
    }

    /**
     * <p>The integer value by which aspiration level corresponding to pattern usage is relaxed if
     * pattern search wasn't succeeded.</p>
     */
    public int getPatternUsageRelaxStep() {
        return patternUsageRelaxStep;
    }

    /**
     * <p>Set the integer value by which aspiration level corresponding to pattern usage is relaxed
     * if pattern search wasn't succeeded.</p>
     *
     * <p>Default value is 1.</p>
     *
     * @param patternUsageRelaxStep The step value subtracted from the current value of the
     *                              aspiration level each time when relaxation of the aspiration
     *                              level is needed.
     */
    public void setPatternUsageRelaxStep(int patternUsageRelaxStep) {
        this.patternUsageRelaxStep = patternUsageRelaxStep;
    }

    /**
     * <p>Sometimes we need to restrict pattern usage. It is done using this upper bound parameter
     * defined as a fractional ratio: for ratio = 1.0 schedule all ordered strip, for ratio = 0.5
     * schedule only one half of the ordered strip, etc.</p>
     */
    public double getPatternUsageUpperBound() {
        return patternUsageRatioUpperBound;
    }

    /**
     * <p>Restrict pattern usage in terms of ordered strip to be produced using a single pattern.
     * Upper bound is defined as a fractional ratio: for ratio = 1.0 schedule all ordered strip,
     * for ratio = 0.5 schedule only one half of the ordered strip, etc.</p>
     *
     * <p>Default value is 1.</p>
     */
    public void setPatternUsageUpperBound(double upperBoundRatio) {
        this.patternUsageRatioUpperBound = upperBoundRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = new Element(XmlTags.PARAMETERS);

        Element typeElm = new Element(XmlTags.TYPE);
        typeElm.setText(getProcedureType());
        paramsElm.addContent(typeElm);

        // trim ratio parameters
        Element trimRatioElm = new Element(XmlTags.TRIM_RATIO);
        paramsElm.addContent(trimRatioElm);

        Element relaxStepElm = new Element(XmlTags.RELAX_STEP);
        relaxStepElm.setText(XmlUtils.formatDouble(getTrimRatioRelaxStep()));
        trimRatioElm.addContent(relaxStepElm);

        Element lowerBoundElm = new Element(XmlTags.LOWER_BOUND);
        lowerBoundElm.setText(XmlUtils.formatDouble(getTrimRatioLowerBound()));
        trimRatioElm.addContent(lowerBoundElm);

        Element upperBoundElm = new Element(XmlTags.UPPER_BOUND);
        upperBoundElm.setText(XmlUtils.formatDouble(getTrimRatioUpperBound()));
        trimRatioElm.addContent(upperBoundElm);

        // pattern usage parameters
        Element patternUsageElm = new Element(XmlTags.PATTERN_USAGE);
        paramsElm.addContent(patternUsageElm);

        relaxStepElm = new Element(XmlTags.RELAX_STEP);
        relaxStepElm.setText(Integer.toString(getPatternUsageRelaxStep()));
        patternUsageElm.addContent(relaxStepElm);

        upperBoundElm = new Element(XmlTags.UPPER_BOUND);
        upperBoundElm.setText(XmlUtils.formatDouble(getPatternUsageUpperBound()));
        patternUsageElm.addContent(upperBoundElm);

        return paramsElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        Element trimRatioElm = rootElm.getChild(XmlTags.TRIM_RATIO);
        if (trimRatioElm != null) {
            double relaxStep = XmlUtils.getDoubleFromText(trimRatioElm, XmlTags.RELAX_STEP,
                                                          DEFAULT_TRIM_RATIO_RELAX_STEP);
            setTrimRatioRelaxStep(relaxStep);

            double lowerBound = XmlUtils.getDoubleFromText(trimRatioElm, XmlTags.LOWER_BOUND,
                                                           DEFAULT_TRIM_RATIO_LOWER_BOUND);
            setTrimRatioLowerBound(lowerBound);

            double upperBound = XmlUtils.getDoubleFromText(trimRatioElm, XmlTags.UPPER_BOUND,
                                                           DEFAULT_TRIM_RATIO_UPPER_BOUND);
            setTrimRatioUpperBound(upperBound);
        }

        Element patternUsageElm = rootElm.getChild(XmlTags.PATTERN_USAGE);
        if (patternUsageElm != null) {
            int relaxStep = XmlUtils.getIntegerFromText(patternUsageElm, XmlTags.RELAX_STEP,
                                                        DEFAULT_PATTERN_USAGE_RELAX_STEP);
            setPatternUsageRelaxStep(relaxStep);

            double upperBound = XmlUtils.getDoubleFromText(patternUsageElm, XmlTags.UPPER_BOUND,
                                                           DEFAULT_PATTER_USAGE_UPPER_BOUND);
            setPatternUsageUpperBound(upperBound);
        }
    }

    protected String getProcedureType() {
        return HaesslerProcedure.METHOD_NAME;
    }

    private interface XmlTags {
        String PARAMETERS = "sequential";
        String TYPE = "type";
        String TRIM_RATIO = "trim-ratio";
        String PATTERN_USAGE = "pattern-usage";
        String RELAX_STEP = "relax-step";
        String LOWER_BOUND = "lower-bound";
        String UPPER_BOUND = "upper-bound";
    }

}
