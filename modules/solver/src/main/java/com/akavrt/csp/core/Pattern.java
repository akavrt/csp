package com.akavrt.csp.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * <p>A cutting pattern is modeled as a simple array of integer multiplies. Number of of elements
 * in this array equals to the number of orders in problem under consideration. Value of the
 * specific multiplier means how many times corresponding order is being cut from the roll when
 * this pattern is being applied to it.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Pattern {
    private Map<Integer, MultiCut> cuts;
    private Roll roll;

    /**
     * <p>Creates an empty pattern.</p>
     */
    public Pattern(List<Order> orders) {
        // list of orders can be immutable:
        // let's create a copy and sort orders in ascending order of width
        List<Order> sorted = Lists.newArrayList(orders);
        Collections.sort(sorted, new Comparator<Order>() {
            @Override
            public int compare(Order lhs, Order rhs) {
                return lhs.getWidth() < rhs.getWidth() ? -1 :
                        (lhs.getWidth() > rhs.getWidth() ? 1 : 0);
            }
        });

        cuts = Maps.newLinkedHashMap();
        for (Order order : sorted) {
            cuts.put(order.getInternalId(), new MultiCut(order));
        }
    }

    private boolean updateCut(Order order, int quantity, boolean isAddition) {
        if (order == null) {
            return false;
        }

        // orders with unknown id should be ignored
        MultiCut cut = cuts.get(order.getInternalId());
        if (cut != null) {
            if (isAddition) {
                cut.addQuantity(quantity);
            } else {
                cut.setQuantity(quantity);
            }
        }

        return cut != null;
    }

    public boolean addCut(Order order, int quantity) {
        return updateCut(order, quantity, true);
    }

    public boolean setCut(Order order, int quantity) {
        return updateCut(order, quantity, false);
    }

    public List<MultiCut> getCuts() {
        return Lists.newArrayList(cuts.values());
    }

    public void setCuts(List<MultiCut> cuts) {
        for (MultiCut cut : cuts) {
            if (cut != null) {
                setCut(cut.getOrder(), cut.getQuantity());
            }
        }
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
     * @return The total width of the orders being cut from the roll according to the pattern.
     */
    public double getWidth() {
        double width = 0;

        for (MultiCut cut : cuts.values()) {
            width += cut.getWidth();
        }

        return width;
    }

    /**
     * <p>Wasted width of the roll which will be left unused after cutting. Measured in abstract
     * units.</p>
     *
     * @return The unused width of the roll.
     */
    public double getTrim() {
        if (roll == null) {
            return 0;
        }

        return roll.getWidth() - getWidth();
    }

    /**
     * <p>Wasted area of the roll which will be left unused after cutting.
     * Measured in abstract square units.</p>
     *
     * @return The unused area of the roll.
     */
    public double getTrimArea() {
        if (roll == null) {
            return 0;
        }

        return (roll.getWidth() - getWidth()) * roll.getLength();
    }

    /**
     * <p>Calculate the length of the finished product we will get for specific order after
     * executing pattern. Measured in abstract units.</p>
     *
     * @param order The order in question.
     * @return The length of produced strip.
     */
    public double getProductionLengthForOrder(Order order) {
        double productionLength = 0;

        // orders with unknown id should be ignored
        MultiCut cut = cuts.get(order.getInternalId());
        if (cut != null && roll != null) {
            productionLength = roll.getLength() * cut.getQuantity();
        }

        return productionLength;
    }

    private int getTotalCutsNumber() {
        int totalCuts = 0;

        for (MultiCut cut : cuts.values()) {
            totalCuts += cut.getQuantity();
        }

        return totalCuts;
    }

    /**
     * <p>Check whether cutting pattern is valid or not. Invalid patterns can't be cut from rolls
     * due to exceeded width or technical limitation.</p>
     *
     * @param allowedCutsNumber The maximum number of cuts allowed within pattern.
     * @return true if pattern is valid, false otherwise.
     */
    public boolean isValid(int allowedCutsNumber) {
        return getTotalCutsNumber() <= allowedCutsNumber &&
                (roll == null || getWidth() <= roll.getWidth());
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
    public int getCutsHashCode() {
        int multipliers[] = new int[cuts.size()];
        int i = 0;
        for (MultiCut cut : cuts.values()) {
            multipliers[i++] = cut.getQuantity();
        }

        return Arrays.hashCode(multipliers);
    }

}
