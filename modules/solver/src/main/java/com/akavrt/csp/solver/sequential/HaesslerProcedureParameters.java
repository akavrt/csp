package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.xml.XmlUtils;
import com.akavrt.csp.xml.XmlCompatible;
import org.jdom2.Element;

/**
 * <p>Parameters of the Haessler's sequential heuristic procedure, see HaesslerProcedure for more
 * details.</p>
 *
 * <p>An instance of this class can be saved to XML and extracted from it using methods defined in
 * XmlCompatible interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class HaesslerProcedureParameters implements XmlCompatible {
    private static final double DEFAULT_TRIM_RATIO_RELAX_DELTA = 0.01;
    private static final int DEFAULT_PATTERN_USAGE_RELAX_DELTA = 1;
    private double trimRatioRelaxDelta = DEFAULT_TRIM_RATIO_RELAX_DELTA;
    private int patternUsageRelaxDelta = DEFAULT_PATTERN_USAGE_RELAX_DELTA;

    /**
     * <p>The fractional value by which aspiration level corresponding to trim loss is relaxed if
     * pattern search wasn't succeeded. Defined as a fractional ratio, i.e. it value may vary from
     * 0 to 1.</p>
     */
    public double getTrimRatioRelaxDelta() {
        return trimRatioRelaxDelta;
    }

    /**
     * <p>Set the fractional value by which aspiration level corresponding to trim loss is relaxed
     * if pattern search wasn't succeeded.</p>
     *
     * @param trimRatioRelaxDelta The step value added to the current value of the aspiration level
     *                            each time when relaxation of the aspiration level is needed.
     */
    public void setTrimRatioRelaxDelta(double trimRatioRelaxDelta) {
        this.trimRatioRelaxDelta = trimRatioRelaxDelta;
    }

    /**
     * <p>The integer value by which aspiration level corresponding to pattern usage is relaxed if
     * pattern search wasn't succeeded.</p>
     */
    public int getPatternUsageRelaxDelta() {
        return patternUsageRelaxDelta;
    }

    /**
     * <p>Set the integer value by which aspiration level corresponding to pattern usage is relaxed
     * if pattern search wasn't succeeded.</p>
     *
     * @param patternUsageRelaxDelta The step value subtracted from the current value of the
     *                               aspiration level each time when relaxation of the aspiration
     *                               level is needed.
     */
    public void setPatternUsageRelaxDelta(int patternUsageRelaxDelta) {
        this.patternUsageRelaxDelta = patternUsageRelaxDelta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = new Element(XmlTags.PARAMETERS);

        Element typeElm = new Element(XmlTags.TYPE);
        typeElm.setText(HaesslerProcedure.METHOD_NAME);
        paramsElm.addContent(typeElm);

        Element trimRatioRelaxElm = new Element(XmlTags.TRIM_RATIO_RELAX_DELTA);
        trimRatioRelaxElm.setText(XmlUtils.formatDouble(getTrimRatioRelaxDelta()));
        paramsElm.addContent(trimRatioRelaxElm);

        Element patternUsageRelaxElm = new Element(XmlTags.PATTERN_USAGE_RELAX_DELTA);
        patternUsageRelaxElm.setText(Integer.toString(getPatternUsageRelaxDelta()));
        paramsElm.addContent(patternUsageRelaxElm);

        return paramsElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        Element trimRatioRelaxElm = rootElm.getChild(XmlTags.TRIM_RATIO_RELAX_DELTA);
        if (trimRatioRelaxElm != null) {
            setTrimRatioRelaxDelta(XmlUtils.getDoubleFromText(trimRatioRelaxElm,
                                                              DEFAULT_TRIM_RATIO_RELAX_DELTA));
        }

        Element patternUsageRelaxElm = rootElm.getChild(XmlTags.PATTERN_USAGE_RELAX_DELTA);
        if (patternUsageRelaxElm != null) {
            setPatternUsageRelaxDelta(XmlUtils.getIntegerFromText(patternUsageRelaxElm,
                                                                  DEFAULT_PATTERN_USAGE_RELAX_DELTA));
        }
    }

    private interface XmlTags {
        String PARAMETERS = "sequential-procedure";
        String TYPE = "type";
        String TRIM_RATIO_RELAX_DELTA = "trim-ratio-relax-step";
        String PATTERN_USAGE_RELAX_DELTA = "pattern-usage-relax-step";
    }

}
