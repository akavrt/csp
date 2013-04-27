package com.akavrt.csp.core;

import com.akavrt.csp.utils.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.DoubleMath;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * <p>A cutting pattern is modeled as a simple array of integer multiplies. Number of elements in
 * this array equals to the number of orders in problem under consideration. Value of the
 * specific multiplier means how many times corresponding order is being cut from the roll when
 * this pattern is being applied to it.</p>
 *
 * <p>To make patterns more autonomous we are storing not only multipliers, but also corresponding
 * orders, see MultiCut. This approach is more flexible and less error prone because we don't need
 * to care about the order in which multipliers are enumerated.</p>
 *
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class Pattern {
    private final Map<Integer, MultiCut> cuts;
    private Roll roll;

    /**
     * <p>Creates an empty pattern.</p>
     */
    public Pattern(Problem problem) {
        cuts = Maps.newLinkedHashMap();
        for (Order order : problem.getOrders()) {
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

    /**
     * <p>Find multiplier linked with order specified as a parameter and add to it prescribed
     * number of cuts.<p/>
     *
     * @param order    The order which is used to find right multiplier.
     * @param quantity The number of cuts which is need to be added to current value.
     * @return true if multiplier was found and updated.
     */
    public boolean addCut(Order order, int quantity) {
        return updateCut(order, quantity, true);
    }

    /**
     * <p>Find multiplier linked with order specified as a parameter and set it to prescribed
     * number of cuts.<p/>
     *
     * @param order    The order which is used to find right multiplier.
     * @param quantity The number of cuts which current value is need to be substituted with.
     * @return true if multiplier was found and updated.
     */
    public boolean setCut(Order order, int quantity) {
        return updateCut(order, quantity, false);
    }

    /**
     * <p>List of multipliers stored within the pattern, each multiplier is represented as an
     * instance of MultiCut.</p>
     *
     * @return List of multipliers.
     */
    public List<MultiCut> getCuts() {
        return Lists.newArrayList(cuts.values());
    }

    /**
     * <p>Replace current list of multipliers with new one, each multiplier is represented as an
     * instance of MultiCut.</p>
     */
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
     * <p>Total width of the roll being used to cut orders when pattern is applied to it. Measured
     * in abstract units.</p>
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
    public double getTrimWidth() {
        if (roll == null) {
            return 0;
        }

        return roll.getWidth() - getWidth();
    }

    /**
     * <p>Wasted area of the roll which will be left unused after cutting. Measured in abstract
     * square units.</p>
     *
     * @return The unused area of the roll.
     */
    public double getTrimArea() {
        if (roll == null) {
            return 0;
        }

        return roll.getLength() * getTrimWidth();
    }

    /**
     * <p>Calculate the length of the finished product we will get for specific order after
     * executing pattern. Measured in abstract units.</p>
     *
     * @param order The order in question.
     * @return The length of produced strip.
     */
    public double getProductionLengthForOrder(Order order) {
        if (roll == null) {
            return 0;
        }

        double productionLength = 0;

        // orders with unknown id should be ignored
        MultiCut cut = cuts.get(order.getInternalId());
        if (cut != null) {
            productionLength = roll.getLength() * cut.getQuantity();
        }

        return productionLength;
    }

    /**
     * <p>Calculate total number of cuts defined within pattern.</p>
     *
     * @return Total number of cuts.
     */
    public int getTotalNumberOfCuts() {
        int totalCuts = 0;

        for (MultiCut cut : cuts.values()) {
            totalCuts += cut.getQuantity();
        }

        return totalCuts;
    }

    /**
     * <p>Check whether cutting pattern is feasible or not. Infeasible patterns can't be cut from
     * rolls due to exceeded width or technical limitation.</p>
     *
     * @param allowedCutsNumber The maximum number of cuts allowed within pattern.
     * @return true if pattern is feasible, false otherwise.
     */
    public boolean isFeasible(int allowedCutsNumber) {
        boolean isCutsValid = allowedCutsNumber == 0 || getTotalNumberOfCuts() <= allowedCutsNumber;
        boolean isPatternWidthValid = roll == null
                || DoubleMath.fuzzyCompare(getWidth(), roll.getWidth(), Constants.TOLERANCE) <= 0;
        return isCutsValid && isPatternWidthValid;
    }

    /**
     * <p>Check whether cutting pattern is active or not. Inactive patterns will produce nothing
     * after execution of the cutting plan.</p>
     *
     * @return true if pattern is active, false otherwise.
     */
    public boolean isActive() {
        return roll != null && getTotalNumberOfCuts() > 0;
    }

    /**
     * <p>Utility method which is used to discover repeating patterns in cutting plan.</p>
     *
     * <p>The idea behind this check is that all patterns within the same cutting plan are based on
     * the same set of orders. This means that any specific position within two different patterns
     * is pointing to the same order.</p>
     *
     * <p>In future we should switch to something more reliable. For instance, we can replace
     * array of quantities with array of hashes. These hashes can be calculated based on both
     * quantities and corresponding order width. Another possible solution is to check pattern
     * consistency on the higher level - within solution itself.</p>
     *
     * @return The content-based hash code for array of multipliers.
     */
    public int getCutsHashCode() {
        int[] multipliers = new int[cuts.size()];
        int i = 0;
        for (MultiCut cut : cuts.values()) {
            multipliers[i++] = cut.getQuantity();
        }

        return Arrays.hashCode(multipliers);
    }

}
