package com.akavrt.csp.solver.evo.es;

import com.akavrt.csp.solver.evo.EvolutionaryAlgorithmParameters;
import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 25.04.13
 * Time: 15:21
 */
public class EvolutionStrategyParameters extends EvolutionaryAlgorithmParameters {
    private static final int DEFAULT_OFFSPRING_COUNT = 20;
    private static final int DEFAULT_TOUR_SIZE = 2;
    private int offspringCount = DEFAULT_OFFSPRING_COUNT;
    private int tourSize = DEFAULT_TOUR_SIZE;

    public int getOffspringCount() {
        return offspringCount;
    }

    public void setOffspringCount(int offspringCount) {
        this.offspringCount = offspringCount;
    }

    public int getTourSize() {
        return tourSize;
    }

    public void setTourSize(int tourSize) {
        this.tourSize = tourSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = super.save();

        Element offspringCountElm = new Element(XmlTags.OFFSPRING_COUNT);
        offspringCountElm.setText(Integer.toString(getOffspringCount()));
        paramsElm.addContent(offspringCountElm);

        Element tourSizeElm = new Element(XmlTags.TOUR_SIZE);
        tourSizeElm.setText(XmlUtils.formatDouble(getTourSize()));
        paramsElm.addContent(tourSizeElm);

        return paramsElm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(Element rootElm) {
        super.load(rootElm);

        int offspringCount = XmlUtils.getIntegerFromText(rootElm, XmlTags.OFFSPRING_COUNT,
                                                         DEFAULT_OFFSPRING_COUNT);
        setOffspringCount(offspringCount);

        int tourSize = XmlUtils.getIntegerFromText(rootElm, XmlTags.TOUR_SIZE, DEFAULT_TOUR_SIZE);
        setTourSize(tourSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRootElementName() {
        return XmlTags.STRATEGY;
    }

    protected String getPopulationSizeElementName() {
        return XmlTags.POPULATION_SIZE;
    }

    private interface XmlTags {
        String STRATEGY = "strategy";
        String POPULATION_SIZE = "mu";
        String OFFSPRING_COUNT = "lambda";
        String TOUR_SIZE = "tour-size";
    }

}
