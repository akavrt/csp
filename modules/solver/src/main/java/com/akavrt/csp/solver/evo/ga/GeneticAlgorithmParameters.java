package com.akavrt.csp.solver.evo.ga;

import com.akavrt.csp.solver.evo.EvolutionaryAlgorithmParameters;
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
public class GeneticAlgorithmParameters extends EvolutionaryAlgorithmParameters {
    private static final int DEFAULT_EXCHANGE_SIZE = 20;
    private static final double DEFAULT_CROSSOVER_RATE = 0.5;
    private int exchangeSize = DEFAULT_EXCHANGE_SIZE;
    private double crossoverRate = DEFAULT_CROSSOVER_RATE;

    public int getExchangeSize() {
        return exchangeSize;
    }

    public void setExchangeSize(int exchangeSize) {
        this.exchangeSize = exchangeSize;
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
        Element paramsElm = super.save();

        Element exchangeSizeElm = new Element(XmlTags.EXCHANGE_SIZE);
        exchangeSizeElm.setText(Integer.toString(getExchangeSize()));
        paramsElm.addContent(exchangeSizeElm);

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
        super.load(rootElm);

        int exchangeSize = XmlUtils.getIntegerFromText(rootElm, XmlTags.EXCHANGE_SIZE,
                                                       DEFAULT_EXCHANGE_SIZE);
        setExchangeSize(exchangeSize);

        double crossoverRate = XmlUtils.getDoubleFromText(rootElm, XmlTags.CROSSOVER_RATE,
                                                          DEFAULT_CROSSOVER_RATE);
        setCrossoverRate(crossoverRate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getRootElementName() {
        return XmlTags.GENETIC;
    }

    private interface XmlTags {
        String GENETIC = "genetic";
        String EXCHANGE_SIZE = "exchange-size";
        String CROSSOVER_RATE = "crossover-rate";
    }

}
