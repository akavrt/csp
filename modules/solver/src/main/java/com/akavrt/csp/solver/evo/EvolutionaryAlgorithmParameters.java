package com.akavrt.csp.solver.evo;

import com.akavrt.csp.utils.BaseParameters;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;

/**
 * User: akavrt
 * Date: 25.04.13
 * Time: 14:43
 */
public class EvolutionaryAlgorithmParameters extends BaseParameters {
    private static final int DEFAULT_POPULATION_SIZE = 30;
    private static final int DEFAULT_RUN_STEPS = 1000;
    private int populationSize = DEFAULT_POPULATION_SIZE;
    private int runSteps = DEFAULT_RUN_STEPS;

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public void setRunSteps(int runSteps) {
        this.runSteps = runSteps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = new Element(getRootElementName());

        // optional description
        if (!Utils.isEmpty(getDescription())) {
            Element descriptionElm = new Element(XmlTags.DESCRIPTION);
            descriptionElm.setText(getDescription());
            paramsElm.addContent(descriptionElm);
        }

        Element populationSizeElm = new Element(getPopulationSizeElementName());
        populationSizeElm.setText(Integer.toString(getPopulationSize()));
        paramsElm.addContent(populationSizeElm);

        Element runStepsElm = new Element(XmlTags.RUN_STEPS);
        runStepsElm.setText(Integer.toString(getRunSteps()));
        paramsElm.addContent(runStepsElm);

        return paramsElm;
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

        int populationSize = XmlUtils.getIntegerFromText(rootElm, getPopulationSizeElementName(),
                                                         DEFAULT_POPULATION_SIZE);
        setPopulationSize(populationSize);

        int runSteps = XmlUtils.getIntegerFromText(rootElm, XmlTags.RUN_STEPS, DEFAULT_RUN_STEPS);
        setRunSteps(runSteps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRootElementName() {
        return XmlTags.EVOLUTIONARY;
    }

    protected String getPopulationSizeElementName() {
        return XmlTags.POPULATION_SIZE;
    }

    private interface XmlTags {
        String EVOLUTIONARY = "evolutionary";
        String POPULATION_SIZE = "population-size";
        String RUN_STEPS = "generations";
        String DESCRIPTION = "description";
    }

}
