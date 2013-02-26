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
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * <p>Set identifier for strip.</p>
     *
     * @param id The identifier for the strip.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * <p>Set width for strip.</p>
     *
     * @param width The width of the strip, in abstract units
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLength() {
        return length;
    }

    /**
     * <p>Set length for strip.</p>
     *
     * @param length The length of the strip, in abstract units
     */
    public void setLength(double length) {
        this.length = length;
    }

}
