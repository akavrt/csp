package com.akavrt.csp.solver.genetic;

/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface GeneticProgressChangeListener {
    void onInitializationProgressChanged(int progress);
    void onGenerationProgressChanged(int progress, Population population);
}
