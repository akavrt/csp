package com.akavrt.csp.core;

/**
 * <p>In roll trimming both stock material and orders are modeled as rectangular strips of known
 * size. Algorithm takes into account only width and length, material thickness can be ignored on
 * this level of specification.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public abstract class Strip {

    private String id;
    private double width;
    private double length;

    /**
     * <p>Strip params should be set only when an instance of class is created.</p>
     *
     * @param id     The identifier for the strip.
     * @param length The width of the strip, in abstract units.
     * @param width  The width of the strip, in abstract units.
     */
    public Strip(String id, double length, double width) {
        this.id = id;
        this.length = length;
        this.width = width;
    }

    /**
     * <p>User-defined string identifier is used to differentiate strips from each other.
     * Uniqueness, if needed, should be insured inside the client code.</p>
     *
     * @return The user defined identifier of the strip.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Length of the strip measured in abstract units.</p>
     *
     * @return The length of the strip.
     */
    public double getLength() {
        return length;
    }

    /**
     * <p>Width of the strip measured in abstract units.</p>
     *
     * @return The width of the strip.
     */
    public double getWidth() {
        return width;
    }

    /**
     * <p>There is no sense in using orders or rolls without usable length or width. If strip with
     * such characteristics is encountered, it should be ignored or deleted at the early stages of
     * the optimization routine.</p>
     *
     * @return true if strip is valid, false otherwise.
     */
    public boolean isValid() {
        return getLength() > 0 && getWidth() > 0;
    }


}
