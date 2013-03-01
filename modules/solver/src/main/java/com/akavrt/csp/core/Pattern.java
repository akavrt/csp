package com.akavrt.csp.core;

import java.util.Arrays;
import java.util.List;

/**
 * <p>A cutting pattern is modeled as a simple array of integer multiplies. Number of of elements
 * in this array equals to the number of orders in problem under consideration. Value of the
 * specific multiplier means how many times corresponding order is being cut from the roll when
 * this pattern is being applied to it.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Pattern {
    // TODO now this class is almost useless but in future we will use it mostly as a helper
    // filled with a plenty of utility methods
    private int[] multipliers;
    private Roll roll;

    /**
     * <p>Creates an empty pattern filled with zeros.</p>
     *
     * @param ordersQuantity The number of orders defined within problem.
     */
    public Pattern(int ordersQuantity) {
        multipliers = new int[ordersQuantity];
        Arrays.fill(multipliers, 0);
    }

    /**
     * <p>Creates pattern with multipliers provided as a parameter.</p>
     *
     * @param multipliers The array of integer multipliers.
     */
    public Pattern(int[] multipliers) {
        this.multipliers = multipliers;
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

    /**
     * <p>Pattern will be applied to this roll when cutting plan is executed.</p>
     *
     * @return The roll attached to pattern or null.
     */
    public Roll getRoll() {
        return roll;
    }

    /**
     * <p>Attach one of the stock rolls to the pattern.</p>
     *
     * @param roll The roll being attached to the pattern.
     */
    public void setRoll(Roll roll) {
        this.roll = roll;
    }

    /**
     * <p>Total width of the roll being used to cut orders when pattern is applied to it.
     * Measured in abstract units.</p>
     *
     * @param orders The orders needed to evaluate pattern width.
     * @return The total width of the orders being cut from the roll according to the pattern.
     */
    public double getWidth(List<Order> orders) {
        // TODO add check whether size of the orders is equal to the length of multiplier's array
        // or not
        double width = 0;

        for (int i = 0; i < orders.size(); i++) {
            width += orders.get(i).getWidth() * multipliers[i];
        }

        return width;
    }

    /**
     * <p>Wasted width of the roll which will be left unused after cutting. Measured in abstract
     * units.</p>
     *
     * @param orders The orders needed to evaluate pattern width.
     * @return The unused width of the roll.
     */
    public double getTrim(List<Order> orders) {
        if (roll == null) {
            return 0;
        }

        return roll.getWidth() - getWidth(orders);
    }

    /**
     * <p>Wasted area of the roll which will be left unused after cutting.
     * Measured in abstract square units.</p>
     *
     * @param orders The orders needed to evaluate pattern width.
     * @return The unused area of the roll.
     */
    public double getTrimArea(List<Order> orders) {
        if (roll == null) {
            return 0;
        }

        return (roll.getWidth() - getWidth(orders)) * roll.getLength();
    }

    private int getTotalCutsNumber() {
        int totalCuts = 0;

        for (int i = 0; i < multipliers.length; i++) {
            totalCuts += multipliers[i];
        }

        return totalCuts;
    }

    /**
     * <p>Check whether cutting pattern is valid or not. Invalid patterns can't be cut from rolls
     * due to exceeded width or technical limitation.</p>
     *
     * @param orders            The orders needed to evaluate pattern width.
     * @param allowedCutsNumber The maximum number of cuts allowed within pattern.
     * @return true if pattern is valid, false otherwise.
     */
    public boolean isValid(List<Order> orders, int allowedCutsNumber) {
        return getTotalCutsNumber() <= allowedCutsNumber &&
                (roll == null || getWidth(orders) <= roll.getWidth());
    }

    /**
     * <p>Check whether cutting pattern is active or not. Inactive patterns will produce nothing
     * after execution of the cutting plan.</p>
     *
     * @return true if pattern is active, false otherwise.
     */
    public boolean isActive() {
        return roll != null && getTotalCutsNumber() > 0;
    }

    /**
     * <p>Utility method which is used to discover repeating patterns in cutting plan.</p>
     *
     * @return The content-based hash code for array of multipliers.
     */
    public int getMultipliersHashCode() {
        return Arrays.hashCode(multipliers);
    }

}
