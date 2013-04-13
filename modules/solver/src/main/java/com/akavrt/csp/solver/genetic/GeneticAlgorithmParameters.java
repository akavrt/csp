package com.akavrt.csp.solver.genetic;

import com.akavrt.csp.utils.BaseParameters;
import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlUtils;
import org.jdom2.Element;

/**
 * <p>Base set of parameters used by genetic algorithm.</p>
 *
 * <p>An instance of this class can be saved to XML and extracted from it using methods defined in
 * XmlCompatible interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class GeneticAlgorithmParameters extends BaseParameters {
    private static final int DEFAULT_POPULATION_SIZE = 30;
    private static final int DEFAULT_EXCHANGE_SIZE = 20;
    private static final int DEFAULT_RUN_STEPS = 1000;
    private static final double DEFAULT_CROSSOVER_RATE = 0.5;
    private int populationSize = DEFAULT_POPULATION_SIZE;
    private int exchangeSize = DEFAULT_EXCHANGE_SIZE;
    private int runSteps = DEFAULT_RUN_STEPS;
    private double crossoverRate = DEFAULT_CROSSOVER_RATE;

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getExchangeSize() {
        return exchangeSize;
    }

    public void setExchangeSize(int exchangeSize) {
        this.exchangeSize = exchangeSize;
    }

    public int getRunSteps() {
        return runSteps;
    }

    public void setRunSteps(int runSteps) {
        this.runSteps = runSteps;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element save() {
        Element paramsElm = new Element(XmlTags.GENETIC);

        // optional description
        if (!Utils.isEmpty(getDescription())) {
            Element descriptionElm = new Element(XmlTags.DESCRIPTION);
            descriptionElm.setText(getDescription());
            paramsElm.addContent(descriptionElm);
        }

        Element populationSizeElm = new Element(XmlTags.POPULATION_SIZE);
        populationSizeElm.setText(Integer.toString(getPopulationSize()));
        paramsElm.addContent(populationSizeElm);

        Element exchangeSizeElm = new Element(XmlTags.EXCHANGE_SIZE);
        exchangeSizeElm.setText(Integer.toString(getExchangeSize()));
        paramsElm.addContent(exchangeSizeElm);

        Element runStepsElm = new Element(XmlTags.RUN_STEPS);
        runStepsElm.setText(Integer.toString(getRunSteps()));
        paramsElm.addContent(runStepsElm);

        Element crossoverRateElm = new Element(XmlTags.CROSSOVER_RATE);
        crossoverRateElm.setText(XmlUtils.formatDouble(getCrossoverRate()));
        paramsElm.addContent(crossoverRateElm);

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

        int populationSize = XmlUtils.getIntegerFromText(rootElm, XmlTags.POPULATION_SIZE,
                                                         DEFAULT_POPULATION_SIZE);
        setPopulationSize(populationSize);

        int exchangeSize = XmlUtils.getIntegerFromText(rootElm, XmlTags.EXCHANGE_SIZE,
                                                       DEFAULT_EXCHANGE_SIZE);
        setExchangeSize(exchangeSize);

        int runSteps = XmlUtils.getIntegerFromText(rootElm, XmlTags.RUN_STEPS, DEFAULT_RUN_STEPS);
        setRunSteps(runSteps);

        double crossoverRate = XmlUtils.getDoubleFromText(rootElm, XmlTags.CROSSOVER_RATE,
                                                          DEFAULT_CROSSOVER_RATE);
        setCrossoverRate(crossoverRate);
    }

    private interface XmlTags {
        String GENETIC = "genetic";
        String POPULATION_SIZE = "population-size";
        String EXCHANGE_SIZE = "exchange-size";
        String RUN_STEPS = "generations";
        String CROSSOVER_RATE = "crossover-rate";
        String DESCRIPTION = "description";
    }

}
