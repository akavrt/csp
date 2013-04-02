package com.akavrt.csp.utils;

/**
 * User: akavrt
 * Date: 02.04.13
 * Time: 20:34
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
