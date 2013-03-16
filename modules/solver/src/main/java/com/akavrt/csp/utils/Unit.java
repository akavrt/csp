package com.akavrt.csp.utils;

/**
 * <p>Units of length. Values of this enum type is used in metadata.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public enum Unit {
    METER("meter", "m", 1),
    MILLIMETER("millimeter", "mm", 0.001),
    CENTIMETER("centimeter", "cm", 0.01),
    INCH("inch", "in", 0.0254);
    private final String name;
    private final String symbol;
    private final double multiplier;

    Unit(String name, String symbol, double multiplier) {
        this.name = name;
        this.symbol = symbol;
        this.multiplier = multiplier;
    }

    /**
     * <p>The standard name of the unit.</p>
     */
    public String getName() {
        return name;
    }

    /**
     * <p>The standard symbol or abbreviation used for the unit.</p>
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * <p>This multiplier is used to convert linear dimension measured in specific units to
     * meters.</p>
     *
     * @return Linear dimension which is measured in specific units should be multiplied by this
     *         factor to convert it into meters.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * <p>Convert linear dimension measured in specific units to meters.</p>
     *
     * @param value Linear dimension to convert.
     * @return Linear dimension converted into meters.
     */
    public double toMeters(double value) {
        return value * getMultiplier();
    }
}
