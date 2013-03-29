package com.akavrt.csp.solver.genetic;

/**
 * User: akavrt
 * Date: 27.03.13
 * Time: 16:12
 */
public class GeneticAlgorithmParameters {
    private int populationSize;
    private int exchangeSize;
    private int runSteps;
    private double crossoverRate;

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

}
