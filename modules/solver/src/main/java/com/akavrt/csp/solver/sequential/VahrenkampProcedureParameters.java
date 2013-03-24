package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;

/**
 * <p>Parameters of the modified sequential heuristic procedure proposed by Vahrenkamp.</p>
 *
 * <p>Two goals (minimization of trim loss and minimization of the total number of unique
 * patterns used in cutting plan) are mixed by a random selection with a given probability of
 * one goal.</p>
 *
 * <p>Trim loss aspiration level is relaxed with a probability defined by a value of the goalmix
 * parameter. Accordingly, to prevent endless looping, the second aspiration level - restriction on
 * minimal pattern usage - is reduced with probability (1 - goalmix).</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class VahrenkampProcedureParameters extends SequentialProcedureParameters {
    private static final double DEFAULT_GOALMIX = 0.5;
    private double goalmix = DEFAULT_GOALMIX;

    /**
     * <p>Goalmix defines relative priority between trim loss and pattern reduction by setting a
     * probability that trim loss aspiration level will be reduced if no suitable pattern was
     * found on the current step of the SHP.</p>
     */
    public double getGoalmix() {
        return goalmix;
    }

    /**
     * <p>Set relative priority between trim loss and pattern reduction, see getGoalmix() for more
     * details.</p>
     */
    public void setGoalmix(double goalmix) {
        this.goalmix = goalmix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = super.save();

        Element goalmixElm = new Element(XmlTags.GOALMIX);
        goalmixElm.setText(XmlUtils.formatDouble(getGoalmix()));
        paramsElm.addContent(goalmixElm);

        return paramsElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        super.load(rootElm);

        Element goalmixElm = rootElm.getChild(XmlTags.GOALMIX);
        if (goalmixElm != null) {
            setGoalmix(XmlUtils.getDoubleFromText(goalmixElm, DEFAULT_GOALMIX));
        }
    }

    private interface XmlTags {
        String GOALMIX = "goalmix";
    }

}
