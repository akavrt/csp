package com.akavrt.csp.core;

import java.util.Arrays;

/**
 * <p>A cutting pattern is modeled as a simple array of integer multiplies. Number of of elements
 * in this array equals to the number of orders in problem under consideration. Value of the
 * specific multiplier means how many times corresponding order is being cut from the roll when
 * this pattern is being applied to it.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Pattern {
    // TODO now this class is almost useless but in future we will use it mostly as a helper filled with a plenty of utility methods
    private int[] multipliers;

    /**
     * <p>Creates an empty pattern willed with zeros.</p>
     *
     * @param ordersQuanity The number of orders defined within problem.
     */
    public Pattern(int ordersQuanity) {
        multipliers = new int[ordersQuanity];
        Arrays.fill(multipliers, 0);
    }

    /**
     * <p>Returns array of integer multipliers.</p>
     */
    public int[] getMultipliers() {
        return multipliers;
    }

    /**
     * <p>Set array of multipliers for pattern.</p>
     *
     * @param multipliers The array of integer multipliers.
     */
    public void setMultipliers(int[] multipliers) {
        this.multipliers = multipliers;
    }

}
