package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.evo.Chromosome;
import com.akavrt.csp.solver.evo.Population;

/**
 * User: akavrt
 * Date: 11.04.13
 * Time: 00:35
 */
public class SeriesData {
    public int age;
    // trim loss
    public double tradeoffSideTrimRatio;
    public double tradeoffTotalTrimRatio;
    // pattern reduction
    public double partialBestPatternsRatio;
    public double tradeoffUniquePatternsCount;
    public double tradeoffTotalPatternsCount;
    // product deviation
    public double tradeoffProductionRatio;
    public double tradeoffMaxUnderProductionRatio;
    public double tradeoffMaxOverProductionRatio;
    // objectives
    public double averageObjectiveRatio;
    public double tradeoffObjectiveRatio;
    public double tradeoffComparativeRatio;

    public SeriesData(Population population, SeriesMetricProvider provider) {
        process(population, provider);
    }

    private void process(Population population, SeriesMetricProvider provider) {
        if (population == null || population.getChromosomes().size() == 0) {
            return;
        }

        age = population.getAge();

        Chromosome best = null;
        Chromosome bestPatterns = null;
        for (Chromosome chromosome : population.getChromosomes()) {
            if (bestPatterns == null
                    || provider.getPatternsMetric().compare(chromosome, bestPatterns) > 0) {
                bestPatterns = chromosome;
            }

            averageObjectiveRatio += provider.getObjectiveMetric().evaluate(chromosome);
            if (best == null || provider.getObjectiveMetric().compare(chromosome, best) > 0) {
                best = chromosome;
            }
        }


        tradeoffSideTrimRatio = provider.getSideTrimMetric().evaluate(best);
        tradeoffTotalTrimRatio = provider.getTotalTrimMetric().evaluate(best);

        partialBestPatternsRatio = provider.getPatternsMetric().evaluate(bestPatterns);
        tradeoffUniquePatternsCount = best.getMetricProvider().getUniquePatternsCount();
        tradeoffTotalPatternsCount = best.getMetricProvider().getActivePatternsCount();

        tradeoffProductionRatio = provider.getProductMetric().evaluate(best);
        tradeoffMaxUnderProductionRatio = best.getMetricProvider()
                                                   .getMaximumUnderProductionRatio();
        tradeoffMaxOverProductionRatio = best.getMetricProvider()
                                                  .getMaximumOverProductionRatio();

        averageObjectiveRatio /= population.getChromosomes().size();
        tradeoffObjectiveRatio = provider.getObjectiveMetric().evaluate(best);
        tradeoffComparativeRatio = provider.getComparativeMetric().evaluate(best);
    }

}
