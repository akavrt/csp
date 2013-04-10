package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.genetic.GeneticPhase;
import com.akavrt.csp.solver.genetic.Population;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 18:10
 */
public class GeneticProgressUpdate {
    public final int progress;
    public final GeneticPhase phase;
    public final SeriesData seriesData;

    public GeneticProgressUpdate(int progress, GeneticPhase phase) {
        this(progress, phase, null);
    }

    public GeneticProgressUpdate(int progress, GeneticPhase phase, Population population) {
        this.progress = progress;
        this.phase = phase;

        this.seriesData = population == null ? null : new SeriesData(population);
    }
}
