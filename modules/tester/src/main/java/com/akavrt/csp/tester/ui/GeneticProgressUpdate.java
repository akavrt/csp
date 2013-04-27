package com.akavrt.csp.tester.ui;

import com.akavrt.csp.solver.evo.EvolutionPhase;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 18:10
 */
public class GeneticProgressUpdate {
    public final int progress;
    public final EvolutionPhase phase;
    public final SeriesData seriesData;

    public GeneticProgressUpdate(int progress, EvolutionPhase phase) {
        this(progress, phase, null);
    }

    public GeneticProgressUpdate(int progress, EvolutionPhase phase, SeriesData seriesData) {
        this.progress = progress;
        this.phase = phase;
        this.seriesData = seriesData;
    }
}
