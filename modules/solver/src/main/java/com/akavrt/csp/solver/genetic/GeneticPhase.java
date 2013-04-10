package com.akavrt.csp.solver.genetic;

/**
 * User: akavrt
 * Date: 10.04.13
 * Time: 17:40
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
