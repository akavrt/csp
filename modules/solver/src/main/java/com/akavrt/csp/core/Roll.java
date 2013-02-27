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
     * {@inheritDoc}
     */
    public Roll(int id, double length, double width) {
        super(id, length, width);
    }

    /**
     * <p>Length of the start edge of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return The unusable length of the start edge.
     */
    public double getStartTrimLength() {
        return startTrimLength;
    }

    /**
     * <p>Length of the end edge of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return The unusable length of the end edge.
     */
    public double getEndTrimLength() {
        return endTrimLength;
    }

    /**
     * <p>Width of the left side of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return The unusable width of the left side.
     */
    public double getLeftTrimWidth() {
        return leftTrimWidth;
    }

    /**
     * <p>Width of the right side of the roll that can be used for production due to defects in
     * material. Measured in abstract units.</p>
     *
     * @return The unusable width of the right side.
     */
    public double getRightTrimWidth() {
        return rightTrimWidth;
    }

    /**
     * <p>Usable length of the roll with no defects in material along it.
     * Measured in abstract units.</p>
     *
     * <p>Algorithm treats it as the real length of the roll during the optimization phase .</p>
     *
     * @return The usable length of the roll.
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
     * @return The usable width of the roll.
     */
    public double getUsableWidth() {
        return getWidth() - getLeftTrimWidth() - getRightTrimWidth();
    }

    /**
     * <p>Usable area of the roll with no defects in material both across and along it.
     * Measured in abstract square units.</p>
     *
     * @return The usable area of the roll.
     */
    public double getUsableArea() {
        return getUsableLength() * getUsableWidth();
    }

    /**
     * {@inheritDoc}
     *
     * @return String representation of the roll with custom formatting.
     */
    @Override
    public String toString() {
        return String.format(FORMAT_TEMPLATE, getId(), getUsableWidth(), getUsableLength());
    }

    public static class Builder {
        private int id;
        private double length;
        private double width;
        // edge trim
        private double startTrimLength;
        private double endTrimLength;
        // side trim
        private double leftTrimWidth;
        private double rightTrimWidth;

        /**
         * <p>Set identifier for the roll.</p>
         *
         * @param id The identifier for the roll.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        /**
         * <p>Set length for the roll.</p>
         *
         * @param length The length of the roll, in abstract units.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setLength(double length) {
            this.length = length;
            return this;
        }

        /**
         * <p>Set unusable length of the start edge of the roll.</p>
         *
         * @param trimLength The unusable length of the start edge.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setStartTrimLength(double trimLength) {
            this.startTrimLength = trimLength;
            return this;
        }

        /**
         * <p>Set width for the roll.</p>
         *
         * @param width The width of the roll, in abstract units.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setWidth(double width) {
            this.width = width;
            return this;
        }

        /**
         * <p>Set unusable length of the end edge of the roll.</p>
         *
         * @param trimLength The unusable length of the end edge.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setEndTrimLength(double trimLength) {
            this.endTrimLength = trimLength;
            return this;
        }

        /**
         * <p>Set unusable width of the left side of the roll.</p>
         *
         * @param trimWidth The unusable width of the left side.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setLeftTrimWidth(double trimWidth) {
            this.leftTrimWidth = trimWidth;
            return this;
        }

        /**
         * <p>Set unusable width of the right side of the roll.</p>
         *
         * @param trimWidth The unusable width of the left side.
         *
         * @return This Builder object to allow for chaining of calls to set methods.
         */
        public Builder setRightTrimWidth(double trimWidth) {
            this.rightTrimWidth = trimWidth;
            return this;
        }

        /**
         * <p>Creates a Roll with the params supplied to this builder.<p/>
         */
        public Roll build() {
            Roll roll = new Roll(id, length, width);
            roll.startTrimLength = startTrimLength;
            roll.endTrimLength = endTrimLength;
            roll.leftTrimWidth = leftTrimWidth;
            roll.rightTrimWidth = rightTrimWidth;

            return roll;
        }

    }
}
