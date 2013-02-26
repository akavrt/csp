package com.akavrt.csp.core;

/**
 * <p>This class represents a single unit of stock material.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Roll extends BaseStrip {
    private static final String FORMAT_TEMPLATE = "Roll '%s':\n    W = %.2f\n    L = %.2f";

    // edge trim
    private double startTrimLength;
    private double endTrimLength;
    // side trim
    private double leftTrimWidth;
    private double rightTrimWidth;

    /**
     * <p>Length of the start edge of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return the unusable length of the start edge
     */
    public double getStartTrimLength() {
        return startTrimLength;
    }

    /**
     * <p>Set unusable length of the start edge of the roll.</p>
     *
     * @param trimLength The unusable length of the start edge
     */
    public void setStartTrimLength(double trimLength) {
        this.startTrimLength = trimLength;
    }

    /**
     * <p>Length of the end edge of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return the unusable length of the end edge
     */
    public double getEndTrimLength() {
        return endTrimLength;
    }

    /**
     * <p>Set unusable length of the end edge of the roll.</p>
     *
     * @param trimLength The unusable length of the end edge
     */
    public void setEndTrimLength(double trimLength) {
        this.endTrimLength = trimLength;
    }

    /**
     * <p>Width of the left side of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return the unusable width of the left side
     */
    public double getLeftTrimWidth() {
        return leftTrimWidth;
    }

    /**
     * <p>Set unusable width of the left side of the roll.</p>
     *
     * @param trimWidth The unusable width of the left side
     */
    public void setLeftTrimWidth(double trimWidth) {
        this.leftTrimWidth = trimWidth;
    }

    /**
     * <p>Width of the right side of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return the unusable width of the right side
     */
    public double getRightTrimWidth() {
        return rightTrimWidth;
    }

    /**
     * <p>Set unusable width of the right side of the roll.</p>
     *
     * @param trimWidth The unusable width of the left side
     */
    public void setRightTrimWidth(double trimWidth) {
        this.rightTrimWidth = trimWidth;
    }

    /**
     * <p>Usable length of the roll with no defects in material along it.
     * Measured in abstract units.</p>
     *
     * <p>Algorithm treats it as the real length of the roll during the optimization phase .</p>
     *
     * @return the usable length of the roll
     */
    public double getUsableLength() {
        return getLength() - getStartTrimLength() - getEndTrimLength();
    }

    /**
     * <p>Usable width of the roll with no defects in material across it.
     * Measured in abstract units.</p>
     *
     * <p>Algorithm treats it as the real width of the roll during the optimization phase.</p>
     *
     * @return the usable width of the roll
     */
    public double getUsableWidth() {
        return getWidth() - getLeftTrimWidth() - getRightTrimWidth();
    }

    /**
     * {@inheritDoc}
     *
     * @return String representation of the roll with custom formatting
     */
    @Override
    public String toString() {
        return String.format(FORMAT_TEMPLATE, getId(), getUsableWidth(), getUsableLength());
    }
}
