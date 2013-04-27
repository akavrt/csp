package com.akavrt.csp.solver.evo;

/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface EvolutionProgressChangeListener {
    void onInitializationProgressChanged(int progress);
    void onGenerationProgressChanged(int progress, Population population);
}
