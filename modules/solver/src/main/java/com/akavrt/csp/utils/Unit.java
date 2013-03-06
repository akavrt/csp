package com.akavrt.csp.utils;

/**
 * <p>Unit of length. Values of this enum type is used in metadata.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public enum Unit {
    METER("meter", "m"),
    MILLIMETER("millimeter", "mm");

    private final String name;
    private final String symbol;

    Unit (String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

}
