package com.akavrt.csp.solver.sequential;

import com.akavrt.csp.core.xml.XmlUtils;
import org.jdom2.Element;

/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class VahrenkampProcedureParameters extends HaesslerProcedureParameters {
    private static final double DEFAULT_GOALMIX = 0.5;
    private double goalmix = DEFAULT_GOALMIX;

    public double getGoalmix() {
        return goalmix;
    }

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

    @Override
    protected String getProcedureType() {
        return VahrenkampProcedure.METHOD_NAME;
    }

    private interface XmlTags {
        String GOALMIX = "goalmix";
    }

}
