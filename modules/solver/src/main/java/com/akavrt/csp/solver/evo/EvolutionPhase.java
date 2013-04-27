package com.akavrt.csp.solver.evo;


/**
 * <p></p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public enum EvolutionPhase {
    INITIALIZATION("initialization", "Initializing"),
    GENERATION("generation", "Solving");
    private final String name;
    private final String description;

    EvolutionPhase(String name, String description) {
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
