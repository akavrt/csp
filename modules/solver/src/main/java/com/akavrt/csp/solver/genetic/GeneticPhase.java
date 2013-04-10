package com.akavrt.csp.solver.genetic;


/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public enum GeneticPhase {
    INITIALIZATION("initialization", "Initializing"),
    GENERATION("generation", "Solving");
    private final String name;
    private final String description;

    GeneticPhase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
