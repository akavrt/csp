package com.akavrt.csp.core;

/**
 * <p>Base implementation for {@link Strip} interface.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class BaseStrip implements Strip {

    private int id;
    private double width;
    private double length;

    /**
     * <p>Strip params should be set only when an instance is created.</p>
     *
     * @param id The identifier for the strip.
     * @param length The width of the strip, in abstract units.
     * @param width The width of the strip, in abstract units.
     */
    public BaseStrip(int id, double length, double width) {
        this.id = id;
        this.length = length;
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLength() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWidth() {
        return width;
    }
}
