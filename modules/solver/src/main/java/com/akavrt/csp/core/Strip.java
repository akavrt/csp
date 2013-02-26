package com.akavrt.csp.core;

/**
 * <p>In roll trimming both stock material and orders are modeled as rectangular strips of known
 * size. Algorithm takes into account only width and length, material thickness can be ignored on
 * this level of detalisation.</p>
 *
 * <p>Classes which will be developed to represent rolls and orders must implement this
 * interface.<p/>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public interface Strip {
    /**
     * <p>Integer identifier is used to differentiate strips from each other (for example, we can
     * have several rolls with the same size). Uniqueness, if needed, should be managed by the
     * client code.</p>
     *
     * @return the int value used as identifier
     */
    int getId();

    /**
     * <p>Width of the strip measured in abstract units.</p>
     *
     * @return the width of the strip
     */
    double getWidth();

    /**
     * <p>Length of the strip measured in abstract units.</p>
     *
     * @return the length of the strip
     */
    double getLength();
}
