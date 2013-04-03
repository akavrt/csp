package com.akavrt.csp.utils;

/**
 * <p>Utility class which should be used as base class for classes representing parameter sets with
 * description defined as a simple textual field.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class BaseParameters implements ParameterSet {
    private String description;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

}
