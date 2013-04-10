package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.genetic.Population;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 00:35
 */
public class SeriesData {
    public int age;
    // trim loss
    public double trimBest;
    public double trimAverage;
    public double trimTradeoff;
    // pattern reduction
    public double patternsBest;
    public double patternsAverage;
    public double patternsTradeoffUnique;
    public double patternsTradeoffTotal;
    // product deviation
    public double productionBest;
    public double productionAverage;
    public double productionTradeoff;
    public double productionTradeoffMaxOverProd;
    public double productionTradeoffMaxUnderProd;
    // scalar
    public double scalarBest;
    public double scalarAverage;

    public SeriesData(Population population) {
        process(population);
    }

    private void process(Population population) {
        if (population == null || population.getChromosomes().size() == 0) {
            return;
        }

        age = population.getAge();

        /*
        int i = 0;
        for (Chromosome chromosome : chromosomes) {
            if (i == 0 || chromosome.getMetricProvider().getTrimRatio() < trimBest) {
                trimBest = chromosome.getMetricProvider().getTrimRatio();
            }

            if (i == 0 || chromosome.getMetricProvider().getPaRatio() < trimBest) {
                trimBest = chromosome.getMetricProvider().getTrimRatio();
            }

            if (i == 0 || chromosome.getMetricProvider().getTrimRatio() < trimBest) {
                trimBest = chromosome.getMetricProvider().getTrimRatio();
            }
        }
        */
    }
}
